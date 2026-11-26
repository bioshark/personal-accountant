package org.roly.personalaccountant.view.rs.controller;

import org.roly.personalaccountant.domain.model.services.MonthClosedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Scoped to {@link ExpenseController} so the web controllers keep their redirect-with-flash-message handling.
 */
@RestControllerAdvice(assignableTypes = ExpenseController.class)
public class ExpenseControllerAdvice {

    @ExceptionHandler(MonthClosedException.class)
    public ResponseEntity<ProblemDetail> handleMonthClosed(MonthClosedException e) {
        return problem(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleInvalidInput(IllegalArgumentException e) {
        return problem(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private static ResponseEntity<ProblemDetail> problem(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, message));
    }
}
