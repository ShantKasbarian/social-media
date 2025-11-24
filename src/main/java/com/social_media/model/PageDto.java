package com.social_media.model;

import com.social_media.converter.ToModelConverter;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageDto<E, M> {
    private final List<M> content;

    private final int pageNo;

    private final int pageSize;

    private final long totalElements;

    private final int totalPages;

    private final boolean empty;

    public PageDto(Page<E> page, ToModelConverter<E, M> converter) {
        this.content = page.getContent().stream().map(converter:: convertToModel).toList();
        this.pageNo = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.empty = page.isEmpty();
    }
}
