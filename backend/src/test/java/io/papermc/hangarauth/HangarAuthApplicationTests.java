package io.papermc.hangarauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.papermc.hangarauth.controller.AvatarController;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HangarAuthApplicationTests {

    @Autowired
    private AvatarController controller;

    @Test
    void contextLoads() {
        assertThat(this.controller).isNotNull();
    }

}
