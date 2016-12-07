package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UISeparator;

public class SWTSeparator extends SWTControl<Label> implements UISeparator {
	
	private int orientationStyle;
	private UIRectangle bounds;
	
	public SWTSeparator(SWTContainer<? extends Composite> parent, int orientationStyle) {
		super(new Label(parent.getControl(), SWT.SEPARATOR | orientationStyle), parent);
		
		this.orientationStyle = orientationStyle;
		this.bounds = new UIRectangle();
	}
	
	public UIRectangle getBounds() {
		return this.bounds;
	}
	
	public void setBounds(UIRectangle bounds) {
		this.bounds.getPosition().setX(bounds.getX());
		this.bounds.getPosition().setY(bounds.getY());
		this.bounds.getSize().setWidth(bounds.getWidth());
		this.bounds.getSize().setHeight(bounds.getHeight());
		
		super.setBounds(this.createTargetBounds());
	}
	
	public UIRectangle createTargetBounds() {
		float x = this.bounds.getX();
		float y = this.bounds.getY();
		float width = this.bounds.getWidth();
		float height = this.bounds.getHeight();
		
		Point computedSize = this.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if((this.orientationStyle & SWT.VERTICAL) != 0) {
			x = (x + (width / 2) - (computedSize.x / 2));
			width = computedSize.x;
		} else {
			y = (y + (height / 2) - (computedSize.y / 2));
			height = computedSize.y;
		}
		
		return new UIRectangle(x, y, width, height);
	}
}
