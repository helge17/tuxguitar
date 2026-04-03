package app.tuxguitar.editor.action.composition;

import java.util.Iterator;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGChangeTempoRangeAction extends TGActionBase {

	public static final String NAME = "action.composition.change-tempo-range";

	public static final String ATTRIBUTE_APPLY_TO = "applyTo";
	public static final String ATTRIBUTE_TEMPO = "tempoValue";
	public static final String ATTRIBUTE_TEMPO_BASE = "tempoBase";
	public static final String ATTRIBUTE_TEMPO_BASE_DOTTED = "tempoBaseDotted";

	public static final int MIN_TEMPO = 1;
	public static final int MAX_TEMPO = 320;

	public static final int APPLY_TO_ALL = 1;
	public static final int APPLY_TO_END = 2;
	public static final int APPLY_TO_NEXT = 3;
	public static final int APPLY_TO_SELECTION = 4;

	public TGChangeTempoRangeAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		int applyTo = ((Integer) context.getAttribute(ATTRIBUTE_APPLY_TO)).intValue();
		int tempoValue = ((Integer) context.getAttribute(ATTRIBUTE_TEMPO)).intValue();
		int tempoBase = ((Integer) context.getAttribute(ATTRIBUTE_TEMPO_BASE)).intValue();
		boolean tempoBaseDotted = ((Boolean) context.getAttribute(ATTRIBUTE_TEMPO_BASE_DOTTED)).booleanValue();

		if( tempoValue >= MIN_TEMPO && tempoValue <= MAX_TEMPO ){
			TGSongManager tgSongManager = getSongManager(context);
			TGSong tgSong = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
			TGBeatRange beatRange = (TGBeatRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);

			TGTempo tgTempo = tgSongManager.getFactory().newTempo();
			tgTempo.setValueBase(tempoValue, tempoBase, tempoBaseDotted);

			long startTick = -1;
			int endMeasureNumber = -1;
			switch (applyTo) {
			case APPLY_TO_ALL:
				startTick = TGDuration.QUARTER_TIME;
				endMeasureNumber = tgSong.countMeasureHeaders();
				break;
			case APPLY_TO_NEXT:
			case APPLY_TO_END:
				startTick = ((TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)).getStart();
				endMeasureNumber = tgSong.countMeasureHeaders();
				break;
			case APPLY_TO_SELECTION:
				if ((beatRange != null) && !beatRange.isEmpty()) {
					startTick = beatRange.getBeats().get(0).getStart();
					endMeasureNumber = beatRange.getBeats().get(beatRange.getBeats().size()-1).getMeasure().getNumber();
				}
				break;
			// default: INVALID!
			}
			if ((startTick > 0) && (endMeasureNumber > 0)) {
				TGMeasureHeader startHeader = tgSongManager.getMeasureHeaderAt(tgSong, startTick);
				TGTempo oldTempo = startHeader != null ? startHeader.getTempo().clone(tgSongManager.getFactory()) : null;
				if( startHeader != null ) {
					Iterator<?> it = tgSongManager.getMeasureHeadersAfter(tgSong, startHeader.getNumber() - 1).iterator();
					TGMeasureHeader nextHeader = null;
					while(it.hasNext()){
						nextHeader = (TGMeasureHeader)it.next();
						if( (nextHeader.getNumber() <= endMeasureNumber) && (applyTo != APPLY_TO_NEXT || nextHeader.getTempo().isEqual(oldTempo)) ){
							context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, nextHeader);
							context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TEMPO, tgTempo);
							TGActionManager.getInstance(getContext()).execute(TGChangeTempoAction.NAME, context);
						} else{
							break;
						}
					}
					// if applied to selection, refresh following measure display (keep tempo unchanged)
					if ((applyTo == APPLY_TO_SELECTION) && (nextHeader != null)) {
						context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, nextHeader);
						context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TEMPO, nextHeader.getTempo());
						TGActionManager.getInstance(getContext()).execute(TGChangeTempoAction.NAME, context);
					}
				}
			}
		}
	}
}
