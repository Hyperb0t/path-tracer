package ru.itmo.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class LogConfig {

    //to use resources/logging.properties file path should be '/logging.proprties'
    public static void configureFromResourceFile(String resourceFilePath) throws IOException {
        InputStream logConfigInputstream = LoggingExperiments.class.getResourceAsStream(resourceFilePath);
        LogManager.getLogManager().readConfiguration(logConfigInputstream);
    }

    public static void configureToStdOut() throws IOException {
        LogManager.getLogManager().updateConfiguration( (String s) -> {
            if(s.equals("handlers")) {
                return (String s1, String s2) -> StdOutHandler.class.getName();
            }
            else {
                return (String s1, String s2) -> s2;
            }
        });
    }

    public static void configureOff() throws IOException {
        LogManager.getLogManager().updateConfiguration( (String s) -> {
            if(s.equals(".level")) {
                return (String s1, String s2) -> "OFF";
            }
            else {
                return (String s1, String s2) -> s2;
            }
        });
    }

    public static void setLoggingConfigProperty(String propertyName, String value) throws IOException {
        LogManager.getLogManager().updateConfiguration( (String s) -> {
            if(s.equals(propertyName)) {
                return (String s1, String s2) -> value;
            }
            else {
                return (String s1, String s2) -> s2;
            }
        });
    }
}
