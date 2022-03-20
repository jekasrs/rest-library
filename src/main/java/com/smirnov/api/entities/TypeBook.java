package com.smirnov.api.entities;

import lombok.Data;

import javax.persistence.*;

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

    public static Boolean isValidData(String name, Double fine, Integer dayCount){
        return name != null && fine >= 0 && dayCount >= 0;
    }

    public TypeBook clone(TypeBook typeBook) {
        setFine    (typeBook.getFine());
        setCount   (typeBook.getCount());
        setDayCount(typeBook.getDayCount());
        setName    (typeBook.getName());
        return this;
    }

    @Override
    public String toString() {
        return "Type[" + id +
                ", " + name +
                ", " + count +
                ", " + fine +
                ", " + dayCount +
                ']';
    }
}
