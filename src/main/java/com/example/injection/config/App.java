package com.example.injection.config;

import com.example.injection.component.FirstComponent;
import com.example.injection.component.IComponent;
import com.example.injection.component.SecondComponent;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * App
 */
@Configuration
public class App {

    @Bean("component1")
    public IComponent getComponent1() {
        return new FirstComponent();
    }

    @Bean("component2")
    public IComponent getComponent2() {
        return new SecondComponent();
    }
}