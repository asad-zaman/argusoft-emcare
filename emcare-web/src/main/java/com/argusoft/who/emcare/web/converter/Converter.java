package com.argusoft.who.emcare.web.converter;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;

import java.util.List;
import java.util.stream.Collectors;

public class Converter {
    public static <T> T toModel(Object obj, Class<T> zClass) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);// chinh xac ten moi dc mapper
        return (T) modelMapper.map(obj, zClass);
    }

    public static <T, Y> List<T> toList(List<Y> list, Class<T> zClass) {
        return list.stream().map(e -> toModel(e, zClass)).collect(Collectors.toList());
    }
}
