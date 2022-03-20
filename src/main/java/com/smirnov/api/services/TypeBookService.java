package com.smirnov.api.services;

import com.example.automationlib.entities.TypeBook;
import com.example.automationlib.exceptions.*;
import com.example.automationlib.repositories.TypeBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeBookService {
    private final TypeBooksRepository typeBooksRepository;

    @Autowired
    public TypeBookService(TypeBooksRepository typeBooksRepository) {
        this.typeBooksRepository = typeBooksRepository;
    }

    /* CREATE */
    public TypeBook createTypeBook(TypeBook typeBook) throws TypeBookIllegalSymbols, TypeBookAlreadyExist {

        if (typeBook.getFine() == null) typeBook.setFine(0.0); if (typeBook.getCount() == null) typeBook.setCount(0);

        if (!TypeBook.isValidData(typeBook.getName(), typeBook.getFine(), typeBook.getDayCount()))
            throw new TypeBookIllegalSymbols("Использованы запрещенные символы");

        if (typeBooksRepository.existsByName(typeBook.getName()))
            throw new TypeBookAlreadyExist("Тип с именем= " + typeBook.getName());

        return typeBooksRepository.save(typeBook);
    }

    /* READ */
    public TypeBook findTypeBookById(Long id) throws TypeBookNotFound {
        if (!typeBooksRepository.existsById(id))
            throw new TypeBookNotFound("Типа не существует с id =" + id);

        return typeBooksRepository.getTypeBookById(id);
    }
    public List<TypeBook> findAllTypesBooks() {
        return typeBooksRepository.findAll();
    }
    public Boolean existByName(String name) throws TypeBookIllegalSymbols {
        if (name == null)
            throw new TypeBookIllegalSymbols("Имя не заполнено");
        return typeBooksRepository.existsByName(name);
    }
    public List<TypeBook> findTypeBooksByFineIsAfter(Double fine) {
        return typeBooksRepository.findTypeBooksByFineIsAfter(fine);
    }
    public List<TypeBook> findTypeBooksByFineIsBefore(Double fine) {
        return typeBooksRepository.findTypeBooksByFineIsBefore(fine);
    }

    /* SORT */
    public List<TypeBook> sortByDayCount() {
        return typeBooksRepository.sortByDayCount();
    }

    /* UPDATE */
    public TypeBook updateTypeBook(TypeBook typeBook, Long id) throws TypeBookIllegalSymbols, TypeBookNotFound {
        if (typeBook.getFine() == null) typeBook.setFine(0.0);
        if (typeBook.getCount() == null) typeBook.setCount(0);

        if (!TypeBook.isValidData(typeBook.getName(), typeBook.getFine(), typeBook.getDayCount()))
            throw new TypeBookIllegalSymbols("Использованы запрещенные символы");

        if (!typeBooksRepository.existsById(id))
            throw new TypeBookNotFound("Такого типа не существует: id=" + id);
        TypeBook newTypeBook = findTypeBookById(id).clone(typeBook);
        return typeBooksRepository.save(newTypeBook);
    }

    /* DELETE */
    public void deleteTypeBookById(Long id) throws TypeBookNotFound {
        if (!typeBooksRepository.existsById(id))
            throw new TypeBookNotFound("Такого типа не существует: id=" + id);
        typeBooksRepository.deleteById(id);
    }
    public void deleteTypeBooksByName(String name) throws TypeBookIllegalSymbols, TypeBookNotFound {
        if (name == null)
            throw new TypeBookIllegalSymbols("Имя не заполнено");
        if (!existByName(name))
            throw new TypeBookNotFound("Такого типа не существует: name=" +  name);
        typeBooksRepository.deleteTypeBooksByName(name);
    }
    public void deleteTypeBooksByCountEquals(Integer count) throws TypeBookIllegalSymbols {
        if (count < 0 || count == null)
            throw new TypeBookIllegalSymbols("Count должен быть больше нуля");
        typeBooksRepository.deleteTypeBooksByCountEquals(count);
    }
    public void deleteTypeBooksByFineEquals(Double fine) throws TypeBookIllegalSymbols {
        if (fine < 0 || fine == null)
            throw new TypeBookIllegalSymbols("Fine должен быть больше нуля");
        typeBooksRepository.deleteTypeBooksByFineEquals(fine);
    }
}