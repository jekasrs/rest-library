package com.smirnov.api.controllers;

import com.smirnov.api.entities.TypeBook;
import com.smirnov.api.exceptions.*;
import com.smirnov.api.services.TypeBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/typebook")
public class TypeBookRestController {

    public final TypeBookService typeBookService;

    @Autowired
    public TypeBookRestController(TypeBookService typeBookService) {
        this.typeBookService = typeBookService;
    }

    @PostMapping(value = "/", consumes = {"application/json"})
    public ResponseEntity add(@RequestBody TypeBook typeBook) {
        try {
            typeBookService.createTypeBook(typeBook);
            return ResponseEntity.ok("Тип успешно добавлен");
        } catch (TypeBookAlreadyExist | TypeBookIncorrectData e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @PutMapping(value = "/{id}", consumes = {"application/json"})
    public ResponseEntity update(@RequestBody TypeBook typeBook, @PathVariable Long id) {
        try {
            typeBookService.updateTypeBook(typeBook, id);
            return ResponseEntity.ok("Тип успешно обновлен");
        } catch (TypeBookNotFound | TypeBookIncorrectData e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(typeBookService.findTypeBookById(id));
        } catch (TypeBookNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @GetMapping(value = "/")
    public ResponseEntity getWithFilter(@RequestParam String filter,
                                        @RequestParam(required = false) Double fine) {

        try {
            if (filter==null)
                throw new FilterNotFound("Не передан параметр поиска");
            switch (filter.toLowerCase()){
                case "all":
                    return ResponseEntity.ok(typeBookService.findAllTypesBooks());
                case "sorted":
                    return ResponseEntity.ok(typeBookService.sortByDayCount());
                case "fine_before":
                    return ResponseEntity.ok(typeBookService.findTypeBooksByFineIsBefore(fine));
                case "fine_after" :
                    return ResponseEntity.ok(typeBookService.findTypeBooksByFineIsAfter(fine));
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            typeBookService.deleteTypeBookById(id);
            return ResponseEntity.ok("Тип успешно удален");
        } catch (TypeBookNotFound | TypeBookDeleteException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @DeleteMapping(value = "/")
    public ResponseEntity deleteWithFilter(@RequestParam String filter,
                                           @RequestParam(required = false) Double fine,
                                           @RequestParam(required = false) Integer count) {
        try {
            if (filter==null)
                throw new FilterNotFound("Не передан параметр поиска");
            switch (filter) {
                case "fine":
                    typeBookService.deleteTypeBooksByFineEquals(fine);
                    break;
                case "count":
                    typeBookService.deleteTypeBooksByCountEquals(count);
                    break;
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }
            return ResponseEntity.ok("типы успешно удалены");
        } catch (TypeBookIncorrectData | TypeBookDeleteException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }
}
