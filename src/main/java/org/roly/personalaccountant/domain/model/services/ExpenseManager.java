package org.roly.personalaccountant.domain.model.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.roly.personalaccountant.domain.model.dto.DailyStatistics;
import org.roly.personalaccountant.domain.model.dto.Income;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseManager {

    private static final String EXPENSE_NOT_FOUND = "Expense not found: ";
    private final MonthlyExpenseRepository repository;

    public ExpenseManager(MonthlyExpenseRepository repository) {
        this.repository = repository;
    }

    public MonthlyExpenses addNewMonthlyExpense(String expenseName, LocalDate startDate, LocalDate endDate) {
        MonthlyExpenses dto = new MonthlyExpenses(expenseName, startDate, endDate);
        if (repository.findByYearAndMonth(dto.getYearMonth().getYear(), dto.getYearMonth().getMonthValue()).isPresent()) {
            throw new IllegalArgumentException("An expense for " + dto.getYearMonth() + " already exists");
        }
        MonthlyExpenseEntity entity = new MonthlyExpenseEntity(
                dto.getYearMonth().getYear(),
                dto.getYearMonth().getMonthValue(),
                startDate,
                dto.getEndDate(),
                dto.getExpenseName()
        );
        repository.save(entity);
        return dto;
    }

    @Transactional
    public MonthlyExpenseEntity editMonthDates(YearMonth yearMonth, LocalDate newStartDate, LocalDate newEndDate) {
        MonthlyExpenseEntity entity = getEntity(yearMonth);

        LocalDate effectiveEnd = PaymentsGenerator.resolveEndDate(newStartDate, newEndDate);
        for (PaymentEntity payment : entity.getPayments()) {
            if (payment.getDate().isBefore(newStartDate) || payment.getDate().isAfter(effectiveEnd)) {
                throw new IllegalArgumentException("Cannot change dates: payment exists on " + payment.getDate());
            }
        }

        entity.setStartDate(newStartDate);
        entity.setEndDate(effectiveEnd);
        return repository.save(entity);
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
        LocalDate paymentDate = payment.date();
        MonthlyExpenseEntity entity = findExpenseForDate(paymentDate);
        entity.addPayment(new PaymentEntity(payment.description(), payment.category(), payment.type(), payment.amount(), payment.date()));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removePayment(Payment payment) {
        MonthlyExpenseEntity entity = findExpenseForDate(payment.date());
        entity.removePayment(new PaymentEntity(payment.description(), payment.category(), payment.type(), payment.amount(), payment.date()));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removePaymentById(Long paymentId) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getPayments().stream().anyMatch(i -> i.getId().equals(paymentId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        entity.getPayments().removeIf(i -> i.getId().equals(paymentId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity editPayment(Long paymentId, String description, LocalDate date, double amount, PaymentType type, String category) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getPayments().stream().anyMatch(i -> i.getId().equals(paymentId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        PaymentEntity payment = entity.getPayments().stream()
                .filter(p -> p.getId().equals(paymentId))
                .findFirst().orElseThrow();
        validatePaymentDateInMonth(entity, date);
        payment.setDescription(description);
        payment.setDate(date);
        payment.setAmount(amount);
        payment.setType(type);
        payment.setCategory(category);
        return repository.save(entity);
    }

    public MonthlyExpenseEntity addIncome(Income income) {
        MonthlyExpenseEntity entity = findExpenseForDate(income.date());
        entity.addIncome(new IncomeEntity(income.source(), income.date(), income.value()));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removeIncome(Income income) {
        MonthlyExpenseEntity entity = findExpenseForDate(income.date());
        entity.removeIncome(new IncomeEntity(income.source(), income.date(), income.value()));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removeIncomeById(Long incomeId) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getIncomes().stream().anyMatch(i -> i.getId().equals(incomeId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Income not found: " + incomeId));
        entity.getIncomes().removeIf(i -> i.getId().equals(incomeId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity editIncome(Long incomeId, String source, LocalDate date, double value) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getIncomes().stream().anyMatch(i -> i.getId().equals(incomeId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Income not found: " + incomeId));
        IncomeEntity income = entity.getIncomes().stream()
                .filter(i -> i.getId().equals(incomeId))
                .findFirst().orElseThrow();
        income.setSource(source);
        income.setDate(date);
        income.setValue(value);
        return repository.save(entity);
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
        entity.toggleDayDone(date);
        return repository.save(entity);
    }

    public MonthlyExpenseEntity addSaving(YearMonth yearMonth, String name, double percentage) {
        MonthlyExpenseEntity entity = getEntity(yearMonth);
        MonthlyExpenses dto = toDto(entity);
        dto.getStatistics().addSaving(new Saving(null, name, percentage));
        entity.addSaving(new SavingEntity(name, percentage));
        return repository.save(entity);
    }

    public void adjustBudget(YearMonth yearMonth, PaymentType paymentType, double newValue) {
        MonthlyExpenseEntity entity = getEntity(yearMonth);
        entity.adjustBudget(paymentType, newValue);
        repository.save(entity);
    }

    public double getProjectedSpending(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(entity -> entity.getFixedBudget() +
                        entity.getLeisureBudget() +
                        entity.getSavingBudget() +
                        DailyStatistics.totalAllocation(entity.getStartDate(), effectiveEndDate(entity)))
                .orElse(0.0);
    }

    public MonthlyExpenseEntity removeSavingById(Long savingId) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getSavings().stream().anyMatch(s -> s.getId().equals(savingId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Saving not found: " + savingId));
        entity.getSavings().removeIf(s -> s.getId().equals(savingId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity editSaving(Long savingId, String name, double percentage) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getSavings().stream().anyMatch(s -> s.getId().equals(savingId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Saving not found: " + savingId));
        SavingEntity saving = entity.getSavings().stream()
                .filter(s -> s.getId().equals(savingId))
                .findFirst().orElseThrow();
        saving.setName(name);
        saving.setPercentage(percentage);
        return repository.save(entity);
    }

    public MonthlyExpenseEntity pullTemplatesIntoMonth(YearMonth yearMonth, List<RecurringPaymentTemplate> templates) {
        MonthlyExpenseEntity entity = getEntity(yearMonth);
        for (RecurringPaymentTemplate template : templates) {
            PendingPaymentEntity pending = new PendingPaymentEntity(
                    template.getName(), template.getDefaultAmount(), template.getCategory(), template.getType());
            entity.addPendingPayment(pending);
            entity.adjustBudget(pending);
        }
        return repository.save(entity);
    }

    public MonthlyExpenseEntity payPendingPayment(Long pendingId, LocalDate date, double amount) {
        MonthlyExpenseEntity entity = getEntityById(pendingId);
        PendingPaymentEntity pending = getPendingFromEntity(pendingId, entity);
        validatePaymentDateInMonth(entity, date);
        entity.addPayment(new PaymentEntity(pending.getName(), pending.getCategory(), pending.getType(), amount, date));
        entity.getPendingPayments().removeIf(p -> p.getId().equals(pendingId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removePendingPayment(Long pendingId) {
        MonthlyExpenseEntity entity = getEntityById(pendingId);
        PendingPaymentEntity pending = getPendingFromEntity(pendingId, entity);
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

    private MonthlyExpenseEntity findExpenseForDate(LocalDate date) {
        return repository.findByDateInRange(date)
                .orElseThrow(() -> new IllegalArgumentException("Can't find Expense for date " + date));
    }

    private MonthlyExpenses toDto(MonthlyExpenseEntity entity) {
        MonthlyExpenses dto = getMonthlyExpenses(entity);
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
                .orElseThrow(() -> new IllegalArgumentException("Pending payment not found: " + pendingId));
    }

    @NonNull
    private static PendingPaymentEntity getPendingFromEntity(Long pendingId, MonthlyExpenseEntity entity) {
        return entity.getPendingPayments().stream()
                .filter(p -> p.getId().equals(pendingId))
                .findFirst().orElseThrow();
    }

    private static LocalDate effectiveEndDate(MonthlyExpenseEntity entity) {
        return entity.getEndDate() != null
                ? entity.getEndDate()
                : PaymentsGenerator.resolveEndDate(entity.getStartDate(), null);
    }

    private static void validatePaymentDateInMonth(MonthlyExpenseEntity entity, LocalDate date) {
        LocalDate start = entity.getStartDate();
        LocalDate end = effectiveEndDate(entity);
        if (date.isBefore(start) || date.isAfter(end)) {
            throw new IllegalArgumentException(
                    "Date " + date + " is outside the month range (" + start + " to " + end + ")");
        }
    }

}
