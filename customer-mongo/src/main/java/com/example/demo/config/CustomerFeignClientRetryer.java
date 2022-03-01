package com.example.demo.config;

import feign.RetryableException;
import feign.Retryer;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerFeignClientRetryer implements Retryer {

    private static Logger log = LoggerFactory.getLogger(CustomerFeignClientRetryer.class);

    private long retryInterval;

    private int retryMaxAttempt;

    private int attempt;

    public CustomerFeignClientRetryer() {
        this(10000L, 6);
    }

    public CustomerFeignClientRetryer(long retryInterval, int retryMaxAttempt) {
        this.retryInterval = retryInterval;
        this.retryMaxAttempt = retryMaxAttempt;
        this.attempt = 1;
    }

    @SneakyThrows
    @Override
    public void continueOrPropagate(RetryableException e) {
        log.info("Feign retry attempt {} due to {} ", attempt, e.getMessage());

        if (attempt++ >= retryMaxAttempt) {
            log.error("max retry attempt exceeded");
            throw e;
        }
        try {
            log.info("waiting for: {}", System.currentTimeMillis());
            Thread.sleep(retryInterval);
            log.info("waiting period over: {}", System.currentTimeMillis());
        } catch (InterruptedException ignored) {
            Thread.sleep(retryInterval);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Retryer clone() {
        return new CustomerFeignClientRetryer(retryInterval, retryMaxAttempt);
    }
}