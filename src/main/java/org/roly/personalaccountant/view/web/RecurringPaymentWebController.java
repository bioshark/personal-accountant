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

@Controller
public class RecurringPaymentWebController {

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
        model.addAttribute("categories", categoryService.listActive());
        return "recurring";
    }

    @PostMapping("/recurring/add")
    public String addTemplate(@RequestParam String name, @RequestParam double defaultAmount,
            @RequestParam String category, @RequestParam PaymentType type) {
        recurringPaymentService.addTemplate(name, defaultAmount, category, type);
        categoryService.addIfAbsent(category);
        return "redirect:/recurring";
    }

    @PostMapping("/recurring/edit/{id}")
    public String editTemplate(@PathVariable Long id, @RequestParam String name,
            @RequestParam double defaultAmount, @RequestParam String category, @RequestParam PaymentType type) {
        recurringPaymentService.editTemplate(id, name, defaultAmount, category, type);
        categoryService.addIfAbsent(category);
        return "redirect:/recurring";
    }

    @PostMapping("/recurring/delete/{id}")
    public String deleteTemplate(@PathVariable Long id) {
        recurringPaymentService.deleteTemplate(id);
        return "redirect:/recurring";
    }
}
