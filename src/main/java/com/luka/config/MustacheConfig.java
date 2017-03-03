package com.luka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.script.ScriptTemplateConfigurer;

/**
 * Created by luciferche on 3/1/17.
 */

@Configuration
@EnableWebMvc
public class MustacheConfig extends WebMvcConfigurerAdapter {

        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.scriptTemplate();
        }

        @Bean
        public ScriptTemplateConfigurer configurer() {
            ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
            configurer.setEngineName("nashorn");
            configurer.setScripts("mustache.js");
            configurer.setRenderObject("Mustache");
            configurer.setRenderFunction("render");
            return configurer;
        }
}
