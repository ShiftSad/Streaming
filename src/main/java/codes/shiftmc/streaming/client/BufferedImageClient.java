package codes.shiftmc.streaming.client;

import codes.shiftmc.streaming.renderer.Renderers;

import java.awt.image.BufferedImage;

public class BufferedImageClient implements Clients {

    private final Renderers renderer;

    public BufferedImageClient(Renderers renderer) {
        this.renderer = renderer;
    }

    @Override
    public void start() {

    }

    public void render(BufferedImage image) {
        renderer.render(image);
    }
}
