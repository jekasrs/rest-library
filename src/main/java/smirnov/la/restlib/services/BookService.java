package smirnov.la.restlib.services;

import smirnov.la.restlib.entities.Book;
import smirnov.la.restlib.entities.TypeBook;
import smirnov.la.restlib.exceptions.*;
import smirnov.la.restlib.models.BookView;
import smirnov.la.restlib.repositories.BooksRepository;
import smirnov.la.restlib.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class BookService {
    private final BooksRepository booksRepository;
    private final TypeBookService typeBookService;
    private final JournalRepository journalRepository;

    @Autowired
    public BookService(BooksRepository booksRepository,
                       TypeBookService typeBookService,
                       JournalRepository journalRepository) {
        this.booksRepository = booksRepository;
        this.typeBookService = typeBookService;
        this.journalRepository = journalRepository;
    }

    private boolean isValidData(String name, Integer count) {
        return name != null && count >= 0;
    }
    private boolean isUnusedBook(Book book) {
        return !journalRepository.existsByBook(book);
    }
    private List<BookView> toRepresentativeForm(List<Book> books) {
        List<BookView> l = new LinkedList<>();
        return books.stream()
                .map(BookView::new)
                .collect(Collectors.toList());
    }

    /* CREATE */
    public Book createBook(BookView bookView) throws BookException, TypeBookException {

        TypeBook typeBook = typeBookService.findTypeBookById(bookView.getTypeBookId());

        if (bookView.getCount() == null)
            bookView.setCount(0);

        if (!isValidData(bookView.getName(), bookView.getCount()))
            throw new BookException("Неправильные значения, книга не добавлена.");

        List<Book> books = booksRepository.findBooksByName(bookView.getName());
        long count = books.stream()
                .map(Book::getTypeBook)
                .filter(t -> t.equals(typeBook))
                .count();
        if (count > 0) throw new BookException("Книга " + bookView.getName() + " уже существует c таким типом");

        typeBook.setCount(typeBook.getCount() + bookView.getCount());
        typeBookService.updateTypeBook(typeBook, typeBook.getId());
        return booksRepository.save(new Book(bookView, typeBook));
    }

    /* READ */
    public List<BookView> findBookById(Long id) throws BookException {
        if (!booksRepository.existsById(id))
            throw new BookException("Нет такой книги с id =" + id);
        List<BookView> list = new LinkedList<>();
        Book b = booksRepository.getBookById(id);
        list.add(new BookView(b));
        return list;
    }
    public List<BookView> findAllBooks() {
        return toRepresentativeForm(booksRepository.findAll());
    }
    public List<BookView> findBooksByTypeId(Long typeBookId) throws TypeBookException {
        TypeBook typeBook = typeBookService.findTypeBookById(typeBookId);
        return toRepresentativeForm(booksRepository.findBooksByTypeBook(typeBook));
    }
    public List<BookView> findBooksByCountIsLessThan(Integer lessThen) {
        return toRepresentativeForm(booksRepository.findBooksByCountIsLessThan(lessThen));
    }
    public List<BookView> findBooksByTypeIdIsNull() {
        return toRepresentativeForm(booksRepository.findBooksByTypeBookIsNull());
    }
    public List<BookView> findBooksByTypeIdIsNotNull() {
        return toRepresentativeForm(booksRepository.findBooksByTypeBookIsNotNull());
    }
    public List<BookView> findBooksByCountEquals(Integer countNum) {
        return toRepresentativeForm(booksRepository.findBooksByCountEquals(countNum));
    }
    public Boolean existByName(String name) throws BookException {
        if (name == null)
            throw new BookException("Книга не может быть без названия");
        return booksRepository.existsByName(name);
    }
    public List<BookView> findBookByName(String name) throws BookException {
        if (!existByName(name))
            throw new BookException("Нет такой книги! ");
        return toRepresentativeForm(booksRepository.getAllByName(name));
    }

    /* SORT */
    public List<BookView> sortByCount() {
        return toRepresentativeForm(booksRepository.sortByCount());
    }

    /* UPDATE */
    public Book updateBook(BookView bookView, Long id) throws BookException, TypeBookException {
        if (!isValidData(bookView.getName(), bookView.getCount()))
            throw new BookException("Неправильные значения, книга не обновлена. ");

        if (!booksRepository.existsById(id))
            throw new BookException("Книга с id: " + id + " не существует, тип не обновлен.");
        TypeBook typeBook = typeBookService.findTypeBookById(bookView.getTypeBookId());

        Book preBook = booksRepository.getBookById(id);
        preBook.setName(bookView.getName());
        preBook.setCount(bookView.getCount());
        preBook.setTypeBook(typeBook);

        typeBook.setCount(typeBook.getCount() + bookView.getCount() - preBook.getCount());
        typeBookService.updateTypeBook(typeBook, typeBook.getId());
        return booksRepository.save(preBook);
    }

    /* DELETE */
    public void deleteBookById(Long id) throws BookException, TypeBookException {
        if (!booksRepository.existsById(id))
            throw new BookException("Такой книги не существует c id: " + id);
        Book book = booksRepository.getBookById(id);
        if (!isUnusedBook(book))
            throw new BookException("Книгу нельзя удалить, так как она используется в журнале");
        TypeBook typeBook = book.getTypeBook();
        typeBook.setCount(typeBook.getCount() - book.getCount());
        typeBookService.updateTypeBook(typeBook, typeBook.getId());
        booksRepository.deleteById(id);
    }
    public void deleteBooksByTypeId(Long typeId) throws TypeBookException {
        TypeBook typeBook = typeBookService.findTypeBookById(typeId);
        List<Book> books = booksRepository.findBooksByTypeBook(typeBook);

        books.stream()
                .filter(this::isUnusedBook)
                .forEach(b -> {
                            typeBook.setCount(typeBook.getCount() - b.getCount());
                            try {
                                typeBookService.updateTypeBook(typeBook, typeBook.getId());
                                booksRepository.deleteById(b.getId());
                            } catch (TypeBookException e) {
                                e.printStackTrace();
                            }
                        }
                );
    }
    public void deleteBooksByName(String name) throws BookException, TypeBookException {
        if (name == null)
            throw new BookException("Книга не может быть без названия");

        List<Book> books = booksRepository.findBooksByName(name);
        books.stream()
                .filter(this::isUnusedBook)
                .forEach(b -> {
                    TypeBook t = b.getTypeBook();
                    t.setCount(t.getCount() - b.getCount());
                    booksRepository.deleteById(b.getId());
                    try {
                        typeBookService.updateTypeBook(t, t.getId());
                    } catch (TypeBookException e) {
                        e.printStackTrace();
                    }

                });
    }
}
