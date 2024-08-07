package codes.shiftmc.streaming.client;

import codes.shiftmc.streaming.data.BlazeNetData;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;

public class BlazeNetClient implements Clients {

    private final Instance instance;
    private final Vec start;

    public BlazeNetClient(Instance instance, Vec start) {
        this.instance = instance;
        this.start = start;
    }

    @Override
    public void start() {

    }

    public void update(BlazeNetData data) {
        data.getPoses().forEach(pose -> {
            pose.getKeypoints3D().forEach(keypoint -> {
                var position = start.add(keypoint.getPosition().mul(-2));
                instance.sendGroupedPacket(new ParticlePacket(
                        Particle.BUBBLE,
                        true,
                        position,
                        Vec.ZERO,
                        0,
                        0
                ));
            });
        });
    }
}
