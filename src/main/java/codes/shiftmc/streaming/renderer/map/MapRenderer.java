package codes.shiftmc.streaming.renderer.map;

import codes.shiftmc.streaming.renderer.Renderers;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.map.MapColors;
import net.minestom.server.map.framebuffers.DirectFramebuffer;
import net.minestom.server.network.packet.server.play.BundlePacket;
import net.minestom.server.network.packet.server.play.MapDataPacket;

import java.awt.image.BufferedImage;

import static codes.shiftmc.streaming.renderer.particle.ParticleImage.resize;

public class MapRenderer implements Renderers {

    private final Instance instance;
    private final int width;
    private final int height;
    private final float frameRate;
    private final float similarity;

    private final int id = Math.toIntExact(System.currentTimeMillis() % Integer.MAX_VALUE);

    private final BufferedImage[][] lastFrameBlocks;

    public MapRenderer(Vec pos, Instance instance, int width, int height, float frameRate, float similarity) {
        this.instance = instance;
        this.width = width;
        this.height = height;
        this.frameRate = frameRate;
        this.similarity = similarity;

        assert width > 0 && height > 0;
        assert width % 128 == 0 && height % 128 == 0;

        int numBlocksX = width / 128;
        int numBlocksY = height / 128;
        lastFrameBlocks = new BufferedImage[numBlocksX][numBlocksY]; // Ensure initialization

        int maxY = height / 128 - 1;
        for (int x = 0; x < width / 128; x++) {
            for (int y = 0; y < height / 128; y++) {
                // Spawn item frame
                new ItemMapFrame(generateUniqueId(id, x, maxY - y), instance, pos.add(x, y, 0.0).asPosition());
            }
        }
    }

    private long lastFrameTime = 0;

    @Override
    public void render(BufferedImage image) {
        if (frameRate != -1) {
            long currentTime = System.currentTimeMillis();
            float frameDuration = 1000 / frameRate;
            if (currentTime - lastFrameTime < frameDuration) return; // Skip the frame it is too soon

            lastFrameTime = currentTime;
        }

        instance.sendGroupedPacket(new BundlePacket());
        // Break image in 128
        BufferedImage resize = resize(image, width, height);

        for (int yBlock  = 0; yBlock  < height / 128; yBlock ++) {
            for (int xBlock  = 0; xBlock  < width / 128; xBlock ++) {
                BufferedImage currentBlock = resize.getSubimage(xBlock * 128, yBlock * 128, 128, 128);

                if (lastFrameBlocks[xBlock][yBlock] != null && isSimilar(lastFrameBlocks[xBlock][yBlock], currentBlock, similarity)) {
                    continue;
                }

                lastFrameBlocks[xBlock][yBlock] = currentBlock;

                var fb = new DirectFramebuffer();
                // Loop 128x128 pixels of the image, based on the x and y locations
                for (int yy = 0; yy < 128; yy++) {
                    for (int xx = 0; xx < 128; xx++) {
                        int imageX = xBlock * 128 + xx;
                        int imageY = yBlock * 128 + yy;

                        int pixel = resize.getRGB(imageX, imageY);
                        MapColors.PreciseMapColor mappedColor = MapColors.closestColor(pixel);
                        fb.set(xx, yy, mappedColor.getIndex());
                    }
                }

                MapDataPacket mapDataPacket = fb.preparePacket(generateUniqueId(id, xBlock, yBlock));
                instance.sendGroupedPacket(mapDataPacket);
            }
        }
        instance.sendGroupedPacket(new BundlePacket());
    }

    private boolean isSimilar(BufferedImage img1, BufferedImage img2, float threshold) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        int width = img1.getWidth();
        int height = img1.getHeight();
        long diffCount = 0;
        long totalPixels = (long) width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);
                if (rgb1 != rgb2) {
                    diffCount++;
                }
            }
        }

        float similarity = 1.0f - (diffCount / (float)totalPixels);
        return similarity >= threshold;
    }

    private int generateUniqueId(int baseId, int x, int y) {
        // Generate a unique ID using a combination of baseId, x, and y
        return baseId + (x * 1000) + y; // 1000 is an arbitrary number large enough to differentiate x and y
    }
}
