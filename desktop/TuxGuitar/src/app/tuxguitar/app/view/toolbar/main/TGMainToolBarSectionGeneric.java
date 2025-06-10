package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIWindow;
/**
 * generic section, to host any kind of TGMainToolBarItem
 */
import app.tuxguitar.util.TGContext;

public class TGMainToolBarSectionGeneric extends TGMainToolBarSection {

	private UIPanel parentPanel;
	private UIWindow parentWindow;

	protected TGMainToolBarSectionGeneric(TGContext context, UIPanel parentPanel, UIWindow parentWindow) {
		super(context);
		this.parentPanel = parentPanel;
		this.parentWindow = parentWindow;
	}

	@Override
	public void addToolBarItem(TGMainToolBarItemConfig toolBarItemConfig) {
		if (toolBarItemConfig.getType() == TGMainToolBarItem.TIME_COUNTER) {
			TGMainToolBarItemTimeCounter counter = new TGMainToolBarItemTimeCounter(toolBarItemConfig, getContext(), parentPanel, parentWindow);
			this.toolBarItems.add(counter);
			this.controls.add(counter.getControl());
		}
		// add here new types of TGToolBarItem to extend toolBar
		else {
			throw new IllegalArgumentException("unmanaged type of mainToolBarItem");
		}
	}

}
