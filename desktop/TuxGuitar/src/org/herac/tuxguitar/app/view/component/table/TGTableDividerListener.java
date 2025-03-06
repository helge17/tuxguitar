package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.ui.event.UIMouseDragListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;

public class TGTableDividerListener implements UIMouseDragListener {

	public static final String LEFT_CONTROL = "leftControl";
	public static final String RIGHT_CONTROL = "rightControl";

	private TGTable table;
	private TGTableHeader leftColumn;
	private TGTableHeader rightColumn;
	private final boolean atEnd;

	public TGTableDividerListener(TGTable table, TGTableHeader leftColumn, TGTableHeader rightColumn, boolean atEnd) {
		this.table = table;
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
		this.atEnd = atEnd;
		}

	public void onMouseDrag(UIMouseEvent event) {
		float move = event.getPosition().getX();

		Float leftWidth = this.computeWidth(this.leftColumn.getControl(), move, false);
		Float rightWidth = this.computeWidth(this.rightColumn.getControl(), -move, atEnd);
		if( leftWidth != null && rightWidth != null ) {
			UITableLayout uiLayout = (UITableLayout) this.table.getColumnControl().getLayout();
			uiLayout.set(this.leftColumn.getControl(), UITableLayout.MINIMUM_PACKED_WIDTH, leftWidth);
			if (!atEnd) {
				uiLayout.set(this.rightColumn.getControl(), UITableLayout.MINIMUM_PACKED_WIDTH, rightWidth);
			}

			this.table.update();
		}
	}

	private Float computeWidth(UIControl control, float move, boolean atEnd) {
		float minWidth;
		if (atEnd) {
			// at least a few measures + space for vertical scroll
			minWidth = 2*this.table.getRowHeight();
			minWidth += this.table.getViewer().getTableVScrollSize().getWidth();
		} else {
			UISize currentPackedSize = control.getPackedSize();
			control.computePackedSize(null, null);
			UISize computedPackedSize = control.getPackedSize();
			control.computePackedSize(currentPackedSize.getWidth(), currentPackedSize.getHeight());
			minWidth = computedPackedSize.getWidth();
		}

		float newWidth = (control.getBounds().getWidth() + move);
		if( newWidth >= minWidth) {
			return newWidth;
		}
		return null;
	}
}
