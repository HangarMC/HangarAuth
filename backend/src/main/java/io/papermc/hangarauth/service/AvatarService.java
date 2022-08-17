package io.papermc.hangarauth.service;

import io.papermc.hangarauth.config.CacheConfig;
import io.papermc.hangarauth.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.db.dao.AvatarDAO;
import io.papermc.hangarauth.db.dao.OrgAvatarDAO;
import io.papermc.hangarauth.db.model.AvatarTable;
import io.papermc.hangarauth.db.model.OrgAvatarTable;
import io.papermc.hangarauth.db.model.UserAvatarTable;
import io.papermc.hangarauth.utils.Crypto;
import io.undertow.util.FileUtils;

@Service
public class AvatarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarService.class);

    private final AvatarDAO avatarDAO;
    private final OrgAvatarDAO orgAvatarDAO;
    private final Path avatarDir;
    private final Path identiconDir;
    private final ImageService imageService;
    private final Cache gravatarCache;

    @Autowired
    public AvatarService(AvatarDAO avatarDAO, OrgAvatarDAO orgAvatarDAO, GeneralConfig config, ImageService imageService, CacheManager cacheManager) throws IOException {
        this.avatarDAO = avatarDAO;
        this.orgAvatarDAO = orgAvatarDAO;
        this.avatarDir = config.getDataDir().resolve("avatars");
        this.identiconDir = this.avatarDir.resolve("identicon");
        this.imageService = imageService;
        this.gravatarCache = cacheManager.getCache(CacheConfig.GRAVATAR_CACHE);

        FileUtil.createDirectories(identiconDir);
        LOGGER.info("Avatars directory: {}", avatarDir.toAbsolutePath());
        Files.copy(AvatarService.class.getClassLoader().getResourceAsStream("avatar/blob.jpeg"), this.avatarDir.resolve("blob.jpeg"), StandardCopyOption.REPLACE_EXISTING);
    }

    public @NotNull Path getAvatarFor(@NotNull String folder, @NotNull String fileName) {
        return this.avatarDir.resolve(folder).resolve(fileName);
    }

    public byte @NotNull [] getFallbackAvatar(final @NotNull String id, final @Nullable String email, final HttpServletRequest request, final HttpServletResponse response) {
        if (email != null) {
            final byte[] gravatar = getGravatar(email, request, response);
            if (gravatar != null) {
                return gravatar;
            }
        }

        Path path = this.identiconDir.resolve(id);
        if (Files.notExists(path)) {
            final Random random = new Random(id.hashCode());
            final int[] rgb = COLORS.get(random.nextInt(COLORS.size()));
            final int size = 250;
            final int margin = (int) (size * 0.08F);
            final int cell = (size - margin * 2) / 5;

            final BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = img.createGraphics();

            g.setBackground(new Color(15790320));
            g.clearRect(0, 0, size, size);
            g.setColor(new Color(rgb[0], rgb[1], rgb[2]));

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    if (random.nextBoolean()) continue;
                    g.fillRect(i * cell + margin, j * cell + margin, cell, cell);
                    if (i == 2) continue;
                    g.fillRect(size - margin - (i + 1) * cell, j * cell + margin, cell, cell);
                }
            }

            g.dispose();

            try {
                ImageIO.write(img, "webp", path.toFile());
            } catch (final IOException e) {
                LOGGER.error("Failed to write identicon for {}", id, e);
                return imageService.getImage(this.avatarDir.resolve("blob.jpeg"), request, response);
            }
        }

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            LOGGER.error("Failed to read identicon for {}", id, e);
        }

        return imageService.getImage(this.avatarDir.resolve("blob.jpeg"), request, response);
    }

    private byte @Nullable [] getGravatar(final String email, final HttpServletRequest req, final HttpServletResponse res) {
        final byte[] key = email.toLowerCase(Locale.ROOT).strip().getBytes(StandardCharsets.UTF_8);
        final String hexHash = DigestUtils.md5DigestAsHex(key);

        if (Boolean.FALSE.equals(gravatarCache.get(hexHash, Boolean.class))) {
            return null;
        }

        final byte[] image = imageService.getImage("https://gravatar.com/avatar/" + hexHash + "?d=404&s=256", req, res);

        if (image == null || image.length == 0) {
            gravatarCache.put(hexHash, false);
            return null;
        }

        return image;
    }

    public @Nullable UserAvatarTable getUsersAvatarTable(@NotNull UUID userId) {
        return this.avatarDAO.getUserAvatar(userId);
    }

    public @Nullable OrgAvatarTable getOrgAvatarTable(@NotNull String orgName) {
        return this.orgAvatarDAO.getOrgAvatar(orgName);
    }

    public void deleteAvatarTable(@NotNull UUID userId) {
        this.avatarDAO.deleteUserAvatar(userId);
    }

    public void deleteAvatarTable(@NotNull String orgName) {
        this.orgAvatarDAO.deleteOrgAvatar(orgName);
    }

    @Transactional
    public void saveAvatar(@NotNull UUID userId, @NotNull MultipartFile avatar) throws IOException {
        checkAvatarFile(avatar);
        updateAvatar(userId, avatar, this.avatarDAO::getUserAvatar, this.avatarDAO::updateUserAvatar, this.avatarDAO::createUserAvatar, UserAvatarTable::new);
    }

    @Transactional
    public void saveOrgAvatar(@NotNull String orgName, @NotNull MultipartFile avatar) throws IOException {
        checkAvatarFile(avatar);
        updateAvatar(orgName, avatar, this.orgAvatarDAO::getOrgAvatar, this.orgAvatarDAO::updateOrgAvatar, this.orgAvatarDAO::createOrgAvatar, OrgAvatarTable::new);
    }

    private <T extends AvatarTable, S> void updateAvatar(S subject, MultipartFile avatar, Function<S, T> getter, Consumer<T> updater, Consumer<T> creator, TriFunction<S, String, String, T> ctor) throws IOException {
        String fileName = avatar.getOriginalFilename();
        if (fileName == null || "blob".equals(fileName)) {
            fileName = "blob.jpeg";
        }

        T table = getter.apply(subject);
        Consumer<T> createOrUpdate;
        if (table != null) {
            final String newHash = Crypto.md5ToHex(avatar.getBytes());
            if (table.getHash().equals(newHash)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can't upload the same image");
            }
            table.setFileName(fileName);
            table.setHash(newHash);
            createOrUpdate = updater;
        } else {
            table = ctor.apply(subject, Crypto.md5ToHex(avatar.getBytes()), fileName);
            createOrUpdate = creator;
        }
        Path path = copyFileTo(subject.toString(), avatar, fileName);
        imageService.evictCache(path.toString());
        createOrUpdate.accept(table);
    }

    private void checkAvatarFile(@NotNull MultipartFile avatar) {
        if (!StringUtils.equalsAny(avatar.getContentType(), MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid file type, only png or jpeg");
        }
        if (StringUtils.isBlank(avatar.getOriginalFilename()) || avatar.getOriginalFilename().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid filename");
        }
    }

    private Path copyFileTo(@NotNull String subject, @NotNull MultipartFile avatar, String fileName) throws IOException {
        final Path subjectDir = this.avatarDir.resolve(subject);
        Files.createDirectories(subjectDir);
        FileUtils.deleteRecursive(subjectDir);
        final Path file = subjectDir.resolve(fileName);

        // convert everything to jpeg cause they compress better
        if (!MediaType.IMAGE_JPEG_VALUE.equals(avatar.getContentType())) {
            BufferedImage img = ImageIO.read(avatar.getInputStream());
            // draw to get rid of alpha
            BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
            boolean dum = ImageIO.write(result, "jpg", file.toFile());
            if (!dum) System.out.println("failed to write jpg " + file.toFile());
        } else {
            Files.copy(avatar.getInputStream(), file);
        }

        return file;
    }

    static final java.util.List<int[]> COLORS = List.of(
        new int[]{198, 125, 40},
        new int[]{61, 155, 243},
        new int[]{74, 243, 75},
        new int[]{238, 89, 166},
        new int[]{52, 240, 224},
        new int[]{177, 156, 155},
        new int[]{240, 120, 145},
        new int[]{111, 154, 78},
        new int[]{237, 179, 245},
        new int[]{237, 101, 95},
        new int[]{89, 239, 155},
        new int[]{43, 254, 70},
        new int[]{163, 212, 245},
        new int[]{65, 152, 142},
        new int[]{165, 135, 246},
        new int[]{181, 166, 38},
        new int[]{187, 229, 206},
        new int[]{77, 164, 25},
        new int[]{179, 246, 101},
        new int[]{234, 93, 37},
        new int[]{225, 155, 115},
        new int[]{142, 140, 188},
        new int[]{223, 120, 140},
        new int[]{249, 174, 27},
        new int[]{244, 117, 225},
        new int[]{137, 141, 102},
        new int[]{75, 191, 146},
        new int[]{188, 239, 142},
        new int[]{164, 199, 145},
        new int[]{173, 120, 149},
        new int[]{59, 195, 89},
        new int[]{222, 198, 220},
        new int[]{68, 145, 187},
        new int[]{236, 204, 179},
        new int[]{159, 195, 72},
        new int[]{188, 121, 189},
        new int[]{166, 160, 85},
        new int[]{181, 233, 37},
        new int[]{236, 177, 85},
        new int[]{121, 147, 160},
        new int[]{234, 218, 110},
        new int[]{241, 157, 191},
        new int[]{62, 200, 234},
        new int[]{133, 243, 34},
        new int[]{88, 149, 110},
        new int[]{59, 228, 248},
        new int[]{183, 119, 118},
        new int[]{251, 195, 45},
        new int[]{113, 196, 122},
        new int[]{197, 115, 70},
        new int[]{80, 175, 187},
        new int[]{103, 231, 238},
        new int[]{240, 72, 133},
        new int[]{228, 149, 241},
        new int[]{180, 188, 159},
        new int[]{172, 132, 85},
        new int[]{180, 135, 251},
        new int[]{236, 194, 58},
        new int[]{217, 176, 109},
        new int[]{88, 244, 199},
        new int[]{186, 157, 239},
        new int[]{113, 230, 96},
        new int[]{206, 115, 165},
        new int[]{244, 178, 163},
        new int[]{230, 139, 26},
        new int[]{241, 125, 89},
        new int[]{83, 160, 66},
        new int[]{107, 190, 166},
        new int[]{197, 161, 210},
        new int[]{198, 203, 245},
        new int[]{238, 117, 19},
        new int[]{228, 119, 116},
        new int[]{131, 156, 41},
        new int[]{145, 178, 168},
        new int[]{139, 170, 220},
        new int[]{233, 95, 125},
        new int[]{87, 178, 230},
        new int[]{157, 200, 119},
        new int[]{237, 140, 76},
        new int[]{229, 185, 186},
        new int[]{144, 206, 212},
        new int[]{236, 209, 158},
        new int[]{185, 189, 79},
        new int[]{34, 208, 66},
        new int[]{84, 238, 129},
        new int[]{133, 140, 134},
        new int[]{67, 157, 94},
        new int[]{168, 179, 25},
        new int[]{140, 145, 240},
        new int[]{151, 241, 125},
        new int[]{67, 162, 107},
        new int[]{200, 156, 21},
        new int[]{169, 173, 189},
        new int[]{226, 116, 189},
        new int[]{133, 231, 191},
        new int[]{194, 161, 63},
        new int[]{241, 77, 99},
        new int[]{241, 217, 53},
        new int[]{123, 204, 105},
        new int[]{210, 201, 119},
        new int[]{229, 108, 155},
        new int[]{240, 91, 72},
        new int[]{187, 115, 210},
        new int[]{240, 163, 100},
        new int[]{178, 217, 57},
        new int[]{179, 135, 116},
        new int[]{204, 211, 24},
        new int[]{186, 135, 57},
        new int[]{223, 176, 135},
        new int[]{204, 148, 151},
        new int[]{116, 223, 50},
        new int[]{95, 195, 46},
        new int[]{123, 160, 236},
        new int[]{181, 172, 131},
        new int[]{142, 220, 202},
        new int[]{240, 140, 112},
        new int[]{172, 145, 164},
        new int[]{228, 124, 45},
        new int[]{135, 151, 243},
        new int[]{42, 205, 125},
        new int[]{192, 233, 116},
        new int[]{119, 170, 114},
        new int[]{158, 138, 26},
        new int[]{73, 190, 183},
        new int[]{185, 229, 243},
        new int[]{227, 107, 55},
        new int[]{196, 205, 202},
        new int[]{132, 143, 60},
        new int[]{233, 192, 237},
        new int[]{62, 150, 220},
        new int[]{205, 201, 141},
        new int[]{106, 140, 190},
        new int[]{161, 131, 205},
        new int[]{135, 134, 158},
        new int[]{198, 139, 81},
        new int[]{115, 171, 32},
        new int[]{101, 181, 67},
        new int[]{149, 137, 119},
        new int[]{37, 142, 183},
        new int[]{183, 130, 175},
        new int[]{168, 125, 133},
        new int[]{124, 142, 87},
        new int[]{236, 156, 171},
        new int[]{232, 194, 91},
        new int[]{219, 200, 69},
        new int[]{144, 219, 34},
        new int[]{219, 95, 187},
        new int[]{145, 154, 217},
        new int[]{165, 185, 100},
        new int[]{127, 238, 163},
        new int[]{224, 178, 198},
        new int[]{119, 153, 120},
        new int[]{124, 212, 92},
        new int[]{172, 161, 105},
        new int[]{231, 155, 135},
        new int[]{157, 132, 101},
        new int[]{122, 185, 146},
        new int[]{53, 166, 51},
        new int[]{70, 163, 90},
        new int[]{150, 190, 213},
        new int[]{210, 107, 60},
        new int[]{166, 152, 185},
        new int[]{159, 194, 159},
        new int[]{39, 141, 222},
        new int[]{202, 176, 161},
        new int[]{95, 140, 229},
        new int[]{168, 142, 87},
        new int[]{93, 170, 203},
        new int[]{159, 142, 54},
        new int[]{14, 168, 39},
        new int[]{94, 150, 149},
        new int[]{187, 206, 136},
        new int[]{157, 224, 166},
        new int[]{235, 158, 208},
        new int[]{109, 232, 216},
        new int[]{141, 201, 87},
        new int[]{208, 124, 118},
        new int[]{142, 125, 214},
        new int[]{19, 237, 174},
        new int[]{72, 219, 41},
        new int[]{234, 102, 111},
        new int[]{168, 142, 79},
        new int[]{188, 135, 35},
        new int[]{95, 155, 143},
        new int[]{148, 173, 116},
        new int[]{223, 112, 95},
        new int[]{228, 128, 236},
        new int[]{206, 114, 54},
        new int[]{195, 119, 88},
        new int[]{235, 140, 94},
        new int[]{235, 202, 125},
        new int[]{233, 155, 153},
        new int[]{214, 214, 238},
        new int[]{246, 200, 35},
        new int[]{151, 125, 171},
        new int[]{132, 145, 172},
        new int[]{131, 142, 118},
        new int[]{199, 126, 150},
        new int[]{61, 162, 123},
        new int[]{58, 176, 151},
        new int[]{215, 141, 69},
        new int[]{225, 154, 220},
        new int[]{220, 77, 167},
        new int[]{233, 161, 64},
        new int[]{130, 221, 137},
        new int[]{81, 191, 129},
        new int[]{169, 162, 140},
        new int[]{174, 177, 222},
        new int[]{236, 174, 47},
        new int[]{233, 188, 180},
        new int[]{69, 222, 172},
        new int[]{71, 232, 93},
        new int[]{118, 211, 238},
        new int[]{157, 224, 83},
        new int[]{218, 105, 73},
        new int[]{126, 169, 36}
    );
}
