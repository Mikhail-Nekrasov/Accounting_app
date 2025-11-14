package fi.haagahelia.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.haagahelia.accounting.model.Entry;
import fi.haagahelia.accounting.model.EntryType;

import java.time.LocalDateTime;


public interface EntryRepository extends JpaRepository<Entry, Long> {

    List<Entry> findByType(EntryType expense);
    List<Entry> findByCategoryName(String categoryName);
    List<Entry> findByDateTime(LocalDateTime dateTime);

}
