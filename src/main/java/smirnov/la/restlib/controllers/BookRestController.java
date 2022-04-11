package smirnov.la.restlib.controllers;

import smirnov.la.restlib.entities.Book;
import smirnov.la.restlib.exceptions.*;
import smirnov.la.restlib.models.BookView;
import smirnov.la.restlib.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookRestController {
    public final BookService bookService;

    @Autowired
    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping(value = "/")
    public Book add(@RequestBody BookView bookView) throws TypeBookException, BookException {
        return bookService.createBook(bookView);
    }

    @PutMapping(value = "/{id}")
    public Book update(@RequestBody BookView bookView, @PathVariable Long id) throws TypeBookException, BookException {
        return bookService.updateBook(bookView, id);
    }

    @GetMapping(value = "/{id}")
    public List<BookView> get(@PathVariable Long id) throws BookException {
        return bookService.findBookById(id);
    }

    @GetMapping(value = "/")
    public List<BookView> getWithFilter(@RequestParam String filter,
                                        @RequestParam(required = false) Long typeId,
                                        @RequestParam(required = false) Integer count,
                                        @RequestParam(required = false) String name)
            throws BookException, TypeBookException {

        if (filter == null)
            throw new BookException("Не передан параметр поиска");

        switch (filter.toLowerCase()) {
            case "all":
                return bookService.findAllBooks();
            case "sorted":
                return bookService.sortByCount();
            case "type":
                return bookService.findBooksByTypeId(typeId);
            case "count_less":
                return bookService.findBooksByCountIsLessThan(count);
            case "name":
                return bookService.findBookByName(name);
            case "count_equals":
                return bookService.findBooksByCountEquals(count);
            default:
                throw new BookException("Не передан параметр поиска");
        }
    }
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) throws TypeBookException, BookException {
        bookService.deleteBookById(id);
    }

    @DeleteMapping(value = "/")
    public void deleteWithFilter(@RequestParam String filter,
                                           @RequestParam(required = false) Long typeId,
                                           @RequestParam(required = false) String name) throws BookException, TypeBookException {

        if (filter == null)
            throw new BookException("Не передан параметр поиска");

        switch (filter.toLowerCase()) {
            case "name":
                bookService.deleteBooksByName(name);
                break;
            case "type":
                bookService.deleteBooksByTypeId(typeId);
                break;
            default:
                throw new BookException("Не передан параметр поиска");
        }
    }
}
