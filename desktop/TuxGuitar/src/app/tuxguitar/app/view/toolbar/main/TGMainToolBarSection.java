package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.toolbar.model.TGToolBarSection;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.util.TGContext;

public abstract class TGMainToolBarSection implements TGToolBarSection {

	private static final String SELECTED_CHAR = "\u2713";

	private TGContext context;
	private UIToolBar toolBar;

	public TGMainToolBarSection(TGContext context, UIToolBar toolBar) {
		this.context = context;
		this.toolBar = toolBar;
	}

	public abstract void createSection();

	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(this.context, actionId);
	}

	public String getText(String key) {
		return TuxGuitar.getProperty(key);
	}

	public String getText(String key, boolean selected) {
		return this.toCheckString(getText(key), selected);
	}

	public String toCheckString(String text, boolean selected) {
		return ((selected ? SELECTED_CHAR : "") + text);
	}

	public TGIconManager getIconManager() {
		return TuxGuitar.getInstance().getIconManager();
	}

	public Tablature getTablature() {
		return TablatureEditor.getInstance(this.context).getTablature();
	}

	public TGSong getSong() {
		return TGDocumentManager.getInstance(this.context).getSong();
	}

	public TGContext getContext() {
		return this.context;
	}

	public UIToolBar getToolBar() {
		return this.toolBar;
	}

}
