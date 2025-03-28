package Service;

import com.revature.project1.Entities.Account;
import com.revature.project1.Entities.Role;
import com.revature.project1.repository.AccountRepository;
import com.revature.project1.repository.RoleRepository;
import com.revature.project1.service.AccountServiceImplementation;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class accountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AccountServiceImplementation accountServiceImplementation;

    private Role testRole;
    private Account testAccount;

    @BeforeEach
    public void setUp(){
        testRole = new Role();
        testRole.setRoleId(1L);
        testRole.setRoleName("ADMIN");

        testAccount = new Account();
        testAccount.setAccountId(1L);
        testAccount.setUsername("TEST");
        testAccount.setPassword("TEST");
        testAccount.setRole(testRole);
    }

    @Test
    public void testCreateAccount(){
        Account newAccount = new Account();
        newAccount.setUsername("TEST");
        newAccount.setPassword("Test");
        Role transientRole = new Role();
        transientRole.setRoleId(1L);
        newAccount.setRole(transientRole);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account result = accountServiceImplementation.createAccount(newAccount);

        assertNotNull(result);
        assertEquals("TEST",result.getUsername());

        verify(roleRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(newAccount);

    }

    @Test
    public void testLoginUser_successfulLogin() {
        String username = "TEST";
        String password = "TEST";

        when(accountRepository.findByUsername("TEST")).thenReturn(testAccount);


        Account result = accountServiceImplementation.loginUser(username, password);

        assertNotNull(result);
        assertEquals("TEST", result.getUsername());
        verify(accountRepository, times(1)).findByUsername(username);
        verifyNoInteractions(roleRepository);
    }
}
