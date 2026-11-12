package app.tuxguitar.graphics.control;

import java.io.InputStream;

import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontAlignment;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIResourceFactory;

/** Headless resources: fonts, colors and images that only remember how they were made. */
public class FakeResourceFactory implements UIResourceFactory {

	public UIColor createColor(int red, int green, int blue) {
		return new FakeColor(red, green, blue);
	}

	public UIColor createColor(UIColorModel model) {
		return new FakeColor(model.getRed(), model.getGreen(), model.getBlue());
	}

	public UIFont createFont(String name, float height, boolean bold, boolean italic) {
		return new FakeFont(name, height, bold, italic, null);
	}

	public UIFont createFont(UIFontModel model) {
		return new FakeFont(model.getName(), model.getHeight(), model.isBold(), model.isItalic(), model.getAlignment());
	}

	public UIImage createImage(float width, float height) {
		return new FakeImage(width, height);
	}

	public UIImage createImage(InputStream inputStream) {
		return new FakeImage(0, 0);
	}

	private static abstract class FakeResource {
		private boolean disposed;

		public void dispose() { this.disposed = true; }
		public boolean isDisposed() { return this.disposed; }
	}

	private static class FakeColor extends FakeResource implements UIColor {
		private final int red, green, blue;

		FakeColor(int red, int green, int blue) { this.red = red; this.green = green; this.blue = blue; }
		public int getRed() { return this.red; }
		public int getGreen() { return this.green; }
		public int getBlue() { return this.blue; }
		public String toString() { return "rgb(" + this.red + "," + this.green + "," + this.blue + ")"; }
	}

	private static class FakeFont extends FakeResource implements UIFont {
		private final String name;
		private final float height;
		private final boolean bold, italic;
		private final UIFontAlignment alignment;

		FakeFont(String name, float height, boolean bold, boolean italic, UIFontAlignment alignment) {
			this.name = name; this.height = height; this.bold = bold; this.italic = italic; this.alignment = alignment;
		}
		public String getName() { return this.name; }
		public float getHeight() { return this.height; }
		public boolean isBold() { return this.bold; }
		public boolean isItalic() { return this.italic; }
		public String toString() {
			return "font(" + this.height + (this.bold ? ",bold" : "") + (this.italic ? ",italic" : "") + aligned() + ")";
		}
		// the alignment (top/middle/bottom) of a font made from a model that has one
		private String aligned() {
			return (this.alignment == null ? "" : ",aligned " + this.alignment.getTop() + "/" + this.alignment.getMiddle() + "/" + this.alignment.getBottom());
		}
	}

	private static class FakeImage extends FakeResource implements UIImage {
		private final float width, height;
		private final RecordingPainter painter = new RecordingPainter();

		FakeImage(float width, float height) { this.width = width; this.height = height; }
		public float getWidth() { return this.width; }
		public float getHeight() { return this.height; }
		// every painter of the image records into the same calls
		public UIPainter createPainter() { return this.painter; }
		public String toString() { return "image(" + this.width + "x" + this.height + ")"; }
	}
}
