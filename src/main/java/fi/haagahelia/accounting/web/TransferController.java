package fi.haagahelia.accounting.web;

import fi.haagahelia.accounting.model.Account;
import fi.haagahelia.accounting.model.Transfer;
import fi.haagahelia.accounting.repository.AccountRepository;
import fi.haagahelia.accounting.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TransferController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    @GetMapping("/transfer/{toId}")
    public String showTransferForm(@PathVariable Long toId, Model model,
                                   @RequestParam(value = "error", required = false) String error) {

        Account toAccount = accountRepository.findById(toId).orElse(null);
        if (toAccount == null) {
            return "redirect:/accounts";
        }

        List<Account> accounts = accountRepository.findAll();

        Map<Long, BigDecimal> balances = new HashMap<>();
        for (Account acc : accounts) {
            balances.put(acc.getAccount_id(), acc.getAmount());
        }

        model.addAttribute("accounts", accounts);
        model.addAttribute("toAccount", toAccount);
        model.addAttribute("balances", balances);
        model.addAttribute("error", error);

        return "transfer";
    }

    @PostMapping("/transfer")
    public String executeTransfer(@RequestParam Long fromId,
                                  @RequestParam Long toId,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam(required = false) String description) {

        Account from = accountRepository.findById(fromId).orElse(null);
        Account to = accountRepository.findById(toId).orElse(null);

        if (from == null || to == null) {
            return "redirect:/accounts";
        }

        if (fromId.equals(toId)) {
            return "redirect:/transfer/" + toId + "?error=same";
        }

        // Checking if > 0
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "redirect:/transfer/" + toId + "?error=invalid";
        }

        // Checking if enough money
        if (from.getAmount().compareTo(amount) < 0) {
            return "redirect:/transfer/" + toId + "?error=nomoney";
        }

        // Renew balances
        from.setAmount(from.getAmount().subtract(amount));
        to.setAmount(to.getAmount().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        Transfer transfer = new Transfer(from, to, amount, description);
        transferRepository.save(transfer);

        return "redirect:/transfers";
    }

    @GetMapping("/transfers")
    public String listTransfers(Model model) {
        List<Transfer> transfers = transferRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(Transfer::getDateTime).reversed())
            .toList();
        model.addAttribute("transfers", transfers);
        return "transfers";
    }
}
