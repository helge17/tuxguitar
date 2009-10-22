package org.herac.tuxguitar.gui.editors.tab.edit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.editors.tab.TGBeatImpl;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.editors.tab.TGNoteImpl;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.editors.tab.TGTrackSpacing;
import org.herac.tuxguitar.gui.editors.tab.TGVoiceImpl;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;

public class MouseKit {
	private static final int FIRST_LINE_VALUES[] = new int[] {65,45,52,55};
	
	private EditorKit kit;
	private Image back = null;
	private int lastx;
	private int lasty;
	
	public MouseKit(EditorKit kit){
		this.kit = kit;
	}
	
	private ViewLayout.TrackPosition getTrackPosition(int y) {
		return this.kit.getTablature().getViewLayout().getTrackPositionAt(y);
	}
	
	public void tryBack(){
		if(!TuxGuitar.instance().isLocked() && !ActionLock.isLocked() && !this.kit.getTablature().isPainting()){
			TGPainter painter = new TGPainter(new GC(this.kit.getTablature()));
			if(this.back != null && !this.back.isDisposed()){
				painter.drawImage(this.back,this.lastx,this.lasty);
			}
			painter.dispose();
		}
		if(this.back != null){
			this.back.dispose();
		}
	}
	
	public void mouseExit() {
		tryBack();
	}
	
	public void mouseMove(MouseEvent e) {
		this.tryBack();
		if(!TuxGuitar.instance().isLocked() && !ActionLock.isLocked() && !this.kit.getTablature().isPainting()){
			
			TGTrackImpl track = this.kit.findSelectedTrack(e.y);
			if (track != null) {
				TGMeasureImpl measure = this.kit.findSelectedMeasure(track,e.x,e.y);
				
				if(measure != null && measure.getTs() != null){
					float scale = this.kit.getTablature().getViewLayout().getScale();
					int minValue = track.getString(track.stringCount()).getValue();
					int maxValue = track.getString(1).getValue() + 29; //Max frets = 29
					int tempValue = FIRST_LINE_VALUES[measure.getClef() - 1];
					int lineSpacing = this.kit.getTablature().getViewLayout().getScoreLineSpacing();
					int width = (int)(10.0f * scale);
					int topHeight = measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
					int bottomHeight = (measure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_DOWN_LINES));
					
					int x1 = (int)(e.x - (width / 2.0f));
					int x2 = (int)(e.x + (width / 2.0f));
					int y1 = (measure.getPosY() + measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
					int y2 = (y1 + (lineSpacing * 5));
					
					if(e.y < (y1 + 3) && e.y >= (y1 - topHeight)){
						this.back = new Image(TuxGuitar.instance().getDisplay(),width + 1,topHeight + 1);
						TGPainter painter = new TGPainter(new GC(this.kit.getTablature()));
						painter.copyArea(this.back,x1, (y1 - topHeight));
						painter.setForeground(this.kit.getTablature().getViewLayout().getResources().getLineColor());
						for(int y = (y1 - lineSpacing); y >= (y1 - topHeight); y -= lineSpacing){
							tempValue += (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue + 1) % 12])?2:1;
							tempValue += (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue + 1) % 12])?2:1;
							if(y < e.y - 5 || tempValue > maxValue){
								break;
							}
							painter.initPath();
							painter.setAntialias(false);
							painter.moveTo(x1, y);
							painter.lineTo(x2, y);
							painter.closePath();
						}
						painter.dispose();
						
						this.lastx = x1;
						this.lasty = (y1 - topHeight);
					}else if(e.y > (y2 - 3) && e.y  < y2 + bottomHeight){
						this.back = new Image(TuxGuitar.instance().getDisplay(),width + 1,bottomHeight + 1);
						TGPainter painter = new TGPainter(new GC(this.kit.getTablature()));
						painter.copyArea(this.back,x1, y2);	
						painter.setForeground(this.kit.getTablature().getViewLayout().getResources().getLineColor());
						tempValue -= 14;
						for(int y = y2; y <= (y2 + bottomHeight); y += lineSpacing){
							if(tempValue > 0){
								tempValue -= (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue - 1) % 12])?2:1;
								tempValue -= (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue - 1) % 12])?2:1;
								if(y > e.y + 5 || tempValue < minValue){
									break;
								}
								painter.initPath();
								painter.setAntialias(false);
								painter.moveTo(x1, y);
								painter.lineTo(x2, y);
								painter.closePath();
							}
						}
						painter.dispose();
						
						this.lastx = x1;
						this.lasty = y2;
					}
				}
			}
		}
	}
	
	public void mouseUp(MouseEvent e) {
		if(!TuxGuitar.instance().isLocked() && !ActionLock.isLocked() && !this.kit.getTablature().isPainting()){
			ActionLock.lock();
			
			ViewLayout.TrackPosition pos = getTrackPosition(e.y) ;
			if(pos != null){
				TGTrackImpl track = this.kit.getTablature().getCaret().getTrack();
				TGMeasureImpl measure = this.kit.getTablature().getCaret().getMeasure();
				if(measure.getTs() != null){
					int minValue = track.getString(track.stringCount()).getValue();
					int maxValue = track.getString(1).getValue() + 29; //Max frets = 29
					
					int lineSpacing = this.kit.getTablature().getViewLayout().getScoreLineSpacing();
					
					int topHeight = measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES);
					int bottomHeight = (measure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) - measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_DOWN_LINES));
					
					int y1 = (pos.getPosY() + measure.getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
					int y2 = (y1 + (lineSpacing * 5));
					
					if(e.y >= (y1 - topHeight) && e.y  < (y2 + bottomHeight)){
						
						int value = 0;
						int tempValue = FIRST_LINE_VALUES[measure.getClef() - 1];
						double limit = (topHeight / (lineSpacing / 2.00));
						for(int i = 0;i < limit;i ++){
							tempValue += (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue + 1) % 12])?2:1;
						}
						
						float minorDistance = 0;
						for(float y = (y1 - topHeight); y <= (y2 + bottomHeight); y += (lineSpacing / 2.00)){
							if(tempValue > 0){
								float distanceY = Math.abs(e.y - y);
								if(value == 0 || distanceY < minorDistance){
									value = tempValue;
									minorDistance = distanceY;
								}
								tempValue -= (TGMeasureImpl.ACCIDENTAL_NOTES[(tempValue - 1) % 12])?2:1;
							}
						}
						if(value >= minValue && value <= maxValue){
							TGVoiceImpl beat = findBestVoice(measure, e.x);
							if(beat != null){
								value = getRealValue(value);
								if(!removeNote(value,beat)){
									makeNote(beat, getRealStart(beat, e.x), value);
								}
							}
							redrawTablature();
						}
					}
				}
			}
			ActionLock.unlock();
		}
	}
	
	private long getRealStart(TGVoiceImpl voice,int x){
		if(voice.isEmpty()){
			return voice.getBeat().getStart();
		}
		TGMeasureImpl measure = voice.getBeatImpl().getMeasureImpl();
		long beatX = (measure.getHeaderImpl().getLeftSpacing( this.kit.getTablature().getViewLayout() ) + measure.getPosX() + voice.getBeatImpl().getPosX() + voice.getBeatImpl().getSpacing());
		long beatStart = voice.getBeat().getStart();
		long beatLength = voice.getDuration().getTime();
		long beatEnd = ( beatStart + beatLength );
		if(x > beatX){
			return Math.min( ( beatStart + ( (x - beatX) * beatLength / voice.getWidth() ) ), (beatEnd - 1 ) );
		}
		return beatStart;
	}
	
	private int getRealValue(int value){
		int realValue = value;
		int key = this.kit.getTablature().getCaret().getMeasure().getKeySignature();
		if(key <= 7){
			if(TGMeasureImpl.KEY_SIGNATURES[key][TGMeasureImpl.ACCIDENTAL_SHARP_NOTES[realValue % 12]] == TGMeasureImpl.SHARP && this.kit.isNatural()){
				realValue ++;
			}
			else if(TGMeasureImpl.KEY_SIGNATURES[key][TGMeasureImpl.ACCIDENTAL_SHARP_NOTES[realValue % 12]] != TGMeasureImpl.SHARP && !this.kit.isNatural()){
				if(TGMeasureImpl.ACCIDENTAL_NOTES[(realValue + 1) % 12]){
					realValue ++;
				}
			}
		}else if(key > 7 ){
			if(TGMeasureImpl.KEY_SIGNATURES[key][TGMeasureImpl.ACCIDENTAL_FLAT_NOTES[realValue % 12]] == TGMeasureImpl.FLAT && this.kit.isNatural()){
				realValue --;
			}
			else if(TGMeasureImpl.KEY_SIGNATURES[key][TGMeasureImpl.ACCIDENTAL_FLAT_NOTES[realValue % 12]] != TGMeasureImpl.FLAT && !this.kit.isNatural()){
				if(TGMeasureImpl.ACCIDENTAL_NOTES[(realValue - 1) % 12]){
					realValue --;
				}
			}
		}
		return realValue;
	}
	
	private boolean removeNote(int value,TGVoice voice) {
		Iterator it = voice.getNotes().iterator();
		while (it.hasNext()) {
			TGNoteImpl note = (TGNoteImpl) it.next();
			
			if (note.getRealValue() == value) {
				//comienza el undoable
				UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
				
				TGSongManager manager = this.kit.getTablature().getSongManager();
				manager.getMeasureManager().removeNote(note);
				
				//termia el undoable
				TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo());
				TuxGuitar.instance().getFileHistory().setUnsavedFile();
				
				return true;
			}
		}
		return false;
	}
	
	private void makeNote(TGVoice voice, long start,  int value){
		Caret caret = this.kit.getTablature().getCaret();
		TGSongManager manager = this.kit.getTablature().getSongManager();
		TGTrack track = caret.getTrack();
		int string = findBestString(track,voice,value);
		if(string > 0){
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			
			TGNote note = manager.getFactory().newNote();
			note.setValue((value - track.getString(string).getValue()));
			note.setVelocity(caret.getVelocity());
			note.setString(string);
			
			TGDuration duration = manager.getFactory().newDuration();
			caret.getDuration().copy(duration);
			
			manager.getMeasureManager().addNote(voice.getBeat(),note,duration, start, voice.getIndex());
			
			caret.moveTo(caret.getTrack(),caret.getMeasure(),note.getVoice().getBeat(),note.getString());
			
			//termia el undoable
			TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo());
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			
			//reprodusco las notas en el pulso
			caret.getSelectedBeat().play();
		}
	}
	
	private void redrawTablature(){
		Caret caret = this.kit.getTablature().getCaret();
		this.kit.getTablature().getViewLayout().fireUpdate(caret.getMeasure().getNumber());
		TuxGuitar.instance().updateCache(true);
	}
	
	private int findBestString(TGTrack track,TGVoice voice,int value){
		List strings = new ArrayList();
		for(int number = 1;number <= track.stringCount();number++){
			boolean used = false;
			TGString string = track.getString(number);
			Iterator it = voice.getNotes().iterator();
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
		int stringForValue = 0;
		for(int i = 0;i < strings.size();i++){
			TGString string = (TGString)strings.get(i);
			int fret = value - string.getValue();
			if((fret >= 0) && (minFret < 0 || fret < minFret)){
				stringForValue = string.getNumber();
				minFret = fret;
			}
		}
		return stringForValue;
	}
	
	public TGVoiceImpl findBestVoice(TGMeasureImpl measure, int x){
		int voiceIndex = this.kit.getTablature().getCaret().getVoice();
		int posX = measure.getHeaderImpl().getLeftSpacing( this.kit.getTablature().getViewLayout() ) + measure.getPosX();
		int bestDiff = -1;
		TGVoiceImpl bestVoice = null;
		TGDuration duration = this.kit.getTablature().getCaret().getDuration();
		Iterator it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeatImpl beat = (TGBeatImpl)it.next();
			TGVoiceImpl voice = beat.getVoiceImpl( voiceIndex );
			if(!voice.isEmpty()){
				int x1 = (beat.getPosX() + beat.getSpacing());
				int x2 = (x1 + voice.getWidth());
				long increment = voice.getWidth();
				if(voice.isRestVoice()){
					increment = (duration.getTime() * voice.getWidth() / voice.getDuration().getTime());
				}
				for( int beatX = x1 ; beatX < x2 ; beatX += increment ){
					int diff = Math.abs(x - (posX + beatX));
					if(bestDiff == -1 || diff < bestDiff){
						bestVoice = voice;
						bestDiff = diff;
					}
				}
			}
		}
		if( bestVoice == null ){
			TGBeat beat = this.kit.getTablature().getViewLayout().getSongManager().getMeasureManager().getFirstBeat(measure.getBeats());
			if( beat != null ){
				bestVoice = (TGVoiceImpl)beat.getVoice(voiceIndex);
			}
		}
		return bestVoice;
	}
}
