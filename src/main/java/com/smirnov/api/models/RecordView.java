package com.smirnov.api.models;

import lombok.Data;
import java.util.Date;
import com.smirnov.api.entities.Record;

@Data
public class RecordView {
    private Long id;
    private Long bookId;
    private Long clientId;
    private Date dateBegin;
    private Date dateEnd;
    private Date dateReturn;

    public RecordView(Long id, Long bookId, Long clientId, Date dateBegin, Date dateEnd, Date dateReturn) {
        this.id = id;
        this.bookId = bookId;
        this.clientId = clientId;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.dateReturn = dateReturn;
    }

    public RecordView(Record record) {
        this.id = record.getId();
        this.bookId = record.getBook().getId();
        this.clientId = record.getClient().getId();
        this.dateBegin = record.getDateBegin();
        this.dateEnd = record.getDateEnd();
        this.dateReturn = record.getDateReturn();
    }

}
