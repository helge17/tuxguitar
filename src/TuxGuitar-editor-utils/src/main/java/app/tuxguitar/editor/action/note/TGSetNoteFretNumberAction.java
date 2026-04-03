package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

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
		TGTrack track = (TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGString string = (TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		Long start = (Long) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION);

		int fret = this.number;
		long time = System.currentTimeMillis();

		if( this.number < 10 ){
			if( lastAddedStart == start.longValue() && lastAddedString == string.getNumber() ){
				if( lastAddedFret > 0 && lastAddedFret < 10 && time <  ( lastAddedTime + DELAY ) ){
					int newFret = ( ( lastAddedFret * 10 ) + fret );
					if( newFret <= track.getMaxFret() || track.isPercussion() ){
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
