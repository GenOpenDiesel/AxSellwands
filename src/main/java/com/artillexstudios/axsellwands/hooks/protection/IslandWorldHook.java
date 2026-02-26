package com.artillexstudios.axsellwands.hooks.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.islandworld.api.IslandWorldApi;
import pl.islandworld.entity.SimpleIsland;

public class IslandWorldHook implements ProtectionHook {

    @Override
    public boolean canPlayerBuildAt(@NotNull Player player, @NotNull Location location) {
        SimpleIsland island = IslandWorldApi.getIsland(location);
        
        // Jeżeli klikany blok jest na wyspie
        if (island != null) {
            // Sprawdzenie permisji na omijanie blokad
            if (player.hasPermission("islandblocklimits.bypass")) {
                return true;
            }
            
            // Wbudowana metoda API IslandWorld - true oznacza że dotyczy też dodanych pomocników.
            // Jeśli metoda zwróci false, AxSellwands natywnie zablokuje użycie różdżki.
            return IslandWorldApi.canBuildOnLocation(player, location, true);
        }
        
        // Jeżeli bloku nie ma na terenie IslandWorld, zwracamy true i przepuszczamy dalej 
        // (inne hooki lub główny serwer zdecydują)
        return true; 
    }
}
