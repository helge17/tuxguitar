package app.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIPainter;

/**
 * Headless painter: records every drawing call as one line ("moveTo 1.0 2.0"),
 * with fixed font metrics (each character is 4 units wide).
 */
public class RecordingPainter implements UIPainter {

	public static final float FM_TOP_LINE = 2f;
	public static final float CHAR_WIDTH = 4f;

	private final List<String> calls = new ArrayList<String>();

	public List<String> calls() {
		return this.calls;
	}

	public List<String> callsStartingWith(String prefix) {
		return this.calls.stream().filter(call -> call.startsWith(prefix)).collect(Collectors.toList());
	}

	// the calls from the given one on, joined by the separator
	public String joined(int from, String separator) {
		return String.join(separator, this.calls.subList(from, this.calls.size()));
	}

	public void clear() {
		this.calls.clear();
	}

	private void record(String name, Object... args) {
		StringBuilder call = new StringBuilder(name);
		for (Object arg : args) {
			call.append(' ').append(arg);
		}
		this.calls.add(call.toString());
	}

	public void initPath(int style) { record("initPath", style); }
	public void initPath() { record("initPath"); }
	public void closePath() { record("closePath"); }
	public void drawString(String string, float x, float y) { record("drawString", "'" + string + "'", x, y); }
	public void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		record("drawImage", destX, destY, destWidth, destHeight);
	}
	public void drawImage(UIImage image, float x, float y) { record("drawImage", x, y); }
	public void cubicTo(float xc1, float yc1, float xc2, float yc2, float x1, float y1) { record("cubicTo", xc1, yc1, xc2, yc2, x1, y1); }
	public void lineTo(float x, float y) { record("lineTo", x, y); }
	public void moveTo(float x, float y) { record("moveTo", x, y); }
	public void addCircle(float x, float y, float width) { record("addCircle", x, y, width); }
	public void addRectangle(float x, float y, float width, float height) { record("addRectangle", x, y, width, height); }
	public void setFont(UIFont font) { record("setFont", font); }
	public void setForeground(UIColor color) { record("setForeground", color); }
	public void setBackground(UIColor color) { record("setBackground", color); }
	public void setLineWidth(float lineWidth) { record("setLineWidth", lineWidth); }
	public void setLineStyleSolid() { record("setLineStyleSolid"); }
	public void setLineStyleDot() { record("setLineStyleDot"); }
	public void setLineStyleDash() { record("setLineStyleDash"); }
	public void setLineStyleDashDot() { record("setLineStyleDashDot"); }
	public void setAlpha(int alpha) { record("setAlpha", alpha); }
	public void setAntialias(boolean enabled) { record("setAntialias", enabled); }
	public float getFontSize() { return 8f; }
	public float getFMBaseLine() { return 6f; }
	public float getFMTopLine() { return FM_TOP_LINE; }
	public float getFMMiddleLine() { return 4f; }
	public float getFMHeight() { return 8f; }
	public float getFMWidth(String text) { return (text.length() * CHAR_WIDTH); }
	public void dispose() { }
	public boolean isDisposed() { return false; }
}
