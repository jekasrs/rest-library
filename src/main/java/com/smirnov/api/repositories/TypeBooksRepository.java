package com.smirnov.api.repositories;

import com.example.automationlib.entities.TypeBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeBooksRepository extends JpaRepository<TypeBook, Long> {

    Boolean existsByName(String name);
    TypeBook getTypeBookById(Long id);

    void deleteTypeBooksByName(String name);
    void deleteTypeBooksByCountEquals(Integer count);
    void deleteTypeBooksByFineEquals(Double fine);

    @Query("SELECT t FROM TypeBook AS t ORDER BY t.dayCount")
    List<TypeBook> sortByDayCount();

    List<TypeBook> findTypeBooksByFineIsAfter(Double fine);
    List<TypeBook> findTypeBooksByFineIsBefore(Double fine);
}
