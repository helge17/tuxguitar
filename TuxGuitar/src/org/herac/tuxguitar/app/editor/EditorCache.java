package org.herac.tuxguitar.app.editor;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.MidiTickUtil;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;

public class EditorCache {
	
	//Modo edition
	private boolean editUpdate;
	private TGBeatImpl editBeat;
	
	//Modo reproduccion
	private int playTrack;
	private long playTick;
	private long playStart;
	private long playBeatEnd;
	private boolean playChanges;
	private boolean playUpdate;
	private TGBeatImpl playBeat;
	private TGMeasureImpl playMeasure;
	
	public EditorCache(){
		this.reset();
	}
	
	public void reset(){
		this.resetEditMode();
		this.resetPlayMode();
	}
	
	private void resetEditMode(){
		this.editBeat = null;
		this.editUpdate = false;
	}
	
	private void resetPlayMode(){
		this.playBeat = null;
		this.playMeasure = null;
		this.playUpdate = false;
		this.playChanges = false;
		this.playTrack =  0;
		this.playTick = 0;
		this.playStart = 0;
		this.playBeatEnd = 0;
	}
	
	public void updateEditMode(){
		this.editUpdate = true;
		this.resetPlayMode();
		this.getEditBeat();
	}
	
	public void updatePlayMode(){
		this.playUpdate = true;
		this.resetEditMode();
		this.getPlayBeat();
	}
	
	public TGBeatImpl getEditBeat() {
		if( this.editUpdate ){
			this.editBeat =  TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedBeat();
			this.editUpdate = false;
		}
		return this.editBeat;
	}
	
	public TGBeatImpl getPlayBeat(){
		if( this.playUpdate ){
			this.playChanges = false;
			
			TGSongManager manager = TuxGuitar.getInstance().getSongManager();
			if( this.isPlaying() ){
				Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
				TGTrack track = caret.getTrack();
				
				long tick = TuxGuitar.getInstance().getPlayer().getTickPosition();
				long start = this.playStart + (tick - this.playTick);
				if( this.playMeasure == null || start < this.playMeasure.getStart() || start > (this.playMeasure.getStart() + this.playMeasure.getLength()) ){
					this.playMeasure = null;
					start = MidiTickUtil.getStart(tick);
				}
				
				if(this.playMeasure == null || this.playBeatEnd == 0 || start > this.playBeatEnd || start < this.playStart || track.getNumber() != this.playTrack){
					this.playBeat = null;
					this.playBeatEnd = 0;
					this.playChanges = true;
					
					if( this.playMeasure == null || !this.playMeasure.hasTrack(track.getNumber())  || !isPlaying(this.playMeasure) ){
						this.playMeasure = (TGMeasureImpl)manager.getTrackManager().getMeasureAt(track,start);
					}
					if (this.playMeasure != null && !isPlayingCountDown()) {
						this.playBeat = (TGBeatImpl)manager.getMeasureManager().getBeatIn(this.playMeasure, start);
						if(this.playBeat != null){
							TGBeat next = manager.getMeasureManager().getNextBeat(this.playMeasure.getBeats(), this.playBeat);
							if( next != null ){
								this.playBeatEnd = next.getStart();
							}else{
								TGDuration duration = manager.getMeasureManager().getMinimumDuration(this.playBeat);
								this.playBeatEnd = (this.playBeat.getStart() + duration.getTime());
							}
						}
					}
				}
				this.playTrack = track.getNumber();
				this.playTick = tick;
				this.playStart = start;
			}
			this.playUpdate = false;
		}
		return this.playBeat;
	}
	
	public long getPlayTick() {
		return this.playTick;
	}
	
	public long getPlayStart() {
		return this.playStart;
	}
	
	public TGMeasureImpl getPlayMeasure() {
		return this.playMeasure;
	}
	
	public boolean shouldRedraw() {
		return this.playChanges;
	}
	
	public boolean isPlaying() {
		return TuxGuitar.getInstance().getPlayer().isRunning();
	}
	
	public boolean isPlayingCountDown() {
		return TuxGuitar.getInstance().getPlayer().getCountDown().isRunning();
	}
	
	public boolean isPlaying(TGMeasure measure) {
		// thread safe
		TGMeasure playMeasure = this.playMeasure;
		
		return (isPlaying() && playMeasure != null && measure.equals(playMeasure));
	}
	
	public boolean isPlaying(TGMeasure measure, TGBeat b) {
		// thread safe
		TGBeat playBeat = this.playBeat;
		
		return (isPlaying(measure) && playBeat != null && playBeat.getStart() == b.getStart());
	}
}
