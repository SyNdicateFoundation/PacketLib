package dev.mhpro.packetlib.data;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Base64;


@Data
public class Favicon {
    private final String encoded;

    public Favicon(BufferedImage image) {
        if (image.getWidth() > 64 || image.getHeight() > 64) {
            throw new IllegalArgumentException("Server icon must be small or equal then 64x64 pixels");
        }

        byte[] imageBytes;

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", stream);
            imageBytes = stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String encoded = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

        if (encoded.length() > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Favicon file too large for server to process");
        }

        this.encoded = encoded;
    }

    public Favicon(@NotNull URL url) throws IOException {
        this(ImageIO.read(url));
    }

    public Favicon(@NotNull String base64) {

        base64 = base64.startsWith("data;") ? base64 : "data:image/png;base64," + base64;

        if (base64.length() > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Favicon file too large for server to process");
        }
        this.encoded = base64;
    }

    public Favicon(Path path) throws IOException {
        this(ImageIO.read(path.toFile()));
    }
}
