package com.smirnov.api.controllers;

import com.smirnov.api.exceptions.*;
import com.smirnov.api.models.BookView;
import com.smirnov.api.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book")
public class BookRestController {
    public final BookService bookService;

    @Autowired
    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping(value = "/", consumes = {"application/json"})
    public ResponseEntity add(@RequestBody BookView bookView) throws TypeBookException, BookException {
        bookService.createBook(bookView);
        return ResponseEntity.ok("Книга успешно добавлена");
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity update(@RequestBody BookView bookView, @PathVariable Long id) throws TypeBookException, BookException {
        bookService.updateBook(bookView, id);
        return ResponseEntity.ok("Книга успешно обновлена");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity get(@PathVariable Long id) throws BookException {
        return ResponseEntity.ok(bookService.findBookById(id));
    }

    @GetMapping(value = "/")
    public ResponseEntity getWithFilter(@RequestParam String filter,
                                        @RequestParam(required = false) Long typeId,
                                        @RequestParam(required = false) Integer count) throws BookException, TypeBookException {

        if (filter == null)
            throw new BookException("Не передан параметр поиска");
        switch (filter.toLowerCase()) {
            case "all":
                return ResponseEntity.ok(bookService.findAllBooks());
            case "sorted":
                return ResponseEntity.ok(bookService.sortByCount());
            case "type":
                return ResponseEntity.ok(bookService.findBooksByTypeId(typeId));
            case "count_less":
                return ResponseEntity.ok(bookService.findBooksByCountIsLessThan(count));
            case "count_equals":
                return ResponseEntity.ok(bookService.findBooksByCountEquals(count));
            default:
                throw new BookException("Не передан параметр поиска");
        }
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) throws TypeBookException, BookException {
        bookService.deleteBookById(id);
        return ResponseEntity.ok("Удаление книги успешно");
    }

    @DeleteMapping(value = "/")
    public ResponseEntity deleteWithFilter(@RequestParam String filter,
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
        return ResponseEntity.ok("Книги успешно удалены");
    }
}
