package com.workbeattalent.books.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleLikeIgnoreCaseOrderByAuthors_FirstnameAsc(@NonNull String title);

    List<Book> findByAuthors_FirstnameIgnoreCaseOrAuthors_LastnameIgnoreCase(@NonNull String firstname, String lastname);
}
