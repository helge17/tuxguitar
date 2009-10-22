/*
 * Created on 30-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.tab;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.util.MidiTickUtil;
import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Caret {
	private Tablature tablature;
	private TGTrackImpl selectedTrack;
	private TGMeasureImpl selectedMeasure;
	private TGBeat selectedBeat;
	private TGNote selectedNote;
	private TGDuration selectedDuration;
	private long position;
	private int string;
	private int voice;
	private int velocity;
	private boolean restBeat;
	private boolean changes;
	
	public Caret(Tablature tablature) {
		this.tablature = tablature;
		this.selectedDuration = getSongManager().getFactory().newDuration();
		this.string = 1;
		this.velocity = TGVelocities.DEFAULT;
		this.changes = false;
	}
	
	public synchronized void update(){
		int trackNumber = (this.selectedTrack != null)?this.selectedTrack.getNumber():1;
		update(trackNumber,this.position,this.string);
	}
	
	public synchronized void update(int trackNumber){
		update(trackNumber,this.position,this.string);
	}
	
	public synchronized void update(int trackNumber,long position,int string){
		update(trackNumber, position, string,getVelocity());
	}
	
	public synchronized void update(int trackNumber,long position,int string,int velocity) {
		long realPosition = ((TuxGuitar.instance().getPlayer().isRunning())?MidiTickUtil.getStart(TuxGuitar.instance().getPlayer().getTickPosition()):position);
		TGTrackImpl track = findTrack(trackNumber); 
		TGMeasureImpl measure = findMeasure(realPosition,track);
		TGBeat beat = findBeat(realPosition,measure);
		if(track != null && measure != null && beat != null){
			moveTo(track, measure, beat,string);
		}
		setVelocity(velocity);
	}
	
	public void moveTo(TGTrackImpl selectedTrack, TGMeasureImpl selectedMeasure, TGBeat selectedBeat,int string) {
		this.selectedTrack = selectedTrack;
		this.selectedMeasure = selectedMeasure;
		this.selectedBeat = selectedBeat;
		this.string = string;
		this.updatePosition();
		this.updateDuration();
		this.updateString();
		this.updateNote();
		this.updateBeat();
		this.checkTransport();
		this.setChanges(true);
	}
	
	private TGTrackImpl findTrack(int number){
		TGTrackImpl track = (TGTrackImpl)getSongManager().getTrack(number);
		if(track == null){
			track = (TGTrackImpl)getSongManager().getFirstTrack();
		}
		return track;
	}
	
	private TGMeasureImpl findMeasure(long position,TGTrackImpl track){
		TGMeasureImpl measure = null;
		if(track != null){
			measure = (TGMeasureImpl)getSongManager().getTrackManager().getMeasureAt(track,position);
			if(measure == null){
				measure = (TGMeasureImpl)getSongManager().getTrackManager().getFirstMeasure(track);
			}
		}
		return measure;
	}
	
	private TGBeat findBeat(long position,TGMeasureImpl measure){
		TGBeat beat = null;
		if(measure != null){
			TGMeasureManager manager = getSongManager().getMeasureManager();
			TGVoice voice = manager.getVoiceIn(measure, position, getVoice());
			if( voice != null ){
				beat = voice.getBeat();
			}
			if (beat == null) {
				beat = manager.getFirstBeat(measure.getBeats());
			}
		}
		return beat;
	}
	
	public synchronized void goToTickPosition(){
		long start = MidiTickUtil.getStart(TuxGuitar.instance().getPlayer().getTickPosition());
		this.update(this.selectedTrack.getNumber(),start,this.string);
		this.setChanges(true);
	}
	
	public void paintCaret(ViewLayout layout,TGPainter painter) {
		if(!TuxGuitar.instance().getPlayer().isRunning()){
			if (this.selectedMeasure != null && this.selectedBeat instanceof TGBeatImpl) {
				TGBeatImpl beat = (TGBeatImpl)this.selectedBeat;
				if( (layout.getStyle() & ViewLayout.DISPLAY_TABLATURE) != 0){
					boolean expectedVoice = (getSelectedNote() == null || getSelectedNote().getVoice().getIndex() == getVoice());
					int stringSpacing = this.tablature.getViewLayout().getStringSpacing();
					int leftSpacing = beat.getMeasureImpl().getHeaderImpl().getLeftSpacing(layout);
					int x = this.selectedMeasure.getPosX() + beat.getPosX() + beat.getSpacing() + leftSpacing - 5;
					int y = this.selectedMeasure.getPosY() + this.selectedMeasure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) + ((this.string * stringSpacing) - stringSpacing) - 7;
					int width = 14;
					int height = 14;
					layout.setCaretStyle(painter, expectedVoice);
					painter.initPath();
					painter.setAntialias(false);
					painter.addRectangle(x, y, width, height);
					painter.closePath();
				}
				else if( (layout.getStyle() & ViewLayout.DISPLAY_SCORE) != 0){
					int line = this.tablature.getViewLayout().getScoreLineSpacing();
					int leftSpacing = beat.getMeasureImpl().getHeaderImpl().getLeftSpacing(layout);
					float xMargin = (2.0f * layout.getScale());
					float x1 = this.selectedMeasure.getPosX() + beat.getPosX() + beat.getSpacing() + leftSpacing - xMargin;
					float x2 = (x1 + layout.getResources().getScoreNoteWidth() + xMargin);
					float y1 = this.selectedMeasure.getPosY() + this.selectedMeasure.getTs().getPosition(TGTrackSpacing.POSITION_TOP) - line;
					float y2 = this.selectedMeasure.getPosY() + this.selectedMeasure.getTs().getPosition(TGTrackSpacing.POSITION_BOTTOM);
					layout.setCaretStyle(painter, true);
					painter.initPath();
					painter.moveTo(x1, y1);
					painter.lineTo(x1 + ((x2 - x1) / 2f), y1 + (line / 2f));
					painter.lineTo(x2, y1);
					painter.moveTo(x1, y2 + line);
					painter.lineTo(x1 + ((x2 - x1) / 2f), y2 + (line / 2f));
					painter.lineTo(x2, y2 + line);
					painter.closePath();
				}
			}
		}
	}
	
	public boolean moveRight() {
		if (getSelectedBeat() != null) {
			TGMeasureImpl measure = getMeasure();
			TGVoice voice = getSongManager().getMeasureManager().getNextVoice(measure.getBeats(),getSelectedBeat(), getVoice());
			TGBeat beat = (voice != null ? voice.getBeat() : null );
			if (beat == null){
				//si no habia mas componentes. busco el siguiente compas
				measure = (TGMeasureImpl)getSongManager().getTrackManager().getNextMeasure(getMeasure());
				if (measure == null) {
					return false;
				}
				voice = getSongManager().getMeasureManager().getFirstVoice(measure.getBeats(), getVoice());
				beat = (voice != null ? voice.getBeat() : null );
				if( beat == null ){
					beat = getSongManager().getMeasureManager().getFirstBeat(measure.getBeats());
				}
			}
			if(beat != null){
				moveTo(getTrack(), measure, beat, getStringNumber());
			}
		}
		return true;
	}
	
	public void moveLeft() {
		if (getSelectedBeat() != null) {
			TGMeasureImpl measure = getMeasure();
			TGVoice voice = getSongManager().getMeasureManager().getPreviousVoice(measure.getBeats(),getSelectedBeat(), getVoice());
			TGBeat beat = (voice != null ? voice.getBeat() : null );
			if (beat == null) {
				//si no habia mas componentes. busco el compas anterior
				measure = (TGMeasureImpl)getSongManager().getTrackManager().getPrevMeasure(getMeasure());
				if (measure == null) {
					return;
				}
				voice = getSongManager().getMeasureManager().getLastVoice(measure.getBeats(), getVoice());
				beat = (voice != null ? voice.getBeat() : null );
				if( beat == null ){
					beat = getSongManager().getMeasureManager().getFirstBeat(measure.getBeats());
				}
			}
			if(beat != null){
				moveTo(getTrack(), measure, beat, getStringNumber());
			}
		}
	}
	
	/**
	 * Luego de mover el Caret. cambia la duracion seleccionada por la del componente. solo si lo que resta del compas no esta vacio
	 */
	private void updateDuration() {
		if (this.selectedBeat != null && !this.selectedBeat.getVoice(getVoice()).isRestVoice()) {
			this.selectedBeat.getVoice(getVoice()).getDuration().copy(this.selectedDuration);
		}
	}
	
	public void moveUp() {
		int stringCount = this.selectedTrack.stringCount() ;
		int nextString = (( (this.string - 2 + stringCount) % stringCount) + 1);
		setStringNumber(nextString);
	}
	
	public void moveDown() {
		int stringCount = this.selectedTrack.stringCount() ;
		int nextString = ( (this.string  % stringCount) + 1);
		setStringNumber(nextString);
	}
	
	public void setStringNumber(int number){
		this.string = number;
		this.updateNote();
	}
	
	public int getStringNumber(){
		return this.string;
	}
	
	public long getPosition() {
		return this.position;
	}
	
	public TGMeasureImpl getMeasure() {
		return this.selectedMeasure;
	}
	
	public TGTrackImpl getTrack() {
		return this.selectedTrack;
	}
	
	public TGDuration getDuration() {
		return this.selectedDuration;
	}
	
	public void setSelectedDuration(TGDuration selectedDuration) {
		this.selectedDuration = selectedDuration;
	}
	
	public TGString getSelectedString() {
		List strings = this.selectedTrack.getStrings();
		Iterator it = strings.iterator();
		while (it.hasNext()) {
			TGString instrumentString = (TGString) it.next();
			if (instrumentString.getNumber() == this.string) {
				return instrumentString;
			}
		}
		return null;
	}
	
	public void changeDuration(TGDuration duration){
		getSongManager().getMeasureManager().changeDuration(getMeasure(),getSelectedBeat(),duration,getVoice(), true);
		setChanges(true);
	}
	
	private void updatePosition(){
		this.position = getSelectedBeat().getStart();
	}
	
	private void updateString(){
		if(this.string < 1 || this.string > getTrack().stringCount() ){
			this.string = 1;
		}
	}
	
	private void checkTransport(){
		TuxGuitar.instance().getTransport().gotoMeasure(getMeasure().getHeader());
	}
	
	public boolean hasChanges() {
		return this.changes;
	}
	
	public void setChanges(boolean changes) {
		this.changes = changes;
	}
	
	public int getVelocity() {
		return this.velocity;
	}
	
	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}
	
	private void updateNote(){
		this.selectedNote = null;
		
		TGString string = getSelectedString();
		if( string != null ){
			this.selectedNote = getSongManager().getMeasureManager().getNote(getMeasure(),getPosition(),string.getNumber());
		}
	}
	
	public TGNote getSelectedNote(){
		return this.selectedNote;
	}
	
	private void updateBeat(){
		this.restBeat = this.selectedBeat.isRestBeat();
	}
	
	public TGBeatImpl getSelectedBeat(){
		return (TGBeatImpl)this.selectedBeat;
	}
	
	public TGSongManager getSongManager(){
		return this.tablature.getSongManager();
	}
	
	public int getVoice() {
		return this.voice;
	}
	
	public void setVoice(int voice) {
		this.voice = voice;
		this.update();
	}
	
	public boolean isRestBeatSelected(){
		return this.restBeat;
	}
}