package codes.shiftmc.streaming.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BlazeNetData {
    private String model;
    private String playerName;
    private List<Pose> poses;

    @JsonCreator
    public BlazeNetData(
            @JsonProperty("model") String model,
            @JsonProperty("playerName") String playerName,
            @JsonProperty("poses") List<Pose> poses
    ) {
        this.model = model;
        this.playerName = playerName;
        this.poses = poses;
    }

    // Getters and setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public List<Pose> getPoses() { return poses; }
    public void setPoses(List<Pose> poses) { this.poses = poses; }
}