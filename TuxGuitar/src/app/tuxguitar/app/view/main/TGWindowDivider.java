package app.tuxguitar.app.view.main;

import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.table.TGTableViewer;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIMouseDragListener;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UICursor;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UIDivider;
import app.tuxguitar.util.TGContext;

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

	public void setVisible(boolean visible) {
		this.divider.setVisible(visible);
	}

	public UIDivider getControl() {
		return divider;
	}
}
