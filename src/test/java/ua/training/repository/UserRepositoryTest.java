package ua.training.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.model.User;
import ua.training.model.enums.Role;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        user1 = new User.Builder()
                .login("user1")
                .password("password1")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        user2 = new User.Builder()
                .login("user2")
                .password("password2")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        user3 = new User.Builder()
                .login("user3")
                .password("password3")
                .isBlocked(false)
                .role(Role.ADMIN)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    @After
    public void tearDown() {
        userRepository.delete(user1);
        userRepository.delete(user2);
        userRepository.delete(user3);
    }

    @Test
    public void findByExistsLogin() {
        User user = userRepository.findByLogin("user1")
                .orElseThrow(() -> new NoSuchElementException("There is on such user"));

        assertEquals(user1.getLogin(), user.getLogin());
        assertEquals(user1.getPassword(), user.getPassword());
        assertEquals(user1.isBlocked(), user.isBlocked());
        assertEquals(user1.getRole(), user.getRole());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByNoExistsLogin() {
        userRepository.findByLogin("noExistUser")
                .orElseThrow(() -> new NoSuchElementException("There is on such user"));
    }

    @Test
    public void findAllByRole() {
        List<User> readers = userRepository.findAllByRole(Role.READER);
        List<User> librarians = userRepository.findAllByRole(Role.LIBRARIAN);
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);


        int expAmountOfReaders = 2;
        int expAmountOfLibrarians = 0;
        int expAmountOfAdmins = 1;

        assertEquals(expAmountOfReaders, readers.size());
        assertEquals(expAmountOfLibrarians, librarians.size());
        assertEquals(expAmountOfAdmins, admins.size());
    }
}