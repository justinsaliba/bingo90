package com.lindar.challenges.jsaliba.beans;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class CollectionShuffler {

    public static <T> List<T> shuffle(List<T> input) {
            Collections.shuffle(input);
            return input;
    }

}
