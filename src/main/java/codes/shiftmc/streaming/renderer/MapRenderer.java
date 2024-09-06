package codes.shiftmc.streaming.renderer;

import net.minestom.server.component.DataComponent;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.map.MapColors;
import net.minestom.server.map.framebuffers.DirectFramebuffer;
import net.minestom.server.map.framebuffers.LargeDirectFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import net.minestom.server.tag.Tag;

import java.awt.image.BufferedImage;

import static codes.shiftmc.streaming.renderer.particle.ParticleImage.resize;

public class MapRenderer implements Renderers {

    private final Vec pos;
    private final Instance instance;
    private final int width;
    private final int height;

    private final int id = Math.toIntExact(System.currentTimeMillis() % Integer.MAX_VALUE);

    public MapRenderer(Vec pos, Instance instance, int width, int height) {
        this.pos = pos;
        this.instance = instance;
        this.width = width;
        this.height = height;

        assert width > 0 && height > 0;
        assert width % 128 == 0 && height % 128 == 0;

        instance.eventNode().addListener(AddEntityToInstanceEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                int i = 0;
                for (int y = 0; y < height / 128; y++) {
                    for (int x = 0; x < width / 128; x++) {
                        var itemStack = ItemStack.of(Material.FILLED_MAP).builder()
                                .set(ItemComponent.MAP_ID, id + i++)
                                .build();
                        player.getInventory().addItemStack(itemStack);
                    }
                }
            }
        });
    }

    @Override
    public void render(BufferedImage image) {
        // Break image in 128
        BufferedImage resize = resize(image, width, height);

        int i = 0;
        for (int yBlock  = 0; yBlock  < height / 128; yBlock ++) {
            for (int xBlock  = 0; xBlock  < width / 128; xBlock ++) {
                var fb = new DirectFramebuffer();
                // Loop 128x128 pixels of the image, based on the x and y locations
                for (int yy = 0; yy < 128; yy++) {
                    for (int xx = 0; xx < 128; xx++) {
                        int imageX = xBlock * 128 + xx;
                        int imageY = yBlock * 128 + yy;

                        int pixel = resize.getRGB(imageX, imageY);
                        MapColors.PreciseMapColor mappedColor = MapColors.closestColor(pixel);
                        fb.set(xx, yy, mappedColor.getIndex());
                    }
                }

                MapDataPacket mapDataPacket = fb.preparePacket(id + i++);
                instance.sendGroupedPacket(mapDataPacket);
            }
        }
    }
}
