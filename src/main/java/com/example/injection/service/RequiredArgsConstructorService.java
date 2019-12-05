package com.example.injection.service;

import javax.annotation.PostConstruct;

import com.example.injection.component.IComponent;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ConstructorAutowiredService
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RequiredArgsConstructorService {

    // @RequiredArgsConstructorを使った場合は@Autowiredは省略できる
    private final IComponent firstComponent;

    // もちろんConstructorも省略できる

    @PostConstruct
    public void execute() {
        log.info("execute()");
        this.firstComponent.execute();
    }
}