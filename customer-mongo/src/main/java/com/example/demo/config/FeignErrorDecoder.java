package com.example.demo.config;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class FeignErrorDecoder implements ErrorDecoder {

    private static Logger log = LoggerFactory.getLogger(FeignErrorDecoder.class);

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        Exception exception = defaultErrorDecoder.decode(s, response);

        if (exception instanceof RetryableException) {
            return exception;
        }

        if (response.status() == 503) {
            log.info("Retrying ...");
            return new RetryableException(response.status(), "503 error",
                    response.request().httpMethod(), new Date(), response.request());
        }

        return exception;
    }
}