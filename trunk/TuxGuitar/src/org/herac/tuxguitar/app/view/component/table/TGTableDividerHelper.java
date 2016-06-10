package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.widget.UIDivider;

public class TGTableDividerHelper {
	
	private TGTable table;
	
	public TGTableDividerHelper(TGTable table) {
		this.table = table;
	}

	public UIDivider createDivider(TGTableColumn leftColumn, TGTableColumn rightColumn) {
		UIFactory uiFactory = this.table.getUIFactory();
		UIDivider uiDivider = uiFactory.createVerticalDivider(this.table.getColumnControl());
		uiDivider.addMouseDragListener(new TGTableDividerListener(this.table, leftColumn, rightColumn));
		uiDivider.setCursor(UICursor.SIZEWE);
		
		return uiDivider;
	}
}
