package org.herac.tuxguitar.gui.undo.undoables;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.tab.Caret;
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
		this.duration = caret.getDuration().clone(TuxGuitar.instance().getSongManager().getFactory());
		this.string = 1;
		TGString instrumentString = caret.getSelectedString();
		if(instrumentString != null){
			this.string = instrumentString.getNumber();
		}
	}
	
	public void update(){
		getCaret().update(this.track,this.position,this.string,this.velocity);
		getCaret().setSelectedDuration(this.duration.clone(TuxGuitar.instance().getSongManager().getFactory()));
	}
	
	private static Caret getCaret(){
		return TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
	}
	
}
