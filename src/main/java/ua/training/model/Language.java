package ua.training.model;

import javax.persistence.*;

/**
 * The class that represents the language with property <b>name</b>
 *
 * @author Yaroslav Koroliuk
 */
@Entity
@Table(name = "language")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 2, nullable = false, unique = true)
    private String name;

    public Language() {
    }

    public Language(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
