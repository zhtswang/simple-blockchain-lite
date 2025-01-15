package com.fwe.flyingwhiteelephant.service;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class StringToUriConverter implements Converter<String, URI> {
    @Override
    public URI convert(String source) {
        try {
            return new URI(source);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
