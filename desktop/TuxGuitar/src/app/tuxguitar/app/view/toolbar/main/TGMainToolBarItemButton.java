package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.ui.toolbar.UIToolActionItem;
import app.tuxguitar.ui.toolbar.UIToolCheckableItem;
import app.tuxguitar.ui.toolbar.UIToolItem;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.util.TGContext;

// basic button, checkable button
public class TGMainToolBarItemButton extends TGMainToolBarItem {

	protected UIToolItem toolItem;
	private boolean checked;

	public TGMainToolBarItemButton(TGMainToolBarItemConfig config, UIToolItem toolItem) {
		super(config);
		this.toolItem = toolItem;
	}

	@Override
	public UIControl getControl() {
		// unused, control is stored in toolBar section
		return null;
	}

	@Override
	public void update(TGContext context, boolean running) {
		if (this.updater != null) {
			this.checked = updater.checked(context, running);
			if (this.toolItem != null) {
				((UIToolActionItem) toolItem).setEnabled(updater.enabled(context, running));
				if (toolItem instanceof UIToolCheckableItem) {
					((UIToolCheckableItem) toolItem).setChecked(this.checked);
				}
			}
			String newIconName = updater.getIconName(context, running);
			if ((newIconName != null) && !newIconName.equals(this.iconFileName)) {
				this.iconFileName = newIconName;
				this.loadIcons(TuxGuitar.getInstance().getIconManager());
			}
			String newText = updater.getText(context, running);
			if ((newText != null) && !newText.equals(this.text)) {
				this.text = newText;
				this.loadProperties();
			}
		}
	}

	@Override
	public void loadProperties() {
		if (toolItem != null) {
			toolItem.setToolTipText(TuxGuitar.getProperty(this.text));
		}
	}

	@Override
	public void loadIcons(TGIconManager iconManager) {
		if (toolItem != null) {
			toolItem.setImage(iconManager.getImageByName(this.iconFileName));
		}
	}

}
