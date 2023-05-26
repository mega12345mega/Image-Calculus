package com.luneruniverse.imagecalculus;

public record ImageSettings(
		Color.Type type,
		boolean integral,
		boolean derivative2,
		boolean slopeField,
		boolean slopeFieldMagnitude,
		boolean rescale) {
	
}
