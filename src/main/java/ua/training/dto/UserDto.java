package ua.training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDto {

    @NotBlank(message = "{login.validation.required}")
    @Size(min = 5, max = 20, message = "{login.validation.message1}")
    @Pattern(regexp = "^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{4,20}$", message = "{login.validation.message2}")
    public String login;

    @NotBlank(message = "{password.validation.required}")
    @Size(min = 8, max = 30, message = "{password.validation.message1}")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,30}$", message = "{password.validation.message2}")
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
