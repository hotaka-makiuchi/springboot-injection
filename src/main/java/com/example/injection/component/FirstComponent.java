package com.example.injection.component;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FirstComponent implements IComponent {

    @Override
    public void execute() {
        log.info("First");
    }
}