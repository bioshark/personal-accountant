package org.roly.personalaccountant.view.web;

import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.services.CategoryService;
import org.roly.personalaccountant.domain.model.services.RecurringPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RecurringPaymentWebController {

    private static final String REDIRECT_RECURRING = "redirect:/recurring";
    private static final String ERROR = "error";

    private final RecurringPaymentService recurringPaymentService;
    private final CategoryService categoryService;

    @Autowired
    public RecurringPaymentWebController(RecurringPaymentService recurringPaymentService, CategoryService categoryService) {
        this.recurringPaymentService = recurringPaymentService;
        this.categoryService = categoryService;
    }

    @GetMapping("/recurring")
    public String listTemplates(Model model) {
        model.addAttribute("templates", recurringPaymentService.getAllTemplates());
        model.addAttribute("templatesByType", recurringPaymentService.getTemplatesByType());
        model.addAttribute("templateTypes", recurringPaymentService.getTemplateTypes());
        model.addAttribute("categories", categoryService.listActive());
        return "recurring";
    }

    @PostMapping("/recurring/add")
    public String addTemplate(@RequestParam String name, @RequestParam double defaultAmount,
            @RequestParam String category, @RequestParam PaymentType type, RedirectAttributes redirectAttributes) {
        try {
            recurringPaymentService.addTemplate(name, defaultAmount, category, type);
            categoryService.addIfAbsent(category);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
        }
        return REDIRECT_RECURRING;
    }

    @PostMapping("/recurring/edit/{id}")
    public String editTemplate(@PathVariable Long id, @RequestParam String name, @RequestParam double defaultAmount,
            @RequestParam String category, @RequestParam PaymentType type, RedirectAttributes redirectAttributes) {
        try {
            recurringPaymentService.editTemplate(id, name, defaultAmount, category, type);
            categoryService.addIfAbsent(category);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
        }
        return REDIRECT_RECURRING;
    }

    @PostMapping("/recurring/delete/{id}")
    public String deleteTemplate(@PathVariable Long id) {
        recurringPaymentService.deleteTemplate(id);
        return REDIRECT_RECURRING;
    }
}
