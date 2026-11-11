package app.tuxguitar.editor.action.composition;

import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
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

	private static final int INVALID = -1;

	public TGChangeTempoRangeAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		int applyTo = ((Integer) context.getAttribute(ATTRIBUTE_APPLY_TO)).intValue();
		int tempoValue = ((Integer) context.getAttribute(ATTRIBUTE_TEMPO)).intValue();
		int tempoBase = ((Integer) context.getAttribute(ATTRIBUTE_TEMPO_BASE)).intValue();
		boolean tempoBaseDotted = ((Boolean) context.getAttribute(ATTRIBUTE_TEMPO_BASE_DOTTED)).booleanValue();

		if( tempoValue >= MIN_TEMPO && tempoValue <= MAX_TEMPO ){
			TGTempo tgTempo = getSongManager(context).getFactory().newTempo();
			tgTempo.setValueBase(tempoValue, tempoBase, tempoBaseDotted);

			this.changeTempoRange(context, applyTo, tgTempo);
		}
	}

	private void changeTempoRange(TGActionContext context, int applyTo, TGTempo tgTempo){
		TGSong tgSong = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGMeasureHeader startHeader = this.findStartHeader(context, tgSong, applyTo);
		int endMeasureNumber = (startHeader != null) ? this.findEndMeasureNumber(context, tgSong, applyTo) : INVALID;
		if( endMeasureNumber > 0 ){
			TGMeasureHeader lastHeader = this.applyTempo(context, tgSong, startHeader, endMeasureNumber, applyTo, tgTempo);
			// if applied to selection, refresh following measure display (keep tempo unchanged)
			if( applyTo == APPLY_TO_SELECTION ){
				this.changeTempo(context, lastHeader, lastHeader.getTempo());
			}
		}
	}

	private TGMeasureHeader findStartHeader(TGActionContext context, TGSong tgSong, int applyTo){
		long startTick = this.findStartTick(context, applyTo);
		return (startTick > 0) ? getSongManager(context).getMeasureHeaderAt(tgSong, startTick) : null;
	}

	private long findStartTick(TGActionContext context, int applyTo){
		switch (applyTo) {
		case APPLY_TO_ALL:
			return TGDuration.QUARTER_TIME;
		case APPLY_TO_NEXT:
		case APPLY_TO_END:
			return ((TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)).getStart();
		case APPLY_TO_SELECTION:
			List<TGBeat> beats = getSelectedBeats(context);
			return beats != null ? beats.get(0).getStart() : INVALID;
		default:
			return INVALID;
		}
	}

	private int findEndMeasureNumber(TGActionContext context, TGSong tgSong, int applyTo){
		if( applyTo == APPLY_TO_SELECTION ){
			// only called once a start header was found, so the selection is not empty
			List<TGBeat> beats = getSelectedBeats(context);
			return beats.get(beats.size() - 1).getMeasure().getNumber();
		}
		return tgSong.countMeasureHeaders();
	}

	private List<TGBeat> getSelectedBeats(TGActionContext context){
		TGBeatRange beatRange = (TGBeatRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		return (beatRange != null && !beatRange.isEmpty()) ? beatRange.getBeats() : null;
	}

	/**
	 * Applies the tempo from startHeader on, while headers are accepted.
	 * Returns the header where it stopped (the first rejected one), or the last header of the song.
	 */
	private TGMeasureHeader applyTempo(TGActionContext context, TGSong tgSong, TGMeasureHeader startHeader, int endMeasureNumber, int applyTo, TGTempo tgTempo){
		TGSongManager tgSongManager = getSongManager(context);
		TGTempo oldTempo = startHeader.getTempo().clone(tgSongManager.getFactory());
		TGMeasureHeader lastHeader = startHeader;
		for( TGMeasureHeader nextHeader : tgSongManager.getMeasureHeadersAfter(tgSong, startHeader.getNumber() - 1) ){
			lastHeader = nextHeader;
			if(!this.accepts(nextHeader, endMeasureNumber, applyTo, oldTempo)){
				break;
			}
			this.changeTempo(context, nextHeader, tgTempo);
		}
		return lastHeader;
	}

	private boolean accepts(TGMeasureHeader header, int endMeasureNumber, int applyTo, TGTempo oldTempo){
		return (header.getNumber() <= endMeasureNumber) && (applyTo != APPLY_TO_NEXT || header.getTempo().isEqual(oldTempo));
	}

	private void changeTempo(TGActionContext context, TGMeasureHeader header, TGTempo tgTempo){
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TEMPO, tgTempo);
		TGActionManager.getInstance(getContext()).execute(TGChangeTempoAction.NAME, context);
	}
}
