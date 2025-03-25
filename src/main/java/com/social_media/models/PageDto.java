package com.social_media.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageDto<T> {
    private List<T> content;

    private int pageNo;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean empty;

    public PageDto(Page<T> page) {
        this.content = page.getContent();
        this.pageNo = page.getTotalPages();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.empty = page.isEmpty();
    }
}
