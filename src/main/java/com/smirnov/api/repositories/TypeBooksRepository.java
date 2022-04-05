package com.smirnov.api.repositories;

import com.smirnov.api.entities.TypeBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface TypeBooksRepository extends JpaRepository<TypeBook, Long> {

    Boolean existsByName(String name);
    TypeBook getTypeBookById(Long id);
    List<TypeBook> getAllByName(String name);

    void deleteAllByCount(Integer count);
    void deleteAllByFine(Double fine);

    @Query("SELECT t FROM TypeBook AS t ORDER BY t.dayCount")
    List<TypeBook> sortByDayCount();

    List<TypeBook> findTypeBooksByFineIsAfter(Double fine);
    List<TypeBook> findAllByFine(Double fine);
    List<TypeBook> findAllByCount(Integer count);
    List<TypeBook> findTypeBooksByFineIsBefore(Double fine);
}
