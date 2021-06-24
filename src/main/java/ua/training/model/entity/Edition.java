package ua.training.model.entity;

public class Edition {
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

        public Edition build() {
            return new Edition(this);
        }
    }

    private Edition(Builder builder) {
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
}
