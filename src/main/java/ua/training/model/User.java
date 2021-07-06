package ua.training.model;

import ua.training.model.enums.Role;

import javax.persistence.*;

/**
 * The class that represents a user with properties <b>login</b>, <b>password</b>, <b>role</b>, <b>isBlocked</b>
 *
 * @author Yaroslav Koroliuk
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private boolean isBlocked;

    public User() {
    }

    /**
     * The class that represents a builder pattern for an User class
     */
    public static class Builder {
        private long id;
        private String login;
        private String password;
        private Role role;
        private boolean isBlocked;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder isBlocked(boolean blocked) {
            isBlocked = blocked;
            return this;
        }

        /**
         * The method that creates a new user from a builder
         *
         * @return - a created user
         */
        public User build() {
            return new User(this);
        }
    }

    /**
     * Constructor - creation of a new user from a builder
     *
     * @param builder - user builder
     * @see User#User()
     */
    private User(Builder builder) {
        this.id = builder.id;
        this.login = builder.login;
        this.password = builder.password;
        this.role = builder.role;
        this.isBlocked = builder.isBlocked;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
