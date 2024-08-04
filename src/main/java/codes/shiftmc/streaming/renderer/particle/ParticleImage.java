package codes.shiftmc.streaming.renderer.particle;

import codes.shiftmc.streaming.renderer.Renderers;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.utils.PacketUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
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

        for (ParticlePacket packet : packets) {
            PacketUtils.sendGroupedPacket(instance.getPlayers(), packet);
        }
    }

    private BufferedImage resize(BufferedImage image) {
        var resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var graphics = resizedImage.createGraphics();
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();
        return resizedImage;
    }
}
