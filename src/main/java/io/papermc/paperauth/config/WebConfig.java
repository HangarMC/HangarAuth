package io.papermc.paperauth.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.List;
import javax.servlet.Filter;

import io.papermc.paperauth.util.HtmlResourceView;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper mapper) {
        return new MappingJackson2HttpMessageConverter(mapper);
    }

    @Bean
    public RestTemplate restTemplate(List<HttpMessageConverter<?>> messageConverters) {
        RestTemplate restTemplate = new RestTemplate();
        super.addDefaultHttpMessageConverters(messageConverters);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    @Bean
    public StandardEvaluationContext standardEvaluationContext(ApplicationContext applicationContext) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext));
        return evaluationContext;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/_nuxt/**")
                .addResourceLocations("/static/_nuxt/", "classpath:/static/_nuxt/");
    }

    @Bean
    public ViewResolver internalResourceViewResolver() {
        UrlBasedViewResolver bean = new UrlBasedViewResolver();
        bean.setViewClass(HtmlResourceView.class);
        bean.setPrefix("/static/");
        bean.setSuffix(".html");
        return bean;
    }

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/account/login").setViewName("account/login/index");
        registry.addViewController("/account/logout/success").setViewName("account/logout/success/index");
        registry.addViewController("/account/reset").setViewName("account/reset/index");
        registry.addViewController("/account/settings").setViewName("account/settings/index");
        registry.addViewController("/account/signup").setViewName("account/signup/index");
    }
}
