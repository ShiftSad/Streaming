package codes.shiftmc.streaming.renderer.particle;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;

public record ParticleData(
        RGBLike color,
        float scale,
        Vec offset
) {
    public ParticlePacket createPacket(Vec pos) {
        return new ParticlePacket(
                Particle.DUST
                        .withColor(color)
                        .withScale(scale),
                pos.add(offset),
                Vec.ZERO,
                0f,
                0
        );
    }
}