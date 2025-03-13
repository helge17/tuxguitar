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
import app.tuxguitar.util.TGContext;

public class TGChangeTempoRangeAction extends TGActionBase {

	public static final String NAME = "action.composition.change-tempo-range";

	public static final String ATTRIBUTE_APPLY_TO = "applyTo";
	public static final String ATTRIBUTE_TEMPO = "tempoValue";
	public static final String ATTRIBUTE_TEMPO_BASE = "tempoBase";
	public static final String ATTRIBUTE_TEMPO_BASE_DOTTED = "tempoBaseDotted";

	public static final int MIN_TEMPO = 30;
	public static final int MAX_TEMPO = 320;

	public static final int APPLY_TO_ALL = 1;
	public static final int APPLY_TO_END = 2;
	public static final int APPLY_TO_NEXT = 3;

	public TGChangeTempoRangeAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		int applyTo = ((Integer) context.getAttribute(ATTRIBUTE_APPLY_TO)).intValue();
		int tempoValue = ((Integer) context.getAttribute(ATTRIBUTE_TEMPO)).intValue();
		int tempoBase = ((Integer) context.getAttribute(ATTRIBUTE_TEMPO_BASE)).intValue();
		boolean tempoBaseDotted = ((Boolean) context.getAttribute(ATTRIBUTE_TEMPO_BASE_DOTTED)).booleanValue();
		if( tempoValue >= MIN_TEMPO && MAX_TEMPO <= 320 ){
			TGSongManager tgSongManager = getSongManager(context);
			TGSong tgSong = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
			TGMeasureHeader tgHeader = (TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
			TGTempo tgTempo = tgSongManager.getFactory().newTempo();
			tgTempo.setValueBase(tempoValue, tempoBase, tempoBaseDotted);

			long start = (applyTo == APPLY_TO_ALL ? TGDuration.QUARTER_TIME : ((TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)).getStart());
			boolean toEnd = (applyTo == APPLY_TO_ALL || applyTo == APPLY_TO_END);

			TGMeasureHeader startHeader = tgSongManager.getMeasureHeaderAt(tgSong, start);
			if( startHeader != null ) {
				TGTempo oldTempo = startHeader.getTempo();
				Iterator<?> it = tgSongManager.getMeasureHeadersAfter(tgSong, startHeader.getNumber() - 1).iterator();
				while(it.hasNext()){
					TGMeasureHeader nextHeader = (TGMeasureHeader)it.next();
					if( toEnd || nextHeader.getTempo().isEqual(oldTempo) ){
						context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, nextHeader);
						context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TEMPO, tgTempo);

						TGActionManager.getInstance(getContext()).execute(TGChangeTempoAction.NAME, context);
					} else{
						break;
					}
				}
			}

			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, tgHeader);
		}
	}
}
