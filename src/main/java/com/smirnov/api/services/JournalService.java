package com.smirnov.api.services;

import com.smirnov.api.entities.Book;
import com.smirnov.api.entities.Client;
import com.smirnov.api.entities.TypeBook;
import com.smirnov.api.entities.Record;
import com.smirnov.api.exceptions.*;
import com.smirnov.api.models.BookView;
import com.smirnov.api.models.RecordView;
import com.smirnov.api.repositories.BooksRepository;
import com.smirnov.api.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class JournalService {

    private final JournalRepository journalRepository;
    private final BooksRepository booksRepository;
    private final ClientService clientService;
    private final TypeBookService typeBookService;
    private final BookService bookService;

    @Autowired
    public JournalService(JournalRepository journalRepository,
                          BooksRepository booksRepository, ClientService clientService,
                          TypeBookService typeBookService,
                          BookService bookService) {
        this.journalRepository = journalRepository;
        this.booksRepository = booksRepository;
        this.clientService = clientService;
        this.bookService = bookService;
        this.typeBookService = typeBookService;
    }

    private Boolean isValidData(Client client, Book book, Date dateBegin, Date dateEnd) {
        return client != null && book != null && dateBegin != null && dateEnd != null;
    }

    private List<RecordView> toRepresentativeForm(List<Record> records) {
        return records.stream()
                .map(RecordView::new)
                .collect(Collectors.toList());
    }

    /* CREATE */
    public Record createRecord(RecordView record) throws RecordException, BookException, ClientException, TypeBookException {

        Client client = clientService.findClientById(record.getClientId());
        Optional<Book> optionalBook = booksRepository.findById(record.getBookId());
        Book book = optionalBook.get();
        TypeBook typeBook = book.getTypeBook();

        // creating time
        Date dateB = new Date();
        record.setDateBegin(dateB);
        Date dateE = new Date();
        dateE.setDate(dateB.getDay() + typeBook.getDayCount());
        record.setDateEnd(dateE);

        if (!isValidData(client, book, record.getDateBegin(), record.getDateEnd()))
            throw new RecordException("Не корректные данные. Запись не заполнена до конца. ");

        if (book.getCount() - 1 <= 0)
            throw new RecordException("Книг больше нет. ");

        typeBook.setCount(typeBook.getCount() - 1);
        book.setCount(book.getCount() - 1);
        BookView bookView = new BookView(book.getId(), book.getName(), book.getCount(), typeBook.getId());
        bookService.updateBook(bookView, bookView.getId());
        typeBookService.updateTypeBook(typeBook, typeBook.getId());

        Record newRecord = new Record(record, client, book);
        return journalRepository.save(newRecord);
    }

    /* READ */
    public List<RecordView> findRecordById(Long id) throws RecordException {
        if (!journalRepository.existsById(id))
            throw new RecordException("Такой записи не существует id: " + id);
        List<RecordView> list = new LinkedList<>();
        Record r = journalRepository.getRecordById(id);
        list.add(new RecordView(r));
        return list;
    }

    public List<RecordView> findAllRecords() {
        return toRepresentativeForm(journalRepository.findAll());
    }

    public List<RecordView> findAllByClientId(Long clientId) throws RecordException, ClientException {
        Client client = clientService.findClientById(clientId);
        if (client == null)
            throw new RecordException("Не существует пользователя. ");
        return toRepresentativeForm(journalRepository.findAllByClient(client));
    }

    public List<RecordView> findAllByBookId(Long bookId) throws RecordException {
        Optional<Book> optionalBook = booksRepository.findById(bookId);
        Book book = optionalBook.get();

        if (book == null)
            throw new RecordException("Не существует книги. ");
        return toRepresentativeForm(journalRepository.findAllByBook(book));
    }

    public List<BookView> findAllBooksNotReturned() {
        return journalRepository.findAllBooksNotReturned()
                .stream()
                .map(BookView::new)
                .collect(Collectors.toList());
    }

    public List<BookView> findAllBooksOverdue() {
        return journalRepository.findAllBooksOverdue().stream()
                .map(BookView::new)
                .collect(Collectors.toList());
    }

    public List<Client> findAllClientsDebtors() {
        return journalRepository.findAllClientsDebtors();
    }

    public List<BookView> findBooksNotReturnedByClient(Long clientId) throws RecordException, ClientException {
        Client client = clientService.findClientById(clientId);
        if (client == null)
            throw new RecordException("Не существует пользователя. ");
        return journalRepository.findBooksNotReturnedByClient(client)
                .stream()
                .map(BookView::new)
                .collect(Collectors.toList());
    }

    /* SORT */
    public List<RecordView> sortByDateBegin() {
        return toRepresentativeForm(journalRepository.sortByDateBegin());
    }

    /* UPDATE */
    public Record updateRecord(RecordView recordView, Long id) throws RecordException, BookException, TypeBookException {
        Record record = journalRepository.getRecordById(id);
        if (record == null)
            throw new RecordException("Не существующие данные");

        Book book = record.getBook();
        TypeBook typeBook = book.getTypeBook();

        typeBook.setCount(typeBook.getCount() + 1);
        book.setCount(book.getCount() + 1);
        BookView bookView = new BookView(book.getId(), book.getName(), book.getCount(), typeBook.getId());
        bookService.updateBook(bookView, bookView.getId());
        typeBookService.updateTypeBook(typeBook, typeBook.getId());

        record.setDateReturn(new Date());
        return journalRepository.save(record);
    }

    /* DELETE */
    public void deleteRecordById(Long id) throws RecordException {
        if (!journalRepository.existsById(id))
            throw new RecordException("Такой записи не существует id: " + id);
        Record record = journalRepository.getRecordById(id);

        if (record.getDateReturn() == null)
            throw new RecordException("Невозможно удалить запись, так как книгу не вернули. ");

        journalRepository.deleteById(id);
    }

    public void deleteRecordsByClientId(Long clientId) throws RecordException, ClientException {
        Client client = clientService.findClientById(clientId);
        if (client == null)
            throw new RecordException("Не существует клиента. ");
        List<Record> records = journalRepository.findAllByClient(client);
        for (Record r : records)
            if (r.getDateReturn() == null)
                throw new RecordException("Невозможно удалить запись, так как книгу не вернули. ");

        journalRepository.deleteRecordsByClient(client);
    }

    public void deleteRecordsByBookId(Long bookId) throws RecordException, BookException {
        Optional<Book> optionalBook = booksRepository.findById(bookId);
        Book book = optionalBook.get();
        if (book == null)
            throw new RecordException("Не существует книги. ");

        List<Record> records = journalRepository.findAllByBook(book);
        for (Record r : records)
            if (r.getDateReturn() == null)
                throw new RecordException("Невозможно удалить запись, так как книгу не вернули. ");

        journalRepository.deleteRecordsByBook(book);

    }
}
