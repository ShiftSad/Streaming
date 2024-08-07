package codes.shiftmc.streaming.socket;

public class MoveData {

    private final String model;
    private final String playerName;
    private final String data;

    public MoveData(String model, String playerName, String data) {
        this.model = model;
        this.playerName = playerName;
        this.data = data;
    }
}
