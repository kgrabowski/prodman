package com.galvanize.prodman.config;

import com.galvanize.prodman.model.Currency;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToCurrencyConverter());
    }

    private static class StringToCurrencyConverter implements Converter<String, Currency> {
        @Override
        public Currency convert(String source) {
            return Currency.parse(source);
        }
    }
}
