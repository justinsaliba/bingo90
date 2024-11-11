package com.lindar.challenges.jsaliba.beans;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TicketNumberPool {

    private final Map<Integer, LinkedList<Integer>> unusedNumbers;

    public TicketNumberPool() {
        final List<Integer> oneToNinety = IntStream.rangeClosed(1, 90).boxed().collect(Collectors.collectingAndThen(Collectors.toList(), CollectionShuffler::shuffle));

        // Pool all possible numbers, grouped by column number.
        // Take away from each group after picking a number
        // Alleviates the need to "look back" when a number is chosen
        // to see if it doesn't exist already in same column across
        // the entire strip.
        unusedNumbers = oneToNinety.stream()
                            .collect(Collectors.groupingBy(s -> (s) / 10, Collectors.toCollection(LinkedList::new)));

        // Quick way of reorganising 90 into the 9th group instead of the 10th group ...
        // Maybe find a cleaner way for this later.
        unusedNumbers.get(8).addAll(unusedNumbers.get(9));
        unusedNumbers.remove(9);
    }

    /**
     * Utility method that returns a number from the request group, if any.
     */
    public Integer requestNumberFromGroup(final int groupNumber) {
        return unusedNumbers.get(groupNumber).removeFirst();
    }

    /**
     * Utility method that returns the first available number, and removes it from the group
     * @return
     */
    public Integer requestNumberFromAnyGroup() {
        LinkedList<Integer> groupWithRemainingNumbers = unusedNumbers.values().stream().filter(numbersInGroup -> !numbersInGroup.isEmpty()).findFirst().orElseThrow(() -> new RuntimeException("No numbers remain in the pool."));
        return groupWithRemainingNumbers.peek();
    }


    public boolean isGroupEmpty(final int groupNumber) {
        validateGroupNumber(groupNumber);

        return unusedNumbers.get(groupNumber).isEmpty();
    }

    public void validateGroupNumber(final int groupNumber) {
        if (groupNumber > 9 || groupNumber < 1) throw new IllegalArgumentException("Group number must be between 1 and 9 (both inclusive)");
    }

}
