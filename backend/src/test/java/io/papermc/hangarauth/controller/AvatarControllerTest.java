package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.service.AvatarService;
import io.papermc.hangarauth.service.KratosService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AvatarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KratosService kratosService;

    @Test
    void getUnknownUserUrl_expectDefaultAvatarUrl() throws Exception {
        when(this.kratosService.getTraits(any(UUID.class))).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        this.mockMvc.perform(get("/avatar/user/" + UUID.randomUUID()))
            .andExpect(status().isOk())
            .andExpect(content().string("http://localhost:3001/avatar/default/default.webp"));
    }

    @Test
    void getUnknownUserUrl_expectDefaultAvatar() throws Exception {
        when(this.kratosService.getTraits(any(UUID.class))).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        this.mockMvc.perform(get("/avatar/user/" + UUID.randomUUID() + ".webp"))
            .andExpect(status().isOk())
            .andExpect(content().bytes(AvatarService.class.getClassLoader().getResourceAsStream("avatar/default.webp").readAllBytes()));
    }

    // TODO more tests
}
