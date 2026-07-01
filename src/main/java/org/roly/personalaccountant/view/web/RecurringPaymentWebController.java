package org.roly.personalaccountant.view.web;

import org.roly.personalaccountant.domain.model.dto.Payment.Category;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.services.RecurringPaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecurringPaymentWebController {

    private final RecurringPaymentService recurringPaymentService;

    public RecurringPaymentWebController(RecurringPaymentService recurringPaymentService) {
        this.recurringPaymentService = recurringPaymentService;
    }

    @GetMapping("/recurring")
    public String listTemplates(Model model) {
        model.addAttribute("templates", recurringPaymentService.getAllTemplates());
        return "recurring";
    }

    @PostMapping("/recurring/add")
    public String addTemplate(@RequestParam String name, @RequestParam double defaultAmount,
            @RequestParam Category category, @RequestParam PaymentType type) {
        recurringPaymentService.addTemplate(name, defaultAmount, category, type);
        return "redirect:/recurring";
    }

    @PostMapping("/recurring/edit/{id}")
    public String editTemplate(@PathVariable Long id, @RequestParam String name,
            @RequestParam double defaultAmount, @RequestParam Category category, @RequestParam PaymentType type) {
        recurringPaymentService.editTemplate(id, name, defaultAmount, category, type);
        return "redirect:/recurring";
    }

    @PostMapping("/recurring/delete/{id}")
    public String deleteTemplate(@PathVariable Long id) {
        recurringPaymentService.deleteTemplate(id);
        return "redirect:/recurring";
    }
}
