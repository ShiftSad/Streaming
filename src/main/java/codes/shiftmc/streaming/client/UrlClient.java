package codes.shiftmc.streaming.client;

import codes.shiftmc.streaming.renderer.Renderers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class UrlClient implements Clients {

    private static final Predicate<String> URL_TEST = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]").asPredicate();
    private Renderers renderer;

    public UrlClient(Renderers renderer) {
        this.renderer = renderer;
    }

    @Override
    public void start() {

    }

    public void render(String url) throws IOException {
        if (!URL_TEST.test(url)) {
            throw new RuntimeException("Invalid URL: " + url);
        }

        BufferedImage image = ImageIO.read(URI.create(url).toURL());
        renderer.render(image);
    }
}
