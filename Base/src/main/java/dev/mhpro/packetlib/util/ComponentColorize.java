package dev.mhpro.packetlib.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@UtilityClass
public class ComponentColorize {
    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static final char COLOR_CHAR = '\u00A7';

    public String clean(Component component) {
        return MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.newline())
                        .resolver(StandardTags.translatable())
                        .build())
                .build()
                .serializeOrNull(GlobalTranslator.render(component, Locale.US))
                .replace("\\<", "<");
    }


    public static @NotNull TextComponent toComponent(String text) {
        return LegacyComponentSerializer.builder()
                .extractUrls()
                .hexColors()
                .build()
                .deserialize(colorize(text));
    }

    private static String colorize(String text) {
        return translateAlternateColorCodes('&', text);
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

}
