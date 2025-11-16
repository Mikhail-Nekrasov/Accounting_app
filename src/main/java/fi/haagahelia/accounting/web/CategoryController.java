package fi.haagahelia.accounting.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fi.haagahelia.accounting.model.Category;
import fi.haagahelia.accounting.model.Entry;
import fi.haagahelia.accounting.model.EntryType;
import fi.haagahelia.accounting.repository.CategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CategoryController {

    @Autowired 
    private CategoryRepository categoryRepository;

    @GetMapping("/categories")
    public String getCategories(@RequestParam(value = "type", required = false) EntryType type, 
                                HttpServletRequest request,
                                Model model) {

        List<Category> categories = (type != null) ? categoryRepository.findByType(type) 
                                                : categoryRepository.findAll();

        String currentUrl = request.getRequestURI() + 
            (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        model.addAttribute("categories", categories);
        model.addAttribute("type", type);
        model.addAttribute("currentUrl", currentUrl);

        return "categories";
    }

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

    @GetMapping("/deletecategory/{id}")
    public String deleteCategory(@PathVariable("id") Long id,
                                @RequestParam(value = "type", required = false) EntryType type) {
        categoryRepository.deleteById(id);

        if (type != null) {
            return "redirect:/categories?type=" + type;
        }
        return "redirect:/categories";
    }


    @PostMapping("/saveEntrySession")
    @ResponseBody
        public void saveEntrySession(@ModelAttribute Entry entry, HttpSession session) {
            session.setAttribute("currentEntryData", entry);
    }



}
