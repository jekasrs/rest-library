package smirnov.la.restlib.entities;

import smirnov.la.restlib.models.BookView;
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
    private TypeBook typeBook;

    public Book() {
        super();
    }

    public Book(BookView bookView, TypeBook typeBook){
        name = bookView.getName();
        count = bookView.getCount();
        this.typeBook = typeBook;
    }

    @Override
    public String toString() {
        return "Book[" + id + ", " + name + " " + count + ", " + typeBook + "]\n";
    }
}
