package org.herac.tuxguitar.app.undo.undoables;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGString;

public class UndoableCaretHelper {
	private long position;
	private int track;
	private int string;
	private int velocity;
	private TGDuration duration;
	
	public UndoableCaretHelper(){
		Caret caret = getCaret();
		this.track = caret.getTrack().getNumber();
		this.position = caret.getPosition();
		this.velocity = caret.getVelocity();
		this.duration = caret.getDuration().clone(TuxGuitar.getInstance().getSongManager().getFactory());
		this.string = 1;
		TGString instrumentString = caret.getSelectedString();
		if(instrumentString != null){
			this.string = instrumentString.getNumber();
		}
	}
	
	public void update(){
		getCaret().update(this.track,this.position,this.string,this.velocity);
		getCaret().setSelectedDuration(this.duration.clone(TuxGuitar.getInstance().getSongManager().getFactory()));
	}
	
	private static Caret getCaret(){
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
	}
	
}
