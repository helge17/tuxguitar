package app.tuxguitar.app.util;

public class TGDisplayScale {
	private static float displayScale = 1.0f;

	public static void init(float scale) {
		displayScale = scale;
	}

	public static float getDisplayScale() {
		return displayScale;
	}

	public static float scale(float value) {
		return value * displayScale;
	}

	public static int scaleInt(float value) {
		return Math.round(value * displayScale);
	}
}
