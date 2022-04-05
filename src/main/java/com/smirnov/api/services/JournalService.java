package com.smirnov.api.services;

import com.smirnov.api.entities.Book;
import com.smirnov.api.entities.Client;
import com.smirnov.api.entities.TypeBook;
import com.smirnov.api.entities.Record;
import com.smirnov.api.exceptions.*;
import com.smirnov.api.models.BookView;
import com.smirnov.api.models.RecordView;
import com.smirnov.api.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class JournalService {

    private final JournalRepository journalRepository;
    private final ClientService clientService;
    private final TypeBookService typeBookService;
    private final BookService bookService;

    @Autowired
    public JournalService(JournalRepository journalRepository,
                          ClientService clientService,
                          TypeBookService typeBookService,
                          BookService bookService) {
        this.journalRepository = journalRepository;
        this.clientService = clientService;
        this.bookService = bookService;
        this.typeBookService = typeBookService;
    }

    private Boolean isValidData(Client client, Book book, Date dateBegin, Date dateEnd) {
        return client != null && book != null && dateBegin != null && dateEnd != null;
    }
    /* CREATE */
    public Record createRecord(RecordView record) throws RecordException, BookException, ClientException, TypeBookException {

        Client client = clientService.findClientById(record.getClientId());
        Book book = bookService.findBookById(record.getBookId());
        TypeBook typeBook = book.getTypeBook();

        if (!isValidData(client, book, record.getDateBegin(), record.getDateEnd()))
            throw new RecordException("Не корректные данные. Запись не заполнена до конца. ");

        if (book.getCount()-1 <= 0)
            throw new RecordException("Книг больше нет. ");

        typeBook.setCount(typeBook.getCount()-1);
        book.setCount(book.getCount()-1);
        BookView bookView = new BookView(book.getId(), book.getName(), book.getCount(), typeBook.getId());
        bookService.updateBook(bookView, bookView.getId());
        typeBookService.updateTypeBook(typeBook, typeBook.getId());

        Record newRecord = new Record(record, client, book);
        return journalRepository.save(newRecord);
    }

    /* READ */
    public Record findRecordById(Long id) throws RecordException {
        if (!journalRepository.existsById(id))
            throw new RecordException("Такой записи не существует id: " + id);
        return journalRepository.getRecordById(id);
    }
    public List<Record> findAllRecords() {
        return journalRepository.findAll();
    }
    public List<Record> findAllByClientId(Long clientId) throws RecordException, ClientException {
        Client client = clientService.findClientById(clientId);
        if (client == null)
            throw new RecordException("Не существует пользователя. ");
        return journalRepository.findAllByClient(client);
    }
    public List<Record> findAllByBookId(Long bookId) throws RecordException, BookException {
        Book book = bookService.findBookById(bookId);
        if (book == null)
            throw new RecordException("Не существует книги. ");
        return journalRepository.findAllByBook(book);
    }
    public List<Book> findAllBooksNotReturned() {
        return journalRepository.findAllBooksNotReturned();
    }
    public List<Book> findAllBooksOverdue() {
        return journalRepository.findAllBooksOverdue();
    }
    public List<Client> findAllClientsDebtors() {
        return journalRepository.findAllClientsDebtors();
    }
    public List<Book> findBooksNotReturnedByClient(Long clientId) throws RecordException, ClientException {
        Client client = clientService.findClientById(clientId);
        if (client == null)
            throw new RecordException("Не существует пользователя. ");
        return journalRepository.findBooksNotReturnedByClient(client);
    }
    public double getFineByClient(Long clientId) throws RecordException, ClientException {
        return findBooksNotReturnedByClient(clientId)
                .stream()
                .map(Book::getTypeBook)
                .map(TypeBook::getFine)
                .reduce(Double::sum).orElse(0.0);
    }

    /* SORT */
    public List<Record> sortByDateBegin() {
        return journalRepository.sortByDateBegin();
    }

    /* UPDATE */
    public Record updateRecord(RecordView recordView, Long id) throws RecordException, BookException, TypeBookException {
        Record record = findRecordById(id);
        if (record == null)
            throw new RecordException("Не существующие данные");

        if (recordView.getDateReturn().after(new Date()))
            throw new RecordException("Дата возврата не может быть из будущего: " + recordView.getDateReturn());

        Book book = record.getBook();
        TypeBook typeBook = book.getTypeBook();

        typeBook.setCount(typeBook.getCount()+1);
        book.setCount(book.getCount()+1);
        BookView bookView = new BookView(book.getId(), book.getName(), book.getCount(), typeBook.getId());
        bookService.updateBook(bookView, bookView.getId());
        typeBookService.updateTypeBook(typeBook, typeBook.getId());

        record.setDateReturn(recordView.getDateReturn());
        return journalRepository.save(record);
    }

    /* DELETE */
    public void deleteRecordById(Long id) throws RecordException {
        if (!journalRepository.existsById(id))
            throw new RecordException("Такой записи не существует id: " + id);
        Record record = findRecordById(id);

        if (record.getDateReturn()==null)
            throw new RecordException("Невозможно удалить запись, так как книгу не вернули. ");

        journalRepository.deleteById(id);
    }
    public void deleteRecordsByClientId(Long clientId) throws RecordException, ClientException {
        Client client = clientService.findClientById(clientId);
        if (client == null)
            throw new RecordException("Не существует клиента. ");
        List<Record> records = journalRepository.findAllByClient(client);
        for(Record r: records)
            if (r.getDateReturn()==null)
                throw new RecordException("Невозможно удалить запись, так как книгу не вернули. ");

        journalRepository.deleteRecordsByClient(client);
    }
    public void deleteRecordsByBookId(Long bookId) throws RecordException, BookException {
        Book book = bookService.findBookById(bookId);
        if (book == null)
            throw new RecordException("Не существует книги. ");

        List<Record> records = journalRepository.findAllByBook(book);
        for(Record r: records)
            if (r.getDateReturn()==null)
                throw new RecordException("Невозможно удалить запись, так как книгу не вернули. ");

        journalRepository.deleteRecordsByBook(book);

    }
}