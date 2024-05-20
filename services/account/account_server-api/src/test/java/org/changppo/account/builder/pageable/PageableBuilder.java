package org.changppo.account.builder.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableBuilder {

    private int page = 0;
    private int size = 10;
    private Sort sort = Sort.unsorted();

    public PageableBuilder page(int page) {
        this.page = page;
        return this;
    }

    public PageableBuilder size(int size) {
        this.size = size;
        return this;
    }

    public PageableBuilder sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public Pageable builder() {
        return PageRequest.of(page, size, sort);
    }

    public static Pageable build() {
        return new PageableBuilder().builder();
    }
}
