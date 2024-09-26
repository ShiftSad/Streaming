package codes.shiftmc.streaming.renderer.map;

import codes.shiftmc.streaming.ImageProcessor;

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
            List<BufferedImage> images
    ) {
        if (arbEncode) cachedImages.forEach(ImageProcessor::resizeAndEncodeImage);
        else cachedImages.addAll(images);


    }

    public void step(int number) {

    }
}
