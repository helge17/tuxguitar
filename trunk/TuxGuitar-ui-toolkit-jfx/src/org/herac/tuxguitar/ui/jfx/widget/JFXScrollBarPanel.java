package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.Region;

public class JFXScrollBarPanel extends JFXPanel implements UIScrollBarPanel {
	
	private JFXScrollBar vScrollBar;
	private JFXScrollBar hScrollBar;
	
	public JFXScrollBarPanel(JFXContainer<? extends Region> parent, boolean vScroll, boolean hScroll, boolean bordered) {
		super(parent, bordered);
		
		if( vScroll ) {
			this.vScrollBar = this.createScrollBar(Orientation.VERTICAL);
		}
		if( hScroll ) {
			this.hScrollBar = this.createScrollBar(Orientation.HORIZONTAL);
		}
	}
	
	public UIScrollBar getVScroll() {
		return this.vScrollBar;
	}
	
	public UIScrollBar getHScroll() {
		return this.hScrollBar;
	}
	
	public JFXScrollBar createScrollBar(Orientation orientation) {
		JFXScrollBar jfxScrollBar = new JFXScrollBar(null, orientation);
		this.getControl().getChildren().add(jfxScrollBar.getControl());
		jfxScrollBar.getControl().applyCss();
		
		return jfxScrollBar;
	}
	
	public Insets getPadding() {
		Insets padding = super.getPadding();
		double top = padding.getTop();
		double left = padding.getTop();
		double right = padding.getTop();
		double bottom = padding.getTop();
		if( this.vScrollBar != null ) {
			right += (this.vScrollBar.getPackedSize().getWidth());
		}
		if( this.hScrollBar != null ) {
			bottom += (this.hScrollBar.getPackedSize().getHeight());
		}
		return new Insets(top, right, bottom, left);
	}
	
	@Override
	public void computePackedSize() {
		if( this.vScrollBar != null ) {
			this.vScrollBar.computePackedSize();
		}
		if( this.hScrollBar != null ) {
			this.hScrollBar.computePackedSize();
		}
		
		super.computePackedSize();
	}
	
	@Override
	public void setBounds(UIRectangle bounds) {		
		UIRectangle childArea = this.getChildArea(bounds.getSize());
		if( this.vScrollBar != null ) {
			this.vScrollBar.setBounds(new UIRectangle((childArea.getX() + childArea.getWidth()), childArea.getY(), this.vScrollBar.getPackedSize().getWidth(), childArea.getHeight()));
			this.vScrollBar.updateClippingArea(new UIRectangle(bounds.getSize()));
		}
		if( this.hScrollBar != null ) {
			this.hScrollBar.setBounds(new UIRectangle(childArea.getX(), (childArea.getY() + childArea.getHeight()), childArea.getWidth(), this.hScrollBar.getPackedSize().getHeight()));
			this.hScrollBar.updateClippingArea(new UIRectangle(bounds.getSize()));
		}
		
		super.setBounds(bounds);
		
		this.updateChildrenClippingArea(childArea);
	}
	
	public void updateChildrenClippingArea(UIRectangle childArea) {
		for(UIControl uiControl : this.getChildren()) {
			JFXNode<?> jfxNode = (JFXNode<?>) uiControl;
			jfxNode.updateClippingArea(childArea);
		}
	}
}
