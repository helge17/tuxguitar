package app.tuxguitar.app.view.toolbar.main;

/**
 * A template for a main toolBar section
 */

import java.util.ArrayList;
import java.util.List;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.view.toolbar.model.TGToolBarSection;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.util.TGContext;

public abstract class TGMainToolBarSection implements TGToolBarSection {

	public static final int TYPE_TOOLITEMS = 1;
	public static final int TYPE_TIME_COUNTER = 2;
	public static final int TYPE_TEMPO = 3;

	private TGContext context;
	protected List<UIControl> controls;
	protected List<TGMainToolBarItem> toolBarItems;

	protected TGMainToolBarSection(TGContext context) {
		this.context = context;
		this.controls = new ArrayList<UIControl>();
		this.toolBarItems = new ArrayList<TGMainToolBarItem>();
	}

	public abstract void addToolBarItem(TGMainToolBarItem toolBarItem);

	protected TGActionProcessorListener createActionProcessor(TGMainToolBarItem item) {
		TGActionProcessorListener actionProcessorListener = new TGActionProcessorListener(this.getContext(), item.getActionName());
		for (String key : item.attributes.keySet()) {
			actionProcessorListener.setAttribute(key, item.attributes.get(key));
		}
		return actionProcessorListener;
	}

	public List<UIControl> getControls() {
		return this.controls;
	}

	public void setLayoutProperties(UITableLayout layout) {
		// default: nothing to do, override if needed
	}

	protected TGContext getContext() {
		return this.context;
	}

	@Override
	public void updateItems() {
		boolean running = MidiPlayer.getInstance(this.getContext()).isRunning();
		for (TGMainToolBarItem toolBarItem : this.toolBarItems) {
			toolBarItem.update(this.getContext(), running);
		}
	}

	@Override
	public void loadIcons() {
		for (TGMainToolBarItem toolBarItem : this.toolBarItems) {
			toolBarItem.loadIcons(TuxGuitar.getInstance().getIconManager());
		}
	}

	@Override
	public void loadProperties() {
		for (TGMainToolBarItem toolBarItem : this.toolBarItems) {
			toolBarItem.loadProperties();
		}
	}

}
