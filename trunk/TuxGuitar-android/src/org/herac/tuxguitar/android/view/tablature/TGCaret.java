package org.herac.tuxguitar.android.view.tablature;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.android.util.MidiTickUtil;
import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGResource;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.graphics.control.TGTrackSpacing;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;

public class TGCaret {
	
	private TGSongViewController tablature;
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
	
	private TGColor color1;
	private TGColor color2;
	
	public TGCaret(TGSongViewController tablature) {
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
	
	public synchronized void update(int trackNumber, long position, int string){
		update(trackNumber, position, string,getVelocity());
	}
	
	public synchronized void update(int trackNumber, long position, int string, int velocity) {
		TGContext context = this.tablature.getContext();
		MidiPlayer midiPlayer = MidiPlayer.getInstance(context);
		
		long realPosition = ((midiPlayer.isRunning()) ? MidiTickUtil.getStart(context, midiPlayer.getTickPosition()):position);
		TGTrackImpl track = findTrack(trackNumber); 
		TGMeasureImpl measure = findMeasure(realPosition,track);
		TGBeat beat = findBeat(realPosition,measure);
		if(track != null && measure != null && beat != null){
			moveTo(track, measure, beat,string);
		}
		setVelocity(velocity);
	}
	
	public void moveTo(TGTrackImpl selectedTrack, TGMeasureImpl selectedMeasure, TGBeat selectedBeat, int string) {
		this.selectedTrack = selectedTrack;
		this.selectedMeasure = selectedMeasure;
		this.selectedBeat = selectedBeat;
		this.string = string;
		this.updatePosition();
		this.updateDuration();
		this.updateString();
		this.updateNote();
		this.updateBeat();
		this.setChanges(true);
	}
	
	private TGTrackImpl findTrack(int number){
		TGTrackImpl track = (TGTrackImpl)getSongManager().getTrack(getSong(), number);
		if(track == null){
			track = (TGTrackImpl)getSongManager().getFirstTrack(getSong());
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
		TGContext context = this.tablature.getContext();
		MidiPlayer midiPlayer = MidiPlayer.getInstance(context);
		
		long start = MidiTickUtil.getStart(context, midiPlayer.getTickPosition());
		this.update(this.selectedTrack.getNumber(),start,this.string);
		this.setChanges(true);
	}
	
	public void paintCaret(TGLayout layout, TGPainter painter) {
		if(!MidiPlayer.getInstance(this.tablature.getContext()).isRunning()){
			if (this.selectedMeasure != null && !this.selectedMeasure.isOutOfBounds() && this.selectedBeat instanceof TGBeatImpl) {
				TGBeatImpl beat = (TGBeatImpl)this.selectedBeat;
				if( (layout.getStyle() & TGLayout.DISPLAY_TABLATURE) != 0){
					boolean expectedVoice = (getSelectedNote() == null || getSelectedNote().getVoice().getIndex() == getVoice());
					float stringSpacing = this.tablature.getLayout().getStringSpacing();
					float leftSpacing = beat.getMeasureImpl().getHeaderImpl().getLeftSpacing(layout);
					float width = ((stringSpacing - (3.0f * layout.getScale())) * 2);
					float height = ((stringSpacing - (3.0f * layout.getScale())) * 2);
					float xMargin = ((width / 2.0f) - (2.0f * layout.getScale()));
					float yMargin = (height / 2.0f);
					float x = (this.selectedMeasure.getPosX() + beat.getPosX() + beat.getSpacing(layout) + leftSpacing - xMargin);
					float y = (this.selectedMeasure.getPosY() + this.selectedMeasure.getTs().getPosition(TGTrackSpacing.POSITION_TABLATURE) + ((this.string * stringSpacing) - stringSpacing) - yMargin);
					this.setPaintStyle(painter, expectedVoice);
					
					painter.initPath();
					painter.setAntialias(false);
					painter.addRectangle(x, y, width, height);
					painter.closePath();
				}
				else if( (layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0){
					float line = this.tablature.getLayout().getScoreLineSpacing();
					float leftSpacing = beat.getMeasureImpl().getHeaderImpl().getLeftSpacing(layout);
					float xMargin = (2.0f * layout.getScale());
					float x1 = this.selectedMeasure.getPosX() + beat.getPosX() + beat.getSpacing(layout) + leftSpacing - xMargin;
					float x2 = (x1 + layout.getScoreNoteWidth() + xMargin);
					float y1 = this.selectedMeasure.getPosY() + this.selectedMeasure.getTs().getPosition(TGTrackSpacing.POSITION_TOP) - line;
					float y2 = this.selectedMeasure.getPosY() + this.selectedMeasure.getTs().getPosition(TGTrackSpacing.POSITION_BOTTOM);
					this.setPaintStyle(painter, true);
					
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
	
	public void setPaintStyle(TGPainter painter, boolean expectedVoice){
		TGColor foreground = ( expectedVoice ? this.color1 : this.color2 );
		if( foreground != null ){
			painter.setForeground( foreground );
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
	
	private void updateDuration() {
		if (this.selectedBeat != null && !this.selectedBeat.getVoice(getVoice()).isRestVoice()) {
			this.selectedDuration.copyFrom(this.selectedBeat.getVoice(getVoice()).getDuration());
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
		List<?> strings = this.selectedTrack.getStrings();
		Iterator<?> it = strings.iterator();
		while (it.hasNext()) {
			TGString instrumentString = (TGString) it.next();
			if (instrumentString.getNumber() == this.string) {
				return instrumentString;
			}
		}
		return null;
	}
	
	private void updatePosition(){
		this.position = getSelectedBeat().getStart();
	}
	
	private void updateString(){
		if( this.string < 1 || this.string > getTrack().stringCount() ){
			this.string = 1;
		}
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
	
	public TGSong getSong(){
		return this.tablature.getSong();
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
	
	public void setColor1(TGColorModel cm){
		this.disposeResource( this.color1 );
		this.color1 = this.tablature.getResourceFactory().createColor(cm);
	}
	
	public void setColor2(TGColorModel cm){
		this.disposeResource( this.color2 );
		this.color2 = this.tablature.getResourceFactory().createColor(cm);
	}
	
	public void disposeResource(TGResource resource){
		if( resource != null && !resource.isDisposed() ){
			resource.dispose();
		}
	}
	
	public void dispose(){
		this.disposeResource( this.color1 );
		this.disposeResource( this.color2 );
	}
}