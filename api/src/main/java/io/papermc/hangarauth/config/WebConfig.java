package io.papermc.hangarauth.config;

import com.google.gson.Gson;
import io.papermc.hangarauth.config.custom.KratosConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sh.ory.kratos.JSON;

import java.util.List;

@EnableWebMvc
@Configuration
@EnableConfigurationProperties(KratosConfig.class)
public class WebConfig implements WebMvcConfigurer {

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

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(byteArrayHttpMessageConverter());
    }

    @Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
        final ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(this.getSupportedMediaTypes());
        return converter;
    }

    private List<MediaType> getSupportedMediaTypes() {
        return List.of(
            MediaType.IMAGE_JPEG,
            MediaType.IMAGE_PNG,
            MediaType.APPLICATION_OCTET_STREAM
        );
    }
}
