package com.smirnov.api.services;

import com.smirnov.api.entities.Book;
import com.smirnov.api.entities.TypeBook;
import com.smirnov.api.exceptions.*;
import com.smirnov.api.models.BookView;
import com.smirnov.api.repositories.BooksRepository;
import com.smirnov.api.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class BookService {
    private final BooksRepository booksRepository;
    private final TypeBookService typeBookService;
    private final JournalRepository journalRepository;

    @Autowired
    public BookService(BooksRepository booksRepository,
                       TypeBookService typeBookService,
                       JournalRepository journalRepository) {
        this.booksRepository = booksRepository;
        this.typeBookService = typeBookService;
        this.journalRepository = journalRepository;
    }

    private boolean isValidData(String name, Integer count) {
        return name != null && count >= 0;
    }

    private boolean isUnusedBook(Book book) {
        return !journalRepository.existsByBook(book);
    }

    /* CREATE */
    public Book createBook(BookView bookView) throws BookAlreadyExist, TypeBookNotFound, TypeBookIncorrectData, BookIncorrectData {

        TypeBook typeBook = typeBookService.findTypeBookById(bookView.getTypeBookId());

        if (bookView.getCount() == null)
            bookView.setCount(0);

        if (!isValidData(bookView.getName(), bookView.getCount()))
            throw new BookIncorrectData("Неправильные значения, книга не добавлена.");

        List<Book> books = booksRepository.findBooksByName(bookView.getName());
        for (Book b : books)
            if (b.getTypeBook().equals(typeBook))
                throw new BookAlreadyExist("Книга " + bookView.getName() + " уже существует c таким типом");

        typeBook.setCount(typeBook.getCount()+bookView.getCount());
        typeBookService.updateTypeBook(typeBook, typeBook.getId());
        return booksRepository.save(new Book(bookView, typeBook));
    }

    /* READ */
    public Book findBookById(Long id) throws BookNotFoundException {
        if (!booksRepository.existsById(id))
            throw new BookNotFoundException("Такой книги с id =" + id);
        return booksRepository.getBookById(id);
    }
    public List<Book> findAllBooks() {
        return booksRepository.findAll();
    }
    public List<Book> findBooksByTypeId(Long typeBookId) throws TypeBookIncorrectData, TypeBookNotFound {
        TypeBook typeBook = typeBookService.findTypeBookById(typeBookId);
        return booksRepository.findBooksByTypeBook(typeBook);
    }
    public List<Book> findBooksByCountIsLessThan(Integer lessThen) {
        return booksRepository.findBooksByCountIsLessThan(lessThen);
    }
    public List<Book> findBooksByTypeIdIsNull() {
        return booksRepository.findBooksByTypeBookIsNull();
    }
    public List<Book> findBooksByTypeIdIsNotNull() {
        return booksRepository.findBooksByTypeBookIsNotNull();
    }
    public List<Book> findBooksByCountEquals(Integer countNum) {
        return booksRepository.findBooksByCountEquals(countNum);
    }

    public Boolean existByName(String name) throws BookIncorrectData {
        if (name == null)
            throw new BookIncorrectData("Книга не может быть без названия");
        return booksRepository.existsByName(name);
    }

    /* SORT */
    public List<Book> sortByCount() {
        return booksRepository.sortByCount();
    }

    /* UPDATE */
    public Book updateBook(BookView bookView, Long id) throws BookIncorrectData, BookNotFoundException, TypeBookNotFound, TypeBookIncorrectData {
        if (!isValidData(bookView.getName(), bookView.getCount()))
            throw new BookIncorrectData("Неправильные значения, книга не обновлена. ");

        if (!booksRepository.existsById(id))
            throw new BookNotFoundException("Книга с id: " + id + " не существует, тип не обновлен.");
        TypeBook typeBook = typeBookService.findTypeBookById(bookView.getTypeBookId());

        Book preBook = findBookById(id);
        preBook.setName(bookView.getName());
        preBook.setCount(bookView.getCount());
        preBook.setTypeBook(typeBook);

        typeBook.setCount(typeBook.getCount()+bookView.getCount()-preBook.getCount());
        typeBookService.updateTypeBook(typeBook, typeBook.getId());
        return booksRepository.save(preBook);
    }

    /* DELETE */
    public void deleteBookById(Long id) throws BookNotFoundException, BookDeleteException, TypeBookNotFound, TypeBookIncorrectData {
        if (!booksRepository.existsById(id))
            throw new BookNotFoundException("Такой книги не существует c id: " + id);
        Book book = booksRepository.getBookById(id);
        if (!isUnusedBook(book))
            throw new BookDeleteException("Книгу нельзя удалить, так как она используется в журнале");
        TypeBook typeBook = book.getTypeBook();
        typeBook.setCount(typeBook.getCount()-book.getCount());
        typeBookService.updateTypeBook(typeBook, typeBook.getId());
        booksRepository.deleteById(id);
    }
    public void deleteBooksByTypeId(Long typeId) throws TypeBookIncorrectData, TypeBookNotFound, BookDeleteException {
        TypeBook typeBook = typeBookService.findTypeBookById(typeId);
        List<Book> books = booksRepository.findBooksByTypeBook(typeBook);
        for (Book b : books) {
            if (!isUnusedBook(b))
                throw new BookDeleteException("Книгу нельзя удалить, так как она используется в журнале");
            typeBook.setCount(typeBook.getCount()-b.getCount());
            typeBookService.updateTypeBook(typeBook, typeBook.getId());
        }
        booksRepository.deleteAllByTypeBook(typeBook);
    }
    public void deleteBooksByName(String name) throws BookIncorrectData, BookDeleteException, TypeBookNotFound, TypeBookIncorrectData {
        if (name == null)
            throw new BookIncorrectData("Книга не может быть без названия");

        List<Book> books = booksRepository.findBooksByName(name);
        for (Book b : books) {
            if (!isUnusedBook(b))
                throw new BookDeleteException("Книгу нельзя удалить, так как она используется в журнале");
            TypeBook t = b.getTypeBook();
            t.setCount(t.getCount()-b.getCount());
            typeBookService.updateTypeBook(t, t.getId());
        }
        booksRepository.deleteAllByName(name);
    }
}