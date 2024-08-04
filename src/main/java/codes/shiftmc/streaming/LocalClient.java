package codes.shiftmc.streaming;


import codes.shiftmc.streaming.client.Clients;
import codes.shiftmc.streaming.renderer.Renderers;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LocalClient implements Clients {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Renderers renderer;

    public LocalClient(Renderers renderer) {
        this.renderer = renderer;
        start();
    }

    @Override
    public void start() {
        try {
            var robot = new Robot();
            var screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            var screenDevice = screenDevices[1]; // Change the index to select the desired monitor
            var bounds = screenDevice.getDefaultConfiguration().getBounds();
            var rectangle = new Rectangle(bounds);

            scheduler.scheduleAtFixedRate(() -> frame(robot, rectangle), 0, 100, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void frame(Robot robot, Rectangle rectangle) {
        var image = robot.createScreenCapture(rectangle);
        renderer.render(image);
    }
}