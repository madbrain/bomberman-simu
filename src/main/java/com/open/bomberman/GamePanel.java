package com.open.bomberman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements GameEngineListener {

	private static final int SCALE = 3;
	private static final int HEADER_HEIGHT = 64;

	private static final long serialVersionUID = -3469029964451336928L;

	private BufferedImage background;

	private GameMap gameMap;

	public GamePanel(GameMap gameMap) {
		setOpaque(true);
		setBackground(Color.BLACK);
		setFocusable(true);

		this.gameMap = gameMap;

		try {

			background = ImageIO.read(getClass().getResourceAsStream("/sprites/background.png"));

		} catch (IOException e) {
		}

	}

	public void bindPlayerControl(final PlayerControl playerControl) {
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					playerControl.setNewDirection(Direction.UP);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					playerControl.setNewDirection(Direction.DOWN);
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					playerControl.setNewDirection(Direction.LEFT);
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					playerControl.setNewDirection(Direction.RIGHT);
				} else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
					playerControl.createBomb();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() != KeyEvent.VK_CONTROL) {
					playerControl.setNewDirection(null);
				}
			}

		});
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(background.getWidth() * SCALE, background.getHeight() * SCALE + HEADER_HEIGHT);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.GRAY);
		g2d.setStroke(new BasicStroke(2.0f));
		g2d.setFont(new Font("Arial", Font.BOLD, 20));

		int x = 20;
		int y = 8;
		int CELL_WIDTH = 48;
		int CELL_HEIGHT = 48;
		int gap = 10;
		for (Player player : new ArrayList<Player>(gameMap.getPlayers())) {
			g2d.drawRoundRect(x - 4, y - 4, CELL_WIDTH + 4 * 2, CELL_HEIGHT + 4 * 2, 8, 8);

			Animation animation = player.getAnimationProvider().getIdleAnimation();

			int offset = animation.height > 24 ? 8 : 0;
			g2d.drawImage(animation.image,
					x, y,
					x + CELL_WIDTH, y + CELL_HEIGHT,
					0, offset,
					animation.width, Math.min(animation.height, CELL_HEIGHT / SCALE) + offset, this);

			String str = player.getName();
			g2d.drawString(str, x + CELL_WIDTH + gap, y + CELL_HEIGHT - 16);
			x += CELL_WIDTH + gap * 2 + g2d.getFontMetrics().stringWidth(str);
		}

		g2d.setTransform(new AffineTransform(new double[] { SCALE, 0.0, 0.0, SCALE, 0, HEADER_HEIGHT }));

		g2d.drawImage(background, 0, 0, this);

		g2d.translate(16, 14);

		gameMap.draw(g2d, this);

	}

}
