package myapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * These are the types Dolphin accepts on protocol level
 *
 */
public enum ValueType {
    STRING(""),
    FLOAT(0.0f),
    DOUBLE(0.0),
    INT(0),
    LONG(0L),
    BOOLEAN(true),
    DATE(new Date());

    private final Object initialValue;

     ValueType(Object initialValue) {
        this.initialValue = initialValue;
    }



    public Object getInitialValue() {
        return initialValue;
    }
}
