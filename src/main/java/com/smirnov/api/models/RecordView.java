package com.smirnov.api.models;

import lombok.Data;
import java.util.Date;

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
}
