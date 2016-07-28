package org.herac.tuxguitar.ui.jfx.menu;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import org.herac.tuxguitar.ui.jfx.resource.JFXSnapshotImage;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIKeyConvination;

public class JFXMenuItem<T extends MenuItem> extends JFXEventReceiver<T> implements UIMenuItem {
	
	private UIKeyConvination keyConvination;
	private UIImage image;
	private JFXMenuItemContainer parent;
	
	public JFXMenuItem(T item, JFXMenuItemContainer parent) {
		super(item);
		
		this.parent = parent;
		this.parent.addItem(this);
	}
	
	public JFXMenuItemContainer getParent() {
		return this.parent;
	}
	
	public void dispose() {
		this.getParent().removeItem(this);
		
		super.dispose();
	}
	
	public boolean isEnabled() {
		return !this.getControl().isDisable();
	}

	public void setEnabled(boolean enabled) {
		this.getControl().setDisable(!enabled);
	}

	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		String textWithAccelerator = text;
		if( this.getKeyConvination() != null ) {
			textWithAccelerator += "\t" + this.getKeyConvination().toString() + "\u0000";
		}
		this.getControl().setText(textWithAccelerator);
	}

	public UIKeyConvination getKeyConvination() {
		return keyConvination;
	}

	public void setKeyConvination(UIKeyConvination keyConvination) {
		if( this.keyConvination == null || !this.keyConvination.equals(keyConvination)) {
			this.keyConvination = keyConvination;
			
			String text = this.getText();
			if( text != null && text.length() > 0 ) {
				this.setText(text);
			}
		}
	}

	public UIImage getImage() {
		return this.image;
	}
	
	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().setGraphic(this.image != null ? new ImageView(((JFXSnapshotImage) this.image).getHandle()) : null);
	}
}
