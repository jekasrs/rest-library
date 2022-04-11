package smirnov.la.restlib.models;

import smirnov.la.restlib.entities.Book;
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

    public BookView(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.count = book.getCount();
        this.typeBookId = book.getTypeBook().getId();
    }

    public BookView() {}
}
