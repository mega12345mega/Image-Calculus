package com.luneruniverse.imagecalculus;

import java.util.Map;
import java.util.stream.Collectors;

public class Color {
	
	public enum Type {
		RGB,
		HSV
	}
	
	public enum Direction {
		UP,
		RIGHT,
		DOWN,
		LEFT
	}
	
	public static Color fromRGB(int rgb) {
		return new Color(rgb);
	}
	public static Color fromRGB(int red, int green, int blue) {
		red = Math.max(0, Math.min(255, red));
		green = Math.max(0, Math.min(255, green));
		blue = Math.max(0, Math.min(255, blue));
		return new Color((((red << 8) + green) << 8) + blue);
	}
	
	public static Color fromHSV(float[] hsv) {
		return fromHSV(hsv[0], hsv[1], hsv[2]);
	}
	public static Color fromHSV(float hue, float saturation, float value) {
		hue = Math.max(0, Math.min(1, hue));
		saturation = Math.max(0, Math.min(1, saturation));
		value = Math.max(0, Math.min(1, value));
		return new Color(java.awt.Color.HSBtoRGB(hue, saturation, value));
	}
	
	public static Color fromAverageRGB(Iterable<Color> colors) {
		int red = 0;
		int green = 0;
		int blue = 0;
		int num = 0;
		for (Color color : colors) {
			red += color.getRed();
			green += color.getGreen();
			blue += color.getBlue();
			num++;
		}
		return fromRGB(red / num, green / num, blue / num);
	}
	public static Color fromAverageHSV(Iterable<Color> colors) {
		float hue = 0;
		float saturation = 0;
		float value = 0;
		int num = 0;
		for (Color color : colors) {
			float[] hsv = color.getHSV();
			hue += hsv[0];
			saturation += hsv[1];
			value += hsv[2];
			num++;
		}
		return fromHSV(hue / num, saturation / num, value / num);
	}
	public static Color fromAverage(Iterable<Color> colors, Type type) {
		return switch (type) {
			case RGB -> fromAverageRGB(colors);
			case HSV -> fromAverageHSV(colors);
		};
	}
	
	public static record ColorVector(double magnitude, double angle) {
		public static double MAX_MAGNITUDE = Math.sqrt(255 * 255 + 255 * 255);
	}
	public static ColorVector calculateVector(Map<Direction, Color> colors) {
		Map<Direction, Float> grayColors = colors.entrySet().stream()
				.map(entry -> Map.entry(entry.getKey(), entry.getValue().getGrayscale()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		float totalX = grayColors.getOrDefault(Direction.RIGHT, 0F) - grayColors.getOrDefault(Direction.LEFT, 0F);
		float totalY = grayColors.getOrDefault(Direction.UP, 0F) - grayColors.getOrDefault(Direction.DOWN, 0F);
		return new ColorVector(Math.sqrt(totalX * totalX + totalY * totalY), Math.atan2(totalY, totalX));
	}
	
	
	private final int rgb;
	
	private Color(int rgb) {
		this.rgb = rgb;
	}
	
	public int getRGB() {
		return 0xFF000000 | rgb;
	}
	public int getRed() {
		return (rgb >> 16) & 0xFF;
	}
	public int getGreen() {
		return (rgb >> 8) & 0xFF;
	}
	public int getBlue() {
		return (rgb >> 0) & 0xFF;
	}
	
	public float[] getHSV() {
		return java.awt.Color.RGBtoHSB(getRed(), getGreen(), getBlue(), null);
	}
	public float getHue() {
		return getHSV()[0];
	}
	public float getSaturation() {
		return getHSV()[1];
	}
	public float getValue() {
		return getHSV()[2];
	}
	
	public float getGrayscale() {
		return (getRed() + getGreen() + getBlue()) / 3;
	}
	
	
	public Color slopeRGB(Color other) {
		return Color.fromRGB(
				(getRed() - other.getRed()) / 2 + 127,
				(getGreen() - other.getGreen()) / 2 + 127,
				(getBlue() - other.getBlue()) / 2 + 127);
	}
	public Color slopeHSV(Color other) {
		float[] hsv = getHSV();
		float[] hsvOther = other.getHSV();
		return Color.fromHSV(
				(hsv[0] - hsvOther[0]) / 2 + 0.5F,
				(hsv[1] - hsvOther[1]) / 2 + 0.5F,
				(hsv[2] - hsvOther[2]) / 2 + 0.5F);
	}
	public Color slope(Color other, Type type) {
		return switch (type) {
			case RGB -> slopeRGB(other);
			case HSV -> slopeHSV(other);
		};
	}
	
	public Color addRGB(Color other) {
		return Color.fromRGB(
				getRed() + (other.getRed() - 127) * 2,
				getGreen() + (other.getGreen() - 127) * 2,
				getBlue() + (other.getBlue() - 127) * 2);
	}
	public Color addHSV(Color other) {
		return Color.fromHSV(
				getHue() + other.getHue() * 2 - 1,
				getSaturation() + other.getSaturation() * 2 - 1,
				getValue() + other.getValue() * 2 - 1);
	}
	public Color add(Color other, Type type) {
		return switch (type) {
			case RGB -> addRGB(other);
			case HSV -> addHSV(other);
		};
	}
	
}
