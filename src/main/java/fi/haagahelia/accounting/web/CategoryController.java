package fi.haagahelia.accounting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fi.haagahelia.accounting.model.Category;
import fi.haagahelia.accounting.model.Entry;
import fi.haagahelia.accounting.model.EntryType;
import fi.haagahelia.accounting.repository.CategoryRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class CategoryController {

    @Autowired 
    private CategoryRepository categoryRepository;

    @GetMapping("/addcategory")
    public String showAddCategoryForm(@RequestParam(required = false) String returnUrl,
                                      @RequestParam(value = "entryType", required = false) EntryType type,
                                      Model model) {
        Category category = new Category();
        if (type != null) {
            category.setType(type);
        }
        model.addAttribute("category", category);
        model.addAttribute("returnUrl", returnUrl);
        return "addcategory";
    }

    @PostMapping("/addcategory")
    public String saveCategory(@ModelAttribute Category category,
                               @RequestParam String returnUrl,
                               HttpSession session,
                               @RequestParam(value = "entryId", required = false) Long entryId,
                               @RequestParam(value = "entryType", required = false) EntryType entryType) {
        categoryRepository.save(category);

        if (entryId != null) {
  
        Object sessionEntry = session.getAttribute("sessionEditEntry_" + entryId);
        if (sessionEntry != null) {
                session.setAttribute("sessionEditEntry_" + entryId, sessionEntry);
            }
        } else if (entryType != null) {
 
            Object sessionEntry = session.getAttribute("sessionNewEntry_" + entryType.name());
            if (sessionEntry != null) {
                session.setAttribute("sessionNewEntry_" + entryType.name(), sessionEntry);
            }
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/saveEntrySession")
    @ResponseBody
        public void saveEntrySession(@ModelAttribute Entry entry, HttpSession session) {
            session.setAttribute("currentEntryData", entry);
    }



}
