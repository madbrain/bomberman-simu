package com.open.bomberman;

import java.awt.Color;
import java.awt.image.RGBImageFilter;
import java.util.Random;

public class PlayerColorFilter extends RGBImageFilter {

	private static class HSLColor {

		private double h;
		private double s;
		private double l;

		public HSLColor(double h, double s, double l) {
			this.h = h;
			this.s = s;
			this.l = l;
		}

		public HSLColor setHue(double hueChange) {
			return new HSLColor(hueChange, s, l);
		}

		public Color toRGB() {
			int r = (int) (l * 255);
			int g = r;
			int b = r;
			if (s != 0.0) {
				double var_2;
				if (l < 0.5) {
					var_2 = l * (1.0 + s);
				} else {
					var_2 = (l + s) - (s * l);
				}

				double var_1 = 2 * l - var_2;

				r = sat((int) (255 * hueToRGB(var_1, var_2, h + (1.0 / 3.0))));
				g = sat((int) (255 * hueToRGB(var_1, var_2, h)));
				b = sat((int) (255 * hueToRGB(var_1, var_2, h - (1.0 / 3.0))));
			}
			return new Color(r, g, b);
		}

		private int sat(int value) {
			return Math.min(Math.max(value, 0), 255);
		}

		private double hueToRGB(double v1, double v2, double vh) {
			if (vh < 0.0) {
				vh += 1.0;
			} else if (vh > 1.0) {
				vh -= 1.0;
			}
			if ((6 * vh) < 1.0) {
				return (v1 + (v2 - v1) * 6 * vh);
			}
			if ((2 * vh) < 1.0) {
				return v2;
			}
			if ((3 * vh) < 2.0) {
				return (v1 + (v2 - v1) * ((2 / 3) - vh) * 6);
			}
			return v1;
		}
	}

	private static final long SEED = 123456L;

	private double newHue = 0.1;

	public PlayerColorFilter(int id) {
		canFilterIndexColorModel = true;

		Random random = new Random(SEED);

		for (int i = 0; i <= id; ++i) {
			newHue = random.nextDouble();
		}
	}

	public int filterRGB(int x, int y, int rgb) {

		int originalAlpha = (rgb & 0xFF000000) >> 24;
		int originalRed = (rgb & 0x00FF0000) >> 16;
		int originalGreen = (rgb & 0x0000FF00) >> 8;
		int originalBlue = (rgb & 0x000000FF);
		HSLColor hslColor = toHSL(originalRed, originalGreen, originalBlue);
		Color newColor = hslColor.setHue(newHue).toRGB();

		return (originalAlpha << 24)
				| (newColor.getRed() << 16)
				| (newColor.getGreen() << 8)
				| newColor.getBlue();
	}

	private HSLColor toHSL(int r, int g, int b) {
		double var_R = r / 255.0;
		double var_G = g / 255.0;
		double var_B = b / 255.0;

		double var_Min = Math.min(var_R, Math.min(var_G, var_B));
		double var_Max = Math.max(var_R, Math.max(var_G, var_B));
		double del_Max = var_Max - var_Min;

		double L = (var_Max + var_Min) / 2;
		double H = 0.0;
		double S = 0.0;

		if (del_Max != 0) {
			if (L < 0.5) {
				S = del_Max / (var_Max + var_Min);
			} else {
				S = del_Max / (2 - var_Max - var_Min);
			}

			double del_R = (((var_Max - var_R) / 6) + (del_Max / 2)) / del_Max;
			double del_G = (((var_Max - var_G) / 6) + (del_Max / 2)) / del_Max;
			double del_B = (((var_Max - var_B) / 6) + (del_Max / 2)) / del_Max;

			if (var_R == var_Max) {
				H = del_B - del_G;
			} else if (var_G == var_Max) {
				H = (1.0 / 3) + del_R - del_B;
			} else if (var_B == var_Max) {
				H = (2.0 / 3) + del_G - del_R;
			}
			if (H < 0.0) {
				H += 1.0;
			} else if (H > 1.0) {
				H -= 1.0;
			}
		}
		return new HSLColor(H, S, L);
	}
}
