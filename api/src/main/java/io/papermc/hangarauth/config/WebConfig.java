package io.papermc.hangarauth.config;

import com.google.gson.Gson;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import sh.ory.kratos.JSON;

import java.util.List;

@EnableWebMvc
@Configuration
@EnableConfigurationProperties(KratosConfig.class)
public class WebConfig {

    private static final JSON KRATOS_JSON = new JSON();

    @Bean
    public RestTemplate restTemplate(List<HttpMessageConverter<?>> messageConverters) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    @Bean("kratos")
    public Gson gson() {
        // This is only to be used for parsing kratos models
        return KRATOS_JSON.getGson();
    }
}
