package org.herac.tuxguitar.ui.jfx.widget;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.layout.UILayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public abstract class JFXLayoutContainer<T extends Region> extends JFXRegion<T> implements JFXContainer<T>, UILayoutContainer {
	
	private List<JFXNode<? extends Node>> children;
	private UILayout layout;
	private UISize packedContentSize;
	
	public JFXLayoutContainer(T control, JFXContainer<? extends Region> parent) {
		super(control, parent);
		
		this.children = new ArrayList<JFXNode<? extends Node>>();
		this.packedContentSize = new UISize();
	}
	
	public void dispose() {
		List<JFXNode<? extends Node>> children = new ArrayList<JFXNode<? extends Node>>(this.children);
		for(JFXNode<? extends Node> child : children) {
			if(!child.isDisposed()) {
				child.dispose();
			}
		}
		super.dispose();
	}
	
	public List<UIControl> getChildren() {
		List<JFXNode<? extends Node>> children = new ArrayList<JFXNode<? extends Node>>(this.children);
		for(JFXNode<? extends Node> child : children) {
			if( child.isDisposed()) {
				this.removeChild(child);
			}
		}
		
		return new ArrayList<UIControl>(this.children);
	}
	
	public void addChild(JFXNode<? extends Node> uiControl) {
		this.children.add(uiControl);
	}
	
	public void removeChild(JFXNode<? extends Node> uiControl) {
		this.children.remove(uiControl);
	}
	
	public UILayout getLayout() {
		return layout;
	}

	public void setLayout(UILayout layout) {
		this.layout = layout;
	}

	public void setPackedContentSize(UISize packedContentSize) {
		this.packedContentSize.setWidth(packedContentSize.getWidth());
		this.packedContentSize.setHeight(packedContentSize.getHeight());
	}

	public UISize getPackedContentSize() {
		return new UISize(this.packedContentSize.getWidth(), this.packedContentSize.getHeight());
	}
	
	public Insets getPadding() {
		return this.getControl().getPadding();
	}
	
	public UIRectangle getChildArea(UISize size) {
		Insets padding = this.getPadding();
		UIRectangle area = new UIRectangle();
		area.getSize().setWidth(Math.max(0f, (float) (size.getWidth() - (padding.getLeft() + padding.getRight()))));
		area.getSize().setHeight(Math.max(0f, (float) (size.getHeight() - (padding.getTop() + padding.getBottom()))));
		
		return area;
	}
	
	public UIRectangle getChildArea() {
		return this.getChildArea(this.getBounds().getSize());
	}

	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		if( this.layout != null ) {
			Insets padding = this.getPadding();
			
			UISize packedSize = new UISize();
			UISize packedContentSize = this.layout.computePackedSize(this);
			
			packedSize.setWidth((float) (padding.getLeft() + padding.getRight() + packedContentSize.getWidth()));
			packedSize.setHeight((float) (padding.getTop() + padding.getBottom() + packedContentSize.getHeight()));
			
			this.setPackedSize(packedSize);
			this.setPackedContentSize(packedContentSize);
		} else {
			for(UIControl uiControl : this.getChildren()) {
				uiControl.computePackedSize(null, null);
			}
		}
		
		UISize packedSize = this.getPackedSize();
		if( fixedWidth != null && fixedWidth != packedSize.getWidth() ) {
			packedSize.setWidth(fixedWidth);
		}
		if( fixedHeight != null && fixedHeight != packedSize.getHeight() ) {
			packedSize.setHeight(fixedHeight);
		}
		this.setPackedSize(packedSize);
	}

	public void setBounds(UIRectangle bounds) {
		super.setBounds(bounds);
		
		if( this.layout != null ) {
			this.layout.setBounds(this, this.getChildArea());
		}
		else {
			for(UIControl uiControl : this.getChildren()) {
				uiControl.setBounds(uiControl.getBounds());
			}
		}
	}

	public void layout() {
		this.layout(this.getBounds());
	}

	public void layout(UIRectangle bounds) {
		this.computePackedSize(null, null);
		this.setBounds(bounds);
	}

	public void pack() {
		this.computePackedSize(null, null);
		this.setBounds(new UIRectangle(this.getBounds().getPosition(), this.getPackedSize()));
	}
}
