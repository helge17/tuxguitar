package app.tuxguitar.app.view.component.tab.edit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.graphics.control.TGBeatImpl;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGMeasureImpl;
import app.tuxguitar.graphics.control.TGNoteImpl;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.graphics.control.TGTrackSpacing;
import app.tuxguitar.graphics.control.TGVoiceImpl;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.util.TGAbstractContext;
import app.tuxguitar.util.TGMusicKeyUtils;

public class EditorKit {

	public static final String ATTRIBUTE_X = "editorKit-x";
	public static final String ATTRIBUTE_Y = "editorKit-y";

	public static final int MOUSE_MODE_SELECTION = 1;
	public static final int MOUSE_MODE_EDITION = 2;

	// TODO: move these clef-specific attributes in a dedicated class representing Clef object
	// first line note: treble -> F4, bass -> A2, tenor -> E3, alto -> G3
	private static final int FIRST_LINE_NOTE_INDEX[] = new int[] {3,5,2,4};
	private static final int FIRST_LINE_NOTE_OCTAVE[] = new int[] {4,2,3,3};

	private int mouseMode;
	private boolean natural;
	private Tablature tablature;
	private MouseKit mouseKit;
	private TGMeasureImpl selectedMeasure;

	public EditorKit(Tablature tablature){
		this.tablature = tablature;
		this.mouseKit = new MouseKit(this);
		this.setDefaults();
	}

	private void setDefaults(){
		this.setMouseMode(TuxGuitar.getInstance().getConfig().getIntegerValue(TGConfigKeys.EDITOR_MOUSE_MODE,MOUSE_MODE_EDITION));
		this.setNatural(TuxGuitar.getInstance().getConfig().getBooleanValue(TGConfigKeys.EDITOR_NATURAL_KEY_MODE,true));
	}

	public int getMouseMode() {
		return this.mouseMode;
	}

	public void setMouseMode(int mouseMode) {
		this.mouseMode = mouseMode;
	}

	public boolean isNatural() {
		return this.natural;
	}

	public void setNatural(boolean natural) {
		this.natural = natural;
	}

	public Tablature getTablature() {
		return this.tablature;
	}

	public MouseKit getMouseKit(){
		return this.mouseKit;
	}

	public boolean isScoreEnabled(){
		return ((getTablature().getViewLayout().getStyle() & TGLayout.DISPLAY_SCORE) != 0);
	}

	public boolean isMouseEditionAvailable(){
		return (isScoreEnabled() && getMouseMode() == MOUSE_MODE_EDITION);
	}

	public boolean fillSelection(TGAbstractContext context) {
		return this.fillSelection(context, false);
	}

	public boolean fillSelection(TGAbstractContext context, boolean ignoreVoice) {
		float x = context.getAttribute(ATTRIBUTE_X);
		float y = context.getAttribute(ATTRIBUTE_Y);
		if( x >= 0 && y >= 0 ){
			TGTrackImpl track = findSelectedTrack(y);
			if (track != null) {
				TGMeasureImpl measure = findSelectedMeasure(track, x, y);
				if (measure != null) {
					TGBeat beat = findSelectedBeat(measure, x, ignoreVoice);
					if( beat != null ) {
						TGString string = findSelectedString(measure, y);
						if( string == null ) {
							string = this.getTablature().getCaret().getSelectedString();
						}

						context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
						context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
						context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, measure.getHeader());
						context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
						context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);

						return true;
					}
				}
			}
		}
		return false;
	}

	// min distance to initialize a click&drag selection
	public boolean isMinDragWidthReached(TGAbstractContext context) {
		Float startPositionX = this.mouseKit.getStartPositionX();
		if (startPositionX != null) {
			float x = context.getAttribute(ATTRIBUTE_X);
			TGLayout layout = getTablature().getViewLayout();
			float minDistanceX = layout.getMinimumDurationWidth() / 3.0f;
			return (Math.abs(x-startPositionX) >= minDistanceX);
		}
		return false;
	}

	private TGTrackImpl findSelectedTrack(float y){
		TGLayout layout = getTablature().getViewLayout();
		int number = layout.getTrackNumberAt(y);
		if( number >= 0){
			return (TGTrackImpl)layout.getSongManager().getTrack(getTablature().getSong(), number);
		}
		return null;
	}

	private TGMeasureImpl findSelectedMeasure(TGTrackImpl track, float x, float y){
		TGMeasureImpl measure = null;
		float minorDistance = 0;

		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasureImpl m = (TGMeasureImpl)it.next();
			if(!m.isOutOfBounds() && m.getTs() != null){
				boolean isAtX = (x >= m.getPosX() && x <= m.getPosX() + m.getWidth(getTablature().getViewLayout()) + m.getSpacing());
				if(isAtX){
					float measureHeight = m.getTs().getSize();
					float distanceY = Math.min(Math.abs(y - (m.getPosY())),Math.abs(y - ( m.getPosY() + measureHeight - 10)));
					if(measure == null || distanceY < minorDistance){
						measure = m;
						minorDistance = distanceY;
					}
				}
			}
		}
		return measure;
	}

	private TGBeatImpl findSelectedBeat(TGMeasureImpl measure, float x, boolean ignoreVoice){
		TGLayout layout = getTablature().getViewLayout();
		int voice = getTablature().getCaret().getVoice();
		float posX = measure.getHeaderImpl().getLeftSpacing(layout) + measure.getPosX();
		float bestDiff = -1;
		TGBeatImpl bestBeat = null;
		Iterator<TGBeat> it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeatImpl beat = (TGBeatImpl)it.next();
			if(ignoreVoice || !beat.getVoice(voice).isEmpty()){
				float diff = Math.abs(x - (posX + (beat.getPosX() + beat.getSpacing(layout))));
				if(bestDiff == -1 || diff < bestDiff){
					bestBeat = beat;
					bestDiff = diff;
				}
			}
		}
		if( bestBeat == null ){
			bestBeat = (TGBeatImpl) layout.getSongManager().getMeasureManager().getFirstBeat(measure.getBeats());
		}
		return bestBeat;
	}

	private TGString findSelectedString(TGMeasureImpl measure, float y) {
		TGString string = null;
		float stringSpacing = getTablature().getViewLayout().getStringSpacing();
		float minorDistance = 0;
		float firstStringY = measure.getPosY() + measure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE);

		Iterator<TGString> it = measure.getTrack().getStrings().iterator();
		while(it.hasNext()){
			TGString currString = (TGString)it.next();
			float distanceX = Math.abs(y - (firstStringY + ((currString.getNumber() * stringSpacing) - stringSpacing)));
			if(string == null || distanceX < minorDistance){
				string = currString;
				minorDistance = distanceX;
			}
		}

		return string;
	}

	private TGString findBestString(TGTrack track,TGVoice voice, int value){
		List<TGString> strings = new ArrayList<TGString>();
		for(int number = 1;number <= track.stringCount();number++){
			boolean used = false;
			TGString string = track.getString(number);
			Iterator<TGNote> it = voice.getNotes().iterator();
			while (it.hasNext()) {
				TGNote note = (TGNote) it.next();
				if(note.getString() == string.getNumber()){
					used = true;
				}
			}
			if(!used){
				strings.add(string);
			}
		}

		int minFret = -1;
		TGString stringForValue = null;
		for(int i = 0;i < strings.size();i++){
			TGString string = (TGString)strings.get(i);
			int fret = value - string.getValue();
			if((fret >= 0) && (minFret < 0 || fret < minFret)){
				stringForValue = string;
				minFret = fret;
			}
		}
		return stringForValue;
	}

	private TGVoiceImpl findBestVoice(TGMeasureImpl measure, float x){
		TGLayout layout = getTablature().getViewLayout();
		int voiceIndex = this.getTablature().getCaret().getVoice();
		float posX = measure.getHeaderImpl().getLeftSpacing( layout ) + measure.getPosX();
		float bestDiff = -1;
		TGVoiceImpl bestVoice = null;
		TGDuration duration = this.getTablature().getCaret().getDuration();
		Iterator<TGBeat> it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeatImpl beat = (TGBeatImpl)it.next();
			TGVoiceImpl voice = beat.getVoiceImpl( voiceIndex );
			if(!voice.isEmpty()){
				float x1 = (beat.getPosX() + beat.getSpacing(layout));
				float x2 = (x1 + voice.getWidth());
				float increment = voice.getWidth();
				if(voice.isRestVoice()){
					increment = (duration.getTime() * voice.getWidth() / voice.getDuration().getTime());
				}
				for( float beatX = x1 ; beatX < x2 ; beatX += increment ){
					float diff = Math.abs(x - (posX + beatX));
					if(bestDiff == -1 || diff < bestDiff){
						bestVoice = voice;
						bestDiff = diff;
					}
				}
			}
		}
		if( bestVoice == null ){
			TGBeat beat = layout.getSongManager().getMeasureManager().getFirstBeat(measure.getBeats());
			if( beat != null ){
				bestVoice = (TGVoiceImpl)beat.getVoice(voiceIndex);
			}
		}
		return bestVoice;
	}

	public boolean fillAddOrRemoveBeat(TGAbstractContext context) {
		float x = context.getAttribute(ATTRIBUTE_X);
		float y = context.getAttribute(ATTRIBUTE_Y);

		TGLayout.TrackPosition pos = this.getTablature().getViewLayout().getTrackPositionAt(y) ;
		if( pos != null){
			TGTrackImpl track = this.getTablature().getCaret().getTrack();
			TGMeasureImpl measure = this.getTablature().getCaret().getMeasure();
			if(measure.getTs() != null){
				int minValue = track.getString(track.stringCount()).getValue();
				int maxValue = track.getString(1).getValue() + track.getMaxFret();
				float lineSpacing = this.getTablature().getViewLayout().getScoreLineSpacing();
				float topHeight = measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
				float bottomHeight = (measure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_DOWN_LINES));
				float yFirstLine = (pos.getPosY() + measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
				float yLastLine = (yFirstLine + (lineSpacing * 4));

				if(y >= (yFirstLine - topHeight) && y  < (yLastLine + bottomHeight)){
					int keySignature = measure.getKeySignature();
					// find note pitch, by comparison with first line note
					int noteIndexFirstLine = FIRST_LINE_NOTE_INDEX[measure.getClef()-1];
					int octaveFirstLine = FIRST_LINE_NOTE_OCTAVE[measure.getClef()-1];
					// distance of selected note to first line note, in number of notes (1 note = lineSpacing/2)
					int noteIndexOffset = Math.round(2 * (yFirstLine - y) / lineSpacing);
					// get selected note index and octave
					int noteIndex = TGMusicKeyUtils.noteIndexAddInterval(noteIndexFirstLine, noteIndexOffset);
					int noteOctave =TGMusicKeyUtils.noteOctaveAddInterval(noteIndexFirstLine, octaveFirstLine, noteIndexOffset);
					// get selected note value, considering alteration and edition mode
					int noteValue = TGMusicKeyUtils.midiNote(noteIndex, noteOctave);
					int noteAlteration = TGMusicKeyUtils.noteIndexAlteration(noteIndex, keySignature);
					if (isNatural()) {
						// normal edition mode, keep note alteration
						if (noteAlteration == TGMusicKeyUtils.SHARP) {
							noteValue++;
						} else if (noteAlteration == TGMusicKeyUtils.FLAT) {
							noteValue--;
						}
					// sharp/flat edition mode: don't consider alteration if present, and add one if absent
					} else if (noteAlteration == TGMusicKeyUtils.NATURAL) {
						if (keySignature<=7) {
							noteValue++;
						} else {
							noteValue--;
						}
					}
					if(noteValue >= minValue && noteValue <= maxValue){
						TGVoiceImpl voice = findBestVoice(measure, x);
						if( voice != null ){
							context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, voice);
							context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VALUE, noteValue);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private long getRealStart(TGVoiceImpl voice, float x){
		if(voice.isEmpty()){
			return voice.getBeat().getStart();
		}
		TGLayout layout = getTablature().getViewLayout();
		TGMeasureImpl measure = voice.getBeatImpl().getMeasureImpl();

		long beatStart = voice.getBeat().getStart();
		float beatX = (measure.getHeaderImpl().getLeftSpacing( layout ) + measure.getPosX() + voice.getBeatImpl().getPosX() + voice.getBeatImpl().getSpacing(layout));
		if( x > beatX ){
			long beatLength = voice.getDuration().getTime();
			long beatEnd = ( beatStart + beatLength );

			return Math.min( ( beatStart + ( Math.round(x - beatX) * beatLength / Math.round(voice.getWidth()) ) ), (beatEnd - 1 ) );
		}
		return beatStart;
	}

	public boolean fillRemoveNoteContext(TGAbstractContext context) {
		TGLayout layout = getTablature().getViewLayout();
		TGVoice voice = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		Integer value = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VALUE);

		Iterator<TGNote> it = voice.getNotes().iterator();
		while (it.hasNext()) {
			TGNoteImpl note = (TGNoteImpl) it.next();

			if (layout.getSongManager().getMeasureManager().getRealNoteValue(note) == value.intValue()) {
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);

				return true;
			}
		}
		return false;
	}

	public boolean fillCreateNoteContext(TGAbstractContext targetContext) {
		TGVoiceImpl voice = targetContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		Integer value = targetContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VALUE);
		float x = targetContext.getAttribute(ATTRIBUTE_X);
		long start = getRealStart(voice, x);

		Caret caret = this.getTablature().getCaret();
		TGTrack track = caret.getTrack();
		TGString string = findBestString(track, voice, value);
		if( string != null ){
			targetContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, start);
			targetContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, (value - string.getValue()));
			targetContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, voice);
			targetContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
			targetContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, voice.getBeat());

			return true;
		}
		return false;
	}

	public boolean updateSelectedMeasure(TGAbstractContext context){
		float x = context.getAttribute(ATTRIBUTE_X);
		float y = context.getAttribute(ATTRIBUTE_Y);
		TGMeasureImpl previousSelection = this.selectedMeasure;

		TGTrackImpl track = this.findSelectedTrack(y);
		if( track != null ) {
			this.selectedMeasure = this.findSelectedMeasure(track, x, y);
		}

		boolean selectionUpdated = false;

		if(!selectionUpdated && this.selectedMeasure == null && previousSelection != null ){
			selectionUpdated = true;
		}
		if(!selectionUpdated && this.selectedMeasure != null && previousSelection == null ){
			selectionUpdated = true;
		}
		if(!selectionUpdated && this.selectedMeasure != null ){
			selectionUpdated = !this.selectedMeasure.equals(previousSelection);
		}

		return selectionUpdated;
	}

	public void resetSelectedMeasure(){
		this.selectedMeasure = null;
	}

	// draw horizontal segments above and below score
	public void paintSelection(TGLayout layout, UIPainter painter) {
		if(!TuxGuitar.getInstance().getPlayer().isRunning()){
			TGMeasureImpl measure = this.selectedMeasure;
			if( measure != null && measure.getTs() != null && measure.getTrack().stringCount() > 0 ){
				float scale = layout.getScale();
				int minValue = measure.getTrack().getString(measure.getTrack().stringCount()).getValue();
				int maxValue = measure.getTrack().getString(1).getValue() + measure.getTrack().getMaxFret();
				float lineSpacing = layout.getScoreLineSpacing();
				float width = (int)(10.0f * scale);
				float topHeight = measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
				float bottomHeight = (measure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_DOWN_LINES));

				float x1 = 0;
				float x2 = 0;
				float yFirstLine = (measure.getPosY() + measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
				float yLastLine = (yFirstLine + (lineSpacing * 4));

				for(int b = 0 ; b < measure.countBeats() ; b++ ){
					TGBeatImpl beat = (TGBeatImpl)measure.getBeat(b);
					if( isPaintableBeat(beat) ){
						x1 = (measure.getHeaderImpl().getLeftSpacing(layout) + measure.getPosX() + beat.getPosX() + beat.getSpacing(layout));
						x2 = x1 + width;

						painter.setForeground(layout.getResources().getLineColor());
						// reference: first line of score, move up line per line (2 notes) until too high
						int noteIndex=FIRST_LINE_NOTE_INDEX[measure.getClef()-1];
						int noteOctave=FIRST_LINE_NOTE_OCTAVE[measure.getClef() -1];
						for(float y = (yFirstLine - lineSpacing); y >= (yFirstLine - topHeight); y -= lineSpacing){
							noteOctave = TGMusicKeyUtils.noteOctaveAddInterval(noteIndex, noteOctave, 2);
							noteIndex = TGMusicKeyUtils.noteIndexAddInterval(noteIndex, 2);
							if (TGMusicKeyUtils.midiNote(noteIndex, noteOctave) > maxValue) {
								break;
							}

							painter.initPath();
							painter.setAntialias(false);
							painter.moveTo(x1, y);
							painter.lineTo(x2, y);
							painter.closePath();
						}
						// reference: last line of score, move down line per line (2 notes) until too low
						noteIndex=TGMusicKeyUtils.noteIndexAddInterval(FIRST_LINE_NOTE_INDEX[measure.getClef()-1], -8);
						noteOctave=TGMusicKeyUtils.noteOctaveAddInterval(
								FIRST_LINE_NOTE_INDEX[measure.getClef()-1], FIRST_LINE_NOTE_OCTAVE[measure.getClef()-1], -8);
						for(float y = yLastLine + lineSpacing; y <= (yLastLine + bottomHeight); y += lineSpacing){
							noteOctave = TGMusicKeyUtils.noteOctaveAddInterval(noteIndex, noteOctave, -2);
							noteIndex = TGMusicKeyUtils.noteIndexAddInterval(noteIndex, -2);
							if (TGMusicKeyUtils.midiNote(noteIndex, noteOctave) < minValue) {
								break;
							}
							painter.initPath();
							painter.setAntialias(false);
							painter.moveTo(x1, y);
							painter.lineTo(x2, y);
							painter.closePath();
						}
					}
				}
			}
		}
	}

	private boolean isPaintableBeat(TGBeat beat){
		if( beat.getStart() == beat.getMeasure().getStart() ){
			return true;
		}
		return (!beat.getVoice(getTablature().getCaret().getVoice()).isEmpty());
	}
}
