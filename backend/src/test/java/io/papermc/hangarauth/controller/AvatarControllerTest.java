package io.papermc.hangarauth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import io.papermc.hangarauth.DummyData;
import io.papermc.hangarauth.controller.model.Traits;
import io.papermc.hangarauth.service.AvatarService;
import io.papermc.hangarauth.service.KratosService;

import static org.mockito.ArgumentMatchers.any;
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
    void notFound404() throws Exception {
        when(this.kratosService.getTraits(any(UUID.class))).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        this.mockMvc.perform(get("/avatar/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    void ok200WithProxiedAvatar() throws Exception {
        final Traits mockedTraits = mock(Traits.class);
        when(mockedTraits.username()).thenReturn("Machine_Maker");
        when(this.kratosService.getTraits(REDIRECTED_UUID)).thenReturn(mockedTraits);
        this.mockMvc.perform(get("/avatar/" + REDIRECTED_UUID)).andExpectAll(status().is2xxSuccessful(), result -> {
            final List<Object> actual = result.getResponse().getHeaderValues(HttpHeaders.SERVER);
            AssertionErrors.assertTrue("Response header '" + HttpHeaders.SERVER + "'", Arrays.asList("HangarAuth", "cloudflare").equals(actual) || Arrays.asList("HangarAuth").equals(actual));
        });
    }

    @Test
    void ok200WithConfiguredAvatar() throws Exception {
        checkDummyAvatarExists();
        this.mockMvc.perform(get("/avatar/" + DummyData.DUMMY_UUID)).andExpectAll(status().isOk(), header().doesNotExist(HttpHeaders.LOCATION), header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE));
    }

    private void checkDummyAvatarExists() {
        Path dummyAvatar = this.avatarService.getAvatarFor(DummyData.DUMMY_UUID.toString(), "blob.jpeg");
        if (Files.notExists(dummyAvatar)) {
            throw new IllegalStateException("Dummy avatar doesn't exist at " + dummyAvatar.toAbsolutePath());
        }
    }
}
