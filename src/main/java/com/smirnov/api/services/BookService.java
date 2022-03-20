package com.smirnov.api.services;

import com.example.automationlib.entities.Book;
import com.example.automationlib.entities.TypeBook;
import com.example.automationlib.exceptions.*;
import com.example.automationlib.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BooksRepository booksRepository;
    private final TypeBookService typeBookService;

    @Autowired
    public BookService(BooksRepository booksRepository, TypeBookService typeBookService) {
        this.booksRepository = booksRepository;
        this.typeBookService = typeBookService;
    }

    /* CREATE */
    public Book createBook(Book book) throws BookIllegalSymbols, BookAlreadyExist {

        if (book.getCount() == null)
            book.setCount(0);

        if (!Book.iValidData(book.getName(), book.getCount(), book.getTypeId()))
            throw new BookIllegalSymbols("Использованы запрещенные символы");

        if (booksRepository.existsByName(book.getName()) && booksRepository.existsByTypeId(book.getTypeId()))
            throw new BookAlreadyExist("Пользователь с такими паспортными данными уже существует");

        return booksRepository.save(book);
    }

    /* READ */
    public Book findBookById(Long id) throws BookNotFoundException {
        if (!booksRepository.existsById(id))
            throw new BookNotFoundException("Такого клиента не существует");
        return booksRepository.getBookById(id);
    }
    public List<Book> findAllBooks() {
        return booksRepository.findAll();
    }
    public List<Book> findBooksByTypeId(TypeBook typeBook) throws TypeBookIllegalSymbols {
        typeBookService.existByName(typeBook.getName());
        return booksRepository.findBooksByTypeId(typeBook);
    }
    public List<Book> findBooksByCountIsLessThan(Integer lessThen) {
        return booksRepository.findBooksByCountIsLessThan(lessThen);
    }
    public List<Book> findBooksByTypeIdIsNull() {
        return booksRepository.findBooksByTypeIdIsNull();
    }
    public List<Book> findBooksByTypeIdIsNotNull() {
        return booksRepository.findBooksByTypeIdIsNotNull();
    }
    public List<Book> findBooksByCountEquals(Integer countNum) {
        return booksRepository.findBooksByCountEquals(countNum);
    }
    public Boolean existByName(String name) throws BookIllegalSymbols {
        if (name == null)
            throw new BookIllegalSymbols("Имя не заполнено");
        return booksRepository.existsByName(name);
    }

    /* SORT */
    public List<Book> sortByCount() {
        return booksRepository.sortByCount();
    }

    /* UPDATE */
    public Book updateBook(Book book, Long id) throws BookIllegalSymbols, BookNotFoundException {
        if (book.getCount() == null)
            book.setCount(0);

        if (!Book.iValidData(book.getName(), book.getCount(), book.getTypeId()))
            throw new BookIllegalSymbols("Использованы запрещенные символы");

        if (!booksRepository.existsById(id))
            throw new BookNotFoundException("Такой книги не существует: id=" + id);
        Book newBook = findBookById(id).clone(book);
        return booksRepository.save(newBook);
    }

    /* DELETE */
    public void deleteBookById(Long id) throws BookNotFoundException {
        if (!booksRepository.existsById(id))
            throw new BookNotFoundException("Такой книги не существует: id=" + id);
        booksRepository.deleteById(id);
    }
    public void deleteBooksByTypeIdIsNull() {
        booksRepository.deleteBooksByTypeIdIsNull();
    }
    public void deleteBooksByTypeId(TypeBook typeBook) throws TypeBookIllegalSymbols {
        typeBookService.existByName(typeBook.getName());
        booksRepository.deleteBooksByTypeId(typeBook);
    }
    public void deleteBooksByName(String name) throws BookIllegalSymbols {
        if (name == null)
            throw new BookIllegalSymbols("Имя не заполнено");
        booksRepository.deleteBooksByName(name);
    }
}
