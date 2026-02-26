package com.artillexstudios.axsellwands.hooks;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axsellwands.hooks.container.ContainerHook;
import com.artillexstudios.axsellwands.hooks.currency.CurrencyHook;
import com.artillexstudios.axsellwands.hooks.currency.VaultHook;
import com.artillexstudios.axsellwands.hooks.protection.IslandWorldHook;
import com.artillexstudios.axsellwands.hooks.protection.ProtectionHook;
import com.artillexstudios.axsellwands.hooks.protection.WorldGuardHook;
import com.artillexstudios.axsellwands.hooks.shop.BuiltinPrices;
import com.artillexstudios.axsellwands.hooks.shop.EconomyShopGuiHook;
import com.artillexstudios.axsellwands.hooks.shop.PricesHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

import static com.artillexstudios.axsellwands.AxSellwands.HOOKS;

public class HookManager {
    private static CurrencyHook currency = null;
    private static PricesHook shopPrices = null;
    private static final HashSet<ContainerHook> CONTAINER_HOOKS = new HashSet<>();
    private static final HashSet<ProtectionHook> PROTECTION_HOOKS = new HashSet<>();

    public static void setupHooks() {
        updateHooks();

        if (HOOKS.getBoolean("hook-settings.WorldGuard.register", true) && Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            PROTECTION_HOOKS.add(new WorldGuardHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxSellwands] Hooked into WorldGuard!"));
        }

        // Dodana rejestracja IslandWorld
        if (Bukkit.getPluginManager().getPlugin("IslandWorld") != null) {
            PROTECTION_HOOKS.add(new IslandWorldHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxSellwands] Hooked into IslandWorld!"));
        }
    }

    public static void updateHooks() {
        final String eco = HOOKS.getString("hooks.economy-plugin", "VAULT").toUpperCase();
        if ("VAULT".equals(eco)) {
            if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                currency = new VaultHook();
                Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxSellwands] Hooked into Vault!"));
            } else {
                Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF3333[AxSellwands] Vault is set in hooks.yml, but it is not installed!"));
            }
        }
        
        if (currency != null)
            currency.setup();

        final String shop = HOOKS.getString("hooks.price-plugin", "ECONOMYSHOPGUI").toUpperCase();
        if ("ECONOMYSHOPGUI".equals(shop)) {
            if (Bukkit.getPluginManager().getPlugin("EconomyShopGUI") != null || Bukkit.getPluginManager().getPlugin("EconomyShopGUI-Premium") != null) {
                shopPrices = new EconomyShopGuiHook();
                Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxSellwands] Hooked into EconomyShopGUI!"));
            } else {
                Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF3333[AxSellwands] EconomyShopGUI is set in hooks.yml, but it is not installed!"));
            }
        } else {
            shopPrices = new BuiltinPrices();
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxSellwands] Using builtin prices!"));
        }
        
        if (shopPrices != null)
            shopPrices.setup();

        if (getShopPrices() == null) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF3333[AxSellwands] Shop prices hook not found! Please check your hooks.yml!"));
        }

        if (getCurrency() == null) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF3333[AxSellwands] Currency hook not found! Please check your hooks.yml!"));
        }
    }

    @SuppressWarnings("unused")
    public static void registerProtectionHook(@NotNull Plugin plugin, @NotNull ProtectionHook protectionHook) {
        PROTECTION_HOOKS.add(protectionHook);
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxSellwands] Hooked into " + plugin.getName() + "!"));
    }

    @SuppressWarnings("unused")
    public static void registerPriceProviderHook(@NotNull Plugin plugin, @NotNull PricesHook pricesHook) {
        shopPrices = pricesHook;
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxSellwands] Hooked into " + plugin.getName() + "!"));
    }

    @SuppressWarnings("unused")
    public static void registerCurrencyHook(@NotNull Plugin plugin, @NotNull CurrencyHook currencyHook) {
        currency = currencyHook;
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxSellwands] Hooked into " + plugin.getName() + "!"));
    }

    @Nullable
    public static CurrencyHook getCurrency() {
        return currency;
    }

    @Nullable
    public static PricesHook getShopPrices() {
        return shopPrices;
    }

    @NotNull
    public static HashSet<ContainerHook> getContainerHooks() {
        return CONTAINER_HOOKS;
    }

    @NotNull
    public static HashSet<ProtectionHook> getProtectionHooks() {
        return PROTECTION_HOOKS;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canBuildAt(@NotNull Player player, @NotNull Location location) {
        for (ProtectionHook hook : PROTECTION_HOOKS) {
            if (!hook.canPlayerBuildAt(player, location)) return false;
        }
        return true;
    }

    @Nullable
    public static ContainerHook getContainerAt(@NotNull Player player, @NotNull Block block) {
        for (ContainerHook hook : CONTAINER_HOOKS) {
            if (!hook.isContainer(player, block)) continue;
            return hook;
        }
        return null;
    }
}
