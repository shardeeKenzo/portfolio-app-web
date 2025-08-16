package com.example.portfolioappprog5resit.presentation.viewmodel.converter;

import com.example.portfolioappprog5resit.domain.Sector;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSectorConverter implements Converter<String, Sector> {

    @Override
    public Sector convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return Sector.valueOf(source.trim().toUpperCase());
    }
}