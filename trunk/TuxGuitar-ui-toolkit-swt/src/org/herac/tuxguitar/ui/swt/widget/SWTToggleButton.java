package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import org.herac.tuxguitar.ui.swt.resource.SWTImage;
import org.herac.tuxguitar.ui.widget.UIToggleButton;

public class SWTToggleButton extends SWTControl<Button> implements UIToggleButton {
	
	private SWTSelectionListenerManager selectionListener;
	
	private UIImage image;
	
	public SWTToggleButton(SWTContainer<? extends Composite> parent) {
		super(new Button(parent.getControl(), SWT.TOGGLE), parent);
		
		this.selectionListener = new SWTSelectionListenerManager(this);
	}

	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}
	
	public boolean isSelected() {
		return this.getControl().getSelection();
	}

	public void setSelected(boolean selected) {
		this.getControl().setSelection(selected);
	}
	
	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		
		this.getControl().setImage(this.image != null ? ((SWTImage) this.image).getHandle() : null);
	}

	public void setDefaultButton() {
		this.getControl().getShell().setDefaultButton(this.getControl());
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().addSelectionListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().removeSelectionListener(this.selectionListener);
		}
	}
}
