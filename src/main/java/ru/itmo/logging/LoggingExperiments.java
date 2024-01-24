package ru.itmo.logging;

import com.sun.jna.Native;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;

public class LoggingExperiments {


    public static void main(String[] args) throws IOException {
//        InputStream logConfigInputstream = LoggingExperiments.class.getResourceAsStream("/logging.properties");
//        LogManager.getLogManager().readConfiguration(logConfigInputstream);
//        LogConfig.configureFromResourceFile("/logging.properties");
        LogConfig.configureToStdOut();
//        LogConfig.configureOff();

        java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(Native.class.getName());
        LOG.log(Level.parse("FINE"), "debug");
        LOG.log(Level.parse("INFO"), "info");
        LOG.log(Level.parse("WARNING"), "warn");
    }

    private static <T> T getLogger(final String loggerName, final Class<T> loggerClass) {
        final org.slf4j.Logger logger = LoggerFactory.getLogger(loggerName);
        try {
            final Class<? extends org.slf4j.Logger> loggerIntrospected = logger.getClass();
            final Field fields[] = loggerIntrospected.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                final String fieldName = fields[i].getName();
                if (fieldName.equals("logger")) {
                    fields[i].setAccessible(true);
                    return loggerClass.cast(fields[i].get(logger));
                }
            }
        } catch (final Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
