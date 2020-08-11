package com.xdream.wisdom.util;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public abstract class Assertion {

    //---------------------------------------------------------------------
    // Null/Empty checks
    //---------------------------------------------------------------------

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void notEmpty(String string, String message) {
        if (StringUtils.isEmpty(string)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(String string) {
        notNull(string, "[Assertion failed] - this argument is required; it must not be empty");
    }


    //---------------------------------------------------------------------
    // Serializations
    //---------------------------------------------------------------------

    public static void serializable(Object object, String message) {
        if(!(object instanceof Serializable)){
            throw new IllegalArgumentException(message);
        }
    }

    public static void serializable(Object object) {
        serializable(object, "[Assertion failed] - this argument should implements interface Serializable");
    }
}
