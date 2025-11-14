package fi.haagahelia.accounting.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fi.haagahelia.accounting.model.Entry;
import fi.haagahelia.accounting.model.EntryType;
import fi.haagahelia.accounting.repository.AccountRepository;
import fi.haagahelia.accounting.repository.CategoryRepository;
import fi.haagahelia.accounting.repository.EntryRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class EntryController {

    @RequestMapping({"/", "index"})
    public String hello() {
        return "index";
    }

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    public CategoryRepository categoryRepository;

    @Autowired
    public AccountRepository accountRepository;

    @GetMapping("/entries")
    public String getEntries(@RequestParam(value = "type", required = false) EntryType type,
                             Model model) {
        List<Entry> entries;
        BigDecimal total = null;
        String pageTitle;

        if (type == null) {                                     // added later, not sure if I want to keep it
            entries = entryRepository.findAll();                //
            pageTitle = "All entries";                          //
            BigDecimal incomeSum = entries.stream()             //
                .filter(e -> e.getType() == EntryType.INCOME)   //
                .map(Entry::getAmount)              
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expenseSum = entries.stream()
                .filter(e -> e.getType() == EntryType.EXPENSE)
                .map(Entry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);      //
            total = incomeSum.subtract(expenseSum);             //

        } else {
            entries = entryRepository.findByType(type);
            pageTitle = (type == EntryType.EXPENSE ? "Expenses" : "Incomes");
            total = entries.stream()
                    .map(Entry::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        model.addAttribute("entries", entries);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("type", type);
        model.addAttribute("total", total);

        return "entries";
    }
    
    @RequestMapping("/deleteentry/{id}")
    public String deleteEntry(@PathVariable("id") Long id,
                              @RequestParam(value = "type", required = false) EntryType type) {
        entryRepository.deleteById(id);

        if (type != null) {
            return "redirect:/entries?type=" + type;
        }
        return "redirect:/entries";
    }

    @RequestMapping("/editentry/{id}")
    public String editEntry(@PathVariable("id") Long id, Model model,
                            @RequestParam(value = "type", required = false) EntryType type,
                            HttpServletRequest request) {
        Entry entry = entryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid entry Id:" + id));
        
        if (entry.getDateTime() == null) {
            entry.setDateTime(LocalDateTime.now());
        }
        
        model.addAttribute("entry", entry);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("accounts", accountRepository.findAll());
        model.addAttribute("type", type);

        String referer = request.getHeader("referer");
        model.addAttribute("returnUrl", referer);

        return "editentry";
    }

    @PostMapping("/editentry/{id}")
    public String saveEditedEntry(@PathVariable("id") Long id,
                                  @ModelAttribute Entry entryFromForm,
                                  @RequestParam(value = "returnUrl", required = false) String returnUrl) {
        Entry existingEntry = entryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid entry Id:" + id));

        existingEntry.setTitle(entryFromForm.getTitle());
        existingEntry.setAmount(entryFromForm.getAmount());
        existingEntry.setType(entryFromForm.getType());
        existingEntry.setDateTime(entryFromForm.getDateTime());
        existingEntry.setDescription(entryFromForm.getDescription());
        existingEntry.setCategory(entryFromForm.getCategory());
        existingEntry.setAccount(entryFromForm.getAccount());

        entryRepository.save(existingEntry);

        // if (type != null) {
        //     return "redirect:/entries?type=" + type;
        // }

        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }

        return "redirect:/entries";
    }

    @GetMapping("/addentry")
    public String addEntry(@RequestParam(value = "type", required = false) EntryType type,
                           Model model) {
        Entry entry = new Entry();

        if (type != null) {
            entry.setType(type);
        }

        entry.setDateTime(LocalDateTime.now());

        model.addAttribute("entry", entry);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("accounts", accountRepository.findAll());
        model.addAttribute("type", type);

        return "addentry";
    }

    @PostMapping("/addentry")
    public String saveNewEntry(@ModelAttribute Entry entry,
                               @RequestParam(value = "type", required = false) EntryType type) {
        entryRepository.save(entry);
        if (type != null) {
            return "redirect:/entries?type=" + type;
        }
        return "redirect:/entries";
    }

}
