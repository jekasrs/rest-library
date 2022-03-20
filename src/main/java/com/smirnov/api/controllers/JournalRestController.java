package com.smirnov.api.controllers;

import com.smirnov.api.entities.Book;
import com.smirnov.api.entities.Client;
import com.smirnov.api.entities.Record;
import com.smirnov.api.exceptions.*;
import com.smirnov.api.services.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/journal")
public class JournalRestController {
    private final JournalService journalService;

    @Autowired
    public JournalRestController(JournalService journalService) {
        this.journalService = journalService;
    }

    @PostMapping(value = "/", consumes = {"application/json"})
    public ResponseEntity add(@RequestBody Record record) {
        try {
            journalService.createRecord(record);
            return ResponseEntity.ok("Запись в журнал успешно добавлена");
        } catch (RecordIllegalOptions e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity update(@RequestBody Record record, @PathVariable Long id) {
        try {
            journalService.updateRecord(record, id);
            return ResponseEntity.ok("Запись в журнале успешно обновлена");
        } catch (RecordNotFound | RecordIllegalOptions e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }

    }

    @PutMapping(value = "/{id}/return_date/{date}")
    public ResponseEntity updateReturnDate(@PathVariable Long id, @PathVariable Date date) {
        try {
            journalService.updateRecord(date, id);
            return ResponseEntity.ok("Запись в журнале успешно обновлена");
        } catch (RecordNotFound | RecordIllegalOptions e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(journalService.findRecordById(id));
        } catch (RecordNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }


    @GetMapping(value = "/filter/records")
    public ResponseEntity getRecordsInfo(@RequestParam String filter,
                                         @RequestParam(required = false) Client client,
                                         @RequestParam(required = false) Book book) {
        try {
            switch (filter.toLowerCase()) {
                case "all":
                    return ResponseEntity.ok(journalService.findAllRecords());
                case "client":
                    return ResponseEntity.ok(journalService.findAllByClientId(client));
                case "book":
                    return ResponseEntity.ok(journalService.findAllByBookId(book));
                case "sort":
                    return ResponseEntity.ok(journalService.sortByDateBegin());
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }

        } catch (RecordIllegalOptions | FilterNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }


    @GetMapping(value = "/filter/books")
    public ResponseEntity getBooksInfo(@RequestParam String filter,
                                       @RequestParam(required = false) Client client) {
        try {
            switch (filter) {
                case "overdue":
                    return ResponseEntity.ok(journalService.findAllBooksOverdue());
                case "not_returned":
                    return ResponseEntity.ok(journalService.findAllBooksNotReturned());
                case "client_not_returned":
                    return ResponseEntity.ok(journalService.findBooksNotReturnedByClient(client));
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }
        } catch (RecordIllegalOptions | FilterNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }


    @GetMapping(value = "/filter/clients")
    public ResponseEntity getClintsInfo(@RequestParam String filter,
                                        @RequestParam(required = false) Client client) {
        try {
            if (filter==null)
                throw new FilterNotFound("Не передан параметр поиска");
            switch (filter) {
                case "regular_clients":
                    return ResponseEntity.ok(journalService.findAllClientsEverTakenBook());
                case "debtors":
                    return ResponseEntity.ok(journalService.findAllClientsDebtors());
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }
        } catch (FilterNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            journalService.deleteRecordById(id);
            return ResponseEntity.ok("Запись из журнала успешно удалена");
        } catch (RecordNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @DeleteMapping(value = "/filter")
    public ResponseEntity deleteWithFilter(@RequestParam String filter,
                                           @RequestParam(required = false) Client client,
                                           @RequestParam(required = false) Book book,
                                           @RequestParam(required = false) Date date) {
        try {
            if (filter==null)
                throw new FilterNotFound("Не передан параметр поиска");
            switch (filter.toLowerCase()) {
                case "client":
                    journalService.deleteRecordsByClientId(client);
                    break;
                case "book":
                    journalService.deleteRecordsByBookId(book);
                    break;
                case "date_before":
                    journalService.deleteRecordsByDateBeginIsBefore(date);
                    break;
                case "no_clients":
                    journalService.deleteRecordsByClientIdIsNull();
                    break;
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }

            return ResponseEntity.ok("Пользователи успешно удалены");
        } catch (RecordIllegalOptions | FilterNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }
}