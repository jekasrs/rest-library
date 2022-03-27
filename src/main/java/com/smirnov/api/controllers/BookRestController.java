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
    public ResponseEntity add(@RequestBody BookView bookView) {


        try {
            bookService.createBook(bookView);
            return ResponseEntity.ok("Книга успешно добавлена");
        } catch (BookAlreadyExist | TypeBookNotFound | BookIncorrectData | TypeBookIncorrectData e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }

    }

    @PutMapping(value = "/{id}")
    public ResponseEntity update(@RequestBody BookView bookView, @PathVariable Long id) {


        try {
            bookService.updateBook(bookView, id);
            return ResponseEntity.ok("Книга успешно обновлена");
        } catch (BookIncorrectData | TypeBookNotFound | BookNotFoundException | TypeBookIncorrectData e) {
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

    @GetMapping(value = "/")
    public ResponseEntity getWithFilter(@RequestParam String filter,
                                        @RequestParam(required = false) Long typeId,
                                        @RequestParam(required = false) Integer count) {
        try {
            if (filter == null)
                throw new FilterNotFound("Не передан параметр поиска");
            switch (filter.toLowerCase()) {
                case "all":
                    return ResponseEntity.ok(bookService.findAllBooks());
                case "sorted":
                    return ResponseEntity.ok(bookService.sortByCount());
                case "type":
                    return ResponseEntity.ok(bookService.findBooksByTypeId(typeId));
                case "count_less":
                    return ResponseEntity.ok(bookService.findBooksByCountIsLessThan(count));
                case "type_null":
                    return ResponseEntity.ok(bookService.findBooksByTypeIdIsNull());
                case "type_not_null":
                    return ResponseEntity.ok(bookService.findBooksByTypeIdIsNotNull());
                case "count_equals":
                    return ResponseEntity.ok(bookService.findBooksByCountEquals(count));
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }

        } catch (TypeBookIncorrectData | FilterNotFound e) {
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
        } catch (BookNotFoundException | BookDeleteException | TypeBookNotFound | TypeBookIncorrectData e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }

    }

    @DeleteMapping(value = "/")
    public ResponseEntity deleteWithFilter(@RequestParam String filter,
                                           @RequestParam(required = false) Long typeId,
                                           @RequestParam(required = false) String name) {
        try {
            if (filter == null)
                throw new FilterNotFound("Не передан параметр поиска");

            switch (filter.toLowerCase()) {
                case "name":
                    bookService.deleteBooksByName(name);
                    break;
                case "type":
                    bookService.deleteBooksByTypeId(typeId);
                    break;
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }
            return ResponseEntity.ok("Книги успешно удалены");

        } catch (FilterNotFound | TypeBookIncorrectData | BookDeleteException | TypeBookNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }
}