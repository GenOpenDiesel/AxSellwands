package com.artillexstudios.axsellwands.hooks;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.islandworld.api.IslandWorldApi;
import pl.islandworld.entity.SimpleIsland;

/**
 * Wsparcie dla IslandWorld (pl.islandworld) na nowej architekturze AxIntegrations.
 * Rejestrowane w {@link HookManager} przez AxIntegrationsAPI.provideIntegration(...).
 * Dla sellwanda wszystkie akcje (place/break/interact/open) mapują się na tę samą
 * regułę: czy gracz może budować na danej lokalizacji wyspy.
 */
public class IslandWorldProtection extends ProtectionIntegration {

    public IslandWorldProtection() {
        super("IslandWorld");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("pl.islandworld.api.IslandWorldApi");
    }

    private boolean canUse(@NotNull Player player, @NotNull Location location) {
        SimpleIsland island = IslandWorldApi.getIsland(location);

        // Jeżeli lokalizacja jest na wyspie
        if (island != null) {
            // Permisja na omijanie blokad
            if (player.hasPermission("islandblocklimits.bypass")) {
                return true;
            }

            // Wbudowana metoda API IslandWorld - true oznacza, że dotyczy też dodanych pomocników.
            // Jeśli zwróci false, AxSellwands zablokuje użycie różdżki.
            return IslandWorldApi.canBuildOnLocation(player, location, true);
        }

        // Poza terenem IslandWorld przepuszczamy dalej (inne integracje/serwer decydują)
        return true;
    }

    @Override
    public boolean canPlace(@NotNull Player player, @NotNull Location location) {
        return canUse(player, location);
    }

    @Override
    public boolean canBreak(@NotNull Player player, @NotNull Location location) {
        return canUse(player, location);
    }

    @Override
    public boolean canInteract(@NotNull Player player, @NotNull Location location) {
        return canUse(player, location);
    }

    @Override
    public boolean canOpenContainer(@NotNull Player player, @NotNull Location location) {
        return canUse(player, location);
    }
}
