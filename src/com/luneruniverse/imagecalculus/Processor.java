package com.luneruniverse.imagecalculus;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Processor {
	
	public static BufferedImage process(BufferedImage img, ImageSettings settings) {
		if (settings.integral())
			return integrate(img, settings);
		if (settings.derivative2())
			return derivative2(img, settings);
		if (settings.slopeField())
			return slopeField(img, settings);
		return derivative(img, settings);
	}
	
	public static BufferedImage derivative(BufferedImage img, ImageSettings settings) {
		BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				Color color = Color.fromRGB(img.getRGB(x, y));
				List<Color> colors = new ArrayList<>();
				if (x > 0)
					colors.add(Color.fromRGB(img.getRGB(x - 1, y)));
				if (y > 0)
					colors.add(Color.fromRGB(img.getRGB(x, y - 1)));
				if (x < img.getWidth() - 1)
					colors.add(Color.fromRGB(img.getRGB(x + 1, y)));
				if (y < img.getHeight() - 1)
					colors.add(Color.fromRGB(img.getRGB(x, y + 1)));
				for (int i = 0; i < colors.size(); i++)
					colors.set(i, color.slope(colors.get(i), settings.type()));
				output.setRGB(x, y, Color.fromAverage(colors, settings.type()).getRGB());
			}
		}
		return output;
	}
	
	public static BufferedImage slopeField(BufferedImage img, ImageSettings settings) {
		BufferedImage output = new BufferedImage(img.getWidth() * 5, img.getHeight() * 5, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				Color color = Color.fromRGB(img.getRGB(x, y));
				Map<Color.Direction, Color> colors = new HashMap<>();
				if (x > 0)
					colors.put(Color.Direction.LEFT, Color.fromRGB(img.getRGB(x - 1, y)));
				if (y > 0)
					colors.put(Color.Direction.UP, Color.fromRGB(img.getRGB(x, y - 1)));
				if (x < img.getWidth() - 1)
					colors.put(Color.Direction.RIGHT, Color.fromRGB(img.getRGB(x + 1, y)));
				if (y < img.getHeight() - 1)
					colors.put(Color.Direction.DOWN, Color.fromRGB(img.getRGB(x, y + 1)));
				colors.replaceAll((dir, dirColor) -> color.slope(dirColor, settings.type()));
				
				Color.ColorVector vector = Color.calculateVector(colors);
				Color arrowColor = color;
				if (settings.slopeFieldMagnitude())
					arrowColor = Color.fromHSV(0, 0, (float) (vector.magnitude() / Color.ColorVector.MAX_MAGNITUDE / 2 + 0.5));
				drawArrow(output, x * 5, y * 5, arrowColor, vector.angle());
			}
		}
		return output;
	}
	private static void drawArrow(BufferedImage output, int x, int y, Color color, double angle) {
		output.setRGB(x + 2, y + 2, color.getRGB());
		if (angle < 0)
			angle += Math.PI;
		int angleCategory = (int) (angle / Math.PI * 12 + 0.5) % 12;
		if (angleCategory == 0) {
			output.setRGB(x, y + 2, color.getRGB());
			output.setRGB(x + 4, y + 2, color.getRGB());
		}
		if (angleCategory == 0 || angleCategory == 1 || angleCategory == 11) {
			output.setRGB(x + 1, y + 2, color.getRGB());
			output.setRGB(x + 3, y + 2, color.getRGB());
		}
		if (angleCategory == 1 || angleCategory == 2) {
			output.setRGB(x, y + 3, color.getRGB());
			output.setRGB(x + 4, y + 1, color.getRGB());
		}
		if (angleCategory == 2 || angleCategory == 3 || angleCategory == 4) {
			output.setRGB(x + 1, y + 3, color.getRGB());
			output.setRGB(x + 3, y + 1, color.getRGB());
		}
		if (angleCategory == 3) {
			output.setRGB(x, y + 4, color.getRGB());
			output.setRGB(x + 4, y, color.getRGB());
		}
		if (angleCategory == 4 || angleCategory == 5) {
			output.setRGB(x + 3, y, color.getRGB());
			output.setRGB(x + 1, y + 4, color.getRGB());
		}
		if (angleCategory == 5 || angleCategory == 6 || angleCategory == 7) {
			output.setRGB(x + 2, y + 1, color.getRGB());
			output.setRGB(x + 2, y + 3, color.getRGB());
		}
		if (angleCategory == 6) {
			output.setRGB(x + 2, y, color.getRGB());
			output.setRGB(x + 2, y + 4, color.getRGB());
		}
		if (angleCategory == 7 || angleCategory == 8) {
			output.setRGB(x + 1, y, color.getRGB());
			output.setRGB(x + 3, y + 4, color.getRGB());
		}
		if (angleCategory == 8 || angleCategory == 9 || angleCategory == 10) {
			output.setRGB(x + 1, y + 1, color.getRGB());
			output.setRGB(x + 3, y + 3, color.getRGB());
		}
		if (angleCategory == 9) {
			output.setRGB(x, y, color.getRGB());
			output.setRGB(x + 4, y + 4, color.getRGB());
		}
		if (angleCategory == 10 || angleCategory == 11) {
			output.setRGB(x, y + 1, color.getRGB());
			output.setRGB(x + 4, y + 3, color.getRGB());
		}
	}
	
	public static BufferedImage derivative2(BufferedImage img, ImageSettings settings) {
		int[][][] colors = new int[img.getWidth()][img.getHeight()][3];
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				Color color = Color.fromRGB(img.getRGB(x, y));
				if (settings.type() == Color.Type.RGB) {
					colors[x][y][0] = color.getRed();
					colors[x][y][1] = color.getGreen();
					colors[x][y][2] = color.getBlue();
				} else {
					colors[x][y][0] = (int) (color.getHue() * 255);
					colors[x][y][1] = (int) (color.getSaturation() * 255);
					colors[x][y][2] = (int) (color.getValue() * 255);
				}
			}
		}
		
		int[][][] newColors = new int[img.getWidth()][img.getHeight()][3];
		int centerX = img.getWidth() / 2;
		int centerY = img.getHeight() / 2;
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				for (int channel = 0; channel < 3; channel++) {
					int value = colors[x][y][channel];
					if (x == centerX && y == centerY)
						newColors[x][y][channel] = value - 127;
					else if (x == centerX)
						newColors[x][y][channel] = value - colors[x][y + (y > centerY ? -1 : 1)][channel];
					else if (y == centerY)
						newColors[x][y][channel] = value - colors[x + (x > centerX ? -1 : 1)][y][channel];
					else {
						int xOffset = (x > centerX ? -1 : 1);
						int yOffset = (y > centerY ? -1 : 1);
						newColors[x][y][channel] = value
								- colors[x + xOffset][y][channel]
								- colors[x][y + yOffset][channel]
								+ colors[x + xOffset][y + yOffset][channel];
					}
				}
			}
		}
		colors = newColors;
		
		BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				for (int channel = 0; channel < 3; channel++)
					colors[x][y][channel] = colors[x][y][channel] / 2 + 127;
				Color color;
				if (settings.type() == Color.Type.RGB)
					color = Color.fromRGB(colors[x][y][0], colors[x][y][1], colors[x][y][2]);
				else
					color = Color.fromHSV(colors[x][y][0] / 255.0F, colors[x][y][1] / 255.0F, colors[x][y][2] / 255.0F);
				output.setRGB(x, y, color.getRGB());
			}
		}
		
		return output;
	}
	
	public static BufferedImage integrate(BufferedImage img, ImageSettings settings) {
		int[][][] colors = new int[img.getWidth()][img.getHeight()][3];
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				Color color = Color.fromRGB(img.getRGB(x, y));
				if (settings.type() == Color.Type.RGB) {
					colors[x][y][0] = color.getRed();
					colors[x][y][1] = color.getGreen();
					colors[x][y][2] = color.getBlue();
				} else {
					colors[x][y][0] = (int) (color.getHue() * 255);
					colors[x][y][1] = (int) (color.getSaturation() * 255);
					colors[x][y][2] = (int) (color.getValue() * 255);
				}
				for (int channel = 0; channel < 3; channel++)
					colors[x][y][channel] = (colors[x][y][channel] - 127) * 2;
			}
		}
		
		accumulate(colors, img.getWidth() / 2, img.getWidth() - 1, img.getHeight(), false, settings);
		accumulate(colors, img.getWidth() / 2, 0, img.getHeight(), false, settings);
		accumulate(colors, img.getHeight() / 2, img.getHeight() - 1, img.getWidth(), true, settings);
		accumulate(colors, img.getHeight() / 2, 0, img.getWidth(), true, settings);
		
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		if (settings.rescale()) {
			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					for (int channel = 0; channel < 3; channel++) {
						int color = colors[x][y][channel];
						if (color < min)
							min = color;
						if (color > max)
							max = color;
					}
				}
			}
			System.out.println("Rescale range: [" + min + ", " + max + "] -> [0, 255]");
		}
		
		BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				for (int channel = 0; channel < 3; channel++)
					colors[x][y][channel] += 127;
				if (settings.rescale()) {
					for (int channel = 0; channel < 3; channel++)
						colors[x][y][channel] = (colors[x][y][channel] - min) * 255 / (max - min);
				}
				Color color;
				if (settings.type() == Color.Type.RGB)
					color = Color.fromRGB(colors[x][y][0], colors[x][y][1], colors[x][y][2]);
				else
					color = Color.fromHSV(colors[x][y][0] / 255.0F, colors[x][y][1] / 255.0F, colors[x][y][2] / 255.0F);
				output.setRGB(x, y, color.getRGB());
			}
		}
		
		return output;
	}
	private static void accumulate(int[][][] output, int center, int edge, int rows, boolean yDir, ImageSettings settings) {
		int dir = (edge > center ? 1 : -1);
		for (int i = center + dir; edge > center ? i <= edge : i >= edge;) {
			for (int row = 0; row < rows; row++) {
				int x = yDir ? row : i;
				int y = yDir ? i : row;
				int[] baseColor = output[x + (yDir ? 0 : -dir)][y + (yDir ? -dir : 0)];
				int[] color = output[x][y];
				for (int channel = 0; channel < 3; channel++)
					color[channel] += baseColor[channel];
			}
			if (edge > center)
				i++;
			else
				i--;
		}
	}
	
	public static BufferedImage addImages(BufferedImage imgA, BufferedImage imgB, ImageSettings settings) {
		BufferedImage output = new BufferedImage(
				Math.min(imgA.getWidth(), imgB.getWidth()),
				Math.min(imgA.getHeight(), imgB.getHeight()),
				BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < output.getWidth(); x++) {
			for (int y = 0; y < output.getHeight(); y++) {
				Color colorA = Color.fromRGB(imgA.getRGB(x, y));
				Color colorB = Color.fromRGB(imgB.getRGB(x, y));
				output.setRGB(x, y, colorA.add(colorB, settings.type()).getRGB());
			}
		}
		return output;
	}
	
}
