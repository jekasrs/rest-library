package com.smirnov.api.controllers;

import com.smirnov.api.entities.TypeBook;
import com.smirnov.api.exceptions.*;
import com.smirnov.api.services.TypeBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/typebook")
public class TypeBookRestController {

    public final TypeBookService typeBookService;

    @Autowired
    public TypeBookRestController(TypeBookService typeBookService) {
        this.typeBookService = typeBookService;
    }

    @PostMapping(value = "/")
    public TypeBook add(@RequestBody TypeBook typeBook) throws TypeBookException {
        return typeBookService.createTypeBook(typeBook);
    }

    @PutMapping(value = "/{id}")
    public TypeBook update(@RequestBody TypeBook typeBook,
                           @PathVariable Long id) throws TypeBookException {
        return typeBookService.updateTypeBook(typeBook, id);
    }

    @GetMapping(value = "/{id}")
    public List<TypeBook> get(@PathVariable Long id) throws TypeBookException {
        List<TypeBook> tmp = new LinkedList<>();
        tmp.add(typeBookService.findTypeBookById(id));
        return tmp;
    }

    @GetMapping(value = "/")
    public List<TypeBook> getWithFilter(@RequestParam String filter,
                                        @RequestParam(required = false) Double fine,
                                        @RequestParam(required = false) String name) throws TypeBookException {
        if (filter == null)
            throw new TypeBookException("Не передан параметр поиска");

        switch (filter.toLowerCase()) {
            case "all":
                return typeBookService.findAllTypesBooks();
            case "sorted":
                return typeBookService.sortByDayCount();
            case "fine_before":
                return typeBookService.findTypeBooksByFineIsBefore(fine);
            case "fine_after":
                return typeBookService.findTypeBooksByFineIsAfter(fine);
            case "name":
                return typeBookService.findTypeBookByName(name);
            default:
                throw new TypeBookException("Не передан параметр поиска");
        }
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) throws TypeBookException {
        typeBookService.deleteTypeBookById(id);
    }

    @DeleteMapping(value = "/")
    public void deleteWithFilter(@RequestParam String filter,
                                 @RequestParam(required = false) Double fine,
                                 @RequestParam(required = false) Integer count) throws TypeBookException {
        if (filter == null)
            throw new TypeBookException("Не передан параметр поиска");
        switch (filter) {
            case "fine":
                typeBookService.deleteTypeBooksByFineEquals(fine);
                break;
            case "count":
                typeBookService.deleteTypeBooksByCountEquals(count);
                break;
            default:
                throw new TypeBookException("Не передан параметр поиска");
        }
    }
}
