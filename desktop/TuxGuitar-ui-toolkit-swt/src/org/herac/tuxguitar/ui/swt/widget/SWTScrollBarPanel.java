package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.swt.util.SWTBackgroundPainter;
import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;

public class SWTScrollBarPanel extends SWTLayoutContainer<Composite> implements UIScrollBarPanel {
	
	private SWTScrollBar vScrollBar;
	private SWTScrollBar hScrollBar;
	private SWTBackgroundPainter backgroundPainter;
	
	public SWTScrollBarPanel(SWTContainer<? extends Composite> parent, boolean vScroll, boolean hScroll, boolean bordered) {
		super(new Composite(parent.getControl(), (vScroll ? SWT.V_SCROLL : 0) | (hScroll ? SWT.H_SCROLL : 0) | (bordered ? SWT.BORDER : 0)), parent);
		
		this.vScrollBar = (vScroll ? new SWTScrollBar(this.getControl().getVerticalBar()) : null);
		this.hScrollBar = (hScroll ? new SWTScrollBar(this.getControl().getHorizontalBar()) : null);
		this.backgroundPainter = new SWTBackgroundPainter(this);
	}

	public UIScrollBar getVScroll() {
		return this.vScrollBar;
	}
	
	public UIScrollBar getHScroll() {
		return this.hScrollBar;
	}
	
	public void setBgColor(UIColor color) {
		super.setBgColor(color);
		
		this.backgroundPainter.update();
	}
}
