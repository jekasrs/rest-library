package com.smirnov.api.repositories;

import com.example.automationlib.entities.Book;
import com.example.automationlib.entities.TypeBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BooksRepository extends JpaRepository<Book, Long> {

    Boolean existsByName(String name);
    Boolean existsByTypeId(TypeBook typeBook);
    Book getBookById(Long id);

    @Query("SELECT b FROM Book AS b ORDER BY b.count")
    List<Book> sortByCount();

    List<Book> findBooksByTypeId(TypeBook typeBook);
    List<Book> findBooksByCountIsLessThan(Integer lessThen);
    List<Book> findBooksByTypeIdIsNull();
    List<Book> findBooksByTypeIdIsNotNull();
    List<Book> findBooksByCountEquals(Integer countNum);

    void deleteBooksByTypeIdIsNull();
    void deleteBooksByTypeId(TypeBook typeBook);
    void deleteBooksByName(String name);
}
