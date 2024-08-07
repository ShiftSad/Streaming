package codes.shiftmc.streaming.socket;

import codes.shiftmc.streaming.client.BlazeNetClient;
import codes.shiftmc.streaming.data.BlazeNetData;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class SocketServer {

    private final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);
    private final SocketIOServer server;

    public SocketServer(
            String hostname,
            int port,
            BlazeNetClient blazeNetClient
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

        server.addEventListener("move::data", String.class, (client, rawData, ackRequest) -> {
            var mapper = new ObjectMapper();
            // Detect what is the model before deserializing, can be blazepose, movenet and posenet
            var model = extract("model", rawData);
            var poses = extract("poses", rawData);

            if (model == null || poses == null) {
                LOGGER.debug("Invalid data received: {}", rawData);
                return;
            }

            switch (model.toLowerCase()) {
                case "blazepose" -> {
                    try {
                        var data = mapper.readValue(rawData, BlazeNetData.class);
                        LOGGER.debug("Received data from model: {}", data.getModel());

                        blazeNetClient.update(data);
                    } catch (Exception e) {
                        LOGGER.error("Error while deserializing data", e);
                    }
                }

                case "movenet", "posenet" -> {
                    throw new UnsupportedOperationException("Model not supported");
                }

                default -> {
                    LOGGER.debug("Model {} not supported", model);
                }
            }
        });
    }

    private String extract(String field, String json) {
        String key = "\"" + field + "\":";
        int startIndex = json.indexOf(key) + key.length();
        char nextChar = json.charAt(startIndex);

        if (nextChar == '\"') {
            startIndex++;
            int endIndex = json.indexOf("\"", startIndex);
            return json.substring(startIndex, endIndex);
        }

        if (nextChar == '[') {
            int endIndex = json.indexOf("]", startIndex) + 1;
            return json.substring(startIndex, endIndex);
        }

        return null;
    }
}