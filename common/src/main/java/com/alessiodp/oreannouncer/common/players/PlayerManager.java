package com.alessiodp.oreannouncer.common.players;

import com.alessiodp.core.common.user.User;
import com.alessiodp.oreannouncer.common.OreAnnouncerPlugin;
import com.alessiodp.oreannouncer.common.configuration.data.ConfigMain;
import com.alessiodp.oreannouncer.common.players.objects.OAPlayerImpl;
import com.alessiodp.oreannouncer.common.players.objects.PlayerDataBlock;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.UUID;

public abstract class PlayerManager {
	protected final OreAnnouncerPlugin plugin;
	
	@Getter private HashMap<UUID, OAPlayerImpl> listPlayers;
	
	public PlayerManager(@NonNull OreAnnouncerPlugin plugin) {
		this.plugin = plugin;
		listPlayers = new HashMap<>();
	}
	
	public void reload() {
		listPlayers.clear();
		
		for (User user : plugin.getOnlinePlayers()) {
			loadPlayer(user.getUUID());
		}
	}
	
	public abstract OAPlayerImpl initializePlayer(UUID playerUUID);
	
	public OAPlayerImpl loadPlayer(UUID uuid) {
		OAPlayerImpl ret = getPlayer(uuid);
		getListPlayers().put(uuid, ret);
		return ret;
	}
	
	public void unloadPlayer(UUID uuid) {
		getListPlayers().remove(uuid);
	}
	
	public OAPlayerImpl getPlayer(UUID uuid) {
		OAPlayerImpl ret;
		if (getListPlayers().containsKey(uuid)) {
			// Get player from online list
			ret = getListPlayers().get(uuid);
		} else {
			// Get player from database
			ret = plugin.getDatabaseManager().getPlayer(uuid);
			
			// Load new player
			if (ret == null)
				ret = initializePlayer(uuid);
		}
		return ret;
	}
	
	public int getTotalBlocks(OAPlayerImpl player) {
		int ret = 0;
		if (player != null) {
			for (PlayerDataBlock pdb : player.getDataBlocks().values()) {
				if (!ConfigMain.STATS_BLACKLIST_BLOCKS.contains(pdb.getMaterialName())) {
					ret += pdb.getDestroyCount();
				}
			}
		}
		return ret;
	}
}
