package com.smirnov.api.controllers;

import com.smirnov.api.entities.Book;
import com.smirnov.api.entities.TypeBook;
import com.smirnov.api.exceptions.*;
import com.smirnov.api.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookRestController {
    public final BookService bookService;

    @Autowired
    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping(value = "/", consumes = {"application/json"})
    public ResponseEntity add(@RequestBody Book book) {
        try {
            bookService.createBook(book);
            return ResponseEntity.ok("Книга успешно добавлена");
        } catch (BookAlreadyExist | BookIllegalSymbols e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity update(@RequestBody Book book, @PathVariable Long id) {
        try {
            bookService.updateBook(book, id);
            return ResponseEntity.ok("Книга успешно обновлена");
        } catch (BookNotFoundException | BookIllegalSymbols e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookService.findBookById(id));
        } catch (BookNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @GetMapping(value = "/filter")
    public ResponseEntity getWithFilter(@RequestParam String filter,
                                        @RequestParam(required = false) TypeBook typeBook,
                                        @RequestParam(required = false) Integer lessThen,
                                        @RequestParam(required = false) Integer counNum) {
        try {
            if (filter==null)
                throw new FilterNotFound("Не передан параметр поиска");
            switch (filter.toLowerCase()) {
                case "all":
                    return ResponseEntity.ok(bookService.findAllBooks());
                case "sorted":
                    return ResponseEntity.ok(bookService.sortByCount());
                case "type":
                    return ResponseEntity.ok(bookService.findBooksByTypeId(typeBook));
                case "count_less":
                    return ResponseEntity.ok(bookService.findBooksByCountIsLessThan(lessThen));
                case "type_null":
                    return ResponseEntity.ok(bookService.findBooksByTypeIdIsNull());
                case "type_not_null":
                    return ResponseEntity.ok(bookService.findBooksByTypeIdIsNotNull());
                case "count_equals":
                    return ResponseEntity.ok(bookService.findBooksByCountEquals(counNum));
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }

        } catch (TypeBookIllegalSymbols | FilterNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            bookService.deleteBookById(id);
            return ResponseEntity.ok("Удаление книги успешно");
        } catch (BookNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @DeleteMapping(value = "/all-without_type")
    public ResponseEntity deleteWithFilter(@RequestParam String filter,
                                           @RequestParam(required = false) TypeBook typeBook,
                                           @RequestParam(required = false) String name) {
        try {
            if (filter==null)
                throw new FilterNotFound("Не передан параметр поиска");
            switch (filter.toLowerCase()) {
                case "namesakes":
                    bookService.deleteBooksByName(name);
                    break;
                case "type":
                    bookService.deleteBooksByTypeId(typeBook);
                    break;
                case "type_null":
                    bookService.deleteBooksByTypeIdIsNull();
                    break;
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }

            return ResponseEntity.ok("Книги успешно удалены");
        } catch (BookIllegalSymbols | TypeBookIllegalSymbols | FilterNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }
}