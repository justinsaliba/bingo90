package com.lindar.challenges.jsaliba;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CollectionShuffler {

    /**
     * A utility method to shuffle a list against a given
     * Random implementation.
     *
     * @param input The input list
     * @param random A {@link java.util.Random} to control randomness
     * @return The shuffled, input list.
     */
    public static <T> List<T> shuffle(List<T> input, Random random) {
        Collections.shuffle(input, random);
        return input;
    }

}