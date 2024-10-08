package codes.shiftmc.streaming.client;

import codes.shiftmc.streaming.renderer.Renderers;
import org.jetbrains.annotations.NotNull;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class RTMPClient implements Clients {

    private final Renderers renderers;
    private final String rmtpUrl;

    private RenderCallbackAdapter renderCallback;
    private BufferFormatCallback bufferCallback;
    private VideoSurface videoSurface;

    private final MediaPlayerFactory mediaPlayerFactory;
    private final EmbeddedMediaPlayer mediaPlayer;
    private final int[] videoBuffer;
    private final int frameRate;

    public RTMPClient(Renderers renderers, String rmtpUrl, int frameRate) {
        this.mediaPlayerFactory = new MediaPlayerFactory("--no-audio");
        this.mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

        this.renderers = renderers;
        this.rmtpUrl = rmtpUrl;
        this.videoBuffer = new int[1920 * 1080];
        this.frameRate = frameRate;

        setupVideoSurface();
    }

    private void setupVideoSurface() {
        // Create a render callback adapter using the buffer
        renderCallback = new RenderCallbackAdapter(videoBuffer) {
            private long lastFrameTime = 0;
            @Override
            protected void onDisplay(MediaPlayer mediaPlayer, int[] buffer) {
                if (frameRate != -1) {
                    long currentTime = System.currentTimeMillis();
                    float frameDuration = (float) 1000 / frameRate;
                    if (currentTime - lastFrameTime < frameDuration) return; // Skip the frame it is too soon

                    lastFrameTime = currentTime;
                }

                // Convert the raw RGB data to a BufferedImage
                BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
                image.setRGB(0, 0, 1920, 1080, buffer, 0, 1920);

                renderers.render(image);
            }
        };

        // Create an anonymous inner class for BufferFormatCallback
        videoSurface = getVideoSurface(renderCallback);
        mediaPlayer.videoSurface().set(videoSurface);
        mediaPlayer.videoSurface().attachVideoSurface();
    }

    private @NotNull VideoSurface getVideoSurface(RenderCallbackAdapter renderCallback) {
        bufferCallback = new BufferFormatCallback() {
            @Override
            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
                return new RV32BufferFormat(1920, 1080);
            }

            @Override
            public void allocatedBuffers(ByteBuffer[] buffers) {}
        };

        return mediaPlayerFactory.videoSurfaces().newVideoSurface(bufferCallback, renderCallback, true);
    }

    @Override
    public void start() {
        mediaPlayer.media().play(rmtpUrl);
    }

    public void stop() {
        mediaPlayer.controls().stop();
        mediaPlayer.release();
        mediaPlayerFactory.release();
    }
}
