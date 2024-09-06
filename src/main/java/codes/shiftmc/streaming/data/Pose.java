package codes.shiftmc.streaming.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Pose {
    @JsonProperty("keypoints")
    private List<Keypoint> keypoints2D;

    @JsonProperty("keypoints3D")
    private List<Keypoint> keypoints3D;

    private double score;

    // Getters and setters
    public List<Keypoint> getKeypoints2D() { return keypoints2D; }
    public void setKeypoints2D(List<Keypoint> keypoints2D) { this.keypoints2D = keypoints2D; }

    public List<Keypoint> getKeypoints3D() { return keypoints3D; }
    public void setKeypoints3D(List<Keypoint> keypoints3D) { this.keypoints3D = keypoints3D; }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}