package com.lindar.challenges.jsaliba;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

        // Should technically always be 36 ...
        int remainingNumbers = 90 - tickets.stream().mapToInt(Ticket::getTotalNumbers).reduce(0, Integer::sum);

        for (int i = remainingNumbers - 1; i >= 0; i--) {
            int nextNumberGroup = i % 9;
            LinkedList<Integer> numbersInGroup = numberPool.get(nextNumberGroup);

            // Quick workaround for unevenly-sized columns
            if (numbersInGroup.isEmpty()) {
                final Map.Entry<Integer, LinkedList<Integer>> nonEmptyColumn = numberPool.entrySet().stream().filter(grp -> !grp.getValue().isEmpty()).findFirst().get();
                nextNumberGroup = nonEmptyColumn.getKey();
                numbersInGroup = nonEmptyColumn.getValue();
            }

            final int finalNextNumberGroup = nextNumberGroup;
            final Optional<Ticket> maybeTicket = tickets.stream().filter(ticket -> !ticket.isComplete() && !ticket.isColumnFull(finalNextNumberGroup)).findFirst();

            final Ticket ticket = maybeTicket.orElseThrow(() -> new RuntimeException("This shouldn't happen ... "));

            ticket.insertNumber(nextNumberGroup, numbersInGroup.removeFirst());
        }

        for (Ticket ticket : tickets) {
            addEmptySpaces(ticket);
        }
    }

    public void addEmptySpaces(Ticket ticket) {
        // Something to keep track where the last column started placing numbers.
        // If column 0 started at row 0, column 1 will start at row 1.
        // If column 0 started at row 2, column 1 will start at row 0.
        // The positionStartSeed is randomised to introduce a bit of indeterminism,
        // since the first column impacts the number positioning of subsequent columns.
        //
        final AtomicInteger positionStartSeed = new AtomicInteger(random.nextInt(3));

        ticket
          .getColumns()
          .forEach( (columnIdx, columnNumbers) -> {
              if (columnNumbers.size() == 3) {
                  // No blank spaces to add ... just sort the column and continue with the next.
                  columnNumbers.sort(Integer::compareTo);
                  return;
              }

              final int previousStartingPosition = positionStartSeed.get() % 3;
              final int nextStartingPosition = positionStartSeed.incrementAndGet() % 3;

              // If there's only one number in the column, add blank spaces
              // depending on where the previous column placed its last number.
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

              // If there are two numbers in the column, swap the numbers
              // if the first is larger than the second. Add a blank space
              // depending on where the previous column placed its last number
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

                  // Remember to call incrementAndGet() a second time, otherwise
                  // the subsequent column will place its first number alongside
                  // the second number of this column.
                  positionStartSeed.incrementAndGet();
              }
          });
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
