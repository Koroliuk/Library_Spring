package ua.training.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class BookTranslateDto {

    @NotBlank(message = "Title is required")
    @Size(min=2, max = 100, message = "The title must have at least 2 and no more than 100 characters")
    private String title;

    @NotBlank(message = "Author or authors is required")
    @Size(min=2, max = 200, message = "The field must have at least 2 and no more than 200 characters")
    private String authorsString;

    @NotBlank(message = "Description is required")
    @Size(min=2, max = 1000, message = "The description must have at least 2 and no more than 1000 characters")
    private String description;

    @NotBlank(message = "Book language is required")
    @Size(min=2, max = 30, message = "The string of language must have at least 2 and no more than 30 characters")
    private String bookLanguage;

    @NotBlank(message = "Edition is required")
    @Size(min=2, max = 50, message = "The edition name must have at least 2 and no more than 50 characters")
    private String edition;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorsString() {
        return authorsString;
    }

    public void setAuthorsString(String authorsString) {
        this.authorsString = authorsString;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookLanguage() {
        return bookLanguage;
    }

    public void setBookLanguage(String bookLanguage) {
        this.bookLanguage = bookLanguage;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }
}
