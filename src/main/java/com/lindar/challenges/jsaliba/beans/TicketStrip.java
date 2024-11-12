package com.lindar.challenges.jsaliba.beans;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.collectingAndThen;

public class TicketStrip {

    public static final int BLANK = -1;

    private final Map<Integer, LinkedList<Integer>> numberPool;
    private final List<Ticket> tickets = IntStream.rangeClosed(1, 6).mapToObj(Ticket::new).toList();

    private Random random;

    public TicketStrip() {
        // Can't use System.currentTimeMillis() because multiple invocations
        // of this same method at exactly the same time will result in
        // the same ticket ....
        this(new Random());
    }

    public TicketStrip(Random random) {
        this.random = random;

        numberPool = buildNumberPool();

        // First pass ... 90 numbers ...
        //
        // Allocate one number in each column of every ticket of the
        // ticket strip. This caters for the first requirement of
        // having non-empty columns. This uses 54 numbers (9 * 6).

        for (Ticket ticket : tickets) {
            this.populateTicket(ticket);
        }

        //
        // 36 numbers remaining to be spread amongst 6 tickets ...
        //
        // Complete the first three tickets using random numbers
        // from any group as long as the ticket cannot have more than
        // 3 numbers from the same group.
        //
        // Leaving us with ...
        //
        // 18 numbers remaining to be spread amongst 3 tickets ...
        //
        // Start eating away from the larger groups for subsequent groups
        // There is a chance that the 9th group might have 5 elements on a bad day
        // So forcefully assign a number from the 9th group to the 4th ticket
        // if that is the case. Collect the remaining numbers as normal.
        //

        for (Ticket ticket : tickets) {
            completeTicket(ticket);
            addEmptySpaces(ticket);
        }
    }

    // Assign a number to every column to every one of the
    // six tickets in the ticket strip. This will ensure that
    // all tickets adhere to the requirement of having at least
    // one number in every column ... rest of the numbers will
    // be sorted out later.
    private void populateTicket(Ticket ticket) {
        ticket.getColumns().forEach( (columnIdx, columnNumbers) -> {
            ticket.insertNumber(columnIdx, numberPool.get(columnIdx).removeFirst());
        });
    }

    private void completeTicket(Ticket ticket) {
        if (ticket.getTicketNumber() == 4) {
            if (numberPool.get(8).size() > 4) {
                ticket.insertNumber(8, numberPool.get(8).removeFirst());
            }
        }

        if (ticket.getTicketNumber() == 5) {
            numberPool.entrySet()
                .stream()
                .filter(numbersInGroup -> numbersInGroup.getValue().size() > 2)
                .forEach(numberGroup -> {
                    final Integer groupNumber = numberGroup.getKey();
                    final LinkedList<Integer> numbersInGroup = numberPool.get(groupNumber);

                    while (numbersInGroup.size() > 2) {
                        ticket.insertNumber(groupNumber, numbersInGroup.removeFirst());
                    }
                });
        }

        while (!ticket.isComplete()) {

            final List<Map.Entry<Integer, LinkedList<Integer>>> applicableNumberGroups = numberPool.entrySet()
              .stream()
              .filter(e -> !e.getValue().isEmpty() && !ticket.isColumnFull(e.getKey()))
              // Avoid using just toList() because it causes the first few tickets to eagerly consume
              // from the smaller-numbered columns. So, add a bit of randomness by shuffling the list
              // of columns applicable to the ticket, and select a random one.
              //
              // TODO: Check if there is a way to avoid looping until the ticket is complete.
              .collect(collectingAndThen(toList(), (theList) -> CollectionShuffler.shuffle(theList, random)))
              ;

            final Map.Entry<Integer, LinkedList<Integer>> firstGroup = applicableNumberGroups.get(0);
            ticket.insertNumber(firstGroup.getKey(), firstGroup.getValue().removeFirst());
        }
    }

    public void addEmptySpaces(Ticket ticket) {
        // Something to keep track where the
        final AtomicInteger positionStartSeed = new AtomicInteger(random.nextInt(3));

        ticket
          .getColumns()
          .forEach( (columnIdx, columnNumbers) -> {
              if (columnNumbers.size() == 3) {
                  columnNumbers.sort(Integer::compareTo);
                  return;
              }

              final int previousStartingPosition = positionStartSeed.get() % 3;
              final int nextStartingPosition = positionStartSeed.incrementAndGet() % 3;

              if (columnNumbers.size() == 1) {
                  if (nextStartingPosition == 0) {
                      columnNumbers.addLast(BLANK);
                      columnNumbers.addLast(BLANK);
                  }
                  else if (nextStartingPosition == 1) {
                      columnNumbers.addFirst(BLANK);
                      columnNumbers.addLast(BLANK);
                  }
                  else if (nextStartingPosition == 2) {
                      columnNumbers.addFirst(BLANK);
                      columnNumbers.addFirst(BLANK);
                  }
                  return;
              }

              if (columnNumbers.size() == 2) {
                  final int firstNumber = columnNumbers.getFirst();
                  final int secondNumber = columnNumbers.getLast();

                  if (firstNumber > secondNumber) {
                      // Swap the two around if the larger appears
                      // before the smaller.
                      columnNumbers.addLast(columnNumbers.removeFirst());
                  }

                  if (previousStartingPosition == 0) {
                      columnNumbers.addFirst(BLANK);
                  }
                  else if (previousStartingPosition == 1) {
                      columnNumbers.add(1, BLANK);
                  }
                  else if (previousStartingPosition == 2) {
                      columnNumbers.addLast(BLANK);
                  }
                  positionStartSeed.incrementAndGet();
              }
          });
    }

    public final Map<Integer, LinkedList<Integer>> buildNumberPool() {
        // Pool all possible numbers, grouped by column number.
        // Take away from each group after picking a number
        // Alleviates the need to "look back" when a number is chosen
        // to see if it doesn't exist already in same column across
        // the entire strip.
        //
        // Shuffle each column so that when numbers are taken from
        // the number pool, numbers are pre-shuffled.

        return IntStream.rangeClosed(1, 90)
          .boxed()
          .collect(collectingAndThen(toList(), (theList) -> CollectionShuffler.shuffle(theList, random)))
          .stream()
          .collect(groupingBy(num -> {
              if (num < 10) return 0;
              if (num > 89) return 8;

              return num / 10;
          }, toCollection(LinkedList::new)))
        ;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder(lineSeparator());
        for (Ticket ticket : tickets) {
            sb.append(ticket).append(lineSeparator());
        }

        return sb.toString();
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
