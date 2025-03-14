package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import app.tuxguitar.ui.toolbar.UIToolActionItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarSectionComposition extends TGMainToolBarSection {

	private UIToolActionItem properties;

	public TGMainToolBarSectionComposition(TGContext context, UIToolBar toolBar) {
		super(context, toolBar);
	}

	public void createSection() {
		this.properties = this.getToolBar().createActionItem();
		this.properties.addSelectionListener(this.createActionProcessor(TGOpenSongInfoDialogAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void loadProperties() {
		this.properties.setToolTipText(this.getText("composition.properties"));
	}

	public void loadIcons() {
		this.properties.setImage(this.getIconManager().getSongProperties());
	}

	public void updateItems() {
		//Nothing to do
	}
}
