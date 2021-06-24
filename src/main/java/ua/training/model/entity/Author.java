package ua.training.model.entity;

import java.util.Objects;

public class Author {
    private long id;
    private String name;
    private String anotherName;

    public static class Builder {
        private long id;
        private String name;
        private String anotherName;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder anotherName(String anotherName) {
            this.anotherName = anotherName;
            return this;
        }

        public Author build() {
            return new Author(this);
        }
    }

    private Author(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.anotherName = builder.anotherName;
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

    public String getAnotherName() {
        return anotherName;
    }

    public void setAnotherName(String anotherName) {
        this.anotherName = anotherName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return getName().equals(author.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
