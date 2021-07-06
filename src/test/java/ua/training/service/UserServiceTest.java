package ua.training.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.model.User;
import ua.training.model.enums.Role;
import ua.training.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void singUpUser() {
        User user = new User.Builder()
                .login("user")
                .password("password")
                .isBlocked(false)
                .role(Role.READER)
                .build();

        userService.singUpUser(user);

        verify(userRepository).save(user);
    }

    @Test
    public void update() {
        User user = new User.Builder()
                .login("user")
                .password("password")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        user.setBlocked(true);

        userService.update(user);

        verify(userRepository).save(user);
    }

    @Test
    public void findById() {
        User expected = new User.Builder()
                .id(1L)
                .login("user")
                .password("password")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        when(userRepository.findById(expected.getId())).thenReturn(Optional.of(expected));

        User actual = userService.findById(expected.getId())
                .orElseThrow(() -> new NoSuchElementException("There is no such a user"));

        verify(userRepository).findById(expected.getId());
        assertEquals(expected, actual);
    }

    @Test
    public void findByNoExistsId() {
        User user = new User.Builder()
                .id(1L)
                .login("user")
                .password("password")
                .isBlocked(false)
                .role(Role.READER)
                .build();

        assertThrows(NoSuchElementException.class, () -> userService.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("There is no such a user")));
    }

    @Test
    public void findByLogin() {
        User expected = new User.Builder()
                .id(1L)
                .login("user")
                .password("password")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        when(userRepository.findByLogin(expected.getLogin())).thenReturn(Optional.of(expected));

        User actual = userService.findByLogin(expected.getLogin())
                .orElseThrow(() -> new NoSuchElementException("There is no such a user"));

        verify(userRepository).findByLogin(expected.getLogin());
        assertEquals(expected, actual);
    }

    @Test
    public void findByNoExistsLogin() {
        User user = new User.Builder()
                .id(1L)
                .login("user")
                .password("password")
                .isBlocked(false)
                .role(Role.READER)
                .build();

        assertThrows(NoSuchElementException.class, () -> userService.findByLogin(user.getLogin())
                .orElseThrow(() -> new NoSuchElementException("There is no such a user")));
    }

    @Test
    public void findAllByRole() {
        User user1 = new User.Builder()
                .id(1L)
                .login("user1")
                .password("password1")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        User user2 = new User.Builder()
                .id(2L)
                .login("user2")
                .password("password2")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        List<User> expected = Arrays.asList(user1, user2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> page = new PageImpl<>(expected, pageable, expected.size());
        when(userRepository.findAllByRole(eq(Role.READER), any(Pageable.class))).thenReturn(page);

        List<User> actual = userService.findAllByRole(Role.READER, 0, 2);

        verify(userRepository).findAllByRole(eq(Role.READER), any(Pageable.class));
        assertEquals(expected, actual);
    }

    @Test
    public void deleteById() {
        User user = new User.Builder()
                .id(1L)
                .login("user")
                .password("password")
                .isBlocked(false)
                .role(Role.READER)
                .build();

        userService.deleteById(user.getId());

        verify(userRepository).deleteById(user.getId());
    }

    @Test
    public void getAmountOfUsers() {
        User user1 = new User.Builder()
                .id(1L)
                .login("user1")
                .password("password1")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        User user2 = new User.Builder()
                .id(2L)
                .login("user2")
                .password("password2")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        int actual = userService.getAmountOfUsers();

        assertEquals(users.size(), actual);
    }

    @Test
    public void getAmountByRole() {
        User user1 = new User.Builder()
                .id(1L)
                .login("user1")
                .password("password1")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        User user2 = new User.Builder()
                .id(2L)
                .login("user2")
                .password("password2")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAllByRole(Role.READER)).thenReturn(users);

        int actual = userService.getAmountByRole(Role.READER);

        assertEquals(users.size(), actual);
    }

    @Test
    public void getCurrentUser() {
        MockedStatic<SecurityContextHolder> contextHolderMockedStatic = mockStatic(SecurityContextHolder.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        String userName = "user";
        User expected = new User.Builder()
                .id(1L)
                .login(userName)
                .password("password1")
                .isBlocked(false)
                .role(Role.READER)
                .build();

        contextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(userName);
        when(userRepository.findByLogin(userName)).thenReturn(Optional.of(expected));

        User actual = userService.getCurrentUser();

        verify(userRepository).findByLogin(anyString());
        assertEquals(expected, actual);
    }
}
