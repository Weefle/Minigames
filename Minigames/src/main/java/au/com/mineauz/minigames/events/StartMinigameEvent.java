package au.com.mineauz.minigames.events;

import java.util.ArrayList;
import java.util.List;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class StartMinigameEvent extends AbstractMinigameEvent{

	private boolean willTeleport;
	private List<MinigamePlayer> players;

	public StartMinigameEvent(List<MinigamePlayer> players, Minigame minigame, boolean willTeleport){
		super(minigame);
		this.willTeleport = willTeleport;
		this.players = players;
	}
	
	public boolean getWillTeleport(){
		return willTeleport;
	}
	
	public List<MinigamePlayer> getPlayers(){
		return new ArrayList<MinigamePlayer>(players);
	}

	@Override
	public boolean isCancelled(){return false;}

    @Override
	public void setCancelled(boolean b){
		throw new UnsupportedOperationException("Cannot cancel a  Minigames Broadcast Event");
	}

}
