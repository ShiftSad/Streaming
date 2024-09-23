package codes.shiftmc.streaming.renderer.map;

import codes.shiftmc.streaming.ImageProcessor;
import codes.shiftmc.streaming.renderer.Renderers;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.map.MapColors;
import net.minestom.server.map.framebuffers.DirectFramebuffer;
import net.minestom.server.network.packet.server.play.BundlePacket;
import net.minestom.server.network.packet.server.play.MapDataPacket;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.concurrent.*;

import static codes.shiftmc.streaming.renderer.particle.ParticleImage.resize;

public class MapRenderer implements Renderers {

    private final Instance instance;
    private final int width;
    private final int height;
    private boolean bundlePacket;
    private boolean slowSend;
    private float frameRate;
    private float similarity;
    private final boolean arbEncode;

    private boolean destroyed = false;

    private int amount = 1;

    private final int id = Math.toIntExact(System.currentTimeMillis() % Integer.MAX_VALUE);

    private final BufferedImage[][] lastFrameBlocks;
    private final ItemMapFrame[][] itemMapFrames;
    private final LinkedList<MapDataPacket> packets = new LinkedList<>();

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public MapRenderer(Vec pos, Instance instance, ItemFrameMeta.Orientation orientation, int width, int height, float frameRate, float similarity, boolean bundlePacket, boolean slowSend, boolean arbEncode) {
        this.instance = instance;
        this.width = width;
        this.height = height;
        this.bundlePacket = bundlePacket;
        this.slowSend = slowSend;
        this.frameRate = frameRate;
        this.similarity = similarity;
        this.arbEncode = arbEncode;

        assert width > 0 && height > 0;
        assert width % 128 == 0 && height % 128 == 0;

        int numBlocksX = width / 128;
        int numBlocksY = height / 128;
        lastFrameBlocks = new BufferedImage[numBlocksX][numBlocksY];
        itemMapFrames = new ItemMapFrame[numBlocksX][numBlocksY];

        int maxY = height / 128 - 1;
        for (int x = 0; x < numBlocksX; x++) {
            for (int y = 0; y < numBlocksY; y++) {
                // Spawn item frame
                itemMapFrames[x][y] = new ItemMapFrame(generateUniqueId(id, x, maxY - y), instance, pos.add(x, y, 0.0).asPosition(), orientation);
            }
        }

        instance.eventNode().addListener(AddEntityToInstanceEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                for (MapDataPacket packet : packets) {
                    if (packet == null) return;
                    player.sendPacket(packet);
                }
            }
        });
    }

    private long lastFrameTime = 0;

    @Override
    public void render(BufferedImage image) {
        if (destroyed) return;

        if (frameRate != -1) {
            long currentTime = System.currentTimeMillis();
            float frameDuration = 1000 / frameRate;
            if (currentTime - lastFrameTime < frameDuration) return; // Skip the frame it is too soon

            lastFrameTime = currentTime;
        }

        if (bundlePacket) {
            instance.sendGroupedPacket(new BundlePacket());
        }

        // Break image in 128
        BufferedImage resize = resize(image, width, height);

        packets.clear();
        for (int yBlock  = 0; yBlock  < height / 128; yBlock ++) {
            for (int xBlock  = 0; xBlock  < width / 128; xBlock ++) {
                BufferedImage currentBlock = resize.getSubimage(xBlock * 128, yBlock * 128, 128, 128);
                if (arbEncode) currentBlock = ImageProcessor.resizeAndEncodeImage(currentBlock);

                if (lastFrameBlocks[xBlock][yBlock] != null && isSimilar(lastFrameBlocks[xBlock][yBlock], currentBlock, similarity)) {
                    continue;
                }

                if (lastFrameBlocks[xBlock][yBlock] != null) {
                    this.lastFrameBlocks[xBlock][yBlock].flush();
                }
                lastFrameBlocks[xBlock][yBlock] = currentBlock;

                var fb = new DirectFramebuffer();
                // Loop 128x128 pixels of the image, based on the x and y locations
                for (int yy = 0; yy < 128; yy++) {
                    for (int xx = 0; xx < 128; xx++) {
                        int imageX = xBlock * 128 + xx;
                        int imageY = yBlock * 128 + yy;

                        int pixel = resize.getRGB(imageX, imageY);
                        if (arbEncode) pixel = currentBlock.getRGB(imageX % 128, imageY % 128);
                        MapColors.PreciseMapColor mappedColor = MapColors.closestColor(pixel);
                        fb.set(xx, yy, mappedColor.getIndex());
                    }
                }

                MapDataPacket mapDataPacket = fb.preparePacket(generateUniqueId(id, xBlock, yBlock));
                packets.add(mapDataPacket);
            }
        }

        if (!slowSend) {
            for (MapDataPacket packet : packets) {
                if (packet == null) return;
                instance.sendGroupedPacket(packet);
            }
            return;
        }

        executor.submit(() -> {
            var clone = new LinkedList<>(packets);
            try {
                for (int i = 0; i < clone.size(); i += amount) {
                    for (int j = 0; j < amount && (i + j) < clone.size(); j++) {
                        if (clone.get(i + j) == null) continue;
                        instance.sendGroupedPacket(clone.get(i + j));
                    }

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                // Clear any remaining references
                System.gc();
                clone.clear();
            }
        });

        if (bundlePacket) {
            instance.sendGroupedPacket(new BundlePacket());
        }
    }

    public void destroy() {
        for (int i = 0; i < itemMapFrames.length; i++) {
            for (int j = 0; j < itemMapFrames[i].length; j++) {
                itemMapFrames[i][j].remove();
                itemMapFrames[i][j] = null;
            }
        }

        for (int i = 0; i < this.lastFrameBlocks.length; i++) {
            for (int j = 0; j < this.lastFrameBlocks[i].length; j++) {
                this.lastFrameBlocks[i][j].flush();
                this.lastFrameBlocks[i][j] = null;
            }
        }

        destroyed = true;
        executor.shutdown();
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

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    public float getSimilarity() {
        return similarity;
    }

    public void setFrameRate(float frameRate) {
        this.frameRate = frameRate;
    }

    public float getFrameRate() {
        return frameRate;
    }

    public void setSlowSend(boolean slowSend) {
        this.slowSend = slowSend;
    }

    public boolean getSlowSend() {
        return slowSend;
    }

    public void setBundlePacket(boolean bundlePacket) {
        this.bundlePacket = bundlePacket;
    }

    public boolean getBundlePacket() {
        return bundlePacket;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
