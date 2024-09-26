package codes.shiftmc.streaming.renderer.map.model;

import codes.shiftmc.streaming.client.Clients;
import codes.shiftmc.streaming.renderer.map.MapRenderer;

public record Display(
        Clients clients,
        MapRenderer renderer
) {
}
