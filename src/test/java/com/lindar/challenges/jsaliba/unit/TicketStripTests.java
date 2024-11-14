package com.lindar.challenges.jsaliba.unit;

import com.lindar.challenges.jsaliba.TicketStrip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lindar.challenges.jsaliba.TicketStrip.BLANK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TicketStripTests {

    private static final Random STATIC_SEED = new Random(0);

    private TicketStrip ticketStrip;

    @BeforeEach
    public void setUp() {
        ticketStrip = new TicketStrip(STATIC_SEED);
    }

    @Test
    public void ticketStripHas6Tickets() {
        assertThat(ticketStrip.getTickets(), hasSize(6));
    }

    @Test
    public void ticketsMustAlwaysHave27NumbersInATicket() {
        ticketStrip.getTickets().forEach(tkt -> {
            final List<Integer> ticketNumbers = tkt.getColumns().values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            assertThat("There must always be 27 numbers in a ticket.", ticketNumbers, hasSize(27));
        });
    }

    @Test
    public void ticketsAllPossessNumbersFrom1To90SansDuplicates() {
        final List<Integer> numbersAcrossEntireTicketStrip = ticketStrip
                  .getTickets()
                  .stream()
                  .map(t -> t.getColumns().values())
                  .flatMap(Collection::stream)
                  .flatMap(Collection::stream)
                  .filter(number -> number != -1)
                  .distinct()
                  .sorted()
                  .toList();

        assertThat(numbersAcrossEntireTicketStrip, hasSize(90));
        assertThat(numbersAcrossEntireTicketStrip.get(0), is(1));
        assertThat(numbersAcrossEntireTicketStrip.get(89), is(90));
    }

    @Test
    public void ticketNumbersAreAllPlacedUnderTheirAppropriateColumn() {
        ticketStrip.getTickets().forEach(tkt -> {
            tkt.getColumns().forEach( (colIdx, numbersWithBlankSpaces) -> {
                final Stream<Integer> numbers = numbersWithBlankSpaces.stream().filter(a -> a != BLANK);

                if (colIdx == 0) {
                    // >= 1 and <= 9
                    numbers
                      .forEach(n -> assertThat(n,
                        both(
                          is(greaterThanOrEqualTo(1)))
                          .and(
                            is(lessThanOrEqualTo(9))
                          )));
                }
                else {
                    if (colIdx == 8) {
                        // >= 80 and <= 90
                        numbers
                          .forEach(n -> assertThat(n,
                          both(
                            is(greaterThanOrEqualTo(80)))
                            .and(
                              is(lessThanOrEqualTo(90))
                            )));
                    }
                    else {
                        // >= 20 and < 30
                        // >= 30 and < 40
                        // >= 40 and < 50
                        // ...
                        numbers
                          .forEach(n -> assertThat(n,
                          both(
                            is(greaterThanOrEqualTo(colIdx * 10)))
                            .and(
                              is(lessThan((colIdx  + 1)* 10))
                            )
                          ));
                    }
                }
            });
        });
    }

    @Test
    public void ticketColumnsHaveAtLeastOneNumberAndAreSortedInAscendingOrder() {
        ticketStrip
          .getTickets()
          .forEach( tkt -> {
              // Assert the columns
              final Map<Integer, LinkedList<Integer>> columns = tkt.getColumns();
              assertThat("A ticket must always have 9 columns", columns.size(), is(equalTo(9)));
              columns.forEach((colIdx, column) -> {
                  assertThat("A column should always have 3 values", column.size(), is(equalTo(3)));

                  final ArrayList<Integer> numbersInColumn = column.stream().filter(nbr -> nbr != BLANK).collect(Collectors.toCollection(ArrayList::new));
                  final List<Integer> sorted = new ArrayList<>(numbersInColumn);
                  Collections.sort(sorted);

                  assertThat("A column must always be sorted in ASC order", numbersInColumn, is(equalTo(sorted)));
                  assertThat("A column must always have at least 1 number", numbersInColumn, is(not(empty())));
              });
          });
    }

    @Test
    public void ticketRowsEachHaveFourEmptySpacesAndFiveNumbers() {
        ticketStrip
          .getTickets()
          .forEach( tkt -> {
              // Assert the rows
              final Map<Integer, LinkedList<Integer>> rows = tkt.getRows();
              assertThat("A ticket must always have 3 rows", rows.size(), is(equalTo(3)));
              rows.forEach((rowIdx, row) -> {
                  assertThat("A row must always have 4 empty spaces.", row.stream().filter(nbr -> nbr == BLANK).count(), is(equalTo(4L)));
                  assertThat("A row must always have 5 numbers.", row.stream().filter(nbr -> nbr != BLANK).count(), is(equalTo(5L)));
              });
          });
    }

}
