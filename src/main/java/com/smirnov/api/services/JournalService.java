package com.smirnov.api.services;

import com.example.automationlib.entities.Book;
import com.example.automationlib.entities.Client;
import com.example.automationlib.entities.TypeBook;
import com.example.automationlib.entities.Record;
import com.example.automationlib.exceptions.RecordIllegalOptions;
import com.example.automationlib.exceptions.RecordNotFound;
import com.example.automationlib.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JournalService {

    private final JournalRepository journalRepository;

    @Autowired
    public JournalService(JournalRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    /* CREATE */
    public Record createRecord(Record record) throws RecordIllegalOptions {
        if (!Record.isValidData(record.getClientId(), record.getBookId(), record.getDateBegin(), record.getDateEnd()))
            throw new RecordIllegalOptions("Не существующие данные");
        return journalRepository.save(record);
    }

    /* READ */
    public Record findRecordById(Long id) throws RecordNotFound {
        if (!journalRepository.existsById(id))
            throw new RecordNotFound("Такой записи не существует: id=" + id);
        return journalRepository.getRecordById(id);
    }

    public List<Record> findAllRecords() {
        return journalRepository.findAll();
    }
    public List<Record> findAllByClientId(Client client) throws RecordIllegalOptions {
        if (client == null)
            throw new RecordIllegalOptions("Не существует пользователя");
        return journalRepository.findAllByClientId(client);
    }
    public List<Record> findAllByBookId(Book book) throws RecordIllegalOptions {
        if (book == null)
            throw new RecordIllegalOptions("Не существует книги");
        return journalRepository.findAllByBookId(book);
    }

    public List<Book> findAllBooksNotReturned() {
        return journalRepository.findAllBooksNotReturned();
    }
    public List<Book> findAllBooksOverdue() {
        return journalRepository.findAllBooksOverdue();
    }
    public List<Client> findAllClientsEverTakenBook() {
        return journalRepository.findAllClientsEverTakenBook();
    }
    public List<Client> findAllClientsDebtors() {
        return journalRepository.findAllClientsDebtors();
    }
    public List<Book> findBooksNotReturnedByClient(Client client) throws RecordIllegalOptions {
        if (client == null)
            throw new RecordIllegalOptions("Не существует пользователя");
        return journalRepository.findBooksNotReturnedByClient(client);
    }
    public double getFineByClient(Client client) throws RecordIllegalOptions {
        return findBooksNotReturnedByClient(client)
                .stream()
                .map(Book::getTypeId)
                .map(TypeBook::getFine)
                .reduce(Double::sum).orElse(0.0);
    }

    /* SORT */
    public List<Record> sortByDateBegin() {
        return journalRepository.sortByDateBegin();
    }

    /* UPDATE */
    public Record updateRecord(Record record, Long id) throws RecordIllegalOptions, RecordNotFound {
        if (!Record.isValidData(record.getClientId(), record.getBookId(), record.getDateBegin(), record.getDateEnd()))
            throw new RecordIllegalOptions("Не существующие данные");
        Record newRecord = findRecordById(id).clone(record);
        return journalRepository.save(newRecord);
    }
    public Record updateRecord(Date dateReturn, Long id) throws RecordIllegalOptions, RecordNotFound {
        Record record = findRecordById(id);
        if (record == null)
            throw new RecordIllegalOptions("Не существующие данные");

        if (dateReturn.after(new Date()))
            throw new RecordIllegalOptions("Дата из будущего: " + dateReturn);
        record.setDateReturn(dateReturn);

        return journalRepository.save(record);
    }

    /* DELETE */
    public void deleteRecordById(Long id) throws RecordNotFound {
        if (!journalRepository.existsById(id))
            throw new RecordNotFound("Такой записи не существует: id=" + id);
        journalRepository.deleteById(id);
    }

    public void deleteRecordsByClientId(Client client) throws RecordIllegalOptions {
        if (client == null)
            throw new RecordIllegalOptions("Не существует клиента");
        journalRepository.deleteRecordsByClientId(client);
    }
    public void deleteRecordsByBookId(Book book) throws RecordIllegalOptions {
        if (book == null)
            throw new RecordIllegalOptions("Не существует книги");
        journalRepository.deleteRecordsByBookId(book);

    }
    public void deleteRecordsByDateBeginIsBefore(Date date) throws RecordIllegalOptions {
        if (date == null)
            throw new RecordIllegalOptions("Не существие даты");
        journalRepository.deleteRecordsByDateBeginIsBefore(date);
    }
    public void deleteRecordsByClientIdIsNull() {
        journalRepository.deleteRecordsByClientIdIsNull();
    }
}