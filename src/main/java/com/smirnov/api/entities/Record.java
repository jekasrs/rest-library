package com.smirnov.api.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "journal")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false,
            cascade = CascadeType.MERGE)
    @JoinColumn(name = "book_id")
    private Book bookId;

    @ManyToOne(optional = false,
            cascade = CascadeType.MERGE)
    @JoinColumn(name = "client_id")
    private Client clientId;

    @Column(name = "date_beg")
    private Date dateBegin;

    @Column(name = "date_end")
    private Date dateEnd;

    @Column(name = "date_ret")
    private Date dateReturn;

    public Record() {}

    public static Boolean isValidData(Client client, Book book, Date dateBegin, Date dateEnd) {
        return client != null && book != null && dateBegin != null && dateEnd != null;
    }
    public Record clone(Record record) {
        setBookId(record.getBookId());
        setClientId(record.getClientId());
        setDateBegin(record.getDateBegin());
        setDateEnd(record.getDateEnd());
        setDateReturn(record.getDateReturn());
        return this;
    }
}