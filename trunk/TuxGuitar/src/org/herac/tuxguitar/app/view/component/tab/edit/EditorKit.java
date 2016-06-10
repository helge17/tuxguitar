package org.herac.tuxguitar.app.view.component.tab.edit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGNoteImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.graphics.control.TGTrackSpacing;
import org.herac.tuxguitar.graphics.control.TGVoiceImpl;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGAbstractContext;

public class EditorKit {
	
	public static final String ATTRIBUTE_X = "editorKit-x";
	public static final String ATTRIBUTE_Y = "editorKit-y";
	
	public static final int MOUSE_MODE_SELECTION = 1;
	public static final int MOUSE_MODE_EDITION = 2;
	
	private static final int FIRST_LINE_VALUES[] = new int[] {65,45,52,55};
	
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
		float x = context.getAttribute(ATTRIBUTE_X);
		float y = context.getAttribute(ATTRIBUTE_Y);
		if( x >= 0 && y >= 0 ){
			TGTrackImpl track = findSelectedTrack(y);
			if (track != null) {
				TGMeasureImpl measure = findSelectedMeasure(track, x, y);
				if (measure != null) {
					TGBeat beat = findSelectedBeat(measure, x);
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
	
	private TGBeatImpl findSelectedBeat(TGMeasureImpl measure, float x){
		TGLayout layout = getTablature().getViewLayout();
		int voice = getTablature().getCaret().getVoice();
		float posX = measure.getHeaderImpl().getLeftSpacing(layout) + measure.getPosX();
		float bestDiff = -1;
		TGBeatImpl bestBeat = null;
		Iterator<TGBeat> it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeatImpl beat = (TGBeatImpl)it.next();
			if(!beat.getVoice(voice).isEmpty()){
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
		
//		if(!this.getTablature().isPainting()){
			TGLayout.TrackPosition pos = this.getTablature().getViewLayout().getTrackPositionAt(y) ;
			if( pos != null){
				TGTrackImpl track = this.getTablature().getCaret().getTrack();
				TGMeasureImpl measure = this.getTablature().getCaret().getMeasure();
				if(measure.getTs() != null){
					int minValue = track.getString(track.stringCount()).getValue();
					int maxValue = track.getString(1).getValue() + 29; //Max frets = 29
					
					float lineSpacing = this.getTablature().getViewLayout().getScoreLineSpacing();
					
					float topHeight = measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
					float bottomHeight = (measure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_DOWN_LINES));
					
					float y1 = (pos.getPosY() + measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
					float y2 = (y1 + (lineSpacing * 5));
					
					if(y >= (y1 - topHeight) && y  < (y2 + bottomHeight)){
						
						int value = 0;
						int tempValue = FIRST_LINE_VALUES[measure.getClef() - 1];
						double limit = (topHeight / (lineSpacing / 2.00));
						for(int i = 0;i < limit;i ++){
							tempValue += (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue + 1) % 12])?2:1;
						}
						
						float minorDistance = 0;
						for(float posY = (y1 - topHeight); posY <= (y2 + bottomHeight); posY += (lineSpacing / 2.00)){
							if(tempValue > 0){
								float distanceY = Math.abs(y - posY);
								if(value == 0 || distanceY < minorDistance){
									value = tempValue;
									minorDistance = distanceY;
								}
								tempValue -= (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue - 1) % 12])?2:1;
							}
						}
						if(value >= minValue && value <= maxValue){
							TGVoiceImpl voice = findBestVoice(measure, x);
							if( voice != null ){
								value = getRealValue(value);
								
								context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, voice);
								context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VALUE, value);
								return true;
							}
						}
					}
				}
			}
//		}
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
	
	private int getRealValue(int value){
		int realValue = value;
		int key = this.getTablature().getCaret().getMeasure().getKeySignature();
		if(key <= 7){
			if(TGMeasureImpl.KEY_SIGNATURES[key][TGMeasureImpl.ACCIDENTAL_SHARP_NOTES[realValue % 12]] == TGMeasureImpl.SHARP && this.isNatural()){
				realValue ++;
			}
			else if(TGMeasureImpl.KEY_SIGNATURES[key][TGMeasureImpl.ACCIDENTAL_SHARP_NOTES[realValue % 12]] != TGMeasureImpl.SHARP && !this.isNatural()){
				if(TGMeasureImpl.ACCIDENTAL_NOTES[(realValue + 1) % 12]){
					realValue ++;
				}
			}
		}else if(key > 7 ){
			if(TGMeasureImpl.KEY_SIGNATURES[key][TGMeasureImpl.ACCIDENTAL_FLAT_NOTES[realValue % 12]] == TGMeasureImpl.FLAT && this.isNatural()){
				realValue --;
			}
			else if(TGMeasureImpl.KEY_SIGNATURES[key][TGMeasureImpl.ACCIDENTAL_FLAT_NOTES[realValue % 12]] != TGMeasureImpl.FLAT && !this.isNatural()){
				if(TGMeasureImpl.ACCIDENTAL_NOTES[(realValue - 1) % 12]){
					realValue --;
				}
			}
		}
		return realValue;
	}
	
	public boolean fillRemoveNoteContext(TGAbstractContext context) {
		TGVoice voice = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		Integer value = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VALUE);
		
		Iterator<TGNote> it = voice.getNotes().iterator();
		while (it.hasNext()) {
			TGNoteImpl note = (TGNoteImpl) it.next();
			
			if (note.getRealValue() == value.intValue()) {
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
	
	public void paintSelection(TGLayout layout, TGPainter painter) {
		if(!TuxGuitar.getInstance().getPlayer().isRunning()){
			TGMeasureImpl measure = this.selectedMeasure;
			if( measure != null && measure.getTs() != null && measure.getTrack().stringCount() > 0 ){
				float scale = layout.getScale();
				int minValue = measure.getTrack().getString(measure.getTrack().stringCount()).getValue();
				int maxValue = measure.getTrack().getString(1).getValue() + 29;
				float lineSpacing = layout.getScoreLineSpacing();
				float width = (int)(10.0f * scale);
				float topHeight = measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
				float bottomHeight = (measure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_DOWN_LINES));
				int tempValue = 0;
				
				float x1 = 0;
				float x2 = 0;
				float y1 = (measure.getPosY() + measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
				float y2 = (y1 + (lineSpacing * 5));
				
				for(int b = 0 ; b < measure.countBeats() ; b++ ){
					TGBeatImpl beat = (TGBeatImpl)measure.getBeat(b);
					if( isPaintableBeat(beat) ){
						x1 = (measure.getHeaderImpl().getLeftSpacing(layout) + measure.getPosX() + beat.getPosX() + beat.getSpacing(layout));
						x2 = x1 + width;
						
						painter.setForeground(layout.getResources().getLineColor());
						
						tempValue = FIRST_LINE_VALUES[measure.getClef() - 1];
						for(float y = (y1 - lineSpacing); y >= (y1 - topHeight); y -= lineSpacing){
							tempValue += (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue + 1) % 12])?2:1;
							tempValue += (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue + 1) % 12])?2:1;
							if( tempValue > maxValue ){
								break;
							}
							painter.initPath();
							painter.setAntialias(false);
							painter.moveTo(x1, y);
							painter.lineTo(x2, y);
							painter.closePath();
						}
						
						tempValue = FIRST_LINE_VALUES[measure.getClef() - 1] - 14;
						for(float y = y2; y <= (y2 + bottomHeight); y += lineSpacing){
							if(tempValue > 0){
								tempValue -= (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue - 1) % 12])?2:1;
								tempValue -= (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue - 1) % 12])?2:1;
								if( tempValue < minValue ){
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
	}
	
	private boolean isPaintableBeat(TGBeat beat){
		if( beat.getStart() == beat.getMeasure().getStart() ){
			return true;
		}
		return (!beat.getVoice(getTablature().getCaret().getVoice()).isEmpty());
	}
}
