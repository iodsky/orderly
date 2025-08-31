package com.iodsky.orderly.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.iodsky.orderly.dto.image.ImageDto;
import com.iodsky.orderly.dto.product.ProductDto;
import com.iodsky.orderly.model.Image;
import com.iodsky.orderly.model.Product;

@Configuration
public class AppConfig {

    @Bean
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.typeMap(Product.class, ProductDto.class).addMapping(src -> src.getCategory().getName(),
                ProductDto::setCategory);

        mapper.typeMap(Image.class, ImageDto.class).addMapping(src -> src.getProduct().getName(), ImageDto::setProduct);

        return mapper;
    }
}
