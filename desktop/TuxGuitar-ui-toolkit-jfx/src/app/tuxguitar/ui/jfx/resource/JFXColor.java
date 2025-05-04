package app.tuxguitar.ui.jfx.resource;

import app.tuxguitar.ui.jfx.JFXComponent;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;

import javafx.scene.paint.Color;

public class JFXColor extends JFXComponent<UIColorModel> implements UIColor{

	public JFXColor(UIColorModel handle){
		super(handle);
	}

	public JFXColor(int red, int green, int blue ){
		this(new UIColorModel(red, green, blue));
	}

	public JFXColor(Color color){
		this((int) Math.round(color.getRed() * 255.0), (int) Math.round(color.getGreen() * 255.0), (int) Math.round(color.getBlue() * 255.0));
	}

	public int getRed() {
		return this.getControl().getRed();
	}

	public int getGreen() {
		return this.getControl().getGreen();
	}

	public int getBlue() {
		return this.getControl().getBlue();
	}

	public Color getHandle() {
		return this.getHandle(1d);
	}

	public Color getHandle(double opacity) {
		return Color.rgb(this.getRed(), this.getGreen(), this.getBlue(), opacity);
	}
}
