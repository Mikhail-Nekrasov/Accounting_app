package fi.haagahelia.accounting.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpSession;

import fi.haagahelia.accounting.model.Account;
import fi.haagahelia.accounting.model.Category;
import fi.haagahelia.accounting.model.Entry;
import fi.haagahelia.accounting.model.EntryType;
import fi.haagahelia.accounting.model.User;
import fi.haagahelia.accounting.repository.AccountRepository;
import fi.haagahelia.accounting.repository.CategoryRepository;
import fi.haagahelia.accounting.repository.EntryRepository;
import fi.haagahelia.accounting.repository.UserRepository;

public class EntryControllerTest {

    @Mock
    private EntryRepository entryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private Authentication auth;

    @Mock
    private HttpSession session;

    @InjectMocks
    private EntryController entryController;

    private User testUser;
    private Category testCategory;
    private Account testAccount;
    private Entry testEntry;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test user
        testUser = new User();
        testUser.setUsername("test_user");

        // Setup test category
        testCategory = new Category();
        testCategory.setName("TestCategory");
        testCategory.setType(EntryType.INCOME);
        testCategory.setUser(testUser);

        // Setup test account
        testAccount = new Account();
        testAccount.setName("TestAccount");
        testAccount.setAmount(BigDecimal.valueOf(1000));
        testAccount.setUser(testUser);

        // Setup test entry
        testEntry = new Entry(
            "TestEntry",
            EntryType.INCOME,
            BigDecimal.valueOf(500),
            LocalDateTime.now(),
            "Description",
            testCategory,
            testAccount,
            testUser
        );

        // Mock authentication
        when(auth.getName()).thenReturn("test_user");
        when(userRepository.findByUsername("test_user")).thenReturn(testUser);

        // Mock entry repository for findById
        when(entryRepository.findById(anyLong())).thenReturn(Optional.of(testEntry));

        // Mock account repository save
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void testGetEntriesReturnsCorrectView() {
        // Mock repository to return a page with the test entry
        Page<Entry> page = new PageImpl<>(List.of(testEntry));
        when(entryRepository.findByUser(eq(testUser), any(Pageable.class))).thenReturn(page);

        String view = entryController.getEntries(null, 0, 5, model, auth);
        assertEquals("entries", view);
        verify(model).addAttribute(eq("entries"), anyList());
        verify(model).addAttribute(eq("pageTitle"), eq("All entries"));
    }

    @Test
    public void testAddEntryReturnsCorrectView() {
        String view = entryController.addEntry(EntryType.INCOME, model, session, auth);
        assertEquals("addentry", view);
    }

    @Test
    public void testEditEntryReturnsCorrectView() {
        String view = entryController.editEntry(1L, model, auth, session);
        assertEquals("editentry", view);
    }

    @Test
    public void testSaveNewEntry() {
        String redirect = entryController.saveNewEntry(testEntry, EntryType.INCOME, auth, session);
        assertEquals("redirect:/entries?type=INCOME", redirect);
        verify(entryRepository).save(testEntry);
        verify(accountRepository).save(testAccount);
    }

    @Test
    public void testSaveEditedEntry() {
        // Change entry amount
        Entry editedEntry = new Entry();
        editedEntry.setTitle("EditedEntry");
        editedEntry.setType(EntryType.INCOME);
        editedEntry.setAmount(BigDecimal.valueOf(800));
        editedEntry.setCategory(testCategory);
        editedEntry.setAccount(testAccount);
        editedEntry.setUser(testUser);
        editedEntry.setDateTime(LocalDateTime.now());

        String redirect = entryController.saveEditedEntry(1L, editedEntry, auth, session);
        assertEquals("redirect:/entries?type=INCOME", redirect);
        assertEquals(BigDecimal.valueOf(1300), testAccount.getAmount()); // 1000 + 500(original) + 800(new)
    }

    @Test
    public void testDeleteEntry() {
        String redirect = entryController.deleteEntry(1L, EntryType.INCOME, auth);
        assertEquals("redirect:/entries?type=INCOME", redirect);
        verify(entryRepository).delete(testEntry);
        verify(accountRepository).save(testAccount);
    }
}
