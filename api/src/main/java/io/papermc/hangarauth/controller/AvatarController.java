package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.db.model.UserAvatarTable;
import io.papermc.hangarauth.service.AvatarService;
import io.papermc.hangarauth.service.KratosService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final KratosService kratosService;
    private final AvatarService avatarService;
    private final GeneralConfig generalConfig;

    @Autowired
    public AvatarController(KratosService kratosService, AvatarService avatarService, GeneralConfig generalConfig) {
        this.kratosService = kratosService;
        this.avatarService = avatarService;
        this.generalConfig = generalConfig;
    }

    @GetMapping("/{userId}")
    public Object getUsersAvatar(@NotNull @PathVariable UUID userId) throws IOException {
        final UserAvatarTable userAvatarTable = this.avatarService.getUsersAvatarTable(userId);
        if (userAvatarTable == null) {
            return getUserAvatarRedirect(userId);
        }
        Path userAvatarPath = this.avatarService.getAvatarFor(userId, userAvatarTable.getFileName());
        if (Files.notExists(userAvatarPath)) {
            // TODO delete avatar table entry cause its missing for some reason
            return getUserAvatarRedirect(userId);
        }
        return Files.readAllBytes(userAvatarPath);
    }

    @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RedirectView setUsersAvatar(@NotNull @PathVariable UUID userId, @RequestParam String flowId, @CookieValue("ory_kratos_session") String session, @RequestParam("csrf_token") String csrfToken, @RequestParam MultipartFile avatar) throws IOException {
        this.kratosService.checkCsrfToken(flowId, session, csrfToken);
        this.avatarService.saveAvatar(userId, avatar);
        return new RedirectView(this.generalConfig.getPublicHost() + "/account/settings");
    }

    private RedirectView getUserAvatarRedirect(@NotNull UUID userId) throws IOException {
        String userName = this.kratosService.getTraits(userId).getUsername();
        String userNameMd5 = DigestUtils.md5DigestAsHex(userName.getBytes(StandardCharsets.UTF_8));
        long userNameHash = Long.parseLong(userNameMd5.substring(0, 15).toUpperCase(Locale.ENGLISH), 16);
        int[] num = COLORS.get((int) (userNameHash % COLORS.size()));
        //noinspection PointlessBitwiseExpression
        int colorRgb = ((num[0] & 0xFF) << 16) | ((num[1] & 0xFF) << 8) | ((num[2] & 0xFF) << 0);
        return new RedirectView(String.format("https://papermc.io/forums/letter_avatar_proxy/v2/letter/%c/%s/240.png", userName.charAt(0), StringUtils.leftPad(Integer.toHexString(colorRgb), 6, '0')));
    }

    static final List<int[]> COLORS = List.of(
        new int[]{198,125,40},
        new int[]{61,155,243},
        new int[]{74,243,75},
        new int[]{238,89,166},
        new int[]{52,240,224},
        new int[]{177,156,155},
        new int[]{240,120,145},
        new int[]{111,154,78},
        new int[]{237,179,245},
        new int[]{237,101,95},
        new int[]{89,239,155},
        new int[]{43,254,70},
        new int[]{163,212,245},
        new int[]{65,152,142},
        new int[]{165,135,246},
        new int[]{181,166,38},
        new int[]{187,229,206},
        new int[]{77,164,25},
        new int[]{179,246,101},
        new int[]{234,93,37},
        new int[]{225,155,115},
        new int[]{142,140,188},
        new int[]{223,120,140},
        new int[]{249,174,27},
        new int[]{244,117,225},
        new int[]{137,141,102},
        new int[]{75,191,146},
        new int[]{188,239,142},
        new int[]{164,199,145},
        new int[]{173,120,149},
        new int[]{59,195,89},
        new int[]{222,198,220},
        new int[]{68,145,187},
        new int[]{236,204,179},
        new int[]{159,195,72},
        new int[]{188,121,189},
        new int[]{166,160,85},
        new int[]{181,233,37},
        new int[]{236,177,85},
        new int[]{121,147,160},
        new int[]{234,218,110},
        new int[]{241,157,191},
        new int[]{62,200,234},
        new int[]{133,243,34},
        new int[]{88,149,110},
        new int[]{59,228,248},
        new int[]{183,119,118},
        new int[]{251,195,45},
        new int[]{113,196,122},
        new int[]{197,115,70},
        new int[]{80,175,187},
        new int[]{103,231,238},
        new int[]{240,72,133},
        new int[]{228,149,241},
        new int[]{180,188,159},
        new int[]{172,132,85},
        new int[]{180,135,251},
        new int[]{236,194,58},
        new int[]{217,176,109},
        new int[]{88,244,199},
        new int[]{186,157,239},
        new int[]{113,230,96},
        new int[]{206,115,165},
        new int[]{244,178,163},
        new int[]{230,139,26},
        new int[]{241,125,89},
        new int[]{83,160,66},
        new int[]{107,190,166},
        new int[]{197,161,210},
        new int[]{198,203,245},
        new int[]{238,117,19},
        new int[]{228,119,116},
        new int[]{131,156,41},
        new int[]{145,178,168},
        new int[]{139,170,220},
        new int[]{233,95,125},
        new int[]{87,178,230},
        new int[]{157,200,119},
        new int[]{237,140,76},
        new int[]{229,185,186},
        new int[]{144,206,212},
        new int[]{236,209,158},
        new int[]{185,189,79},
        new int[]{34,208,66},
        new int[]{84,238,129},
        new int[]{133,140,134},
        new int[]{67,157,94},
        new int[]{168,179,25},
        new int[]{140,145,240},
        new int[]{151,241,125},
        new int[]{67,162,107},
        new int[]{200,156,21},
        new int[]{169,173,189},
        new int[]{226,116,189},
        new int[]{133,231,191},
        new int[]{194,161,63},
        new int[]{241,77,99},
        new int[]{241,217,53},
        new int[]{123,204,105},
        new int[]{210,201,119},
        new int[]{229,108,155},
        new int[]{240,91,72},
        new int[]{187,115,210},
        new int[]{240,163,100},
        new int[]{178,217,57},
        new int[]{179,135,116},
        new int[]{204,211,24},
        new int[]{186,135,57},
        new int[]{223,176,135},
        new int[]{204,148,151},
        new int[]{116,223,50},
        new int[]{95,195,46},
        new int[]{123,160,236},
        new int[]{181,172,131},
        new int[]{142,220,202},
        new int[]{240,140,112},
        new int[]{172,145,164},
        new int[]{228,124,45},
        new int[]{135,151,243},
        new int[]{42,205,125},
        new int[]{192,233,116},
        new int[]{119,170,114},
        new int[]{158,138,26},
        new int[]{73,190,183},
        new int[]{185,229,243},
        new int[]{227,107,55},
        new int[]{196,205,202},
        new int[]{132,143,60},
        new int[]{233,192,237},
        new int[]{62,150,220},
        new int[]{205,201,141},
        new int[]{106,140,190},
        new int[]{161,131,205},
        new int[]{135,134,158},
        new int[]{198,139,81},
        new int[]{115,171,32},
        new int[]{101,181,67},
        new int[]{149,137,119},
        new int[]{37,142,183},
        new int[]{183,130,175},
        new int[]{168,125,133},
        new int[]{124,142,87},
        new int[]{236,156,171},
        new int[]{232,194,91},
        new int[]{219,200,69},
        new int[]{144,219,34},
        new int[]{219,95,187},
        new int[]{145,154,217},
        new int[]{165,185,100},
        new int[]{127,238,163},
        new int[]{224,178,198},
        new int[]{119,153,120},
        new int[]{124,212,92},
        new int[]{172,161,105},
        new int[]{231,155,135},
        new int[]{157,132,101},
        new int[]{122,185,146},
        new int[]{53,166,51},
        new int[]{70,163,90},
        new int[]{150,190,213},
        new int[]{210,107,60},
        new int[]{166,152,185},
        new int[]{159,194,159},
        new int[]{39,141,222},
        new int[]{202,176,161},
        new int[]{95,140,229},
        new int[]{168,142,87},
        new int[]{93,170,203},
        new int[]{159,142,54},
        new int[]{14,168,39},
        new int[]{94,150,149},
        new int[]{187,206,136},
        new int[]{157,224,166},
        new int[]{235,158,208},
        new int[]{109,232,216},
        new int[]{141,201,87},
        new int[]{208,124,118},
        new int[]{142,125,214},
        new int[]{19,237,174},
        new int[]{72,219,41},
        new int[]{234,102,111},
        new int[]{168,142,79},
        new int[]{188,135,35},
        new int[]{95,155,143},
        new int[]{148,173,116},
        new int[]{223,112,95},
        new int[]{228,128,236},
        new int[]{206,114,54},
        new int[]{195,119,88},
        new int[]{235,140,94},
        new int[]{235,202,125},
        new int[]{233,155,153},
        new int[]{214,214,238},
        new int[]{246,200,35},
        new int[]{151,125,171},
        new int[]{132,145,172},
        new int[]{131,142,118},
        new int[]{199,126,150},
        new int[]{61,162,123},
        new int[]{58,176,151},
        new int[]{215,141,69},
        new int[]{225,154,220},
        new int[]{220,77,167},
        new int[]{233,161,64},
        new int[]{130,221,137},
        new int[]{81,191,129},
        new int[]{169,162,140},
        new int[]{174,177,222},
        new int[]{236,174,47},
        new int[]{233,188,180},
        new int[]{69,222,172},
        new int[]{71,232,93},
        new int[]{118,211,238},
        new int[]{157,224,83},
        new int[]{218,105,73},
        new int[]{126,169,36}
    );
}
