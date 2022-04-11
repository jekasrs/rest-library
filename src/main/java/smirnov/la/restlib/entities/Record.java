package smirnov.la.restlib.entities;

import smirnov.la.restlib.models.RecordView;
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
    private Book book;

    @ManyToOne(optional = false,
            cascade = CascadeType.MERGE)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "date_beg")
    private Date dateBegin;

    @Column(name = "date_end")
    private Date dateEnd;

    @Column(name = "date_ret")
    private Date dateReturn;

    public Record() {}

    public Record(RecordView recordView, Client client, Book book) {
        this.client = client;
        this.book = book;
        dateBegin = recordView.getDateBegin();
        dateEnd = recordView.getDateEnd();
        dateReturn = recordView.getDateReturn();
    }
}