package fi.haagahelia.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.haagahelia.accounting.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByName(String name);

}
