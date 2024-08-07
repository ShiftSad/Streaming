package codes.shiftmc.streaming;

import codes.shiftmc.streaming.client.BlazeNetClient;
import codes.shiftmc.streaming.client.CameraClient;
import codes.shiftmc.streaming.renderer.particle.ParticleImage;
import codes.shiftmc.streaming.socket.SocketServer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.block.Block;

public class Server {

    public static void main(String[] args) {
        var server = MinecraftServer.init();

        var instanceManager = MinecraftServer.getInstanceManager();
        var instanceContainer = instanceManager.createInstanceContainer();

        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.BLACK_CONCRETE));

        var geh = MinecraftServer.getGlobalEventHandler();
        geh.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            var player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setGameMode(GameMode.CREATIVE);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

        var socketServer = new SocketServer("0.0.0.0", 3000, new BlazeNetClient(
                instanceContainer,
                new Vec(0, 43, 0)
        ));

        server.start("0.0.0.0", 25565);
    }
}
