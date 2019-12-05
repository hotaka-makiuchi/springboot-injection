package com.example.injection.service;

import javax.annotation.PostConstruct;

import com.example.injection.component.IComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * FirstComponentService
 */
@Slf4j
@Service
public class FirstComponentService {

    @Autowired
    private IComponent firstComponent;
    // public DemoService(IComponent secondComponent) {
    //     this.component = secondComponent;
    // }
    
    @PostConstruct
    public void execute() {
        log.info("execute()");
        this.firstComponent.execute();
    }

}