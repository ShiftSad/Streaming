package codes.shiftmc.streaming.renderer.map;

import codes.shiftmc.streaming.ImageProcessor;
import codes.shiftmc.streaming.renderer.map.model.Map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageBundleRenderer {

    private final ArrayList<BufferedImage> cachedImages = new ArrayList<>();

    public ImageBundleRenderer(
            int width,
            int height,
            float frameRate,
            float similarity,
            boolean bundlePacket,
            boolean slowSend,
            boolean arbEncode,
            List<BufferedImage> images,
            List<Map> maps
    ) {
        if (arbEncode) cachedImages.addAll(images.stream().map(ImageProcessor::resizeAndEncodeImage).toList());
        else cachedImages.addAll(images);


    }

    public void step(int number) {

    }
}
