package com.smirnov.api.repositories;

import com.smirnov.api.entities.Client;
import com.smirnov.api.entities.Book;
import com.smirnov.api.entities.Record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface JournalRepository extends JpaRepository<Record, Long> {

    List<Record> findAllByClientId(Client client);
    List<Record> findAllByBookId(Book book);
    Record getRecordById(Long id);

    void deleteRecordsByClientId(Client client);
    void deleteRecordsByBookId(Book book);
    void deleteRecordsByDateBeginIsBefore(Date date);
    void deleteRecordsByClientIdIsNull();

    @Query("SELECT r FROM Record AS r ORDER BY r.dateBegin")
    List<Record> sortByDateBegin();

    @Query("SELECT b FROM Book b JOIN Record r ON r.bookId = b AND r.dateReturn IS NULL")
    List<Book> findAllBooksNotReturned();

    @Query("SELECT b FROM Book b JOIN Record r ON r.bookId = b AND r.dateReturn IS NULL AND r.dateEnd > CURRENT_DATE")
    List<Book> findAllBooksOverdue();

    @Query("SELECT b FROM Book b JOIN Record r ON r.clientId = ?1 AND r.dateEnd > CURRENT_DATE AND r.dateReturn IS NULL")
    List<Book> findBooksNotReturnedByClient(Client client);

    @Query("SELECT DISTINCT c FROM Client c JOIN Record r ON r.clientId = c")
    List<Client> findAllClientsEverTakenBook();

    @Query("SELECT DISTINCT c FROM Client c JOIN Record r ON r.clientId = c AND r.dateEnd > r.dateReturn")
    List<Client> findAllClientsDebtors();

}