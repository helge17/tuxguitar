package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.widgets.ScrollBar;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UIScrollBar;

public class SWTScrollBar extends SWTEventReceiver<ScrollBar> implements UIScrollBar {
	
	private Integer thumb;
	private Integer maximum;
	private Integer minimum;
	private Integer increment;
	
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
	
	public void setMaximum(int maximum) {
		this.maximum = maximum;
		this.updateRangeValues();
	}

	public int getMaximum() {
		return (this.maximum != null ? this.maximum : -1);
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
		this.updateRangeValues();
	}

	public int getMinimum() {
		return (this.minimum != null ? this.minimum : -1);
	}

	public void setIncrement(int increment) {
		this.increment = increment;
		this.updateRangeValues();
	}

	public int getIncrement() {
		return (this.increment != null ? this.increment : -1);
	}
	
	public void setThumb(int thumb) {
		this.thumb = thumb;
		this.updateRangeValues();
	}
	
	public int getThumb() {
		return (this.thumb != null ? this.thumb : -1);
	}
	
	public void updateRangeValues() {
		if( this.increment != null ) {
			this.getControl().setIncrement(this.increment);
		}
		if( this.minimum != null ) {
			this.getControl().setMinimum(this.minimum);
		}
		if( this.maximum != null ) {
			this.getControl().setMaximum(this.maximum + (this.thumb != null ? this.thumb : 0));
		}
		if( this.thumb != null ) {
			this.getControl().setThumb(this.thumb);
		}
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
