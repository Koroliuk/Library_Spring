package ua.training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class BookTranslateDto {

    @NotBlank(message = "{title.validation.required}")
    @Size(min = 2, max = 100, message = "{title.validation.message1}")
    private String title;

    @NotBlank(message = "{authors.validation.required}")
    @Size(min = 2, max = 200, message = "{authors.validation.message1}")
    private String authorsString;

    @NotBlank(message = "{description.validation.required}")
    @Size(min = 2, max = 1000, message = "{description.validation.message1}")
    private String description;

    @NotBlank(message = "{language.validation.required}")
    @Size(min = 2, max = 30, message = "{language.validation.message1}")
    private String bookLanguage;

    @NotBlank(message = "{edition.validation.required}")
    @Size(min = 2, max = 50, message = "{edition.validation.message1}")
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
