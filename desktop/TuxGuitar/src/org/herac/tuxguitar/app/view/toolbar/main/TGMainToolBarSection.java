package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.toolbar.model.TGToolBarSection;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.song.models.TGSong;

public abstract class TGMainToolBarSection implements TGToolBarSection {
	
	private static final String SELECTED_CHAR = "\u2713";
	
	private TGMainToolBar toolBar;
	
	public TGMainToolBarSection(TGMainToolBar toolBar) {
		this.toolBar = toolBar;
	}
	
	public abstract void createSection();
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(this.toolBar.getContext(), actionId);
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
		return TablatureEditor.getInstance(this.toolBar.getContext()).getTablature();
	}
	
	public TGSong getSong() {
		return TGDocumentManager.getInstance(this.toolBar.getContext()).getSong();
	}

	public TGMainToolBar getToolBar() {
		return this.toolBar;
	}
}
