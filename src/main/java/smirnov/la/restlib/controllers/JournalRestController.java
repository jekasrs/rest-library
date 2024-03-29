package smirnov.la.restlib.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import smirnov.la.restlib.entities.Client;
import smirnov.la.restlib.exceptions.*;
import smirnov.la.restlib.models.BookView;
import smirnov.la.restlib.models.RecordView;
import smirnov.la.restlib.entities.Record;
import smirnov.la.restlib.services.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/journal")
public class JournalRestController {
    private final JournalService journalService;

    @Autowired
    public JournalRestController(JournalService journalService) {
        this.journalService = journalService;
    }

    @PostMapping(value = "/")
    public Record add(@RequestBody RecordView recordView, @AuthenticationPrincipal Client client) throws TypeBookException, RecordException, BookException, ClientException {
        recordView.setClientId(client.getId());
        return journalService.createRecord(recordView);
    }

    @PutMapping(value = "/{id}")
    public Record update(@PathVariable Long id, @AuthenticationPrincipal Client client) throws TypeBookException, RecordException, BookException {
        return journalService.updateRecord(id, client.getId());
    }

    @GetMapping(value = "/{id}")
    public List<RecordView> get(@PathVariable Long id) throws RecordException {
        return journalService.findRecordById(id);
    }

    @GetMapping(value = "/")
    public List<RecordView> getRecordsInfo(@RequestParam String filter,
                                         @RequestParam(required = false) Long clientId,
                                         @RequestParam(required = false) Long bookId) throws RecordException, ClientException, BookException {
        switch (filter.toLowerCase()) {
            case "all":
                return journalService.findAllRecords();
            case "sorted":
                return journalService.sortByDateBegin();
            case "by_client":
                return journalService.findAllByClientId(clientId);
            case "by_book":
                return journalService.findAllByBookId(bookId);
            default:
                throw new RecordException("Не передан параметр поиска");
        }
    }

    @GetMapping(value = "/extraInfo/books")
    public List<BookView> getExtraInfo(@RequestParam String filter,
                                       @RequestParam(required = false) Long clientId) throws RecordException, ClientException {
        switch (filter) {
            case "overdue":
                return journalService.findAllBooksOverdue();
            case "not_returned":
                return journalService.findAllBooksNotReturned();
            case "not_returned_by_client":
                return journalService.findBooksNotReturnedByClient(clientId);
            default:
                throw new RecordException("Не передан параметр поиска");
        }
    }

    @GetMapping(value = "/extraInfo/clients")
    public List<Client> getExtraInfo(@RequestParam String filter) throws RecordException {
        if ("debtors".equals(filter)) {
            return journalService.findAllClientsDebtors();
        }
        throw new RecordException("Не передан параметр поиска");
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) throws RecordException {
        journalService.deleteRecordById(id);
    }

    @DeleteMapping(value = "/")
    public void deleteWithFilter(@RequestParam String filter,
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
    }
}
