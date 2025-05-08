package com.workbeattalent.books.book;

import com.workbeattalent.books.author.Author;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer pages;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Builder.Default
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "books_authors",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id")}
    )
    private Set<Author> authors = new HashSet<>();
}
