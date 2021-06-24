package ua.training.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDto {

    @NotBlank(message = "Login is required")
    @Size(min = 5, max = 20, message = "The login must have at least 5 and no more than 20 characters")
    @Pattern(regexp = "^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{4,20}$", message = "The login can only contain letters, numbers, \".\" and \"_\"")
    public String login;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "The password must be at least 8 and at most 30 characters long")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,30}$", message = "Password must contain at least one letter and number")
    public String password;

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
}
