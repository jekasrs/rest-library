package com.smirnov.api.repositories;

import com.smirnov.api.entities.Book;
import com.smirnov.api.entities.TypeBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BooksRepository extends JpaRepository<Book, Long> {

    Boolean existsByName(String name);
    Boolean existsByTypeBook(TypeBook typeBook);
    Book getBookById(Long id);
    List<Book> getAllByName(String name);

    @Query("SELECT b FROM Book AS b ORDER BY b.count")
    List<Book> sortByCount();

    List<Book> findBooksByName(String name);
    List<Book> findBooksByTypeBook(TypeBook typeBook);
    List<Book> findBooksByCountIsLessThan(Integer lessThen);
    List<Book> findBooksByTypeBookIsNull();
    List<Book> findBooksByTypeBookIsNotNull();
    List<Book> findBooksByCountEquals(Integer countNum);

    void deleteAllByTypeBookIsNull();
    void deleteAllByTypeBook(TypeBook typeBook);
    void deleteAllByName(String name);
}
