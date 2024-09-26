package codes.shiftmc.streaming.renderer.map.model;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;

public record Map(
        Instance instance,
        Vec vec,
        ItemFrameMeta.Orientation orientation
) {
}
