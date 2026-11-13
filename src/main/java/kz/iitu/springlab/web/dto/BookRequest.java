package kz.iitu.springlab.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kz.iitu.springlab.web.Genre;

@Schema(description = "Request body for creating or updating a book")
public record BookRequest(
        @Schema(description = "Title of the book", example = "Effective Java", maxLength = 120)
        @NotBlank @Size(max = 120) String title,

        @Schema(description = "Author of the book", example = "Joshua Bloch")
        @NotBlank String author,

        @Schema(description = "Publication year (between 1450 and 2100)", example = "2018")
        @Min(1450) @Max(2100) int year,

        @Schema(description = "Genre of the book", example = "SCIENCE")
        @NotNull Genre genre) { }
