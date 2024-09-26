package codes.shiftmc.streaming.renderer.map;

import codes.shiftmc.streaming.ImageProcessor;
import codes.shiftmc.streaming.renderer.map.model.Map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageBundleRenderer {

    private final ArrayList<BufferedImage> cachedImages = new ArrayList<>();
    private final List<MapRenderer> mapRenderers = new ArrayList<>();

    public ImageBundleRenderer(
            int width,
            int height,
            float frameRate,
            float similarity,
            boolean bundlePacket,
            boolean slowSend,
            boolean arbEncode,
            List<BufferedImage> images,
            List<MapRenderer> mapRenderers
    ) {
        if (arbEncode) cachedImages.addAll(images.stream().map(ImageProcessor::resizeAndEncodeImage).toList());
        else cachedImages.addAll(images);
        this.mapRenderers.addAll(mapRenderers);

        mapRenderers.forEach(renderer -> {
            renderer.render(cachedImages.getFirst());

        });
    }

    public void step(int number) {

    }
}
