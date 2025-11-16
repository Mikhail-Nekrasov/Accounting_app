package fi.haagahelia.accounting.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import fi.haagahelia.accounting.model.Entry;
import fi.haagahelia.accounting.model.EntryType;

import java.time.LocalDateTime;


public interface EntryRepository extends JpaRepository<Entry, Long> {

    //List<Entry> findByType(EntryType type, Pageable pageable);
    Page<Entry> findByType(EntryType type, Pageable pageable);
    Page<Entry> findAll(Pageable pageable);
    List<Entry> findByCategoryName(String categoryName);
    List<Entry> findByDateTime(LocalDateTime dateTime);

    List<Entry> findByType(EntryType type);
    List<Entry> findAll();

    //List<Entry> findByType(EntryType type, Sort sort);

}
