package codes.shiftmc.streaming.renderer.map;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class ItemMapFrame extends Entity {
    public ItemMapFrame(int mapId, Instance instance, Pos position, ItemFrameMeta.Orientation orientation) {
        super(EntityType.ITEM_FRAME);

        var meta = (ItemFrameMeta) getEntityMeta();
        meta.setNotifyAboutChanges(false);

        var itemStack = ItemStack.of(Material.FILLED_MAP).builder()
                .set(ItemComponent.MAP_ID, mapId)
                .build();

        meta.setItem(itemStack);
        setInstance(instance, position);
        meta.setOrientation(orientation);
        meta.setNotifyAboutChanges(true);
    }
}
