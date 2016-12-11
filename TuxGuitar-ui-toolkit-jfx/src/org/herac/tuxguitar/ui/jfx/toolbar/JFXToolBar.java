package org.herac.tuxguitar.ui.jfx.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.jfx.widget.JFXContainer;
import org.herac.tuxguitar.ui.jfx.widget.JFXControl;
import org.herac.tuxguitar.ui.jfx.widget.JFXNode;
import org.herac.tuxguitar.ui.layout.UILayout;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolActionMenuItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;
import org.herac.tuxguitar.ui.toolbar.UIToolCustomItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;

public class JFXToolBar extends JFXControl<ToolBar> implements JFXContainer<ToolBar>, UILayoutContainer, UIToolBar {
	
	private UILayout layout;
	private UISize packedContentSize;
	private List<JFXNode<? extends Node>>  toolItems;
	
	public JFXToolBar(JFXContainer<? extends Region> container, Orientation orientation) {
		super(new ToolBar(), container);
		
		this.layout = new UITableLayout(0f);
		this.packedContentSize = new UISize();
		this.toolItems = new ArrayList<JFXNode<? extends Node>>();
		
		this.getControl().setOrientation(orientation);
	}
	
	public UIComponent createSeparator() {
		return new JFXToolSeparatorItem(this);
	}
	
	public UIToolActionItem createActionItem() {
		return new JFXToolActionItem(this);
	}
	
	public UIToolCheckableItem createCheckItem() {
		return new JFXToolCheckableItem(this);
	}
	
	public UIToolMenuItem createMenuItem() {
		return new JFXToolMenuItem(this);
	}
	
	public UIToolActionMenuItem createActionMenuItem() {
		return new JFXToolActionMenuItem(this);
	}
	
	public UIToolCustomItem createCustomItem() {
		return new JFXToolCustomItem(this);
	}
	
	public void addChild(JFXNode<? extends Node> uiControl) {
		if(!this.toolItems.contains(uiControl)) { 
			this.toolItems.add(uiControl);
			this.setLayoutDefaults(uiControl);
			this.getControl().getItems().add(uiControl.getControl());
		}
	}
	
	public void removeChild(JFXNode<? extends Node> uiControl) {
		if( this.toolItems.contains(uiControl)) { 
			this.toolItems.remove(uiControl);
			this.getControl().getItems().remove(uiControl.getControl());
		}
	}
	
	public List<UIControl> getChildren() {
		List<JFXNode<? extends Node>> children = new ArrayList<JFXNode<? extends Node>>(this.toolItems);
		for(JFXNode<? extends Node> child : children) {
			if( child.isDisposed()) {
				this.removeChild(child);
			}
		}
		return new ArrayList<UIControl>(this.toolItems);
	}
	
	public void dispose() {
		List<JFXNode<? extends Node>> children = new ArrayList<JFXNode<? extends Node>>(this.toolItems);
		for(JFXNode<? extends Node> child : children) {
			if(!child.isDisposed()) {
				child.dispose();
			}
		}
		super.dispose();
	}
	
	public void setLayoutDefaults(UIControl uiControl) {
		this.layout.set(uiControl, UITableLayout.FILL_X, false);
		this.layout.set(uiControl, UITableLayout.FILL_Y, false);
		this.layout.set(uiControl, UITableLayout.ALIGN_X, UITableLayout.ALIGN_FILL);
		this.layout.set(uiControl, UITableLayout.ALIGN_Y, UITableLayout.ALIGN_FILL);
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
	
	public UIRectangle getChildArea() {
		Insets padding = this.getPadding();
		UISize size = this.getBounds().getSize();
		UIRectangle area = new UIRectangle();
		area.getSize().setWidth(Math.max(0f, (float) (size.getWidth() - (padding.getLeft() + padding.getRight()))));
		area.getSize().setHeight(Math.max(0f, (float) (size.getHeight() - (padding.getTop() + padding.getBottom()))));
		
		return area;
	}
	
	public void computeLayoutPositions() {
		Orientation orientation = this.getControl().getOrientation();
		
		int position = 1;
		for(UIControl uiControl : this.getChildren()) {
			uiControl.computePackedSize();
			
			this.layout.set(uiControl, UITableLayout.ROW, (Orientation.HORIZONTAL.equals(orientation) ? 1 : position ++));
			this.layout.set(uiControl, UITableLayout.COL, (Orientation.VERTICAL.equals(orientation) ? 1 : position ++));
		}
	}
	
	public void computePackedSize() {
		this.computeLayoutPositions();
		
		for(UIControl uiControl : this.getChildren()) {
			uiControl.computePackedSize();
		}
		
		Insets padding = this.getPadding();
		
		UISize packedSize = new UISize();
		UISize packedContentSize = this.layout.computePackedSize(this);
		
		packedSize.setWidth((float) (padding.getLeft() + padding.getRight() + packedContentSize.getWidth()));
		packedSize.setHeight((float) (padding.getTop() + padding.getBottom() + packedContentSize.getHeight()));
		
		this.setPackedSize(packedSize);
		this.setPackedContentSize(packedContentSize);
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
		this.computePackedSize();
		this.setBounds(bounds);
	}

	public void pack() {
		this.computePackedSize();
		this.setBounds(new UIRectangle(this.getBounds().getPosition(), this.getPackedSize()));
	}

	public String getFillAttributeName() {
		return (Orientation.HORIZONTAL.equals(this.getControl().getOrientation()) ? UITableLayout.FILL_X : UITableLayout.FILL_Y);
	}
	
	public String getPackedWidthAttributeName() {
		return (Orientation.HORIZONTAL.equals(this.getControl().getOrientation()) ? UITableLayout.PACKED_WIDTH : UITableLayout.PACKED_HEIGHT);
	}
	
	public String getMinimumPackedWidthAttributeName() {
		return (Orientation.HORIZONTAL.equals(this.getControl().getOrientation()) ? UITableLayout.MINIMUM_PACKED_WIDTH : UITableLayout.MINIMUM_PACKED_HEIGHT);
	}
	
	public String getMaximumPackedWidthAttributeName() {
		return (Orientation.HORIZONTAL.equals(this.getControl().getOrientation()) ? UITableLayout.MAXIMUM_PACKED_WIDTH : UITableLayout.MAXIMUM_PACKED_HEIGHT);
	}
}
