package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGSetNoteFretNumberAction extends TGActionBase  {
	
	public static final String NAME_PREFIX = "action.note.general.set-fret-number-";
	
	private static final int DELAY = 1000;
	
	private static int lastAddedFret;
	private static int lastAddedString;
	private static long lastAddedStart;
	private static long lastAddedTime;
	
	private int number;
	
	public TGSetNoteFretNumberAction(TGContext context, int number){
		super(context, getActionName(number));
		this.number = number;
	}
	
	protected void processAction(TGActionContext context){
		TGSong song = (TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGTrack track = (TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGString string = (TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		Long start = (Long) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION);
		
		int fret = this.number;
		long time = System.currentTimeMillis();
		
		if( this.number < 10 ){
			if( lastAddedStart == start.longValue() && lastAddedString == string.getNumber() ){
				if( lastAddedFret > 0 && lastAddedFret < 10 && time <  ( lastAddedTime + DELAY ) ){
					int newFret = ( ( lastAddedFret * 10 ) + fret );
					if( newFret < 30 || getSongManager(context).isPercussionChannel(song, track.getChannelId()) ){
						fret = newFret;
					}
				}
			}
			lastAddedTime = time;
			lastAddedFret = fret;
			lastAddedStart = start.longValue();
			lastAddedString = string.getNumber();
		}
		
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, Integer.valueOf(fret));
		
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGChangeNoteAction.NAME, context);
	}
	
	public static final String getActionName(int number){
		return (NAME_PREFIX + number);
	}
}
