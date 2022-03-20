package com.smirnov.api.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "cnt")
    private Integer count;

    // у книги есть один тип_книги, но тип_книги может быть у многих разных книг
    @ManyToOne (optional=false,
            cascade=CascadeType.MERGE)
    @JoinColumn(name = "type_id")
    private TypeBook typeId;

    public Book() {}

    public static Boolean iValidData(String name, Integer count, TypeBook typeId) {
        return name != null && count >= 0 && typeId != null;
    }
    public Book clone(Book book){
        setCount(book.getCount());
        setName(book.getName());
        setTypeId(book.getTypeId());
        return this;
    }

    @Override
    public String toString() {
        return "Book[" + id + ", " + name + " " + count + ", " + typeId + "]\n";
    }
}
