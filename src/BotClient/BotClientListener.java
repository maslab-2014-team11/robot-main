package BotClient;

public interface BotClientListener {

	public void onGameState(boolean gameStarted);
	
	public void onReceiveMap(String map);
}
