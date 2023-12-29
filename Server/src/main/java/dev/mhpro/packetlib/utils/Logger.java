package dev.mhpro.packetlib.utils;

import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

@UtilityClass
public class Logger {
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Simple-Server");

    static {
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord record) {
                SimpleTools.ConsoleColors color = SimpleTools.ConsoleColors.RESET;
                switch (record.getLevel().getName()) {
                    case "ALL":
                    case "OFF":
                        color = SimpleTools.ConsoleColors.GRAY;
                        break;

                    case "CONFIG":
                        color = SimpleTools.ConsoleColors.YELLOW;
                        break;

                    case "FINER":
                    case "FINE":
                        color = SimpleTools.ConsoleColors.GREEN;
                        break;

                    case "FINEST":
                        color = SimpleTools.ConsoleColors.DARK_GREEN;
                        break;

                    case "INFO":
                        color = SimpleTools.ConsoleColors.BLUE;
                        break;
                    case "SEVERE":
                        color = SimpleTools.ConsoleColors.DARK_RED;
                        break;
                    case "WARNING":
                        color = SimpleTools.ConsoleColors.GOLD;
                        break;
                }

                return String.format("%7$s[%6$s%1$tF %1$tT%7$s] %7$s[%3$s%2$s%5$s%7$s]%5$s %4$s%8$s%n",
                        new Date(record.getMillis()),
                        record.getLevel().getLocalizedName(),
                        color,
                        record.getMessage(),
                        SimpleTools.ConsoleColors.WHITE,
                        SimpleTools.ConsoleColors.AQUA,
                        SimpleTools.ConsoleColors.DARK_GRAY,
                        SimpleTools.ConsoleColors.RESET
                );
            }
        });

        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
    }

    public void error(String s, Throwable e) {
        logger.severe(s);
        e.printStackTrace();
    }

    public void error(Throwable e) {
        Logger.error(Optional.of(e.getMessage()).orElse(e.getLocalizedMessage()), e);
    }

    public void info(String s) {
        logger.info(s);
    }

    public static void error(String s) {
        logger.severe(s);
    }

    public static void fine(String s) {
        logger.fine(s);
    }

    public static void warn(String s) {
        logger.warning(s);
    }

    public static void error(Class<?> aClass, Throwable cause) {
        logger.severe("Error in class " + aClass.getName());
        cause.printStackTrace();
    }
}
