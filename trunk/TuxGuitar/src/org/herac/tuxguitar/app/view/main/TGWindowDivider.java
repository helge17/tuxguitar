package org.herac.tuxguitar.app.view.main;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseDragListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIDivider;
import org.herac.tuxguitar.util.TGContext;

public class TGWindowDivider implements UIMouseDragListener {

	private TGContext context;
	private UIDivider divider;
	
	public TGWindowDivider(TGContext context) {
		this.context = context;
	}
	
	public void createDivider(UIContainer parent) {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		
		this.divider = uiFactory.createHorizontalDivider(parent);
		this.divider.addMouseDragListener(this);
		this.divider.setCursor(UICursor.SIZENS);
	}
	
	public void onMouseDrag(UIMouseEvent event) {
		UIControl control = TGTableViewer.getInstance(this.context).getControl();
		
		TGWindow tgWindow = TGWindow.getInstance(this.context);
		TGWindowLayout tgWindowLayout = (TGWindowLayout) tgWindow.getWindow().getLayout();
		tgWindowLayout.set(control, UITableLayout.PACKED_HEIGHT, this.computeHeight(control, event.getPosition().getY()));
		tgWindow.getWindow().layout();
	}
	
	public Float computeHeight(UIControl control, float move) {
		return Math.max((control.getBounds().getHeight() - move), 0f);
	}

	public UIDivider getControl() {
		return divider;
	}
}
