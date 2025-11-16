package fi.haagahelia.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.haagahelia.accounting.model.Category;
import fi.haagahelia.accounting.model.EntryType;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByName(String name);
    List<Category> findByType(EntryType type);

}
