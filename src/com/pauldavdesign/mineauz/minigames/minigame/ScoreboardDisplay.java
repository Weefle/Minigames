package com.pauldavdesign.mineauz.minigames.minigame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemScoreboardSave;

public class ScoreboardDisplay {
	private Location loc;
	private ScoreboardType type = ScoreboardType.COMPLETIONS;
	private ScoreboardOrder ord = ScoreboardOrder.DESCENDING;
	private Minigame mgm;
	private int width;
	private int height;
	private BlockFace dir;
	
	public ScoreboardDisplay(Minigame mgm, int width, int height, Location signLoc, BlockFace dir){
		this.mgm = mgm;
		this.width = width;
		this.height = height;
		loc = signLoc;
		this.dir = dir;
	}

	public Location getLocation() {
		return loc;
	}

	public ScoreboardType getType() {
		return type;
	}

	public void setType(ScoreboardType type) {
		this.type = type;
	}

	public ScoreboardOrder getOrder() {
		return ord;
	}

	public void setOrd(ScoreboardOrder ord) {
		this.ord = ord;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Minigame getMinigame() {
		return mgm;
	}
	
	public BlockFace getDirection(){
		return dir;
	}
	
	public void updateStats(){
		ScoreboardSortThread thread = new ScoreboardSortThread(mgm.getScoreboardData().getPlayers(), type, ord, this);
		thread.start();
	}
	
	public void displayStats(List<ScoreboardPlayer> results){
		Location cur = loc.clone();
		cur.setY(cur.getY() - height);
		int ord;
		int ory = cur.getBlockY();
		if(dir == BlockFace.EAST || dir == BlockFace.WEST){
			cur.setZ(cur.getZ() - Math.floor(width / 2));
			ord = cur.getBlockZ();
		}
		else{
			cur.setX(cur.getX() - Math.floor(width / 2));
			ord = cur.getBlockX();
		}
		
		int place = 1;
mainLoop:for(int y = height - 1; y >= 0; y--){
			cur.setY(ory + y);
			for(int i = 0; i < width; i++){
				if(dir == BlockFace.EAST || dir == BlockFace.WEST){
					cur.setZ(ord + i);
				}
				else{
					cur.setX(ord + i);
				}
				if(cur.getBlock().getType() != Material.AIR){
					if(cur.getBlock().getState() instanceof Sign){
						if(place >= results.size()) break mainLoop;
						Sign nsign = (Sign)cur.getBlock().getState();
						nsign.setLine(0, ChatColor.GREEN.toString() + place + ". " + ChatColor.BLACK + results.get(place - 1).getPlayerName());
						if(type != ScoreboardType.TOTAL_TIME && type != ScoreboardType.LEAST_TIME)
							nsign.setLine(1, ChatColor.BLUE + ((Integer)results.get(place - 1).getByType(type)).toString() + " " + type.getTypeName());
						else
							nsign.setLine(1, ChatColor.BLUE + MinigameUtils.convertTime((int)((Long)results.get(place - 1).getByType(type) / 1000), true));
						place++;
						nsign.setLine(2, ChatColor.GREEN.toString() + place + ". " + ChatColor.BLACK + results.get(place - 1).getPlayerName());
						if(type != ScoreboardType.TOTAL_TIME && type != ScoreboardType.LEAST_TIME)
							nsign.setLine(3, ChatColor.BLUE + ((Integer)results.get(place - 1).getByType(type)).toString() + " " + type.getTypeName());
						else
							nsign.setLine(3, ChatColor.BLUE + MinigameUtils.convertTime((int)((Long)results.get(place - 1).getByType(type) / 1000), true));
						place++;
						nsign.update();
					}
				}
			}
		}
	}
	
	public void displayMenu(MinigamePlayer player){
		Menu setupMenu = new Menu(3, "Setup Scoreboard", player);
		List<MenuItem> items = new ArrayList<MenuItem>();
		List<String> sbtypes = new ArrayList<String>();
		for(ScoreboardType t : ScoreboardType.values()){
			sbtypes.add(t.toString().toLowerCase().replace("_", " "));
		}
		items.add(new MenuItemList("Scoreboard Display", Material.ENDER_PEARL, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				type = ScoreboardType.valueOf(value.toUpperCase().replace(" ", "_"));
			}
			
			@Override
			public String getValue() {
				return type.toString().toLowerCase().replace("_", " ");
			}
		}, sbtypes));
		List<String> sbotypes = new ArrayList<String>();
		for(ScoreboardOrder o : ScoreboardOrder.values()){
			sbotypes.add(o.toString().toLowerCase());
		}
		items.add(new MenuItemList("Scoreboard Order", Material.ENDER_PEARL, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				ord = ScoreboardOrder.valueOf(value.toUpperCase());
			}
			
			@Override
			public String getValue() {
				return ord.toString().toLowerCase();
			}
		}, sbotypes));
		setupMenu.addItems(items);
		setupMenu.addItem(new MenuItemScoreboardSave("Create Scoreboard", Material.REDSTONE_TORCH_ON, this), setupMenu.getSize() - 1);
		setupMenu.displayMenu(player);
	}
}
