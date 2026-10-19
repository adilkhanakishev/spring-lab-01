package kz.iitu.springlab.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kz.iitu.springlab.web.Genre;

import java.time.Instant;

@Schema(description = "Representation of a book in the catalog")
public record BookResponse(
        @Schema(description = "Unique identifier assigned by the server", example = "1")
        Long id,

        @Schema(description = "Title of the book", example = "Effective Java")
        String title,

        @Schema(description = "Author of the book", example = "Joshua Bloch")
        String author,

        @Schema(description = "Publication year", example = "2018")
        int year,

        @Schema(description = "Genre of the book", example = "SCIENCE")
        Genre genre,

        @Schema(description = "Timestamp when the book was created on the server")
        Instant createdAt) { }
