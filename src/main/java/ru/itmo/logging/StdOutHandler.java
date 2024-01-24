package ru.itmo.logging;

import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class StdOutHandler extends StreamHandler {
    public StdOutHandler() {
        this.setOutputStream(System.out);
        this.setFormatter(new SimpleFormatter());
    }
}
