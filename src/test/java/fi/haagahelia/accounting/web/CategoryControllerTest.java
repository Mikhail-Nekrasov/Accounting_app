package fi.haagahelia.accounting.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import fi.haagahelia.accounting.model.Category;
import fi.haagahelia.accounting.model.EntryType;
import fi.haagahelia.accounting.model.User;
import fi.haagahelia.accounting.repository.CategoryRepository;
import fi.haagahelia.accounting.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

public class CategoryControllerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication auth;

    @InjectMocks
    private CategoryController categoryController;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a test user
        testUser = new User();
        testUser.setUsername("test_user");

        // Mock authentication
        when(auth.getName()).thenReturn("test_user");

        // Mock userRepository to return testUser
        when(userRepository.findByUsername("test_user")).thenReturn(testUser);

        // Mock HttpServletRequest
        when(request.getRequestURI()).thenReturn("/categories");
        when(request.getQueryString()).thenReturn(null);
    }

    @Test
    void testGetCategoriesReturnsCorrectView() {
        // Prepare some categories
        Category cat1 = new Category("Groceries", EntryType.EXPENSE, testUser);
        Category cat2 = new Category("Salary", EntryType.INCOME, testUser);

        // Mock categoryRepository
        when(categoryRepository.findByUser(testUser)).thenReturn(List.of(cat1, cat2));

        // Call the controller method
        String viewName = categoryController.getCategories(null, request, model, auth);

        // Verify that the correct view is returned
        assertEquals("categories", viewName);

        // Verify that model attributes were set
        verify(model).addAttribute(eq("categories"), any());
        verify(model).addAttribute(eq("type"), isNull());
        verify(model).addAttribute(eq("currentUrl"), eq("/categories"));
    }


    @Test
    public void testShowAddCategoryFormSetsCategoryAndReturnUrl() {
        String returnUrl = "/categories";

        String viewName = categoryController.showAddCategoryForm(returnUrl, EntryType.EXPENSE, model, auth);

        assertEquals("addcategory", viewName);
        verify(model, times(1)).addAttribute(eq("category"), any(Category.class));
        verify(model, times(1)).addAttribute("returnUrl", returnUrl);
    }

    @Test
    public void testSaveCategorySetsUserAndSaves() {
        Category category = new Category();
        category.setName("Test Category");

        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String returnUrl = "/categories";
        String result = categoryController.saveCategory(category, returnUrl, null, null, null, auth);

        assertEquals("redirect:" + returnUrl, result);
        assertEquals("test_user", category.getUser().getUsername());
        verify(categoryRepository, times(1)).save(category);
    }
}
