package com.example.demo.feign;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class HystrixFallBackFactory implements FallbackFactory<AccountFeign> {
    @Override
    public AccountFeign create(Throwable cause) {
        return null;
    }
}
