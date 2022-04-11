package smirnov.la.restlib.repositories;

import smirnov.la.restlib.entities.Client;
import smirnov.la.restlib.entities.Book;
import smirnov.la.restlib.entities.Record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface JournalRepository extends JpaRepository<Record, Long> {

    List<Record> findAllByClient(Client client);
    List<Record> findAllByBook(Book book);
    Record getRecordById(Long id);

    void deleteRecordsByClient(Client client);
    void deleteRecordsByBook(Book book);

    Boolean existsByBook(Book book);

    @Query("SELECT r FROM Record AS r ORDER BY r.dateBegin")
    List<Record> sortByDateBegin();

    @Query("SELECT b FROM Book b JOIN Record r ON r.book = b AND r.dateReturn IS NULL")
    List<Book> findAllBooksNotReturned();

    @Query("SELECT b FROM Book b JOIN Record r ON r.book = b AND r.dateReturn IS NULL AND r.dateEnd > CURRENT_DATE")
    List<Book> findAllBooksOverdue();

    @Query("SELECT b FROM Book b JOIN Record r ON r.client = ?1 AND r.dateEnd > CURRENT_DATE AND r.dateReturn IS NULL")
    List<Book> findBooksNotReturnedByClient(Client client);

    @Query("SELECT DISTINCT c FROM Client c JOIN Record r ON r.client = c")
    List<Client> findAllClientsEverTakenBook();

    @Query("SELECT DISTINCT c FROM Client c JOIN Record r ON r.client = c AND r.dateEnd > r.dateReturn")
    List<Client> findAllClientsDebtors();

}