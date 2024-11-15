package com.lindar.challenges.jsaliba.beans;

/**
 * Utility class to help throw exceptions when certain assumptions are
 * not upheld in the algorithm.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}
