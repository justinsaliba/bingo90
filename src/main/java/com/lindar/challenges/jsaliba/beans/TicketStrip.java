package com.lindar.challenges.jsaliba.beans;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TicketStrip {

    private final List<Integer> numbers = IntStream.rangeClosed(1, 90)
                .boxed()
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionShuffler::shuffle));

    private final Map<Integer, List<Integer>> numberPool;

    public static void main(String[] args) {

        for (int attempts = 0; attempts < 20; attempts++) {
            long now = System.currentTimeMillis();

            final int numStrips = 10000;
            IntStream.rangeClosed(1, numStrips).forEach(e -> {
                System.out.println("===========");
                  new TicketStrip();
              });

            long then = System.currentTimeMillis();

            System.out.printf("Took %s ms to generate %s ticket strips\n", then - now, numStrips);
        }
    }

    public TicketStrip() {

        // Pool all possible numbers, grouped by column number.
        // Take away from each group after picking a number
        // Alleviates the need to "look back" when a number is chosen
        // to see if it doesn't exist already in same column across
        // the entire strip.
        numberPool = numbers.stream().collect(Collectors.groupingBy(s -> (s) / 10));
        numberPool.get(8).addAll(numberPool.get(9));
        numberPool.remove(9);

        // First pass ... 90 numbers

        // Assign a number to every column to every one of the
        // six tickets in the ticket strip. This will ensure that
        // all tickets adhere to the requirement of having at least
        // one number in every column ... rest of the numbers will
        // be sorted out later.
        List<Ticket> tickets = new ArrayList<>();
        for (int ticketIndex = 0; ticketIndex < 6; ticketIndex++) {
            Ticket ticket = new Ticket(ticketIndex+1);
            for (int columnNumber = 0; columnNumber < 9; columnNumber++) {
                List<Integer> columnNumbers = numberPool.get(columnNumber);
                ticket.insertNumber(columnNumbers.get(0));
                columnNumbers.remove(0);
            }
            tickets.add(ticket);
        }

        // Second pass ... 36 numbers

        // Complete the first three tickets using random numbers
        // from any group as long as the ticket cannot have more than
        // 3 numbers from the same group.
        completeTicket(tickets.get(0));
        completeTicket(tickets.get(1));
        completeTicket(tickets.get(2));

        // Third pass ... 18 numbers remaining to be spread amongst 3 tickets

        // Start eating away from the larger groups for subsequent groups
        // There is a chance that the 9th group might have 5 elements on a bad day
        // So forcefully assign a number from the 9th group to the 4th ticket
        // if that is the case. Collect the remaining numbers as normal.
        completeTicket(tickets.get(3));
        completeTicket(tickets.get(4));
        completeTicket(tickets.get(5));


        for (int i = 0; i < 6; i++) {
            Ticket.PrintableTicket printableTicket = new Ticket.PrintableTicket(tickets.get(i));
            printableTicket.print();
        }
    }

    private void completeTicket(Ticket ticket) {
        if (ticket.getTicketNumber() == 4) {
            if (numberPool.get(8).size() > 4) {
                ticket.insertNumber(numberPool.get(8).get(0));
                numberPool.get(8).remove(0);
            }
        }

        if (ticket.getTicketNumber() == 5) {
            numberPool.entrySet()
                .stream()
                .filter(numbersInGroup -> numbersInGroup.getValue().size() > 2)
                .forEach(numberGroup -> {
                    Integer groupNumber = numberGroup.getKey();
                    List<Integer> numbersInGroup = numberPool.get(groupNumber);

                    while (numbersInGroup.size() > 2) {
                        ticket.insertNumber(numbersInGroup.get(0));
                        numbersInGroup.remove(0);
                    }
                });
        }

        while (!ticket.isComplete()) {
            final List<Map.Entry<Integer, List<Integer>>> applicableNumberGroups = numberPool.entrySet()
                    .stream()
                    .filter(e -> e.getValue().size() > 0 && !ticket.isColumnFull(e.getKey()))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionShuffler::shuffle));

            Map.Entry<Integer, List<Integer>> firstGroup = applicableNumberGroups.get(0);

            Integer numberToAdd = firstGroup.getValue().get(0);
            ticket.insertNumber(numberToAdd);
            firstGroup.getValue().remove(0);
        }


    }

}
