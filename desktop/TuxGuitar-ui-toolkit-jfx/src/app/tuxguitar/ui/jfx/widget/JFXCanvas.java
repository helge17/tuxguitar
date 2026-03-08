package app.tuxguitar.ui.jfx.widget;

import app.tuxguitar.ui.event.UIPaintListener;
import app.tuxguitar.ui.event.UIResizeListener;
import app.tuxguitar.ui.jfx.event.JFXPaintListenerManager;
import app.tuxguitar.ui.jfx.event.JFXPaintListenerManagerAsync;
import app.tuxguitar.ui.jfx.event.JFXResizeListenerManager;
import app.tuxguitar.ui.jfx.resource.JFXPainter;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UICanvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Region;
import javafx.scene.control.Tooltip;

public class JFXCanvas extends JFXNode<Canvas> implements UICanvas {

	private JFXPaintListenerManager paintListener;
	private JFXResizeListenerManager resizeListener;
	private Tooltip tooltip;

	public JFXCanvas(JFXContainer<? extends Region> parent, boolean bordered) {
		super(new Canvas(), parent);
		this.paintListener = new JFXPaintListenerManagerAsync(this);
		this.resizeListener = new JFXResizeListenerManager(this);
	}

	public UIPainter createPainter() {
		JFXPainter jfxPainter = new JFXPainter(this.getControl().getGraphicsContext2D());
		jfxPainter.clearArea(0f, 0f, (float) this.getControl().getWidth(), (float) this.getControl().getHeight());

		return jfxPainter;
	}

	public UIRectangle getBounds() {
		UIRectangle bounds = new UIRectangle();
		bounds.getPosition().setX((float) this.getControl().getLayoutX());
		bounds.getPosition().setY((float) this.getControl().getLayoutY());
		bounds.getSize().setWidth((float) this.getControl().getWidth());
		bounds.getSize().setHeight((float) this.getControl().getHeight());

		return bounds;
	}

	public void setBounds(UIRectangle bounds) {
		this.getControl().setLayoutX(bounds.getX());
		this.getControl().setLayoutY(bounds.getY());
		this.getControl().setWidth(bounds.getWidth());
		this.getControl().setHeight( bounds.getHeight());

		this.redraw();
	}

	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		UISize packedSize = this.getPackedSize();
		if( fixedWidth != null ) {
			packedSize.setWidth(fixedWidth);
		}
		if( fixedHeight != null ) {
			packedSize.setHeight(fixedHeight);
		}
		this.setPackedSize(packedSize);
	}

	public void redraw() {
		this.paintListener.fireEvent();
	}

	public void addPaintListener(UIPaintListener listener) {
		this.paintListener.addListener(listener);
	}

	public void removePaintListener(UIPaintListener listener) {
		this.paintListener.removeListener(listener);
	}

	public void addResizeListener(UIResizeListener listener) {
		if( this.resizeListener.isEmpty() ) {
			this.getControl().widthProperty().addListener(this.resizeListener);
			this.getControl().heightProperty().addListener(this.resizeListener);
		}
		this.resizeListener.addListener(listener);
	}

	public void removeResizeListener(UIResizeListener listener) {
		this.resizeListener.removeListener(listener);
		if( this.resizeListener.isEmpty() ) {
			this.getControl().widthProperty().removeListener(this.resizeListener);
			this.getControl().heightProperty().removeListener(this.resizeListener);
		}
	}

	public void setToolTipText(String text) {
		if( text == null || text.isEmpty() ) {
			Tooltip.uninstall(this.getControl(), tooltip);
		} else {
			tooltip = new Tooltip(text);
			Tooltip.install(this.getControl(), tooltip);
		}
	}
}
