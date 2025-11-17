package fi.haagahelia.accounting.web;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import fi.haagahelia.accounting.model.Account;
import fi.haagahelia.accounting.model.Entry;
import fi.haagahelia.accounting.model.EntryType;
import fi.haagahelia.accounting.repository.AccountRepository;

@Controller
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/accounts")
    public String accounts(@RequestParam(value = "type", required = false) String type, Model model) {
        List<Account> accounts = accountRepository.findAll();

        Map<Long, BigDecimal> accountSpent = new HashMap<>();

        for (Account acc : accounts) {
            BigDecimal spent = BigDecimal.ZERO;

            if (acc.getEntries() != null) {
                for (Entry e : acc.getEntries()) {
                    if (e.getType() == EntryType.EXPENSE && e.getAmount() != null) {
                        spent = spent.add(e.getAmount());
                    }
                }
            }

            accountSpent.put(acc.getAccount_id(), spent);
        }

        // Total
        BigDecimal total = accounts.stream()
            .map(a -> a.getAmount() != null ? a.getAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("accounts", accounts);
        model.addAttribute("accountSpent", accountSpent);
        model.addAttribute("total", total);
        model.addAttribute("type", type);

        return "accounts";
    }

    @GetMapping("/addaccount")
    public String showAddAccountForm(Model model) {
        model.addAttribute("account", new Account());
        return "addaccount";
    }

    @PostMapping("/addaccount")
    public String saveAccount(@ModelAttribute Account account) {
        if (account.getAmount() == null) { 
            account.setAmount(BigDecimal.ZERO);
        }
        accountRepository.save(account);
        return "redirect:/accounts";
    }

    @GetMapping("/deleteaccount/{id}")
    public String deleteAccount(@PathVariable("id") Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            boolean hasEntries = account.getEntries() != null && !account.getEntries().isEmpty();
            boolean hasTransfers = !account.getFromTransfers().isEmpty() || !account.getToTransfers().isEmpty();

            if (!hasEntries && !hasTransfers) {
                accountRepository.deleteById(id);
            }
        }
        return "redirect:/accounts";
    }
}

