package org.herac.tuxguitar.app.view.component.table;

import java.util.List;

import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

public class TGTableBodyLayout extends UITableLayout {
	
	private float rowHeight;
	
	public TGTableBodyLayout() {
		super(0f);
	}
	
	public UISize computePackedSize(UILayoutContainer container) {
		this.rowHeight = 0f;
		
		List<UIControl> controls = container.getChildren();
		for(UIControl control : controls) {
			this.rowHeight = Math.max(this.rowHeight, control.getPackedSize().getHeight());
		}
		
		int row = 0;
		for(UIControl control : controls) {
			this.set(control, (++row), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			this.set(control, UITableLayout.PACKED_HEIGHT, this.rowHeight);
			this.set(control, UITableLayout.MARGIN, 0f);
			this.set(control, UITableLayout.MARGIN_TOP, 1f);
		}
		
		return super.computePackedSize(container);
	}
	
	public float getRowHeight() {
		return this.rowHeight;
	}
}
