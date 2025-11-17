package fi.haagahelia.accounting.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import fi.haagahelia.accounting.model.Entry;
import fi.haagahelia.accounting.model.EntryType;
import fi.haagahelia.accounting.model.User;

import java.time.LocalDateTime;


public interface EntryRepository extends JpaRepository<Entry, Long> {

    //List<Entry> findByType(EntryType type, Pageable pageable);
    Page<Entry> findByType(EntryType type, Pageable pageable);
    Page<Entry> findAll(Pageable pageable);
    List<Entry> findByCategoryName(String categoryName);
    List<Entry> findByDateTime(LocalDateTime dateTime);

    List<Entry> findByType(EntryType type);
    List<Entry> findByUser(User user);
    List<Entry> findAll();
    Page<Entry> findByUser(User currentUser, Pageable pageable);
    Page<Entry> findByUserAndType(User currentUser, EntryType type, Pageable pageable);
    List<Entry> findByUserAndType(User currentUser, EntryType type);

    //List<Entry> findByType(EntryType type, Sort sort);

}
