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

    private final Vec pos;
    private final Instance instance;
    private final int width;
    private final int height;

    private final int id = Math.toIntExact(System.currentTimeMillis() % Integer.MAX_VALUE);

    public MapRenderer(Vec pos, Instance instance, int width, int height) {
        this.pos = pos;
        this.instance = instance;
        this.width = width;
        this.height = height;

        assert width > 0 && height > 0;
        assert width % 128 == 0 && height % 128 == 0;

        System.out.println("width: " + width + " " + width / 128 + " " + height + " " + height / 128);
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
        long currentTime = System.currentTimeMillis();
        int targetFps = 20;
        long frameDuration = 1000 / targetFps;
        if (currentTime - lastFrameTime < frameDuration) return; // Skip the frame it is to soon

        lastFrameTime = currentTime;

        instance.sendGroupedPacket(new BundlePacket());

        // Break image in 128
        BufferedImage resize = resize(image, width, height);

        for (int yBlock  = 0; yBlock  < height / 128; yBlock ++) {
            for (int xBlock  = 0; xBlock  < width / 128; xBlock ++) {
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

    private int generateUniqueId(int baseId, int x, int y) {
        // Generate a unique ID using a combination of baseId, x, and y
        return baseId + (x * 1000) + y; // 1000 is an arbitrary number large enough to differentiate x and y
    }
}
