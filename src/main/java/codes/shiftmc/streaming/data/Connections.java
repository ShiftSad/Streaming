package codes.shiftmc.streaming.data;

import java.util.ArrayList;

public class Connections {
    public static final ArrayList<Connection> connections = new ArrayList<>();

    public Connections() {
        connections.add(new Connection("left_shoulder", "left_hip"));
        connections.add(new Connection("right_shoulder", "right_hip"));
//        connections.add(new Connection("left_hip", "left_knee"));
//        connections.add(new Connection("left_knee", "left_ankle"));
//        connections.add(new Connection("left_ankle", "left_heel"));
//        connections.add(new Connection("left_heel", "left_foot_index"));
//        connections.add(new Connection("left_foot_index", "left_ankle"));
//        connections.add(new Connection("right_shoulder", "left_shoulder"));
//        connections.add(new Connection("right_shoulder", "right_hip"));
//        connections.add(new Connection("right_hip", "left_hip"));
//        connections.add(new Connection("right_hip", "right_knee"));
//        connections.add(new Connection("right_knee", "right_ankle"));
//        connections.add(new Connection("right_ankle", "right_heel"));
//        connections.add(new Connection("right_heel", "right_foot_index"));
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }
}
