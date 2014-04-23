package com.open.bomberman;

import java.awt.BorderLayout;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;

public class ClientLauncher {

	public static void main(String[] args) throws IOException {
		new ClientLauncher().run(args);
	}

	public void run(String args[]) throws IOException {

		Socket socket = new Socket("localhost", 9000);
		final PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		writer.println("Tutu");
		writer.flush();

		AnimationProvider animationProvider = new AnimationProvider();
		final ClientGameMap gameMap = new ClientGameMap(animationProvider);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Bomberman");

		final GamePanel panel = new GamePanel(gameMap);

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		panel.bindPlayerControl(new PlayerControl() {

			private Direction direction;

			@Override
			public void setNewDirection(Direction direction) {
				this.direction = direction;
				sendCommand(false);
			}

			@Override
			public void createBomb() {
				sendCommand(true);
			}

			private void sendCommand(boolean createBomb) {
				if (direction == null) {
					writer.print("NOP");
				} else {
					writer.print(direction.name());
				}
				if (createBomb) {
					writer.print(" BOMB");
				}
				writer.println();
				writer.flush();
			}

		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						for (int y = 0; y < GameMap.HEIGHT; ++y) {
							String[] elements = reader.readLine().split(" ");
							for (int x = 0; x < GameMap.WIDTH; ++x) {
								gameMap.set(x, y, GameTile.fromCode(elements[x]));
							}
							writer.println();
						}
						int playerCount = Integer.valueOf(reader.readLine());
						for (int i = 0; i < playerCount; ++i) {
							String[] elements = reader.readLine().split(" ");
							gameMap.setPlayerInfo(i,
									new Point(Integer.valueOf(elements[0]), Integer.valueOf(elements[1])),
									elements[2].equals("ALIVE"));
						}
						panel.repaint();
					}
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			}
		}, "screen-thread").start();

	}

}
