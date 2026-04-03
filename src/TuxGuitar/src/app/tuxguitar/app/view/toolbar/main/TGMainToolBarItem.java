package app.tuxguitar.app.view.toolbar.main;
/**
 * A template for a toolBar item
 * One toolBar item is supposed to host one single control
 */

import java.util.HashMap;
import java.util.Map;

import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.util.TGContext;

public abstract class TGMainToolBarItem {

	public static final int SEPARATOR = 0;
	public static final int ACTION_ITEM = 1;
	public static final int CHECKABLE_ITEM = 2;
	public static final int MENU = 3;
	public static final int TIME_COUNTER = 4;
	public static final int TEMPO_INDICATOR = 5;

	protected String actionName;
	protected String iconFileName;
	protected String text;
	protected TGMainToolBarItemUpdater updater;
	protected Map<String,Object> attributes;	// parameters to be passed to the action processor

	protected TGMainToolBarItem(TGMainToolBarItemConfig config) {
		this.actionName = config.getActionName();
		this.iconFileName = config.getIconFileName();
		this.text = config.getText();
		this.updater = config.getUpdater();
		this.attributes = new HashMap<String, Object>();
	}

	public abstract UIControl getControl();

	public String getText() {
		return this.text;
	}

	protected void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}

	public String getIconFileName() {
		return this.iconFileName;
	}

	public void update(TGContext context, boolean running) {
		// default: do nothing, override if needed
	}

	public void loadProperties() {
		// default: do nothing, override if needed
	}

	public void loadIcons(TGIconManager iconManager) {
		// default: do nothing, override if needed
	}
	
	public void setLayoutProperties(UITableLayout layout) {
		// default: do nothing, override if needed
	}
	
}
