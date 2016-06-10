package org.herac.tuxguitar.app.view.dialog.lyric;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGSetTrackLyricsAction;
import org.herac.tuxguitar.ui.event.UIModifyEvent;
import org.herac.tuxguitar.ui.event.UIModifyListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;

public class TGLyricModifyListener implements UIModifyListener, UISelectionListener{
	
	private boolean enabled;
	private TGLyricEditor editor;
	
	public TGLyricModifyListener(TGLyricEditor editor){
		this.editor = editor;
	}
	
	public void processEvent() {
		if( isEnabled() && !TuxGuitar.getInstance().getPlayer().isRunning() ){
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.editor.getContext(), TGSetTrackLyricsAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.editor.getTrack());
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_LYRIC, this.editor.createLyrics());
			tgActionProcessor.setAttribute(TGLyricEditor.ATTRIBUTE_BYPASS_EVENTS_FROM, this.editor);
			tgActionProcessor.process();
		}
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void onSelect(UISelectionEvent event) {
		this.processEvent();
	}

	public void onModify(UIModifyEvent event) {
		this.processEvent();
	}
}
