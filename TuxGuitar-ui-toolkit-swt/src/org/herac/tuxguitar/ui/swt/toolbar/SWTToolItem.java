package org.herac.tuxguitar.ui.swt.toolbar;

import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.swt.resource.SWTImage;
import org.herac.tuxguitar.ui.toolbar.UIToolItem;

public class SWTToolItem extends SWTToolControl<ToolItem> implements UIToolItem {
	
	private UIImage image;
	
	public SWTToolItem(ToolItem item, SWTToolBar parent) {
		super(item, parent);
	}
	
	public void dispose() {
		this.getParent().dispose(this);
	}
	
	public void disposeControl() {
		this.getControl().dispose();
	}

	public boolean isControlDisposed() {
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
		this.getControl().setText(text);
	}

	public String getToolTipText() {
		return this.getControl().getToolTipText();
	}

	public void setToolTipText(String text) {
		this.getControl().setToolTipText(text);
	}
	
	public UIImage getImage() {
		return this.image;
	}
	
	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().setImage(this.image != null ? ((SWTImage) this.image).getHandle() : null);
	}
}
