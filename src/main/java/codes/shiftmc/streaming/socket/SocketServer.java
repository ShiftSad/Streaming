package codes.shiftmc.streaming.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;

import java.util.concurrent.atomic.AtomicInteger;

public class SocketServer {

    private final SocketIOServer server;

    public SocketServer(
            String hostname,
            int port
    ) {
        var start = System.currentTimeMillis();
        AtomicInteger received = new AtomicInteger();

        var config = new Configuration();
        config.setHostname(hostname);
        config.setPort(port);

        var socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);

        config.setSocketConfig(socketConfig);

        server = new SocketIOServer(config);
        server.start();

        server.addConnectListener(client -> {
            System.out.println("Client connected: " + client.getSessionId());
        });

        server.addEventListener("move::data", MoveData.class, (client, data, ackRequest) -> {
            System.out.println("Received data: " + data + " from client: " + client.getSessionId() + " received/second: " + (received.getAndIncrement() / ((System.currentTimeMillis() - start) / 1000)));
        });
    }
}