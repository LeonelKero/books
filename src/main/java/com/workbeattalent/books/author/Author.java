package com.workbeattalent.books.author;

import com.workbeattalent.books.book.Book;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstname;
    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;

    @Builder.Default
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();
}
