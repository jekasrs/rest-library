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
    public ResponseEntity add(@RequestBody RecordView recordView) throws TypeBookException, RecordException, BookException, ClientException {
        journalService.createRecord(recordView);
        return ResponseEntity.ok("Запись в журнал успешно добавлена");
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity update(@RequestBody RecordView record, @PathVariable Long id) throws TypeBookException, RecordException, BookException {
        journalService.updateRecord(record, id);
        return ResponseEntity.ok("Запись в журнале успешно обновлена. ");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity get(@PathVariable Long id) throws RecordException {
        return ResponseEntity.ok(journalService.findRecordById(id));
    }

    @GetMapping(value = "/")
    public ResponseEntity getRecordsInfo(@RequestParam String filter,
                                         @RequestParam(required = false) Long clientId,
                                         @RequestParam(required = false) Long bookId) throws RecordException, ClientException, BookException {
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
                throw new RecordException("Не передан параметр поиска");
        }
    }

    @GetMapping(value = "/extraInfo")
    public ResponseEntity getExtraInfo(@RequestParam String filter,
                                       @RequestParam(required = false) Long clientId) throws RecordException, ClientException {
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
                throw new RecordException("Не передан параметр поиска");
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) throws RecordException {
        journalService.deleteRecordById(id);
        return ResponseEntity.ok("Запись из журнала успешно удалена");
    }

    @DeleteMapping(value = "/")
    public ResponseEntity deleteWithFilter(@RequestParam String filter,
                                           @RequestParam(required = false) Long clientId,
                                           @RequestParam(required = false) Long bookId) throws RecordException, ClientException, BookException {

        if (filter == null)
            throw new RecordException("Не передан параметр поиска");
        switch (filter.toLowerCase()) {
            case "by_client":
                journalService.deleteRecordsByClientId(clientId);
                break;
            case "by_book":
                journalService.deleteRecordsByBookId(bookId);
                break;
            default:
                throw new RecordException("Не передан параметр поиска");
        }

        return ResponseEntity.ok("Пользователи успешно удалены");
    }
}
