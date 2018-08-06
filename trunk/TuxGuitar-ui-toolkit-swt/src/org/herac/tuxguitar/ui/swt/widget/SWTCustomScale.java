package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseDragListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseWheelEvent;
import org.herac.tuxguitar.ui.event.UIMouseWheelListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIPosition;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.swt.resource.SWTPainter;
import org.herac.tuxguitar.ui.widget.UIScale;

public class SWTCustomScale extends SWTControl<Composite> implements UIScale, UIMouseDownListener, UIMouseDragListener, UIMouseWheelListener, PaintListener {
	
	private static final int DEFAULT_MAXIMUM = 100;
	private static final int DEFAULT_MINIMUM = 0;
	private static final int DEFAULT_INCREMENT = 1;
	private static final float DEFAULT_PACKED_WIDTH = 32f;
	private static final float DEFAULT_PACKED_HEIGHT = 32f;
	
	private static final float MARGIN = 12;
	private static final float TRACK_WIDTH = 8;
	private static final float INDICATOR_WIDTH = 12;
	
	private int maximum;
	private int minimum;
	private int increment;
	private int value;
	private boolean horizontal;
	
	private UIPosition mousePosition;
	private UISelectionListenerManager selectionHandler;
	
	public SWTCustomScale(SWTContainer<? extends Composite> parent, boolean horizontal) {
		super(new Composite(parent.getControl(), SWT.DOUBLE_BUFFERED), parent);
		
		this.horizontal = horizontal;
		this.maximum = DEFAULT_MAXIMUM;
		this.minimum = DEFAULT_MINIMUM;
		this.increment = DEFAULT_INCREMENT;
		this.mousePosition = new UIPosition();
		this.selectionHandler = new UISelectionListenerManager();
		this.addMouseDownListener(this);
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

	public UIRectangle getTrackBounds() {
		Rectangle area = this.getControl().getClientArea();
		
		UIRectangle bounds = new UIRectangle();
		bounds.getSize().setWidth(this.horizontal ? area.width - MARGIN : (Math.min(area.width - MARGIN, TRACK_WIDTH)));
		bounds.getSize().setHeight(this.horizontal ? (Math.min(area.height - MARGIN, TRACK_WIDTH)) : area.height - MARGIN);
		bounds.getPosition().setX(area.x + (this.horizontal ? (MARGIN / 2f) : (area.width / 2f) - (bounds.getWidth() - 2f)));
		bounds.getPosition().setY(area.y + (this.horizontal ? (area.height/ 2f) - (bounds.getHeight() - 2f) : (MARGIN / 2f)));
		
		return bounds;
	}
	
	public void updateValueFromPosition(UIPosition position) {
		UIRectangle bounds = this.getTrackBounds();
		
		float trackSize = (this.horizontal ? bounds.getWidth() : bounds.getHeight());
		float trackSelection = (this.horizontal ? (position.getX() - bounds.getX()) : (trackSize - (position.getY() - bounds.getY())));
		float percent = (trackSize > 0 && trackSelection > 0 ? trackSelection / trackSize : 0f);
		
		this.setValue(Math.round(Math.max(Math.min((this.maximum - this.minimum) * percent, this.maximum), this.minimum)));
	}
	
	public void paintControl(PaintEvent e) {
		UIRectangle bounds = this.getTrackBounds();
		
		// value
		float value = (this.value - this.minimum);
		float maximum = (this.maximum - this.minimum);
		float percent = (value > 0 && maximum > 0 ? (value / maximum) : 0f);
		float valueX = (bounds.getX() + (this.horizontal ? (bounds.getWidth() * percent) : (bounds.getWidth() / 2f)));
		float valueY = (bounds.getY() + (this.horizontal ? (bounds.getHeight() / 2f) : (bounds.getHeight() - (bounds.getHeight() * percent))));
		
		UIPainter uiPainter = new SWTPainter(e.gc);
		uiPainter.initPath(UIPainter.PATH_DRAW);
		uiPainter.moveTo(bounds.getX(), bounds.getY());
		uiPainter.addRectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		uiPainter.closePath();
		
		uiPainter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
		uiPainter.moveTo(valueX, valueY);
		uiPainter.addCircle(valueX, valueY, INDICATOR_WIDTH);
		uiPainter.closePath();
	}
	
	public void onMouseWheel(UIMouseWheelEvent event) {
		this.setValue(Math.round(Math.max(Math.min(this.value + (Math.signum(event.getValue()) * this.increment), this.maximum), this.minimum)));
	}
	
	public void onMouseDrag(UIMouseEvent event) {
		UIPosition uiPosition = new UIPosition();
		uiPosition.setX(this.mousePosition.getX() + event.getPosition().getX());
		uiPosition.setY(this.mousePosition.getY() + event.getPosition().getY());
		
		this.updateValueFromPosition(uiPosition);
	}
	
	public void onMouseDown(UIMouseEvent event) {
		this.mousePosition.setX(event.getPosition().getX());
		this.mousePosition.setY(event.getPosition().getY());
		this.updateValueFromPosition(this.mousePosition);
	}
}
