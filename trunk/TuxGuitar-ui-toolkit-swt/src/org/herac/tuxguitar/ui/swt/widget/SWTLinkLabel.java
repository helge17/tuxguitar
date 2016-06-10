package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.herac.tuxguitar.ui.event.UILinkListener;
import org.herac.tuxguitar.ui.swt.event.SWTLinkListenerManager;
import org.herac.tuxguitar.ui.widget.UILinkLabel;

public class SWTLinkLabel extends SWTControl<Link> implements UILinkLabel {
	
	private SWTLinkListenerManager linkListener;
	
	private Float wrapWidth;
	
	public SWTLinkLabel(SWTContainer<? extends Composite> parent) {
		super(new Link(parent.getControl(), SWT.NORMAL), parent);
		
		this.linkListener = new SWTLinkListenerManager(this);
	}
	
	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}
	
	public Float getWrapWidth() {
		return wrapWidth;
	}

	public void setWrapWidth(Float wrapWidth) {
		this.wrapWidth = wrapWidth;
	}
	
	public void computePackedSize() {
		super.computePackedSize();
		
		if( this.getWrapWidth() != null && this.getPackedSize().getWidth() > this.getWrapWidth() ) {
			this.computePackedSize(Math.round(this.getWrapWidth()), SWT.DEFAULT);
		}
	}
	
	public void addLinkListener(UILinkListener listener) {
		if( this.linkListener.isEmpty() ) {
			this.getControl().addSelectionListener(this.linkListener);
		}
		this.linkListener.addListener(listener);
	}

	public void removeLinkListener(UILinkListener listener) {
		this.linkListener.removeListener(listener);
		if( this.linkListener.isEmpty() ) {
			this.getControl().removeSelectionListener(this.linkListener);
		}
	}
}
