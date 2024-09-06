package codes.shiftmc.streaming.data;

public class Connection {

    private final String from;
    private final String to;

    public Connection(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
