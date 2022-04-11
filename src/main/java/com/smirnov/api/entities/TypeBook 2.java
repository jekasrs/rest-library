package com.smirnov.api.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
@Table(name = "book_types")
public class TypeBook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "cnt")
    private Integer count;
    @Column(name = "fine")
    private Double fine;
    @Column(name = "day_count")
    private Integer dayCount;

    public TypeBook() {}

    @Override
    public String toString() {
        return "Type[" + id +
                ", " + name +
                ", " + count +
                ", " + fine +
                ", " + dayCount +
                ']';
    }

    public Boolean equals(TypeBook typeBook){
        return typeBook.getName().equals(name);
    }
}
