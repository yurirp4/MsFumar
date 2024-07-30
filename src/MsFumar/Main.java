package MsFumar;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin {

	private static Economy econ = null;

	public void onEnable() {
		if (!setupEconomy()) {
			getLogger().severe(
					String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		ConsoleCommandSender b = Bukkit.getConsoleSender();
		b.sendMessage("§8##########################");
		b.sendMessage("§7MsFumar : §aAtivo");
		b.sendMessage("§7Version: §e1.5");
		b.sendMessage("§aAutor: yurirp4");
		b.sendMessage("§8##########################");
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}
	}

	public void onDisable() {
		ConsoleCommandSender b = Bukkit.getConsoleSender();
		b.sendMessage("§8##########################");
		b.sendMessage("§7MsFumar : §cDesativo");
		b.sendMessage("§7Version: §e1.5");
		b.sendMessage("§aAutor: yurirp4");
		b.sendMessage("§8##########################");
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private HashMap<Player, Long> playerLongMap = new HashMap<>();

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;
		if (label.equalsIgnoreCase("fumar")) {

			Long delay = TimeUnit.MINUTES.toMillis(3);
			if (playerLongMap.keySet().contains(p)) {
				if (playerLongMap.get(p) > System.currentTimeMillis()) {
					p.sendMessage("§cEsperer Um Pouco Para Usar O §4/fumar");
					return true;
				}
				playerLongMap.put(p, System.currentTimeMillis() + delay);
			}
			playerLongMap.put(p, System.currentTimeMillis() + delay);
			if (econ.getBalance(p.getName()) >= getConfig().getDouble("money")) {
				econ.withdrawPlayer(p.getName(), getConfig().getDouble("money"));
			} else {
				p.sendMessage("§cVocê não tem dinheiro suficiente!");
				return true;
			}
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 100));
		p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 10));
		p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 100));
		p.sendMessage(getConfig().getString("Player").replace("&", "§"));
		Bukkit.getServer().broadcastMessage("");
		Bukkit.getServer().broadcastMessage(getConfig().getString("global").replace("&", "§").replace("{player}", p.getName()));
		Bukkit.getServer().broadcastMessage("");
		return false;
	}
}