package smirnov.la.restlib.services;

import smirnov.la.restlib.entities.TypeBook;
import smirnov.la.restlib.exceptions.*;
import smirnov.la.restlib.repositories.BooksRepository;
import smirnov.la.restlib.repositories.TypeBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Transactional(rollbackOn = Exception.class)
@Service
public class TypeBookService {

    private final TypeBooksRepository typeBooksRepository;
    private final BooksRepository booksRepository;

    private boolean isValidData(TypeBook typeBook) {
        boolean isValid = !Objects.equals(typeBook.getName(), "");
        if (typeBook.getFine() == null)
            typeBook.setFine(10.0);
        if (typeBook.getDayCount() == null)
            typeBook.setDayCount(10);
        if (typeBook.getCount() == null)
            typeBook.setCount(0);
        if (typeBook.getName() == null) isValid = false;
        else {
            if (typeBook.getFine() < 0) isValid = false;
            if (typeBook.getDayCount() <= 0) isValid = false;
            if (typeBook.getCount() < 0) isValid = false;
        }

        return isValid;
    }

    private boolean isUnusedTypeBook(TypeBook typeBook) {
        return !booksRepository.existsByTypeBook(typeBook);
    }

    @Autowired
    public TypeBookService(TypeBooksRepository typeBooksRepository, BooksRepository booksRepository) {
        this.typeBooksRepository = typeBooksRepository;
        this.booksRepository = booksRepository;
    }

    /* CREATE */
    public TypeBook createTypeBook(TypeBook typeBook) throws TypeBookException {

        if (!isValidData(typeBook))
            throw new TypeBookException("Неправильные значения, тип не добавлен. ");

        if (typeBooksRepository.existsByName(typeBook.getName()))
            throw new TypeBookException("Тип с названием: " + typeBook.getName() + " уже существует, тип не добавлен. ");

        return typeBooksRepository.save(typeBook);
    }

    /* READ */
    public TypeBook findTypeBookById(Long id) throws TypeBookException {
        if (!typeBooksRepository.existsById(id))
            throw new TypeBookException("Типа не существует с id =" + id);

        return typeBooksRepository.getTypeBookById(id);
    }

    public List<TypeBook> findAllTypesBooks() {
        return typeBooksRepository.findAll();
    }

    public Boolean existByName(String name) throws TypeBookException {
        if (name == null)
            throw new TypeBookException("Тип не может быть без названия");
        return typeBooksRepository.existsByName(name);
    }

    public List<TypeBook> findTypeBooksByFineIsAfter(Double fine) {
        return typeBooksRepository.findTypeBooksByFineIsAfter(fine);
    }

    public List<TypeBook> findTypeBooksByFineIsBefore(Double fine) {
        return typeBooksRepository.findTypeBooksByFineIsBefore(fine);
    }

    public List<TypeBook> findTypeBookByName(String name) throws TypeBookException {
        if (!existByName(name))
            throw new TypeBookException("Нет такого типа))))");

        return typeBooksRepository.getAllByName(name);
    }

    /* SORT */
    public List<TypeBook> sortByDayCount() {
        return typeBooksRepository.sortByDayCount();
    }

    /* UPDATE */
    public TypeBook updateTypeBook(TypeBook typeBook, Long id) throws TypeBookException {

        if (!isValidData(typeBook))
            throw new TypeBookException("Неправильные значения, тип не обновлен. ");

        if (!typeBooksRepository.existsById(id))
            throw new TypeBookException("Типа с id: " + id + " не существует, тип не обновлен.");

        if (typeBooksRepository.getAllByName(typeBook.getName()).size() > 1)
            throw new TypeBookException("Тип c именем " + typeBook.getName() + " занят.");

        TypeBook preTypeBook = findTypeBookById(id);
        preTypeBook.setCount(typeBook.getCount());
        preTypeBook.setFine(typeBook.getFine());
        preTypeBook.setDayCount(typeBook.getDayCount());
        preTypeBook.setName(typeBook.getName());
        return typeBooksRepository.save(preTypeBook);
    }

    /* DELETE */
    public void deleteTypeBookById(Long id) throws TypeBookException {

        if (!typeBooksRepository.existsById(id))
            throw new TypeBookException("Такого типа не существует id: " + id);

        TypeBook typeBook = typeBooksRepository.getTypeBookById(id);
        if (!isUnusedTypeBook(typeBook))
            throw new TypeBookException("Тип нельзя удалить, так как он используется в книгах");

        typeBooksRepository.deleteById(id);
    }

    public void deleteTypeBooksByCountEquals(Integer count) throws TypeBookException {
        if (count < 0)
            throw new TypeBookException("Поле \"число книг\" должно быть больше нуля, тип не удален. ");

        List<TypeBook> typeBook = typeBooksRepository.findAllByCount(count);
        for (TypeBook t : typeBook)
            if (!isUnusedTypeBook(t))
                throw new TypeBookException("Тип нельзя удалить, так как он используется в книгах");

        typeBooksRepository.deleteAllByCount(count);
    }

    public void deleteTypeBooksByFineEquals(Double fine) throws TypeBookException {
        if (fine < 0)
            throw new TypeBookException("Поле \"штраф\" должен быть больше нуля, тип не удален. ");

        List<TypeBook> typeBook = typeBooksRepository.findAllByFine(fine);
        for (TypeBook t : typeBook)
            if (!isUnusedTypeBook(t))
                throw new TypeBookException("Тип нельзя удалить, так как он используется в книгах");

        typeBooksRepository.deleteAllByFine(fine);
    }
}
