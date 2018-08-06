package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.event.UIMouseDragListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UIMouseWheelEvent;
import org.herac.tuxguitar.ui.event.UIMouseWheelListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.swt.resource.SWTPainter;
import org.herac.tuxguitar.ui.widget.UIKnob;

public class SWTCustomKnob extends SWTControl<Composite> implements UIKnob, UIMouseDragListener, UIMouseUpListener, UIMouseWheelListener, PaintListener {
	 
	private static final int DEFAULT_MAXIMUM = 100;
	private static final int DEFAULT_MINIMUM = 0;
	private static final int DEFAULT_INCREMENT = 1;
	private static final float DEFAULT_PACKED_WIDTH = 32f;
	private static final float DEFAULT_PACKED_HEIGHT = 32f;
	
	private static final float MARGIN = 2;
	
	private int maximum;
	private int minimum;
	private int increment;
	private int value;
	private float lastDragY;
	private UISelectionListenerManager selectionHandler;
	
	public SWTCustomKnob(SWTContainer<? extends Composite> parent) {
		super(new Composite(parent.getControl(), SWT.DOUBLE_BUFFERED), parent);
		
		this.maximum = DEFAULT_MAXIMUM;
		this.minimum = DEFAULT_MINIMUM;
		this.increment = DEFAULT_INCREMENT;
		this.selectionHandler = new UISelectionListenerManager();
		this.addMouseUpListener(this);
		this.addMouseDragListener(this);
		this.addMouseWheelListener(this);
		this.getControl().addPaintListener(this);
	}

	public int getValue(){
		return this.value;
	}
	
	public void setValue(int value){
		if( this.value != value ){
			this.value = value;
			this.invalidate();
			this.fireSelectionEvent();
		}
	}
	
	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		if( this.maximum != maximum ){
			if( this.minimum > maximum ) {
				this.minimum = maximum;
			}
			if( this.value > maximum ) {
				this.value = maximum;
			}
			this.maximum = maximum;
			this.invalidate();
		}
	}

	public int getMinimum() {
		return minimum;
	}

	public void setMinimum(int minimum) {
		if( this.minimum != minimum ){
			if( this.maximum < minimum ) {
				this.maximum = minimum;
			}
			if( this.value < minimum ) {
				this.value = minimum;
			}
			this.minimum = minimum;
			this.invalidate();
		}
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
		this.invalidate();
	}

	public void addSelectionListener(UISelectionListener listener) {
		this.selectionHandler.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionHandler.removeListener(listener);
	}
	
	public void fireSelectionEvent() {
		if(!this.isIgnoreEvents()) {
			this.selectionHandler.onSelect(new UISelectionEvent(this));
		}
	}
	
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		super.computePackedSize(fixedWidth != null ? fixedWidth : DEFAULT_PACKED_WIDTH, fixedHeight != null ? fixedHeight : DEFAULT_PACKED_HEIGHT);
	}
	
	public void invalidate() {
		this.getControl().redraw();
	}

	public void paintControl(PaintEvent e) {
		Rectangle area = this.getControl().getClientArea();
		
		// knob
		float ovalSize = (Math.min(area.width, area.height) - MARGIN);
		float x = area.x + (area.width  / 2f);
		float y = area.y + (area.height / 2f);
		
		// value
		float value = (this.value - this.minimum);
		float maximum = (this.maximum - this.minimum);
		float percent = (0.5f + (value > 0 && maximum > 0 ? ((value / maximum) * 1.5f) : 0f));
		float valueSize = (ovalSize / 10f);
		float valueX = (x +  Math.round((ovalSize / 3f) * Math.cos(Math.PI * percent)));
		float valueY = (y + Math.round((ovalSize / 3f) * Math.sin(Math.PI * percent)));
		
		UIPainter uiPainter = new SWTPainter(e.gc);
		uiPainter.initPath(UIPainter.PATH_DRAW);
		uiPainter.moveTo(x, y);
		uiPainter.addCircle(y, y, ovalSize);
		uiPainter.closePath();
		
		uiPainter.initPath(UIPainter.PATH_DRAW);
		uiPainter.moveTo(valueX, valueY);
		uiPainter.addCircle(valueX, valueY, valueSize);
		uiPainter.closePath();
	}
	
	public void onMouseWheel(UIMouseWheelEvent event) {
		this.setValue(Math.round(Math.max(Math.min(this.value + (Math.signum(event.getValue()) * this.increment), this.maximum), this.minimum)));
	}
	
	public void onMouseDrag(UIMouseEvent event) {
		float dragY = event.getPosition().getY();
		float move = (this.lastDragY - dragY);
		this.lastDragY = dragY;
		this.setValue(Math.round(Math.max(Math.min(this.value + (move * this.increment), this.maximum), this.minimum)));
	}
	
	public void onMouseUp(UIMouseEvent event) {
		this.lastDragY = 0f;
	}
}
