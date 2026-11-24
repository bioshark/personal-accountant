package org.roly.personalaccountant.domain.model.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.roly.personalaccountant.domain.model.dto.Income;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.dto.OverallSumsTracker;
import org.roly.personalaccountant.domain.model.dto.Payment;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.dto.Saving;
import org.roly.personalaccountant.domain.model.entity.IncomeEntity;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.entity.PaymentEntity;
import org.roly.personalaccountant.domain.model.entity.PendingPaymentEntity;
import org.roly.personalaccountant.domain.model.entity.RecurringPaymentTemplate;
import org.roly.personalaccountant.domain.model.entity.SavingEntity;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.roly.personalaccountant.utils.PaymentsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseManager {

    private static final String EXPENSE_NOT_FOUND = "Expense not found: ";
    private static final String PENDING_PAYMENT_NOT_FOUND = "Pending payment not found: ";

    private final MonthlyExpenseRepository repository;
    private final PaymentService paymentService;
    private final IncomeService incomeService;

    @Autowired
    public ExpenseManager(MonthlyExpenseRepository repository, PaymentService paymentService, IncomeService incomeService) {
        this.repository = repository;
        this.paymentService = paymentService;
        this.incomeService = incomeService;
    }

    @Transactional
    public void addNewMonthlyExpense(String expenseName, LocalDate startDate, LocalDate endDate, List<RecurringPaymentTemplate> templates) {
        MonthlyExpenses dto = new MonthlyExpenses(expenseName, startDate, endDate);
        if (repository.findByYearAndMonth(dto.getYearMonth().getYear(), dto.getYearMonth().getMonthValue()).isPresent()) {
            throw new IllegalArgumentException("An expense for " + dto.getYearMonth() + " already exists");
        }
        MonthlyExpenseEntity entity = new MonthlyExpenseEntity(
                dto.getYearMonth().getYear(),
                dto.getYearMonth().getMonthValue(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getExpenseName()
        );
        if (thereIsAnOverlap(entity)) {
            throw new IllegalArgumentException("An expense already contains the new boundaries");
        }
        applyTemplates(entity, templates);
        repository.save(entity);
    }

    private boolean thereIsAnOverlap(MonthlyExpenseEntity entity) {
        return repository.existsOverlappingMonth(entity.getStartDate(), entity.getEndDate(), entity.getId());
    }

    @Transactional
    public void editMonth(YearMonth yearMonth, String newName, LocalDate newStartDate, LocalDate newEndDate) {
        MonthlyExpenseEntity entity = getEntity(yearMonth);
        requireOpen(entity);
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Month name must not be empty");
        }

        LocalDate effectiveEnd = PaymentsGenerator.resolveEndDate(newStartDate, newEndDate);
        checkForLoosePayments(newStartDate, entity, effectiveEnd);
        checkForLooseIncomes(newStartDate, entity, effectiveEnd);

        entity.setExpenseName(newName.trim());
        entity.setStartDate(newStartDate);
        entity.setEndDate(effectiveEnd);
        if (thereIsAnOverlap(entity)) {
            throw new IllegalArgumentException("An expense already contains the boundaries");
        }
        repository.save(entity);
    }

    private static void checkForLooseIncomes(LocalDate newStartDate, MonthlyExpenseEntity entity, LocalDate effectiveEnd) {
        for (IncomeEntity income : entity.getIncomes()) {
            if (income.getDate().isBefore(newStartDate) || income.getDate().isAfter(effectiveEnd)) {
                throw new IllegalArgumentException("Cannot change dates: income exists on " + income.getDate());
            }
        }
    }

    private static void checkForLoosePayments(LocalDate newStartDate, MonthlyExpenseEntity entity, LocalDate effectiveEnd) {
        for (PaymentEntity payment : entity.getPayments()) {
            if (payment.getDate().isBefore(newStartDate) || payment.getDate().isAfter(effectiveEnd)) {
                throw new IllegalArgumentException("Cannot change dates: payment exists on " + payment.getDate());
            }
        }
    }

    public boolean deleteMonthlyExpense(YearMonth yearMonth) {
        Optional<MonthlyExpenseEntity> monthlyExpenseEntity = repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue());

        if (monthlyExpenseEntity.isEmpty()) {
            return false;
        }
        repository.delete(monthlyExpenseEntity.get());
        return true;
    }

    public MonthlyExpenseEntity addPayment(Payment payment) {
        return paymentService.addPayment(payment);
    }

    public MonthlyExpenseEntity removePayment(Payment payment) {
        return paymentService.removePayment(payment);
    }

    public MonthlyExpenseEntity removePaymentById(Long paymentId) {
        return paymentService.removePaymentById(paymentId);
    }

    public MonthlyExpenseEntity editPayment(Long paymentId, String description, LocalDate date, double amount, PaymentType type, String category) {
        return paymentService.editPayment(paymentId, description, date, amount, type, category);
    }

    public MonthlyExpenseEntity addIncome(Income income) {
        return incomeService.addIncome(income);
    }

    public MonthlyExpenseEntity removeIncome(Income income) {
        return incomeService.removeIncome(income);
    }

    public MonthlyExpenseEntity removeIncomeById(Long incomeId) {
        return incomeService.removeIncomeById(incomeId);
    }

    public MonthlyExpenseEntity editIncome(Long incomeId, String source, LocalDate date, double value) {
        return incomeService.editIncome(incomeId, source, date, value);
    }

    public MonthlyExpenses getExpense(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(this::toDto)
                .orElse(null);
    }

    public List<PendingPaymentEntity> getPendingPayments(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(MonthlyExpenseEntity::getPendingPayments)
                .orElse(List.of());
    }

    public double getTotalPendingPayments(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(MonthlyExpenseEntity::getPendingPayments)
                .orElse(List.of()).stream()
                .mapToDouble(PendingPaymentEntity::getAmount)
                .sum();
    }

    public double getFixedBudget(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(MonthlyExpenseEntity::getFixedBudget)
                .orElse(0.0);
    }

    public double getSavingBudget(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(MonthlyExpenseEntity::getSavingBudget)
                .orElse(0.0);
    }

    public double getLeisureBudget(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(MonthlyExpenseEntity::getLeisureBudget)
                .orElse(0.0);
    }

    public MonthlyExpenseEntity toggleDayDone(LocalDate date) {
        MonthlyExpenseEntity entity = findExpenseForDate(date);
        requireOpen(entity);
        entity.toggleDayDone(date);
        return repository.save(entity);
    }

    public MonthlyExpenseEntity toggleMonthDone(YearMonth yearMonth) {
        MonthlyExpenseEntity entity = getEntity(yearMonth);
        entity.toggleMonthDone();
        return repository.save(entity);
    }

    public MonthlyExpenseEntity addSaving(YearMonth yearMonth, String name, double percentage) {
        MonthlyExpenseEntity entity = getEntity(yearMonth);
        requireOpen(entity);
        MonthlyExpenses dto = toDto(entity);
        dto.getStatistics().addSaving(new Saving(null, name, percentage));
        entity.addSaving(new SavingEntity(name, percentage));
        return repository.save(entity);
    }

    public void adjustBudget(YearMonth yearMonth, PaymentType paymentType, double newValue) {
        MonthlyExpenseEntity entity = getEntity(yearMonth);
        requireOpen(entity);
        entity.adjustBudget(paymentType, newValue);
        repository.save(entity);
    }

    /**
     * Each budget counts as the larger of planned and spent, so overspending lowers the projection; the daily part is the allocation minus the Total
     * daily difference, so done days count their actual spend and the projection converges to the actual cash left as the month fills in.
     */
    public double getProjectedSpending(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(entity -> {
                    OverallSumsTracker stats = toDto(entity).getStatistics();
                    return Math.max(entity.getFixedBudget(), stats.getFixedExpenseTotal())
                            + Math.max(entity.getLeisureBudget(), stats.getLeisureExpenseTotal())
                            + Math.max(entity.getSavingBudget(), stats.getSavingExpenseTotal())
                            + stats.getTotalDailyAllocation() - stats.getTotalDailyDiff();
                })
                .orElse(0.0);
    }

    public MonthlyExpenseEntity removeSavingById(Long savingId) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getSavings().stream().anyMatch(s -> s.getId().equals(savingId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Saving not found: " + savingId));
        requireOpen(entity);
        entity.getSavings().removeIf(s -> s.getId().equals(savingId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity editSaving(Long savingId, String name, double percentage) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getSavings().stream().anyMatch(s -> s.getId().equals(savingId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Saving not found: " + savingId));
        requireOpen(entity);
        SavingEntity saving = entity.getSavings().stream()
                .filter(s -> s.getId().equals(savingId))
                .findFirst().orElseThrow();
        saving.setName(name);
        saving.setPercentage(percentage);
        return repository.save(entity);
    }

    @Transactional
    public MonthlyExpenseEntity pullTemplatesIntoMonth(YearMonth yearMonth, List<RecurringPaymentTemplate> templates) {
        MonthlyExpenseEntity entity = getEntity(yearMonth);
        requireOpen(entity);
        applyTemplates(entity, templates);
        return repository.save(entity);
    }

    /**
     * Rejects DAILY templates before touching the month, so a bad selection never leaves some templates applied and others not.
     */
    private static void applyTemplates(MonthlyExpenseEntity entity, List<RecurringPaymentTemplate> templates) {
        templates.stream()
                .filter(template -> template.getType() == PaymentType.DAILY)
                .findFirst()
                .ifPresent(template -> {
                    throw new IllegalArgumentException("Daily recurring payment cannot be pulled into a month: " + template.getName());
                });
        for (RecurringPaymentTemplate template : templates) {
            PendingPaymentEntity pending = new PendingPaymentEntity(
                    template.getName(), template.getDefaultAmount(), template.getCategory(), template.getType());
            entity.addPendingPayment(pending);
            entity.adjustBudget(pending);
        }
    }

    public MonthlyExpenseEntity payPendingPayment(Long pendingId, LocalDate date, double amount) {
        MonthlyExpenseEntity entity = getEntityById(pendingId);
        PendingPaymentEntity pending = getPendingFromEntity(pendingId, entity);
        requireOpen(entity);
        validatePaymentDateInMonth(entity, date);
        entity.addPayment(new PaymentEntity(pending.getName(), pending.getCategory(), pending.getType(), amount, date));
        entity.getPendingPayments().removeIf(p -> p.getId().equals(pendingId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removePendingPayment(Long pendingId) {
        MonthlyExpenseEntity entity = getEntityById(pendingId);
        PendingPaymentEntity pending = getPendingFromEntity(pendingId, entity);
        requireOpen(entity);
        entity.removeFromBudget(pending);
        entity.getPendingPayments().removeIf(p -> p.getId().equals(pendingId));
        return repository.save(entity);
    }

    public Map<YearMonth, MonthlyExpenses> getExpenses() {
        Map<YearMonth, MonthlyExpenses> result = new LinkedHashMap<>();
        for (MonthlyExpenseEntity entity : repository.findAll()) {
            YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
            result.put(ym, toDto(entity));
        }
        return result;
    }

    public void clearExpenses() {
        repository.deleteAll();
    }

    private MonthlyExpenses toDto(MonthlyExpenseEntity entity) {
        MonthlyExpenses dto = getMonthlyExpenses(entity);
        dto.setDone(entity.isMonthDone());
        for (SavingEntity saving : entity.getSavings()) {
            dto.getStatistics().addSaving(new Saving(saving.getId(), saving.getName(), saving.getPercentage()));
        }
        for (LocalDate doneDay : entity.getDoneDays()) {
            var stats = dto.getStatistics().getDailyPayments().get(doneDay);
            if (stats != null) {
                stats.setDayDone(true);
            }
        }
        return dto;
    }

    @NonNull
    private static MonthlyExpenses getMonthlyExpenses(MonthlyExpenseEntity entity) {
        MonthlyExpenses dto = new MonthlyExpenses(
                entity.getExpenseName(),
                entity.getStartDate(),
                entity.getEndDate());
        for (IncomeEntity income : entity.getIncomes()) {
            dto.addIncome(new Income(income.getId(), income.getSource(), income.getDate(), income.getValue()));
        }
        for (PaymentEntity payment : entity.getPayments()) {
            dto.addPayment(new Payment(payment.getId(), payment.getDescription(), payment.getCategory(), payment.getType(), payment.getAmount(),
                    payment.getDate()));
        }
        return dto;
    }

    @NonNull
    private MonthlyExpenseEntity getEntity(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .orElseThrow(() -> new IllegalArgumentException(EXPENSE_NOT_FOUND + yearMonth));
    }

    @NonNull
    private MonthlyExpenseEntity getEntityById(Long pendingId) {
        return repository.findAll().stream()
                .filter(e -> e.getPendingPayments().stream().anyMatch(p -> p.getId().equals(pendingId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(PENDING_PAYMENT_NOT_FOUND + pendingId));
    }

    @NonNull
    private static PendingPaymentEntity getPendingFromEntity(Long pendingId, MonthlyExpenseEntity entity) {
        return entity.getPendingPayments().stream()
                .filter(p -> p.getId().equals(pendingId))
                .findFirst().orElseThrow();
    }

    private static void requireOpen(MonthlyExpenseEntity entity) {
        if (entity.isMonthDone()) {
            throw new MonthClosedException(YearMonth.of(entity.getYear(), entity.getMonth()));
        }
    }

}
