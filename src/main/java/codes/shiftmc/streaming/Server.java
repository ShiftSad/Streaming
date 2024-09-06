package codes.shiftmc.streaming;

import codes.shiftmc.streaming.client.BlazeNetClient;
import codes.shiftmc.streaming.client.CameraClient;
import codes.shiftmc.streaming.client.LocalClient;
import codes.shiftmc.streaming.renderer.MapRenderer;
import codes.shiftmc.streaming.renderer.particle.ParticleImage;
import codes.shiftmc.streaming.socket.SocketServer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;

import java.text.DecimalFormat;

public class Server {

    public static void main(String[] args) {
        var server = MinecraftServer.init();

        var instanceManager = MinecraftServer.getInstanceManager();
        var instanceContainer = instanceManager.createInstanceContainer();

        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 42, Block.BLACK_CONCRETE));
        instanceContainer.setChunkSupplier(LightingChunk::new);

        var geh = MinecraftServer.getGlobalEventHandler();
        geh.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            var player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setGameMode(GameMode.CREATIVE);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

        var particle = new ParticleImage(
                new Vec(0, 43, 0),
                instanceContainer,
                82,
                48,
                0.5f,
                0.15f
        );

        var map = new MapRenderer(
                new Vec(0, 48, 0),
                instanceContainer,
                1080,
                1980
        );

        var localClient = new LocalClient(
                map
        );

        // Debug show MSPT
        BossBar bossBar = BossBar.bossBar(Component.empty(), 1f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
        DecimalFormat dec = new DecimalFormat("0.00");
        MinecraftServer.getGlobalEventHandler().addListener(ServerTickMonitorEvent.class, e -> {
            double tickTime = Math.floor(e.getTickMonitor().getTickTime() * 100.0) / 100.0;
            bossBar.name(
                    Component.text()
                            .append(Component.text("MSPT: " + dec.format(tickTime)))
            );
            bossBar.progress(Math.min((float)tickTime / (float)MinecraftServer.TICK_MS, 1f));

            if (tickTime > MinecraftServer.TICK_MS)
                bossBar.color(BossBar.Color.RED);
            else bossBar.color(BossBar.Color.GREEN);

        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, e -> {
            e.getPlayer().showBossBar(bossBar);
        });

        server.start("0.0.0.0", 25565);
    }
}
