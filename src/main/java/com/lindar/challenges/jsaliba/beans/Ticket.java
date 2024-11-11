package com.lindar.challenges.jsaliba.beans;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Ticket {

    public static final int MAX_NUMBERS = 15;

    private int ticketNumber;
    private Map<Integer, TreeSet<Integer>> columns;
    private int totalNumbers;

    public Ticket(int ticketNumber) {
        this.ticketNumber = ticketNumber;
        columns = new HashMap<>();
        totalNumbers = 0;

        for (int i = 0; i < 9; i++) {
            columns.put(i, new TreeSet<>());
        }
    }

    public Collection<TreeSet<Integer>> getColumns() {
        return columns.values();
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public int getTotalNumbers() {
        return totalNumbers;
    }

    public Set<Integer> getColumnNumbers(int columnNumber) {
        return columns.get(columnNumber);
    }

    public boolean isColumnFull(int columnNumber) {
        int columnSize = this.getColumnNumbers(columnNumber).size();
        return columnSize == 3;
    }

    public void insertNumber(int number) {
        int columnNumber = this.findNumberPlacement(number);

        columns.get(columnNumber).add(number);
        totalNumbers++;
//        System.out.printf("[Ticket %s] Inserted %s into column %s\n", ticketNumber, number, columnNumber);
    }

    public boolean isComplete() {
        return totalNumbers == MAX_NUMBERS;
    }

    public int findNumberPlacement(int number) {
        if (number <= 9) return 0;
        else if (number >= 80) return 8;
        else return number / 10;
    }

    @Override
    public String toString() {
        return String.format("Ticket [ticketNumber=%s, count=%s, columns=%s]", ticketNumber, totalNumbers, columns);
    }

    public static final class TicketColumn {
        private int columnNumber;
        private TreeSet<Integer> values;

        public TicketColumn(int columnNumber) {
            this.columnNumber = columnNumber;
            values = new TreeSet<>();
        }

        public int getColumnNumber() {
            return columnNumber;
        }

        public TreeSet<Integer> getValues() {
            return values;
        }

        public boolean hasSpaceRemaining() {
            return values.size() < 3;
        }

        public boolean isFull() {
            return !hasSpaceRemaining();
        }
    }

    public static final class PrintableTicket {

        private final int[][] ticketGrid = new int[9][3];

        public PrintableTicket(Ticket ticket) {
            final AtomicInteger positionStartSeed = new AtomicInteger(new Random().nextInt(3));
            ticket.columns.entrySet()
              .stream()
              .forEach( e -> {

                  TreeSet<Integer> columnNumbers = e.getValue();
                  if (columnNumbers.size() == 3) {
                      ticketGrid[e.getKey()] = new int[] { columnNumbers.pollFirst(), columnNumbers.pollFirst(), columnNumbers.pollFirst() };
                      return;
                  }

                  // init the grid with blank values
                  ticketGrid[e.getKey()] = new int[] { -1, -1, -1 };

                  int previousStartingPosition = positionStartSeed.get() % 3;
                  int nextStartingPosition = positionStartSeed.incrementAndGet() % 3;

                  if (columnNumbers.size() == 1) {
                      int soleNumber = columnNumbers.first();
                      ticketGrid[e.getKey()][nextStartingPosition] = soleNumber;
                  }

                  if (columnNumbers.size() == 2) {
                      int firstNumber = columnNumbers.first();
                      int secondNumber = columnNumbers.last();

                      if (previousStartingPosition == 0)
                          ticketGrid[e.getKey()] = new int[] { -1, firstNumber, secondNumber };

                      if (previousStartingPosition == 1)
                          ticketGrid[e.getKey()] = new int[] { firstNumber, -1, secondNumber };

                      if (previousStartingPosition == 2)
                          ticketGrid[e.getKey()] = new int[] { firstNumber, secondNumber, -1 };

                      positionStartSeed.incrementAndGet();
                  }

              });
        }

        public void print() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    System.out.printf("%s\t", ticketGrid[j][i]);
                }
                System.out.println();
            }
        }
    }
}
