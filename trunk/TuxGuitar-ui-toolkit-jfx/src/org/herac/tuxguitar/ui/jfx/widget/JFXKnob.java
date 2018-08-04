package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.event.UIMouseDragListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UIMouseWheelEvent;
import org.herac.tuxguitar.ui.event.UIMouseWheelListener;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIKnob;

import javafx.scene.layout.Region;

public class JFXKnob extends JFXCanvas implements UIKnob, UIMouseDragListener, UIMouseUpListener, UIMouseWheelListener, UIPaintListener {
	
	private static final int DEFAULT_MAXIMUM = 100;
	private static final int DEFAULT_MINIMUM = 0;
	private static final int DEFAULT_INCREMENT = 1;
	private static final int DEFAULT_PACKED_WIDTH = 32;
	private static final int DEFAULT_PACKED_HEIGHT = 32;
	
	private static final float MARGIN = 4;
	
	private int maximum;
	private int minimum;
	private int increment;
	private int value;
	private float lastDragY;
	private UISelectionListenerManager selectionHandler;
	
	public JFXKnob(JFXContainer<? extends Region> parent) {
		super(parent, false);
		
		this.maximum = DEFAULT_MAXIMUM;
		this.minimum = DEFAULT_MINIMUM;
		this.increment = DEFAULT_INCREMENT;
		this.selectionHandler = new UISelectionListenerManager();
		this.addPaintListener(this);
		this.addMouseUpListener(this);
		this.addMouseDragListener(this);
		this.addMouseWheelListener(this);
	}

	public int getValue(){
		return this.value;
	}
	
	public void setValue(int value){
		if( this.value != value ){
			this.value = value;
			this.redraw();
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
			this.redraw();
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
			this.redraw();
		}
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
		this.redraw();
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
	
	public void computePackedSize() {
		this.setPackedSize(new UISize(DEFAULT_PACKED_WIDTH, DEFAULT_PACKED_HEIGHT));
	}
	
	public void onPaint(UIPaintEvent event) {
		UIRectangle bounds = getBounds();
		
		// knob
		float ovalSize = (Math.min(bounds.getWidth(), bounds.getHeight()) - MARGIN);
		float x = (bounds.getWidth() / 2f);
		float y = (bounds.getHeight() / 2f);
		
		// value
		float value = (this.value - this.minimum);
		float maximum = (this.maximum - this.minimum);
		float percent = (0.5f + (value > 0 && maximum > 0 ? ((value / maximum) * 1.5f) : 0f));
		float valueSize = (ovalSize / 10f);
		float valueX = (x +  Math.round((ovalSize / 3f) * Math.cos(Math.PI * percent)));
		float valueY = (y + Math.round((ovalSize / 3f) * Math.sin(Math.PI * percent)));
		
		UIPainter uiPainter = event.getPainter();
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
