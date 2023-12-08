package org.herac.tuxguitar.ui.jfx.menu;

import org.herac.tuxguitar.ui.jfx.resource.JFXImage;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIKey;
import org.herac.tuxguitar.ui.resource.UIKeyCombination;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;

public class JFXMenuItem<T extends MenuItem> extends JFXEventReceiver<T> implements UIMenuItem {
	
	private UIKeyCombination keyCombination;
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
		this.getControl().setText(text);
	}

	public UIKeyCombination getKeyCombination() {
		return keyCombination;
	}

	public void setKeyCombination(UIKeyCombination keyCombination) {
		if( this.keyCombination == null || !this.keyCombination.equals(keyCombination)) {
			this.keyCombination = keyCombination;
			
			KeyCombination jfxKeyConvination = null;
			if( this.keyCombination != null ) {
				try {
					StringBuilder sb = new StringBuilder();
					for(UIKey uiKey : this.keyCombination.getKeys()) {
						if( sb.length() > 0 ) {
							sb.append("+");
						}
						if( UIKey.ALT.equals(uiKey) ) {
							sb.append(KeyCode.ALT.getName());
						} else if( UIKey.SHIFT.equals(uiKey) ) {
							sb.append(KeyCode.SHIFT.getName());
						} else if( UIKey.CONTROL.equals(uiKey) ) {
							sb.append(KeyCode.CONTROL.getName());
						} else if( UIKey.COMMAND.equals(uiKey) ) {
							sb.append(KeyCode.META.getName());
						} else {
							sb.append(uiKey.toString());
						}
					}
					jfxKeyConvination = KeyCombination.keyCombination(sb.toString());
				}catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			this.getControl().setAccelerator(jfxKeyConvination);
		}
	}

	public UIImage getImage() {
		return this.image;
	}
	
	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().setGraphic(this.image != null ? new ImageView(((JFXImage) this.image).getHandle()) : null);
	}
}
