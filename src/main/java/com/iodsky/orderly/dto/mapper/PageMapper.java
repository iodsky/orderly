package com.iodsky.orderly.dto.mapper;

import com.iodsky.orderly.dto.PaginationDto;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public class PageMapper {

    private PageMapper(){}

    public static <T, U>PaginationDto<U> map(Page<T> page, Function<T, U> mapper) {
        return PaginationDto.<U>builder()
                .content(page.getContent().stream().map(mapper).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
