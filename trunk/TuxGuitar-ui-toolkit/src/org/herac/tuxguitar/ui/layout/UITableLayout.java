package org.herac.tuxguitar.ui.layout;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class UITableLayout extends UIAbstractLayout {
	
	public static final String ROW = "row";
	public static final String COL = "col";
	public static final String ROW_SPAN = "row_span";
	public static final String COL_SPAN = "col_span";
	
	public static final String MARGIN = "margin";
	public static final String MARGIN_TOP = "margin_top";
	public static final String MARGIN_BOTTOM = "margin_bottom";
	public static final String MARGIN_LEFT = "margin_left";
	public static final String MARGIN_RIGHT = "margin_right";
	
	public static final Float DEFAULT_GLOBAL_MARGIN = 4f;
	public static final Float DEFAULT_CONTROL_MARGIN = 2f;
	
	public static final String FILL_X = "fill_x";
	public static final String FILL_Y = "fill_y";
	
	public static final String ALIGN_X = "align_x";
	public static final String ALIGN_Y = "align_y";
	
	public static final Integer ALIGN_FILL = 1;
	public static final Integer ALIGN_CENTER = 2;
	public static final Integer ALIGN_ENDING = 3;
	public static final Integer ALIGN_BEGINNING = 4;
	public static final Integer ALIGN_TOP = ALIGN_BEGINNING;
	public static final Integer ALIGN_LEFT = ALIGN_BEGINNING;
	public static final Integer ALIGN_RIGHT = ALIGN_ENDING;
	public static final Integer ALIGN_BOTTOM = ALIGN_ENDING;
	
	public static final String PACKED_WIDTH = "packed_width";
	public static final String PACKED_HEIGHT = "packed_height";
	public static final String MINIMUM_PACKED_WIDTH = "minimum_packed_width";
	public static final String MINIMUM_PACKED_HEIGHT = "minimum_packed_height";
	public static final String MAXIMUM_PACKED_WIDTH = "maximum_packed_width";
	public static final String MAXIMUM_PACKED_HEIGHT = "maximum_packed_height";
	
	public static final String IGNORE_INVISIBLE = "ignore_invisible";
	
	private List<UITableCellSize> xSizes;
	private List<UITableCellSize> ySizes;
	
	public UITableLayout(Float margin) {
		this.xSizes = new ArrayList<UITableCellSize>();
		this.ySizes = new ArrayList<UITableCellSize>();
		this.set(MARGIN, margin);
	}
	
	public UITableLayout() {
		this(DEFAULT_GLOBAL_MARGIN);
	}
	
	public void set(UIControl control, Integer row, Integer col, Integer alignX, Integer alignY, Boolean fillX, Boolean fillY, Integer rowSpan, Integer colSpan,Float minPackedWidth, Float minPackedHeight, Float margin){
		this.set(control, UITableLayout.COL, col);
		this.set(control, UITableLayout.ROW, row);
		this.set(control, UITableLayout.COL_SPAN, colSpan);
		this.set(control, UITableLayout.ROW_SPAN, rowSpan);
		this.set(control, UITableLayout.FILL_X, fillX);
		this.set(control, UITableLayout.FILL_Y, fillY);
		this.set(control, UITableLayout.ALIGN_X, alignX);
		this.set(control, UITableLayout.ALIGN_Y, alignY);
		this.set(control, UITableLayout.MINIMUM_PACKED_WIDTH, minPackedWidth);
		this.set(control, UITableLayout.MINIMUM_PACKED_HEIGHT, minPackedHeight);
		this.set(control, UITableLayout.MARGIN, margin);
	}
	
	public void set(UIControl control, Integer row, Integer col, Integer alignX, Integer alignY, Boolean fillX, Boolean fillY, Integer rowSpan, Integer colSpan){
		this.set(control, row, col, alignX, alignY, fillX, fillY, rowSpan, colSpan, null, null, DEFAULT_CONTROL_MARGIN);
	}
	
	public void set(UIControl control, Integer row, Integer col, Integer alignX, Integer alignY, Boolean fillX, Boolean fillY){
		this.set(control, row, col, alignX, alignY, fillX, fillY, 1, 1);
	}
	
	public UISize computePackedSize(UILayoutContainer container) {
		this.xSizes.clear();
		this.ySizes.clear();
		
		for(UIControl control : container.getChildren()) {
			if(!(control instanceof UIWindow)) {
				UISize packedSize = this.getPreferredSizeFor(control);
				
				List<UITableCellSize> xRange = this.getSizes(this.xSizes, control, COL, COL_SPAN);
				List<UITableCellSize> yRange = this.getSizes(this.ySizes, control, ROW, ROW_SPAN);
				
				float margin = this.get(control, MARGIN, DEFAULT_CONTROL_MARGIN);
				float marginTop = this.get(control, MARGIN_TOP, margin);
				float marginBottom = this.get(control, MARGIN_BOTTOM, margin);
				float marginLeft = this.get(control, MARGIN_LEFT, margin);
				float marginRight = this.get(control, MARGIN_RIGHT, margin);
				
				float xPackedSize = ((marginLeft + packedSize.getWidth() + marginRight) / xRange.size());
				float yPackedSize = ((marginTop + packedSize.getHeight() + marginBottom) / yRange.size());
				
				for(UITableCellSize xSize : xRange) {
					xSize.packedSize = Math.max(xSize.packedSize, xPackedSize);
					xSize.fillSize = (xSize.fillSize || Boolean.TRUE.equals(this.get(control, FILL_X)));
				}
				
				for(UITableCellSize ySize : yRange) {
					ySize.packedSize = Math.max(ySize.packedSize, yPackedSize);
					ySize.fillSize = (ySize.fillSize || Boolean.TRUE.equals(this.get(control, FILL_Y)));
				}
			}
		}
		
		float margin = this.get(MARGIN, DEFAULT_GLOBAL_MARGIN);
		float marginTop = this.get(MARGIN_TOP, margin);
		float marginBottom = this.get(MARGIN_BOTTOM, margin);
		float marginLeft = this.get(MARGIN_LEFT, margin);
		float marginRight = this.get(MARGIN_RIGHT, margin);
		
		UISize packedSize = new UISize(marginLeft + marginRight, marginTop + marginBottom);
		for(UITableCellSize xSize : this.xSizes) {
			packedSize.setWidth(packedSize.getWidth() + xSize.packedSize);
		}
		for(UITableCellSize ySize : this.ySizes) {
			packedSize.setHeight(packedSize.getHeight() + ySize.packedSize);
		}
		
		return packedSize;
	}
	
	public void setBounds(UILayoutContainer container, UIRectangle bounds) {
		float margin = this.get(MARGIN, DEFAULT_GLOBAL_MARGIN);
		float marginTop = this.get(MARGIN_TOP, margin);
		float marginBottom = this.get(MARGIN_BOTTOM, margin);
		float marginLeft = this.get(MARGIN_LEFT, margin);
		float marginRight = this.get(MARGIN_RIGHT, margin);
		
		UIRectangle childArea = this.getChildArea(bounds, marginTop, marginBottom, marginLeft, marginRight);
		UISize packedSize = container.getPackedContentSize();
		UISize childSize = new UISize(packedSize.getWidth() - (marginLeft + marginRight), packedSize.getHeight() - (marginTop + marginBottom));
		UISize excessSize = new UISize();
		
		excessSize.setWidth(getExcessSize(this.xSizes, childArea.getWidth(), childSize.getWidth()));
		excessSize.setHeight(getExcessSize(this.ySizes, childArea.getHeight(), childSize.getHeight()));
		
		this.updateSizes(this.xSizes, excessSize.getWidth());
		this.updateSizes(this.ySizes, excessSize.getHeight());
		
		for(UIControl control : container.getChildren()) {
			if(!(control instanceof UIWindow)) {
				UIRectangle cellBounds = new UIRectangle();
				
				List<UITableCellSize> xRange = this.getSizes(this.xSizes, control, COL, COL_SPAN);
				List<UITableCellSize> yRange = this.getSizes(this.ySizes, control, ROW, ROW_SPAN);
				
				cellBounds.getPosition().setX(childArea.getX() + this.getPosition(this.xSizes, xRange.get(0)));
				cellBounds.getPosition().setY(childArea.getY() + this.getPosition(this.ySizes, yRange.get(0)));
				
				for(UITableCellSize xSize : xRange) {
					cellBounds.getSize().setWidth(cellBounds.getWidth() + xSize.size);
				}
				for(UITableCellSize ySize : yRange) {
					cellBounds.getSize().setHeight(cellBounds.getHeight() + ySize.size);
				}
				
				UIRectangle cellArea = this.getChildArea(control, cellBounds);
				UIRectangle alignedArea = this.getAlignedArea(control, cellArea);
				
				control.setBounds(alignedArea);
			}
		}
	}
	
	public UIRectangle getChildArea(UIControl control, UIRectangle cellBounds) {
		float margin = this.get(control, MARGIN, DEFAULT_CONTROL_MARGIN);
		float marginTop = this.get(control, MARGIN_TOP, margin);
		float marginBottom = this.get(control, MARGIN_BOTTOM, margin);
		float marginLeft = this.get(control, MARGIN_LEFT, margin);
		float marginRight = this.get(control, MARGIN_RIGHT, margin);
		
		return this.getChildArea(cellBounds, marginTop, marginBottom, marginLeft, marginRight);
	}
	
	public UIRectangle getChildArea(UIRectangle bounds, float marginTop, float marginBottom, float marginLeft, float marginRight) {
		UIRectangle uiRectangle = new UIRectangle();
		uiRectangle.getPosition().setX(bounds.getX() + marginLeft);
		uiRectangle.getPosition().setY(bounds.getY() + marginTop);
		uiRectangle.getSize().setWidth(bounds.getWidth() - (marginLeft + marginRight));
		uiRectangle.getSize().setHeight(bounds.getHeight() - (marginTop + marginBottom));
		
		return uiRectangle;
	}
	
	public UIRectangle getAlignedArea(UIControl control, UIRectangle cellArea) {
		UIRectangle bounds = new UIRectangle();
		UISize packedSize = this.getPreferredSizeFor(control);
		if( cellArea.getWidth() > packedSize.getWidth() ) {
			Integer alignX = this.get(control, ALIGN_X, ALIGN_CENTER);
			if( ALIGN_FILL.equals(alignX) ) {
				bounds.getPosition().setX(cellArea.getX());
				bounds.getSize().setWidth(cellArea.getWidth());
			}
			else if( ALIGN_BEGINNING.equals(alignX) ) {
				bounds.getPosition().setX(cellArea.getX());
				bounds.getSize().setWidth(packedSize.getWidth());
			}
			else if( ALIGN_ENDING.equals(alignX) ) {
				bounds.getPosition().setX(cellArea.getX() + (cellArea.getWidth() - packedSize.getWidth()));
				bounds.getSize().setWidth(packedSize.getWidth());
			}
			else if( ALIGN_CENTER.equals(alignX) ) {
				bounds.getPosition().setX(cellArea.getX() + (cellArea.getWidth() / 2f) - (packedSize.getWidth() / 2f));
				bounds.getSize().setWidth(packedSize.getWidth());
			}
		} else {
			bounds.getPosition().setX(cellArea.getX());
			bounds.getSize().setWidth(packedSize.getWidth());
		}
		
		if( cellArea.getHeight() > packedSize.getHeight() ) {
			Integer alignY = this.get(control, ALIGN_Y, ALIGN_CENTER);
			if( ALIGN_FILL.equals(alignY) ) {
				bounds.getPosition().setY(cellArea.getY());
				bounds.getSize().setHeight(cellArea.getHeight());
			}
			else if( ALIGN_BEGINNING.equals(alignY) ) {
				bounds.getPosition().setY(cellArea.getY());
				bounds.getSize().setHeight(packedSize.getHeight());
			}
			else if( ALIGN_ENDING.equals(alignY) ) {
				bounds.getPosition().setY(cellArea.getY() + (cellArea.getHeight() - packedSize.getHeight()));
				bounds.getSize().setHeight(packedSize.getHeight());
			}
			else if( ALIGN_CENTER.equals(alignY) ) {
				bounds.getPosition().setY(cellArea.getY() + (cellArea.getHeight() / 2f) - (packedSize.getHeight() / 2f));
				bounds.getSize().setHeight(packedSize.getHeight());
			}
		} else {
			bounds.getPosition().setY(cellArea.getY());
			bounds.getSize().setHeight(packedSize.getHeight());
		}
		
		return bounds;
	}
	
	public UISize getPreferredSizeFor(UIControl control) {
		if(!control.isVisible() ) {
			Boolean ignoreInvisibleDefault = this.get(IGNORE_INVISIBLE);
			Boolean ignoreInvisible = this.get(control, IGNORE_INVISIBLE, ignoreInvisibleDefault);
			if( Boolean.TRUE.equals(ignoreInvisible) ) {
				return new UISize(0f, 0f);
			}
		}
		
		Float packedWidth = this.get(control, PACKED_WIDTH);
		Float packedHeight = this.get(control, PACKED_HEIGHT);
		Float minimumPackedWidth = this.get(control, MINIMUM_PACKED_WIDTH);
		Float minimumPackedHeight = this.get(control, MINIMUM_PACKED_HEIGHT);
		Float maximumPackedWidth = this.get(control, MAXIMUM_PACKED_WIDTH);
		Float maximumPackedHeight = this.get(control, MAXIMUM_PACKED_HEIGHT);
		
		UISize packedSize = control.getPackedSize();
		UISize preferredSize = new UISize(packedSize.getWidth(), packedSize.getHeight());
		if( packedWidth != null ) {
			preferredSize.setWidth(packedWidth);
		}
		if( packedHeight != null ) {
			preferredSize.setHeight(packedHeight);
		}
		if( minimumPackedWidth != null && minimumPackedWidth > preferredSize.getWidth()) {
			preferredSize.setWidth(minimumPackedWidth);
		}
		if( minimumPackedHeight != null && minimumPackedHeight > preferredSize.getHeight()) {
			preferredSize.setHeight(minimumPackedHeight);
		}
		if( maximumPackedWidth != null && maximumPackedWidth < preferredSize.getWidth()) {
			preferredSize.setWidth(maximumPackedWidth);
		}
		if( maximumPackedHeight != null && maximumPackedHeight < preferredSize.getHeight()) {
			preferredSize.setHeight(maximumPackedHeight);
		}
		return preferredSize;
	}
	
	public void updateSizes(List<UITableCellSize> sizes, float excessSize) {
		for(UITableCellSize size : sizes) {
			size.size = (size.fillSize ? size.packedSize + excessSize : size.packedSize);
		}
	}
	
	public int getFillSizeCount(List<UITableCellSize> sizes) {
		int count = 0;
		for(UITableCellSize size : sizes) {
			if( size.fillSize ) {
				count ++;
			}
		}
		return count;
	}
	
	public float getExcessSize(List<UITableCellSize> sizes, float size, float packedSize) {
		if( size > packedSize ) {
			int fillSizeCount = this.getFillSizeCount(sizes);
			if( fillSizeCount > 0 ) {
				return ((size - packedSize) / fillSizeCount);
			}
		}
		return 0;
	}
	
	public float getPosition(List<UITableCellSize> sizes, UITableCellSize size) {
		float position = 0;
		for(UITableCellSize current : sizes) {
			if( current.equals(size)) {
				return position;
			}
			position += current.size;
		}
		
		return position;
	}
	
	public List<UITableCellSize> getSizes(List<UITableCellSize> sizes, UIControl control, String positionAttribute, String spanAttribute) {
		Integer position = this.get(control, positionAttribute, 1);
		Integer span = this.get(control, spanAttribute, 1);
		
		return this.getSizes(sizes, (position - 1), span);
	}
	
	public List<UITableCellSize> getSizes(List<UITableCellSize> sizes, int startIndex, int count) {
		List<UITableCellSize> range = new ArrayList<UITableCellSize>();
		
		while(sizes.size() < (startIndex + count)) {
			sizes.add(new UITableCellSize());
		}
		
		for(int i = startIndex ; i < (startIndex + count); i ++) {
			range.add(sizes.get(i));
		}
		
		return range;
	}
	
	private class UITableCellSize {

		private float size;
		private float packedSize;
		private boolean fillSize;
	}
}
