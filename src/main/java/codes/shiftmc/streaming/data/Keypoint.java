package codes.shiftmc.streaming.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class Keypoint {
    @JsonProperty("x")
    private double x;

    @JsonProperty("y")
    private double y;

    @JsonProperty("z")
    private double z;

    @JsonProperty("score")
    private double score;

    @JsonProperty("name")
    private String name;

    // Getters and setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Vec getPosition() {
        return new Vec(x, y, z);
    }
}
