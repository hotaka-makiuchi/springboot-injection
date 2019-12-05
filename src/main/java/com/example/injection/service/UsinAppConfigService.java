package com.example.injection.service;

import javax.annotation.PostConstruct;

import com.example.injection.component.IComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsinAppConfigService {

    private final IComponent component;

    @Autowired
    public UsinAppConfigService(@Qualifier("component1") IComponent component) {
        this.component = component;
    }

    @PostConstruct
    public void execute() {
        log.info("service executed");
        this.component.execute();
    }
}