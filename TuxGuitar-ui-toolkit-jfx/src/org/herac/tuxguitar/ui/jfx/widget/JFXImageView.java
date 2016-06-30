package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UIResizeListener;
import org.herac.tuxguitar.ui.jfx.event.JFXResizeListenerManager;
import org.herac.tuxguitar.ui.jfx.resource.JFXSnapshotImage;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UIImageView;

public class JFXImageView extends JFXNode<ImageView> implements UIImageView {
	
	private UIImage image;
	private JFXResizeListenerManager resizeListener;
	
	public JFXImageView(JFXContainer<? extends Region> parent) {
		super(new ImageView(), parent);
		
		this.resizeListener = new JFXResizeListenerManager(this);
	}
	
	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		
		this.getControl().setImage(this.image != null ? ((JFXSnapshotImage) this.image).getHandle() : null);
	}

	public UIRectangle getBounds() {
		UIRectangle bounds = new UIRectangle();
		bounds.getPosition().setX((float) this.getControl().getLayoutX());
		bounds.getPosition().setY((float) this.getControl().getLayoutY());
		bounds.getSize().setWidth((float) this.getControl().getFitWidth());
		bounds.getSize().setHeight((float) this.getControl().getFitHeight());
		
		return bounds;
	}
	
	public void setBounds(UIRectangle bounds) {
		this.getControl().setLayoutX(bounds.getX());
		this.getControl().setLayoutY(bounds.getY());
		this.getControl().setFitWidth(bounds.getWidth());
		this.getControl().setFitHeight( bounds.getHeight());
		
		this.redraw();
	}

	public void redraw() {
		this.getControl().requestFocus();
	}

	public void addResizeListener(UIResizeListener listener) {
		if( this.resizeListener.isEmpty() ) {
			this.getControl().fitWidthProperty().addListener(this.resizeListener);
			this.getControl().fitHeightProperty().addListener(this.resizeListener);
		}
		this.resizeListener.addListener(listener);
	}

	public void removeResizeListener(UIResizeListener listener) {
		this.resizeListener.removeListener(listener);
		if( this.resizeListener.isEmpty() ) {
			this.getControl().fitWidthProperty().removeListener(this.resizeListener);
			this.getControl().fitHeightProperty().removeListener(this.resizeListener);
		}
	}
}
