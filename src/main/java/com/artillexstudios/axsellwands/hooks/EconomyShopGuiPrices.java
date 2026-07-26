package com.artillexstudios.axsellwands.hooks;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.ShopIntegration;
import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import me.gypopo.economyshopgui.objects.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Wlasna integracja EconomyShopGUI dla tego forka.
 *
 * Powod: wbudowana integracja z AxIntegrations liczy cene sprzedazy przez transakcyjne
 * EconomyShopGUIHook.getSellPrice(OfflinePlayer, item) -> Optional&lt;SellPrice&gt;, ktore
 * zwraca pusto gdy przedmiotu AKTUALNIE nie da sie sprzedac (limity sprzedazy, dynamiczne
 * ceny, uprawnienia sekcji, kontekst offline-gracza). Sellwand dostawal wtedy null i zglaszal
 * "nothing-sold" mimo ze przedmiot jest w sklepie ESG.
 *
 * Ta wersja odtwarza zachowanie starego, dzialajacego hooka forka: liczy BAZOWA cene
 * sprzedazy przez getItemSellPrice(shopItem, item[, player]).
 *
 * Nazwa celowo rozni sie od wbudowanej ("EconomyShopGUI"), zeby predykat z hooks.yml
 * (price-plugin) wybieral wlasnie te integracje, a nie wbudowana. Ustaw:
 *   hooks.price-plugin: EconomyShopGUIDirect
 */
public class EconomyShopGuiPrices extends ShopIntegration {

    public EconomyShopGuiPrices() {
        super("EconomyShopGUIDirect");
    }

    @Override
    public @NotNull String getFormattedName() {
        return "EconomyShopGUI (direct/base price)";
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("me.gypopo.economyshopgui.api.EconomyShopGUIHook");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Nullable
    @Override
    public Double getSellPrice(@NotNull ItemStack item) {
        ItemStack copy = copy(item);
        ShopItem shopItem = EconomyShopGUIHook.getShopItem(copy);
        if (shopItem == null) return null;
        Double price = EconomyShopGUIHook.getItemSellPrice(shopItem, copy);
        if (price == null || price <= 0) return null;
        return price * item.getAmount();
    }

    @Nullable
    @Override
    public Double getSellPrice(UUID playerUUID, @NotNull ItemStack item) {
        ItemStack copy = copy(item);
        ShopItem shopItem = EconomyShopGUIHook.getShopItem(copy);
        if (shopItem == null) return null;
        Player player = playerUUID == null ? null : Bukkit.getPlayer(playerUUID);
        Double price = player != null
                ? EconomyShopGUIHook.getItemSellPrice(shopItem, copy, player)
                : EconomyShopGUIHook.getItemSellPrice(shopItem, copy);
        if (price == null || price <= 0) return null;
        return price * item.getAmount();
    }

    @Nullable
    @Override
    public Double getBuyPrice(@NotNull ItemStack item) {
        ItemStack copy = copy(item);
        ShopItem shopItem = EconomyShopGUIHook.getShopItem(copy);
        if (shopItem == null) return null;
        Double price = EconomyShopGUIHook.getItemBuyPrice(shopItem, copy);
        if (price == null || price <= 0) return null;
        return price * item.getAmount();
    }

    @Nullable
    @Override
    public Double getBuyPrice(UUID playerUUID, @NotNull ItemStack item) {
        ItemStack copy = copy(item);
        ShopItem shopItem = EconomyShopGUIHook.getShopItem(copy);
        if (shopItem == null) return null;
        Player player = playerUUID == null ? null : Bukkit.getPlayer(playerUUID);
        Double price = player != null
                ? EconomyShopGUIHook.getItemBuyPrice(shopItem, copy, player)
                : EconomyShopGUIHook.getItemBuyPrice(shopItem, copy);
        if (price == null || price <= 0) return null;
        return price * item.getAmount();
    }
}
