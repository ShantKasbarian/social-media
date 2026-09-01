package com.social_media.model;

import com.social_media.entity.BaseEntity;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageDto<E extends BaseEntity, M> {
  private final List<M> content;

  private final int pageNo;

  private final int pageSize;

  private final long totalElements;

  private final int totalPages;

  public PageDto(Page<E> page, Function<E, M> mapper) {
    this.content = page.getContent().stream().map(mapper).toList();
    this.pageNo = page.getNumber();
    this.pageSize = page.getSize();
    this.totalElements = page.getTotalElements();
    this.totalPages = page.getTotalPages();
  }
}
