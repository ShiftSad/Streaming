package codes.shiftmc.streaming.client;

import codes.shiftmc.streaming.ParticleLib;
import codes.shiftmc.streaming.data.BlazeNetData;
import codes.shiftmc.streaming.data.Pose;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BlazeNetClient implements Clients {

    private final Instance instance;
    private final Vec start;

    private final HashMap<String, List<Pose>> poses = new HashMap<>();

    public BlazeNetClient(Instance instance, Vec start) {
        this.instance = instance;
        this.start = start;
    }

    @Override
    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::render, 50, 50, TimeUnit.MILLISECONDS);
    }

    public void update(BlazeNetData data) {
        poses.put(data.getPlayerName(), data.getPoses());
    }

    public void render() {
        poses.forEach((playerName, poses) -> {
            ParticleLib.drawKeypoints3D(poses, instance, start);
        });
    }
}
