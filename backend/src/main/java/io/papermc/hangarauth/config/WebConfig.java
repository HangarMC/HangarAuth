package io.papermc.hangarauth.config;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
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

    private List<MediaType> getSupportedMediaTypes() {
        return List.of(
            MediaType.IMAGE_JPEG,
            MediaType.IMAGE_PNG,
            MediaType.APPLICATION_OCTET_STREAM,
            MediaType.IMAGE_GIF,
            new MediaType("image", "svg+xml"),
            new MediaType("image", "webp"),
            new MediaType("image", "apng"),
            new MediaType("image", "avif")
        );
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

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilterFilterRegistrationBean() {
        final FilterRegistrationBean<ShallowEtagHeaderFilter> bean = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        bean.addUrlPatterns("/image/*");
        bean.setName("etagFilter");
        return bean;
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
