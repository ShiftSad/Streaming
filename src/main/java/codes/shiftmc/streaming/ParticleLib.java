package codes.shiftmc.streaming;

import codes.shiftmc.streaming.data.Connections;
import codes.shiftmc.streaming.data.Keypoint;
import codes.shiftmc.streaming.data.Pose;
import codes.shiftmc.streaming.renderer.particle.ParticleData;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ParticleLib {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticleLib.class);

    public static void drawLine(Vec start, Vec end, ParticleData data, Instance instance) {
        Vec direction = start.sub(end);
        for (double i = 0; i < start.distance(end); i += 0.1) {
            Vec pos = start.add(direction.normalize().mul(i));

            instance.sendGroupedPacket(new ParticlePacket(
                    Particle.DUST
                            .withColor(data.color())
                            .withScale(0.25f),
                    pos,
                    Vec.ZERO,
                    0f,
                    0
            ));
        }
    }

    public static void drawKeypoints3D(List<Pose> poses, Instance instance, Vec offset) {
        for (Pose pose : poses) {
            var keypoints3D = pose.getKeypoints3D();
            var connections = new Connections().getConnections();

            connections.forEach(connection -> {
//                var from = keypoints3D.stream().filter(keypoint -> keypoint.getName().equals(connection.getFrom())).findFirst();
//                var to = keypoints3D.stream().filter(keypoint -> keypoint.getName().equals(connection.getTo())).findFirst();
//
//                if (from.isEmpty() || to.isEmpty()) {
//                    LOGGER.error("Connection not found: {} -> {}", connection.getFrom(), connection.getTo());
//                    return;
//                }
//
//                Vec fromPos = from.get().getPosition().mul(-1).add(offset);
//                Vec toPos = to.get().getPosition().mul(-1).add(offset);
//
//                drawLine(fromPos, toPos, new ParticleData(new Color(0, 255, 255), 0.25f, Vec.ZERO), instance);

                var names = keypoints3D.stream().map(Keypoint::getName).toList();
                System.out.println(names);
            });

            keypoints3D.forEach(keypoint -> {
                Vec pos = keypoint.getPosition().mul(-1).add(offset);
                spawnParticle(pos, new Color(0, 255, 255), instance);
            });
        }
    }

    private static void spawnParticle(Vec pos, Color color, Instance instance) {
        instance.sendGroupedPacket(new ParticlePacket(
                Particle.DUST
                        .withColor(color)
                        .withScale(0.4f),
                pos,
                Vec.ZERO,
                0f,
                0
        ));
    }
}
