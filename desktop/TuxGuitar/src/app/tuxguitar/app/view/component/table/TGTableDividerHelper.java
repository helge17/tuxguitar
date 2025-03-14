package app.tuxguitar.app.view.component.table;

import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.resource.UICursor;
import app.tuxguitar.ui.widget.UIDivider;

public class TGTableDividerHelper {

	private TGTable table;

	public TGTableDividerHelper(TGTable table) {
		this.table = table;
	}

	public UIDivider createDivider(TGTableHeader leftColumn, TGTableHeader rightColumn, boolean atEnd) {
		UIFactory uiFactory = this.table.getUIFactory();
		UIDivider uiDivider = uiFactory.createVerticalDivider(this.table.getColumnControl());
		uiDivider.setBgColor(this.table.getViewer().getColorModel().getColor(TGTableColorModel.CELL_BACKGROUND));
		uiDivider.addMouseDragListener(new TGTableDividerListener(this.table, leftColumn, rightColumn, atEnd));
		uiDivider.setCursor(UICursor.SIZEWE);

		return uiDivider;
	}
}
