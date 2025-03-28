package Service;

import com.revature.project1.Entities.Loan;
import com.revature.project1.Entities.LoanStatus;
import com.revature.project1.Entities.LoanType;
import com.revature.project1.Entities.User;
import com.revature.project1.repository.LoanRepository;
import com.revature.project1.service.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class loanServiceImplTest {
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanServiceimpl;

    private Loan testLoan;
    private LoanStatus testStatusLoan;
    private LoanType testLoanType;
    private User testUser;

    @BeforeEach
    public void setUp(){
        testLoan = new Loan();
        testLoan.setId(1L);
        testLoan.setAmountRequested(1000L);
        testLoan.setLastUpdate("2024-03-22");
        testLoan.setStatusReason("TEST");
        testLoan.setManagerUpdate("TEST");
        testLoan.setUser(testUser);
        testLoan.setLoanType(testLoanType);
        testLoan.setLoanStatus(testStatusLoan);
    }
    @Test
    public void testFindAllLoans(){
        when(loanRepository.findAll()).thenReturn(List.of(testLoan));
        List<Loan> loans = loanServiceimpl.findAllLoan();
        assertEquals(1, loans.size());
        assertEquals("TEST", loans.get(0).getStatusReason());
        verify(loanRepository, times(1)).findAll();

    }
    @Test
    public void testFindLoanById(){
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));
        Optional<Loan> result = loanServiceimpl.findLoanById(1L);
        assertTrue(result.isPresent());
        assertEquals("TEST",result.get().getStatusReason());
        verify(loanRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateLoan(){
        Loan newLoan = new Loan();
        newLoan.setId(1L);
        newLoan.setAmountRequested(2000L);
        newLoan.setLastUpdate("TEST");
        newLoan.setStatusReason("TEST");
        newLoan.setManagerUpdate("TEST");
        newLoan.setUser(testUser);
        newLoan.setLoanStatus(testStatusLoan);
        newLoan.setLoanType(testLoanType);

        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);
        Loan result = loanServiceimpl.createLoan(newLoan);

        assertNotNull(result);
        assertEquals("TEST", result.getStatusReason());

        verify(loanRepository, times(1)).save(newLoan);
    }
}
