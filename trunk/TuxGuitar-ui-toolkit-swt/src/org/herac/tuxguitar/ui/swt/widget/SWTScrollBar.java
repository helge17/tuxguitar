package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.widgets.ScrollBar;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.swt.SWTComponent;
import org.herac.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UIScrollBar;

public class SWTScrollBar extends SWTComponent<ScrollBar> implements UIScrollBar {
	
	private SWTSelectionListenerManager selectionListener;
	
	public SWTScrollBar(ScrollBar control) {
		super(control);
		
		this.selectionListener = new SWTSelectionListenerManager(this);
	}

	public void dispose() {
		this.getControl().dispose();
	}

	public boolean isDisposed() {
		return this.getControl().isDisposed();
	}
	
	public void setValue(int value) {
		this.getControl().setSelection(value);
	}

	public int getValue() {
		return this.getControl().getSelection();
	}

	public void setMaximum(int maximum) {
		this.getControl().setMaximum(maximum);
	}

	public int getMaximum() {
		return this.getControl().getMaximum();
	}

	public void setMinimum(int minimum) {
		this.getControl().setMinimum(minimum);
	}

	public int getMinimum() {
		return this.getControl().getMinimum();
	}

	public void setIncrement(int increment) {
		this.getControl().setIncrement(increment);
	}

	public int getIncrement() {
		return this.getControl().getIncrement();
	}
	
	public void setThumb(int thumb) {
		this.getControl().setThumb(thumb);
	}
	
	public int getThumb() {
		return this.getControl().getThumb();
	}
	
	public boolean isEnabled() {
		return this.getControl().isEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.getControl().setEnabled(enabled);
	}

	public boolean isVisible() {
		return this.getControl().isVisible();
	}

	public void setVisible(boolean visible) {
		this.getControl().setVisible(visible);
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
