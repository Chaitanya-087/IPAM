package com.ipam.api.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponse<T> {
    
    private int totalPages;
    private long currentPageSize;
    private long maxPageSize;
    private long totalElements;
    private int currentPage;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<?> data;
}
