package com.smirnov.api.models;

import lombok.Data;

@Data
public class BookView {
    private Long id;
    private String name;
    private Integer count;
    private Long typeBookId;

    public BookView(Long id, String name, Integer count, Long typeBookId) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.typeBookId = typeBookId;
    }
}
