package com.workbeattalent.books.book;

import com.workbeattalent.books.author.AuthorService;
import com.workbeattalent.books.dto.AuthorRequest;
import com.workbeattalent.books.dto.BookRequest;
import com.workbeattalent.books.dto.BookResponse;
import com.workbeattalent.books.exceptions.BookManagementInvalidException;
import com.workbeattalent.books.exceptions.EntityElementNotFoundException;
import com.workbeattalent.books.util.EntityDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository repository;
    private final AuthorService authorService;
    private final EntityDtoMapper mapper;

    public BookResponse store(final BookRequest bookRequest) {
        final var authors = this.authorService.findAll(bookRequest.authorIds());
        if (authors.size() != bookRequest.authorIds().size()) {
            throw new EntityElementNotFoundException("One or more author not found");
        }
        final var book = this.mapper.toBook(bookRequest);
        book.setAuthors(authors);
        final var savedBook = this.repository.save(book);
        return this.mapper.fromBook(savedBook);
    }


    public List<BookResponse> findAll() {
        return this.repository.findAll().stream()
                .map(this.mapper::fromBook)
                .toList();
    }

    public List<BookResponse> findByTitleContaining(final String keyword) {
        return repository.findByTitleContainingIgnoreCaseOrderByAuthors_FirstnameAsc(keyword).stream()
                .map(this.mapper::fromBook)
                .toList();
    }

    public List<BookResponse> findByAuthorName(final AuthorRequest request) {
        if (!this.authorService.exists(request.id()))
            throw new BookManagementInvalidException("Author not found. So, unable to retrieve related books");

        return repository.findByAuthors_FirstnameIgnoreCaseOrAuthors_LastnameIgnoreCase(request.firstname(), request.lastname()).stream()
                .map(this.mapper::fromBook)
                .toList();
    }

    public BookResponse findById(final Long bookId) {
        return this.repository.findById(bookId)
                .map(this.mapper::fromBook)
                .orElseThrow(() -> new EntityElementNotFoundException("No Book found with id: " + bookId));
    }

    public Long delete(final Long bookId) {
        return this.repository.findById(bookId)
                .map(book -> {
                    this.repository.delete(book);
                    return book.getId();
                })
                .orElseThrow(() -> new EntityElementNotFoundException("Unable to delete book with id: " + bookId));
    }

    public BookResponse update(final Long bookId, final BookRequest newUpdates) {
        final var optionalBook = this.repository.findById(bookId);
        if (optionalBook.isPresent()) {
            final var newAuthors = this.authorService.findAll(newUpdates.authorIds());
            if (newAuthors.size() == newUpdates.authorIds().size()) {
                final var book = optionalBook.get();
                book.setTitle(newUpdates.title());
                book.setPages(newUpdates.pages());
                book.setSummary(newUpdates.summary());
                book.setAuthors(newAuthors);
                Book saved = this.repository.save(book);
                return this.mapper.fromBook(saved);
            }
            throw new BookManagementInvalidException("One or more author(s) not found for update");
        }
        throw new EntityElementNotFoundException("Unable to find book with ID: " + bookId);
    }
}
