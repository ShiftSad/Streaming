package codes.shiftmc.streaming.client;

import codes.shiftmc.streaming.renderer.Renderers;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;

import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;
import static org.bytedeco.opencv.global.opencv_core.cvFlip;

public class CameraClient implements Clients {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Renderers renderer;

    public CameraClient(Renderers renderer) {
        this.renderer = renderer;
        start();
    }

    @Override
    public void start() {
        var grabber = new OpenCVFrameGrabber(1); // 1 for next camera
        var converter = new OpenCVFrameConverter.ToIplImage();

        try {
            grabber.start();
            scheduler.scheduleAtFixedRate(() -> frame(grabber, converter), 0, 50, MILLISECONDS);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void frame(OpenCVFrameGrabber grabber, OpenCVFrameConverter.ToIplImage converter) {
        try {
            var frame = grabber.grab();
            var image = converter.convert(frame);
            cvFlip(image, image, 1);

            BufferedImage bufferedImage = IplImageToBufferedImage(image);
            renderer.render(bufferedImage);
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage IplImageToBufferedImage(IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }
}
