package com.open.bomberman;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;

public class ServerLauncher {

	public static void main(String[] args) throws IOException {
		new ServerLauncher().run();
	}

	public void run() throws IOException {

		AnimationProvider animationProvider = new AnimationProvider();
		ServerGameMap gameMap = new ServerGameMap(animationProvider);

		GameEngine gameEngine = new GameEngine(gameMap);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Bomberman Server");

		GamePanel panel = new GamePanel(gameMap);

		gameEngine.addListener(panel);

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		gameEngine.start();
	}

}
