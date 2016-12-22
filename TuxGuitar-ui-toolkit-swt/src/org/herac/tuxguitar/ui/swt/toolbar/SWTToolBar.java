package org.herac.tuxguitar.ui.swt.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.swt.widget.SWTContainer;
import org.herac.tuxguitar.ui.swt.widget.SWTControl;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolActionMenuItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;
import org.herac.tuxguitar.ui.toolbar.UIToolCustomItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class SWTToolBar extends SWTControl<ToolBar> implements UIToolBar {
	
	private boolean horizontal;
	private List<SWTToolItem>  toolItems;
	private List<SWTToolCustomItem>  toolCustomItems;
	
	public SWTToolBar(SWTContainer<? extends Composite> container, int orientation) {
		super(new ToolBar(container.getControl(), (SWT.FLAT | SWT.WRAP | orientation)), container);
		
		this.horizontal = ((orientation & SWT.HORIZONTAL) != 0);
		this.toolItems = new ArrayList<SWTToolItem>();
		this.toolCustomItems = new ArrayList<SWTToolCustomItem>();
	}
	
	public UIComponent createSeparator() {
		ToolItem toolItem = new ToolItem(this.getControl(), SWT.SEPARATOR);
		
		return this.append(new SWTToolItem(toolItem, this));
	}
	
	public UIToolActionItem createActionItem() {
		ToolItem toolItem = new ToolItem(this.getControl(), SWT.PUSH);
		
		return this.append(new SWTToolActionItem(toolItem, this));
	}
	
	public UIToolCheckableItem createCheckItem() {
		ToolItem toolItem = new ToolItem(this.getControl(), SWT.CHECK);
		
		return this.append(new SWTToolCheckableItem(toolItem, this));
	}
	
	public UIToolMenuItem createMenuItem() {
		ToolItem toolItem = new ToolItem(this.getControl(), SWT.PUSH);
		
		return this.append(new SWTToolMenuItem(toolItem, this));
	}
	
	public UIToolActionMenuItem createActionMenuItem() {
		ToolItem toolItem = new ToolItem(this.getControl(), SWT.DROP_DOWN);
		
		return this.append(new SWTToolActionMenuItem(toolItem, this));
	}
	
	public UIToolCustomItem createCustomItem() {
		ToolItem toolItem = new ToolItem(this.getControl(), SWT.SEPARATOR);
		
		return this.append(new SWTToolCustomItem(toolItem, this));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends UIComponent> T append(SWTToolItem item) {
		this.toolItems.add(item);
		
		return (T) item;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends UIComponent> T append(SWTToolCustomItem item) {
		this.toolCustomItems.add(item);
		
		return (T) item;
	}
	
	public void dispose(SWTToolItem item) {
		if( this.toolItems.contains(item)) { 
			this.toolItems.remove(item);
		}
		item.disposeControl();
	}
	
	public void dispose(SWTToolCustomItem item) {
		if( this.toolCustomItems.contains(item)) { 
			this.toolCustomItems.remove(item);
		}
		item.disposeControl();
	}
	
	public void computePackedSize() {
		super.computePackedSize();
		
		if(!this.toolCustomItems.isEmpty()) {
			UISize defaultPackedSize = this.getPackedSize();
			
			float packedSize = this.findClientMargin();
			for(SWTToolItem control : this.toolItems) {
				packedSize += findLeftMargin(control.getControl());
				packedSize += control.getControl().getWidth();
			}
			
			for(SWTToolCustomItem control : this.toolCustomItems) {
				control.computePackedSize();
				
				packedSize += findLeftMargin(control.getItem());
				packedSize += this.findSize(control.getPackedSize());
			}
			
			if( this.horizontal ) {
				defaultPackedSize.setWidth(packedSize);
			} else {
				defaultPackedSize.setHeight(packedSize);
			}
			
			super.setPackedSize(defaultPackedSize);
		}
	}
	
	public void setBounds(UIRectangle bounds) {
		super.setBounds(this.getPreferredBounds(bounds));
		
		if(!this.toolCustomItems.isEmpty()) {			
			int clientWidth = this.findSize(getControl().getClientArea());
			int itemsWidth = this.findClientMargin();
			int itemsHeight = 0;
			int itemsToFill = 0;
			
			for(SWTToolItem control : this.toolItems) {
				itemsWidth += findLeftMargin(control.getControl());
				itemsWidth += control.getControl().getWidth();
				itemsHeight = Math.max(itemsHeight, this.findHeight(control.getControl().getBounds()));
			}
			
			for(SWTToolCustomItem control : this.toolCustomItems) {
				itemsWidth += findLeftMargin(control.getItem());
				itemsWidth += Math.round(this.findSize(control.getPackedSize()));
				itemsHeight = Math.max(itemsHeight, Math.round(this.findHeight(control.getPackedSize())));
				if( Boolean.TRUE.equals(control.getLayoutAttribute(UIToolCustomItem.FILL))) {
					itemsToFill ++;
				}
			}
			
			float widthToFill = (itemsToFill > 0 && clientWidth > itemsWidth ? ((clientWidth - itemsWidth) / itemsToFill) : 0);
			for(SWTToolCustomItem control : this.toolCustomItems) {
				float controlWidth = this.findSize(control.getPackedSize());
				
				if( Boolean.TRUE.equals(control.getLayoutAttribute(UIToolCustomItem.FILL))) {
					controlWidth += widthToFill;
				}
				
				int width = (this.horizontal ? Math.round(controlWidth) : itemsHeight);
				int height = (this.horizontal ? itemsHeight : Math.round(controlWidth));
				
				control.setSize(width, height);
			}
		}
	}
	
	private UIRectangle getPreferredBounds(UIRectangle bounds) {
		UIRectangle preferred = new UIRectangle(bounds.getPosition(), this.getPackedSize());
		if( this.horizontal ) {
			preferred.getSize().setWidth(bounds.getWidth());
		} else {
			preferred.getSize().setHeight(bounds.getHeight());
		}
		return preferred;
	}
	
	private int findClientMargin() {
		ToolItem firstItem = findFirstItem();
		if( firstItem != null ) {
			// left + right margins
			return (findPosition(firstItem.getBounds()) * 2);
		}
		return 0;
	}
	
	private int findLeftMargin(ToolItem item) {
		ToolItem previousItem = findPreviousItem(item);
		if( previousItem != null ) {
			Rectangle itemBounds = item.getBounds();
			Rectangle previousBounds = previousItem.getBounds();
			
			// left + right margins
			return Math.max(0, (findPosition(itemBounds) - (findPosition(previousBounds) + findSize(previousBounds))));
		}
		return 0;
	}
	
	private ToolItem findFirstItem() {
		ToolItem[] items = this.getControl().getItems();
		return (items.length > 0 ? items[0] : null);
	}
	
	private ToolItem findPreviousItem(ToolItem item) {
		ToolItem[] items = this.getControl().getItems();
		for(int i = 0 ; i < items.length; i ++) {
			if( items[i].equals(item) ) {
				return (i > 0 ? items[i - 1] : null);
			}
		}
		return null;
	}
	
	private int findPosition(Rectangle r) {
		return (this.horizontal ? r.x : r.y);
	}
	
	private int findSize(Rectangle r) {
		return (this.horizontal ? r.width : r.height);
	}
	
	private float findSize(UISize r) {
		return (this.horizontal ? r.getWidth() : r.getHeight());
	}
	
	private int findHeight(Rectangle r) {
		return (this.horizontal ? r.height : r.width);
	}
	
	private float findHeight(UISize r) {
		return (this.horizontal ? r.getHeight() : r.getWidth());
	}
}
