package fi.haagahelia.accounting.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long account_id;
    private String name;
    private BigDecimal amount;
    private String description;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "account")
    private List<Entry> entries;

    @OneToMany(mappedBy = "fromAccount")
    private List<Transfer> fromTransfers = new ArrayList<>();

    @OneToMany(mappedBy = "toAccount")
    private List<Transfer> toTransfers = new ArrayList<>();


    public Account() {
    }

    public Account(String name, BigDecimal amount, String description) {
        this.name = name;
        this.amount = amount;
        this.description = description;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Transfer> getFromTransfers() {
        return fromTransfers;
    }

    public void setFromTransfers(List<Transfer> fromTransfers) {
        this.fromTransfers = fromTransfers;
    }

    public List<Transfer> getToTransfers() {
        return toTransfers;
    }

    public void setToTransfers(List<Transfer> toTransfers) {
        this.toTransfers = toTransfers;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
