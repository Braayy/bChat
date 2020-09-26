package braayy.chat.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    public static Economy economy;

    public static boolean setupEconomy() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) return false;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (rsp == null) return false;

        economy = rsp.getProvider();

        return economy != null;
    }

}