package fi.haagahelia.accounting.security;

import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import fi.haagahelia.accounting.model.Entry;
import fi.haagahelia.accounting.model.User;
import fi.haagahelia.accounting.repository.EntryRepository;
import fi.haagahelia.accounting.repository.UserRepository;

@Component("entrySecurity")
public class EntrySecurity {

    private final EntryRepository entryRepository;
    private final UserRepository userRepository;

    public EntrySecurity(EntryRepository entryRepository, UserRepository userRepository) {
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
    }

    public boolean isOwner(Long entryId, Authentication auth) {
        Entry entry = entryRepository.findById(entryId).orElse(null);
        if (entry == null) return false;

        User currentUser = userRepository.findByUsername(auth.getName());
        if (currentUser == null) return false;

        return entry.getUser().getId().equals(currentUser.getId());
    }
}
