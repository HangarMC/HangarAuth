package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.DummyData;
import io.papermc.hangarauth.controller.model.Traits;
import io.papermc.hangarauth.service.AvatarService;
import io.papermc.hangarauth.service.KratosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

    @MockBean
    private KratosService kratosService;

    private static final UUID REDIRECTED_UUID = UUID.randomUUID();

    @Test
    void badRequest400() throws Exception {
        this.mockMvc.perform(get("/avatar/not-a-uuid")).andExpect(status().isBadRequest());
    }

    @Test
    void notFound404() throws Exception {
        this.mockMvc.perform(get("/avatar/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    void ok200WithRedirectedAvatar() throws Exception {
        final Traits mockedTraits = mock(Traits.class);
        when(mockedTraits.getUsername()).thenReturn("Machine_Maker");
        when(this.kratosService.getTraits(REDIRECTED_UUID)).thenReturn(mockedTraits);
        this.mockMvc.perform(get("/avatar/" + REDIRECTED_UUID)).andExpectAll(status().is3xxRedirection(), header().exists(HttpHeaders.LOCATION));
    }

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
