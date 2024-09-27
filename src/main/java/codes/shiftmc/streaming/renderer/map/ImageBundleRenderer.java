package codes.shiftmc.streaming.renderer.map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageBundleRenderer {

    private final ArrayList<BufferedImage> cachedImages = new ArrayList<>();
    private final List<MapRenderer> mapRenderers = new ArrayList<>();

    private final List<Integer> ids = new ArrayList<>();

    private int currentIndex = 0;

    public ImageBundleRenderer(
            List<BufferedImage> images,
            List<MapRenderer> mapRenderers
    ) {
        cachedImages.addAll(images);
        this.mapRenderers.addAll(mapRenderers);

        ids.add((int) System.currentTimeMillis() + 10000);
        for (int i = 0; i < mapRenderers.size(); i++) {
            final var renderer = mapRenderers.get(i);
            renderer.render(images.get(i));
            ids.add(renderer.getId());
        }
        ids.add((int) System.currentTimeMillis() + 100000);
    }

    public void step(int number) {
        if (currentIndex + number >= cachedImages.size()) currentIndex = 0;
        else if (currentIndex + number < 0) return;
        else currentIndex += number;

        for (final MapRenderer renderer : mapRenderers) {
            for (int i = 0; i < renderer.getItemMapFrames().length; i++) {
                final var item = renderer.getItemMapFrames()[i];
                for (int j = 0; j < item.length; j++) {
                    final var k = renderer.getItemMapFrames()[i][j];
                    k.changeId(MapRenderer.generateUniqueId(ids.get(i), i, j));
                }
            }
        }
    }
}
