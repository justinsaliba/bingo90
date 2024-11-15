package com.lindar.challenges.jsaliba.beans;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lindar.challenges.jsaliba.beans.TicketStrip.BLANK;

public class Ticket {

    public static final int MAX_NUMBERS = 15;
    public static final int ROWS = 3;
    public static final int COLUMNS = 9;

    private final int ticketNumber;
    private final Map<Integer, LinkedList<Integer>> columns = IntStream
                                                            .range(0, COLUMNS)
                                                            .boxed()
                                                            .collect(Collectors.toMap(idx -> idx, idx -> new LinkedList<>() ));

    private int totalNumbers = 0;

    public Ticket(int ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Map<Integer, LinkedList<Integer>> getColumns() {
        return columns;
    }

    public Map<Integer, LinkedList<Integer>> getRows() {
        final Map<Integer, LinkedList<Integer>> rows = new HashMap<>();
        for (int i = 0; i < ROWS; i++) {
            LinkedList<Integer> row = new LinkedList<>();
            for (int j = 0; j < COLUMNS; j++) {
                row.add(columns.get(j).get(i));
            }
            rows.put(i, row);
        }
        return rows;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public LinkedList<Integer> getColumnNumbers(int columnNumber) {
        return columns.get(columnNumber);
    }

    public boolean isColumnFull(int columnNumber) {
        int columnSize = this.getColumnNumbers(columnNumber).size();
        return columnSize == 3;
    }

    public void insertNumber(int columnNumber, int number) {
        columns.get(columnNumber).add(number);
        totalNumbers++;
    }

    public boolean isComplete() {
        return totalNumbers == MAX_NUMBERS;
    }

    public int getTotalNumbers() {
        return totalNumbers;
    }

    public String toString() {

        int charactersInALine = 37; // 2 chars per number + 2 whitespaces between each ...

        final StringBuilder printedTicket  = new StringBuilder();
        printedTicket
          .append(" ")
          .append("-".repeat(charactersInALine))
          .append(" ")
          .append(System.lineSeparator());

        for (int i = 0; i < ROWS; i++) {
            printedTicket.append("| ");
            for (int j = 0; j < COLUMNS; j++) {
                int number = columns.get(j).get(i);
                printedTicket.append(String.format("%2s  ", number != BLANK ? number : "--"));
            }

            printedTicket.append("| ");
            printedTicket.append(System.lineSeparator());
        }

        printedTicket
          .append(" ")
          .append("-".repeat(charactersInALine))
          .append(" ");

        printedTicket.append(System.lineSeparator());

        return printedTicket.toString();
    }

}
