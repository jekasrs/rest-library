package com.smirnov.api.controllers;

import com.smirnov.api.exceptions.*;
import com.smirnov.api.models.RecordView;
import com.smirnov.api.services.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/journal")
public class JournalRestController {
    private final JournalService journalService;

    @Autowired
    public JournalRestController(JournalService journalService) {
        this.journalService = journalService;
    }

    @PostMapping(value = "/", consumes = {"application/json"})
    public ResponseEntity add(@RequestBody RecordView recordView) {
        try {
            journalService.createRecord(recordView);
            return ResponseEntity.ok("Запись в журнал успешно добавлена");
        } catch (RecordIllegalOptions | BookNotFoundException | ClientNotFoundException | BookIncorrectData | TypeBookNotFound | TypeBookIncorrectData e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity update(@RequestBody RecordView record, @PathVariable Long id) {
        try {
            journalService.updateRecord(record, id);
            return ResponseEntity.ok("Запись в журнале успешно обновлена. ");
        } catch (RecordNotFound | RecordIllegalOptions | BookNotFoundException | BookIncorrectData | TypeBookNotFound | TypeBookIncorrectData e) {
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

    @GetMapping(value = "/")
    public ResponseEntity getRecordsInfo(@RequestParam String filter,
                                         @RequestParam(required = false) Long clientId,
                                         @RequestParam(required = false) Long bookId) {
        try {
            switch (filter.toLowerCase()) {
                case "all":
                    return ResponseEntity.ok(journalService.findAllRecords());
                case "sorted":
                    return ResponseEntity.ok(journalService.sortByDateBegin());
                case "by_client":
                    return ResponseEntity.ok(journalService.findAllByClientId(clientId));
                case "by_book":
                    return ResponseEntity.ok(journalService.findAllByBookId(bookId));
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }

        } catch (RecordIllegalOptions | BookNotFoundException | FilterNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @GetMapping(value = "/extraInfo")
    public ResponseEntity getExtraInfo(@RequestParam String filter,
                                       @RequestParam(required = false) Long clientId) {
        try {
            switch (filter) {
                case "overdue":
                    return ResponseEntity.ok(journalService.findAllBooksOverdue());
                case "not_returned":
                    return ResponseEntity.ok(journalService.findAllBooksNotReturned());
                case "not_returned_by_client":
                    return ResponseEntity.ok(journalService.findBooksNotReturnedByClient(clientId));
                case "debtors":
                    return ResponseEntity.ok(journalService.findAllClientsDebtors());
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }
        } catch (RecordIllegalOptions | FilterNotFound e) {
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
        } catch (RecordIllegalOptions | RecordNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @DeleteMapping(value = "/")
    public ResponseEntity deleteWithFilter(@RequestParam String filter,
                                           @RequestParam(required = false) Long clientId,
                                           @RequestParam(required = false) Long bookId) {
        try {
            if (filter == null)
                throw new FilterNotFound("Не передан параметр поиска");
            switch (filter.toLowerCase()) {
                case "by_client":
                    journalService.deleteRecordsByClientId(clientId);
                    break;
                case "by_book":
                    journalService.deleteRecordsByBookId(bookId);
                    break;
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }

            return ResponseEntity.ok("Пользователи успешно удалены");
        } catch (BookNotFoundException | RecordIllegalOptions | FilterNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }
}
