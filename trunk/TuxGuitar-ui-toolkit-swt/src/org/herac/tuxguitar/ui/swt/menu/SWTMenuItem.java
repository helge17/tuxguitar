package org.herac.tuxguitar.ui.swt.menu;

import org.eclipse.swt.widgets.MenuItem;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIKeyConvination;
import org.herac.tuxguitar.ui.swt.resource.SWTImage;
import org.herac.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTMenuItem extends SWTEventReceiver<MenuItem> implements UIMenuItem {
	
	private UIKeyConvination keyConvination;
	private UIImage image;
	private SWTMenu parent;
	
	public SWTMenuItem(MenuItem item, SWTMenu parent) {
		super(item);
		
		this.parent = parent;
	}
	
	public SWTMenu getParent() {
		return this.parent;
	}
	
	public void dispose() {
		this.getParent().dispose(this);
	}

	public boolean isDisposed() {
		return this.getControl().isDisposed();
	}

	public boolean isEnabled() {
		return this.getControl().isEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.getControl().setEnabled(enabled);
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
		this.getControl().setImage(this.image != null ? ((SWTImage) this.image).getHandle() : null);
	}
}
