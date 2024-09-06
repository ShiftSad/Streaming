package codes.shiftmc.streaming.renderer.particle;

import codes.shiftmc.streaming.renderer.Renderers;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.BundlePacket;
import net.minestom.server.network.packet.server.play.ParticlePacket;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParticleImage implements Renderers {

    private final Vec pos;
    private final Instance instance;
    private final int width;
    private final int height;
    private final float particleSize;
    private final float particleSpacing;

    public ParticleImage(
            Vec pos,
            Instance instance,
            int width,
            int height,
            float particleSize,
            float particleSpacing
    ) {
        this.pos = pos;
        this.instance = instance;
        this.width = width;
        this.height = height;
        this.particleSize = particleSize;
        this.particleSpacing = particleSpacing;
    }

    @Override
    public void render(BufferedImage image) {
        // Resize image to fit the width and height
        var resizedImage = resize(image);

        List<ParticleData> particles = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = resizedImage.getRGB(x, y);

                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                var color = new Color(red, green, blue);
                var offset = new Vec(x, 0, y).mul(particleSpacing);

                particles.add(new ParticleData(color, particleSize, offset));
            }
        }

        var packets = particles.stream()
                .map(particle -> particle.createPacket(pos))
                .toArray(ParticlePacket[]::new);

        var packetChunks = splitPackets(packets, 4000);
        for (var chunk : packetChunks) {
            var bundlePacket = new BundlePacket();
            instance.sendGroupedPacket(bundlePacket);
            for (ParticlePacket packet : chunk) {
                instance.sendGroupedPacket(packet);
            }
            instance.sendGroupedPacket(bundlePacket);
        }
    }

    private List<ParticlePacket[]> splitPackets(ParticlePacket[] packets, int maxSize) {
        List<ParticlePacket[]> packetChunks = new ArrayList<>();
        for (int i = 0; i < packets.length; i += maxSize) {
            int end = Math.min(packets.length, i + maxSize);
            packetChunks.add(Arrays.copyOfRange(packets, i, end));
        }
        return packetChunks;
    }

    private BufferedImage resize(BufferedImage image) {
        return resize(image, image.getWidth(), image.getHeight());
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        var resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var graphics = resizedImage.createGraphics();
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();
        return resizedImage;
    }
}
