package com.lindar.challenges.jsaliba.unit;

import com.lindar.challenges.jsaliba.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TicketTests {

    Ticket ticket;

    @BeforeEach
    public void setUp() {
        ticket = new Ticket(1);
    }

    @Test
    public void testTicketNumber() {
        // very smart ...
        assertThat(ticket.getTicketNumber(), is(1));
    }

    @Test
    public void testTicketIsInitialisedWith9EmptyColumns() {
        assertThat(ticket.getColumns(), aMapWithSize(9));
        assertThat(ticket.getColumns().values(), everyItem(empty()));
    }

    @Test
    public void testInsertedNumbersArePlacedInTheAppropriateColumn() {
        ticket.insertNumber(1, 1);
        ticket.insertNumber(2, 2);
        ticket.insertNumber(3, 3);

        assertThat(ticket.getColumnNumbers(1), contains(1));
        assertThat(ticket.getColumnNumbers(2), contains(2));
        assertThat(ticket.getColumnNumbers(3), contains(3));
    }

    @Test
    public void testTicketColumnIsFullWhenItHasThreeNumbers() {
        ticket.insertNumber(1, 1);
        assertThat(ticket.isColumnFull(1), is(false));

        ticket.insertNumber(1, 2);
        assertThat(ticket.isColumnFull(1), is(false));

        ticket.insertNumber(1, 3);
        assertThat(ticket.isColumnFull(1), is(true));
    }

}
