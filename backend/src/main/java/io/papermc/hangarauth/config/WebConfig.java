package io.papermc.hangarauth.config;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final GeneralConfig generalConfig;

    @Autowired
    public WebConfig(final GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    @Bean
    public RestTemplate restTemplate(final List<HttpMessageConverter<?>> messageConverters) {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(this.byteArrayHttpMessageConverter());
    }

    @Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
        final ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(this.getSupportedMediaTypes());
        return converter;
    }

    @Bean
    public Filter identifyFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
                response.setHeader("Server", "HangarAuth");
                filterChain.doFilter(request, response);
            }
        };
    }

    private List<MediaType> getSupportedMediaTypes() {
        return List.of(
            MediaType.IMAGE_JPEG,
            MediaType.IMAGE_PNG,
            MediaType.APPLICATION_OCTET_STREAM
        );
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        final String[] avatarCorsOrigins = new String[this.generalConfig.allowedOrigins().length + 1];
        avatarCorsOrigins[0] = this.generalConfig.publicHost();
        System.arraycopy(this.generalConfig.allowedOrigins(), 0, avatarCorsOrigins, 1, avatarCorsOrigins.length - 1);
        registry.addMapping("/avatar/**").allowedOrigins(avatarCorsOrigins);
        registry.addMapping("/image/**").allowedOrigins(this.generalConfig.allowedOrigins());
    }
}
