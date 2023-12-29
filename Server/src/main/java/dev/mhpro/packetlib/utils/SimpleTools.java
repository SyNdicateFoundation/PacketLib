package dev.mhpro.packetlib.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class SimpleTools {

    private final Gson gson = new GsonBuilder().serializeNulls().create();
    public String colorize(Component component) {
        String text = MiniMessage.miniMessage().serializeOrNull(component);

        for (ConsoleColors value : ConsoleColors.values()) {
            text = text.replaceAll(String.format("<%s>", value.name().toLowerCase()), value.toString());
        }

        text = text.replaceAll("<click:open_url:[\"'](\\S+)[\"']>(?:.*</click)?", "");
        text = text.replaceAll("</[\\w_]+>", ConsoleColors.RESET.toString());
        text = text.replace("\\<", "<");

        return text + ConsoleColors.RESET;
    }

    @RequiredArgsConstructor
    public enum ConsoleColors {
        AQUA('b', 36, ConsoleColors.LIGHT),
        BLACK('0', 30, ConsoleColors.DARK),
        BLUE('9', 34, ConsoleColors.LIGHT),
        BOLD('l', 1, ConsoleColors.DARK),
        DARK_AQUA('3', 36, ConsoleColors.DARK),
        DARK_BLUE('1', 34, ConsoleColors.DARK),
        DARK_GREEN('2', 32, ConsoleColors.DARK),
        DARK_GRAY('8', 30, ConsoleColors.LIGHT),
        DARK_PURPLE('5', 35, ConsoleColors.DARK),
        DARK_RED('4', 31, ConsoleColors.DARK),
        GOLD('6', 33, ConsoleColors.DARK),
        GRAY('7', 37, ConsoleColors.DARK),
        GREEN('a', 32, ConsoleColors.LIGHT),
        ITALIC('o', 3, ConsoleColors.DARK),
        PURPLE('d', 35, ConsoleColors.LIGHT),
        RED('c', 31, ConsoleColors.LIGHT),
        RESET('r', 0, ConsoleColors.DARK),
        STRIKETHROUGH('m', 9, ConsoleColors.DARK),
        UNDERLINE('n', 4, ConsoleColors.DARK),
        WHITE('f', 37, ConsoleColors.LIGHT),
        YELLOW('e', 33, ConsoleColors.LIGHT);


        private static final String LIGHT = "\u001b[0;%d;1m";
        private static final String DARK = "\u001b[0;%d;22m";
        private final char bukkit;
        private final int ansi;
        private final String pattern;
        private String result;

        public static @NotNull ConsoleColors get(char code) {
            for (ConsoleColors color : values()) {
                if (color.bukkit != code) continue;
                return color;
            }
            throw new IllegalArgumentException("Color with code " + code + " is not exists");
        }

        @Override
        public String toString() {
            if (result == null) {
                result = String.format(this.pattern, this.ansi);
            }
            return result;
        }
    }

}
