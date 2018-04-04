package net.comorevi.nukkit.checkpoint;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CheckPoint extends PluginBase {

	private int CHECKPOINT_BLOCK;
	private boolean allowAddCommand;
	private boolean allowDeleteCommand;

	private HashMap<String, HashMap<String, Integer>> playerCheckPoint = new HashMap<String, HashMap<String, Integer>>();

	@Override
	public void onEnable() {
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().exists();
		}
		Config config = new Config(
				new File(this.getDataFolder(), "config.yml"),
				Config.YAML,
				new LinkedHashMap<String, Object>() {
					{
						put("checkPointBlock", Block.EMERALD_BLOCK);
						put("allowAddCommnad", false);
						put("allowDeleteCommand", true);
					}
				});
		config.save();

		this.setCheckPointBlock(config.getInt("checkPointBlock"));
		this.allowAddCommand = config.getBoolean("allowAddCommnad", false);
		this.allowDeleteCommand = config.getBoolean("allowDeleteCommand", true);

		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			this.getServer().getLogger().alert("このコマンドはゲーム内でのみ使用可能です。");
			return true;
		}

		switch (command.getName()) {
			case "cp":
				this.teleportPlayer((Player) sender);
				return true;
			case "cpadd":
				if (sender.isOp()) {
					this.setPlayerCheckPoint((Player) sender);
				} else {
					if (this.allowAddCommand) {
						this.setPlayerCheckPoint((Player) sender);
					} else {
						sender.sendMessage(TextFormat.YELLOW + "[CheckPoint]" + TextFormat.WHITE + "このコマンドは使用できません。");
					}
				}
				return true;
			case "cpdel":
				if (sender.isOp()) {
					this.deletePlayerCheckPoint((Player) sender);
				} else {
					if (this.allowDeleteCommand) {
						this.deletePlayerCheckPoint((Player) sender);
					} else {
						sender.sendMessage(TextFormat.YELLOW + "[CheckPoint]" + TextFormat.WHITE + "このコマンドは使用できません。");
					}
				}
				return true;
		}
		return false;
	}

	public void teleportPlayer(Player player) {
		try {
			int[] loc = this.getPlayerCheckPoint(player);
			player.teleport(new Vector3(loc[0] + 0.5, loc[1] + 0.3, loc[2] + 0.5));
			player.sendMessage(TextFormat.GREEN + "[CheckPoint]" + TextFormat.WHITE + "チェックポイントに移動しました。");
		} catch (NullPointerException e) {
			player.sendMessage(TextFormat.YELLOW + "[CheckPoint]" + TextFormat.WHITE + "チェックポイントがありません。");
		}
	}

	private void setCheckPointBlock(int blockID) {
		this.CHECKPOINT_BLOCK = blockID;
	}

	public int getCheckPointBlock() {
		return this.CHECKPOINT_BLOCK;
	}

	public void setPlayerCheckPoint(Player player) {
		String name = player.getName();
		final int x = (int) player.x;
		final int y = (int) player.y;
		final int z = (int) player.z;
		this.playerCheckPoint.put(
				name,
				new LinkedHashMap<String, Integer>() {
					{
						put("x", x);
						put("y", y);
						put("z", z);
					}
				});
		player.sendMessage(TextFormat.GREEN + "[CheckPoint]" + TextFormat.WHITE + "チェックポイントを設定しました。");
	}

	public void deletePlayerCheckPoint(Player player) {
		if (hasCheckPoint(player)) {
			this.playerCheckPoint.remove(player.getName());
			player.sendMessage(TextFormat.RED + "[CheckPoint]" + TextFormat.WHITE + "チェックポイントを削除しました。");
		} else {
			player.sendMessage(TextFormat.YELLOW + "[CheckPoint]" + TextFormat.WHITE + "チェックポイントがありません。");
		}
	}

	public int[] getPlayerCheckPoint(Player player) {
		if (hasCheckPoint(player)) {
			HashMap<String, Integer> map = this.playerCheckPoint.get(player.getName());
			int x = map.get("x");
			int y = map.get("y");
			int z = map.get("z");
			int[] loc = {x, y , z};
			return loc;
		} else {
			return null;
		}
	}

	public boolean hasCheckPoint(Player player) {
		if (this.playerCheckPoint.containsKey(player.getName())) {
			return true;
		} else {
			return false;
		}
	}
}
