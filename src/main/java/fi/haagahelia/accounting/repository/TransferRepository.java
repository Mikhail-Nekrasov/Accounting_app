package fi.haagahelia.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fi.haagahelia.accounting.model.Transfer;
import fi.haagahelia.accounting.model.User;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;


public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByDateTime(LocalDateTime dateTime);

}
