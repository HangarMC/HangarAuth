package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.DummyData;
import io.papermc.hangarauth.service.AvatarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AvatarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AvatarService avatarService;

    @Test
    void badRequest400() throws Exception {
        this.mockMvc.perform(get("/avatar/not-a-uuid")).andExpect(status().isBadRequest());
    }

    @Test
    void notFound404() throws Exception {
        this.mockMvc.perform(get("/avatar/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }

    // TODO ok200WithRedirectedAvatar

    @Test
    void ok200WithConfiguredAvatar() throws Exception {
        checkDummyAvatarExists();
        this.mockMvc.perform(get("/avatar/" + DummyData.DUMMY_UUID)).andExpectAll(status().isOk(), header().doesNotExist(HttpHeaders.LOCATION), header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    private void checkDummyAvatarExists() {
        Path dummyAvatar = this.avatarService.getAvatarFor(DummyData.DUMMY_UUID, "avatar.png");
        if (Files.notExists(dummyAvatar)) {
            throw new IllegalStateException("Dummy avatar doesn't exist at " + dummyAvatar.toAbsolutePath());
        }
    }
}
