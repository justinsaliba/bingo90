package com.lindar.challenges.jsaliba.beans;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class TicketValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Ticket.class);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final Ticket ticket = (Ticket) target;

        assert ticket.getTotalNumbers() == 15;

    }
}
