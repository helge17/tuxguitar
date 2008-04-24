package org.herac.tuxguitar.gui.editors.lyric;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackLyric;
import org.herac.tuxguitar.song.models.TGTrack;

public class LyricModifyListener implements ModifyListener{
	
	private boolean enabled;
	private LyricEditor editor;
	private int lastPosition;
	
	public LyricModifyListener(LyricEditor editor){
		this.editor = editor;
	}
	
	public void modifyText(ModifyEvent e) {
		if(isEnabled() && !TuxGuitar.instance().getPlayer().isRunning()){
			
			if(e.widget instanceof Text){
				TGTrack track = this.editor.getTrack();
				Text text = (Text)e.widget;
				String value = text.getText();
				int position = text.getCaretPosition();
				
				UndoableTrackLyric undoable = UndoableTrackLyric.startUndo(track,this.lastPosition);
				track.getLyrics().setLyrics(value);
				TuxGuitar.instance().getUndoableManager().addEdit( undoable.endUndo(track,position) );
				TuxGuitar.instance().getFileHistory().setUnsavedFile();
				
				this.lastPosition = position;
			}else if(e.widget instanceof Spinner){
				TGTrack track = this.editor.getTrack();
				UndoableTrackLyric undoable = UndoableTrackLyric.startUndo(track,this.lastPosition);
				track.getLyrics().setFrom(((Spinner)e.widget).getSelection());
				TuxGuitar.instance().getUndoableManager().addEdit( undoable.endUndo(track,this.lastPosition) );
				TuxGuitar.instance().getFileHistory().setUnsavedFile();
			}
			
			TuxGuitar.instance().updateCache(true);
		}
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
