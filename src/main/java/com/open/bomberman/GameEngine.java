package com.open.bomberman;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameEngine {

	private static final Logger LOG = Logger.getLogger(GameEngine.class.getName());

	private static final int CYCLE_TIME = 30;

	private Thread gameThread;
	private Thread acceptThread;
	private List<PlayerRunnable> players = new ArrayList<PlayerRunnable>();

	private boolean running = true;
	private ServerGameMap gameMap;

	private List<GameEngineListener> listeners = new ArrayList<GameEngineListener>();

	public GameEngine(ServerGameMap gameMap) {
		this.gameMap = gameMap;
	}

	public void start() {
		gameThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running && gameMap.isPlaying()) {
					try {
						Thread.sleep(CYCLE_TIME);
					} catch (InterruptedException e) {
					}

					gameMap.stepObjects();

					for (GameEngineListener listener : listeners) {
						listener.repaint();
					}

					for (PlayerRunnable player : players) {
						player.sendScreen();
					}

				}
			}
		}, "game-thread");
		gameThread.start();

		acceptThread = new Thread(new Runnable() {

			@Override
			public void run() {
				ServerSocket serverSocket = null;
				try {
					serverSocket = new ServerSocket(9000);
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "Erreur à la création de la socket de rendez-vous", e);
				}
				if (serverSocket != null) {
					while (running) {
						try {
							final Socket socket = serverSocket.accept();
							PlayerRunnable playerRunnable = new PlayerRunnable(socket);
							playerRunnable.handleStart();
							players.add(playerRunnable);
						} catch (IOException e) {
							LOG.log(Level.SEVERE, "Erreur à l'ouverture de la socket", e);
						}
					}
				}
			}
		}, "connection-acceptor");
		acceptThread.start();
	}

	private class PlayerRunnable implements Runnable {

		private Socket socket;
		private Player player;
		private Thread thread;
		private BufferedReader reader;
		private PrintWriter writer;

		public PlayerRunnable(Socket socket) {
			this.socket = socket;
		}

		public void handleStart() throws IOException {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

			String line = reader.readLine();
			String name = line;
			player = gameMap.createPlayer(name);
			thread = new Thread(this, "player-" + name);
			thread.start();
		}

		public void sendScreen() {
			for (int y = 0; y < GameMap.HEIGHT; ++y) {
				for (int x = 0; x < GameMap.WIDTH; ++x) {
					if (x > 0) {
						writer.print(" ");
					}
					writer.print(gameMap.get(x, y).getCode());
				}
				writer.println();
			}
			writer.println(players.size());
			for (PlayerRunnable p : players) {
				Point position = p.player.getPosition();
				writer.println(position.x + " " + position.y + " " + (p.player.isBeingKilled() ? "KILLED" : "ALIVE"));
			}
		}

		@Override
		public void run() {
			try {
				while (running) {
					String line = reader.readLine();
					String[] commands = line.split(" ");
					if (commands.length > 0 && commands[0].length() > 0) {
						if (commands[0].equals("NOP")) {
							player.setNewDirection(null);
						} else {
							player.setNewDirection(Direction.valueOf(commands[0]));
						}
						if (commands.length > 1 && commands[1].equals("BOMB")) {
							player.createBomb();
						}
					}
				}
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Erreur à l'ouverture de la socket", e);
			}
			players.remove(this);
			gameMap.removePlayer(player);
		}
	}

	public static int getTicks(int time) {
		return time * 1000 / GameEngine.CYCLE_TIME;
	}

	public void addListener(GameEngineListener listener) {
		this.listeners.add(listener);
	}
}
