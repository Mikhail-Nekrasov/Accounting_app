package fi.haagahelia.accounting.web;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import fi.haagahelia.accounting.model.Account;
import fi.haagahelia.accounting.model.Category;
import fi.haagahelia.accounting.model.Entry;
import fi.haagahelia.accounting.model.EntryType;
import fi.haagahelia.accounting.repository.AccountRepository;
import fi.haagahelia.accounting.repository.CategoryRepository;
import fi.haagahelia.accounting.repository.EntryRepository;

@Component
public class DataInitializer implements CommandLineRunner{

    private final EntryRepository entryRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    public DataInitializer(EntryRepository entryRepository,
                           CategoryRepository categoryRepository,
                           AccountRepository accountRepository) {
        this.entryRepository = entryRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Expense categories
        Category groceriesCategory = new Category("Groceries", EntryType.EXPENSE);
        Category rentCategory = new Category("Rent", EntryType.EXPENSE);
        Category transportCategory = new Category("Transport", EntryType.EXPENSE);
        Category clothesCategory = new Category("Clothes", EntryType.EXPENSE);
        Category entertainmentCategory = new Category("Entertainment", EntryType.EXPENSE);
        Category healthCategory = new Category("Health", EntryType.EXPENSE);
        Category taxCategory = new Category("Tax", EntryType.EXPENSE);
        Category homeCategory = new Category("Home", EntryType.EXPENSE);
        Category mobileComAndInternetCategory = new Category("Mobile Communication and Internet", EntryType.EXPENSE);
        Category bankServicesCategory = new Category("Bank Services", EntryType.EXPENSE);
        Category subscriptionsAndServicesCategory = new Category("Subscriptions and Services", EntryType.EXPENSE);
        Category giftsCategory = new Category("Gifts", EntryType.EXPENSE);
        Category educationCategory = new Category("Education", EntryType.EXPENSE);
        Category otherExpencesCategory = new Category("Other Expenses", EntryType.EXPENSE);

        categoryRepository.save(groceriesCategory);
        categoryRepository.save(rentCategory);
        categoryRepository.save(transportCategory);
        categoryRepository.save(clothesCategory);
        categoryRepository.save(entertainmentCategory);
        categoryRepository.save(healthCategory);
        categoryRepository.save(taxCategory);
        categoryRepository.save(homeCategory);
        categoryRepository.save(mobileComAndInternetCategory);
        categoryRepository.save(bankServicesCategory);
        categoryRepository.save(subscriptionsAndServicesCategory);
        categoryRepository.save(giftsCategory);
        categoryRepository.save(educationCategory);
        categoryRepository.save(otherExpencesCategory);

        // Income categories
        Category salaryCategory = new Category("Salary", EntryType.INCOME);
        Category interestCategory = new Category("Interest", EntryType.INCOME);
        Category giftsIncomeCategory = new Category("Gifts Income", EntryType.INCOME);
        Category rentIncomCategory = new Category("Rent Income", EntryType.INCOME);
        Category otherIncomeCategory = new Category("Other Income", EntryType.INCOME);  

        categoryRepository.save(salaryCategory);
        categoryRepository.save(interestCategory);
        categoryRepository.save(giftsIncomeCategory);
        categoryRepository.save(rentIncomCategory);
        categoryRepository.save(otherIncomeCategory);

        // Accounts
        Account cashAccount = new Account("Cash", BigDecimal.ZERO, "Physical cash on hand");
        Account bankOPAccount = new Account("Bank OP", BigDecimal.ZERO, "Main bank account at OP");
        Account bankSPankkiAccount = new Account("Bank S-Pankki", BigDecimal.ZERO, "Secondary bank account at S-Pankki");

        accountRepository.save(cashAccount);
        accountRepository.save(bankOPAccount);
        accountRepository.save(bankSPankkiAccount);

        // Entries
        Entry entry1 = new Entry("Grocery Shopping", EntryType.EXPENSE, 
                new BigDecimal("75.50"), 
                java.time.LocalDateTime.now().minusDays(2), 
                "Weekly groceries at the K market", 
                groceriesCategory,
                cashAccount);
        
        Entry entry2 = new Entry("Monthly Rent", EntryType.EXPENSE, 
                new BigDecimal("950.00"), 
                java.time.LocalDateTime.now().minusDays(5), 
                "Rent for the apartment", 
                rentCategory,
                bankOPAccount);

        Entry entry3 = new Entry("Salary", EntryType.INCOME, 
                new BigDecimal("2500.00"), 
                java.time.LocalDateTime.now().minusDays(10), 
                "Monthly salary for October", 
                salaryCategory,
                bankOPAccount);

        Entry entry4 = new Entry("Publick transport ticket", EntryType.EXPENSE, 
                new BigDecimal("64.60"), 
                java.time.LocalDateTime.now().minusDays(15), 
                "Season ticket, ABC zones, 30 days for a student", 
                transportCategory,
                cashAccount);

        Entry entry5 = new Entry("Furniture", EntryType.EXPENSE, 
                new BigDecimal("470.00"), 
                java.time.LocalDateTime.now().minusDays(20), 
                "Table, chairs and cutlery for kitchen from IKEA", 
                homeCategory,
                bankSPankkiAccount);

        Entry entry6 = new Entry("Jacket", EntryType.EXPENSE, 
                new BigDecimal("120.00"), 
                java.time.LocalDateTime.now().minusDays(3), 
                "Winter jacket from outdoor store", 
                clothesCategory,
                cashAccount);

        Entry entry7 = new Entry("Birthday Gift", EntryType.EXPENSE, 
                new BigDecimal("25.00"), 
                java.time.LocalDateTime.now().minusDays(15), 
                "Earphones as a birthday present", 
                giftsCategory,
                bankSPankkiAccount);

        // Addition due to chanhing logic (Every Entry changes amount on the according Account)
        Entry[] entries = {entry1, entry2, entry3, entry4, entry5, entry6, entry7};

        for (Entry e : entries) {
            Account acc = e.getAccount();
            BigDecimal currentAmount = acc.getAmount() != null ? acc.getAmount() : BigDecimal.ZERO;

            if (e.getType() == EntryType.INCOME) {
                acc.setAmount(currentAmount.add(e.getAmount()));
            } else if (e.getType() == EntryType.EXPENSE) {
                acc.setAmount(currentAmount.subtract(e.getAmount()));
            }

            accountRepository.save(acc);
            entryRepository.save(e);
        }

        // entryRepository.save(entry1);
        // entryRepository.save(entry2);
        // entryRepository.save(entry3);
        // entryRepository.save(entry4);
        // entryRepository.save(entry5);
        // entryRepository.save(entry6);
        // entryRepository.save(entry7);

    }

}
