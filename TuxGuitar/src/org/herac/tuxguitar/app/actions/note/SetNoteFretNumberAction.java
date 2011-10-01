package org.herac.tuxguitar.app.actions.note;

import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.editors.tab.Caret;

public class SetNoteFretNumberAction extends ChangeNoteAction {
	
	public static final String NAME_PREFIX = "action.note.general.set-fret-number-";
	
	private static final int DELAY = 1000;
	
	private static int lastAddedFret;
	private static int lastAddedString;
	private static long lastAddedStart;
	private static long lastAddedTime;
	
	private int number;
	
	public SetNoteFretNumberAction(int number){
		super(getActionName(number), AUTO_LOCK | AUTO_UNLOCK | KEY_BINDING_AVAILABLE | DISABLE_ON_PLAYING);
		this.number = number;
	}
	
	protected int execute(ActionData actionData){
		Caret caret = getEditor().getTablature().getCaret();
		
		int fret = this.number;
		int string = caret.getSelectedString().getNumber();
		long start = caret.getPosition();
		long time = System.currentTimeMillis();
		
		if( this.number < 10 ){
			if( lastAddedStart == start && lastAddedString == string ){
				if (lastAddedFret > 0 && lastAddedFret < 10 && time <  ( lastAddedTime + DELAY ) ){
					int newFret = ( ( lastAddedFret * 10 ) + fret );
					if( newFret < 30 || getSongManager().isPercussionChannel(caret.getTrack().getChannelId()) ){
						fret = newFret;
					}
				}
			}
			lastAddedFret = fret;
			lastAddedStart = start;
			lastAddedString = string;
			lastAddedTime = time;
		}
		
		actionData.put(PROPERTY_START, new Long(start) );
		actionData.put(PROPERTY_FRET, new Integer(fret));
		actionData.put(PROPERTY_STRING, new Integer(string));
		
		return super.execute(actionData);
	}
	
	public static final String getActionName(int number){
		return (NAME_PREFIX + number);
	}
}
