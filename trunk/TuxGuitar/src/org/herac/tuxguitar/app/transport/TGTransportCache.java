package org.herac.tuxguitar.app.transport;

import org.herac.tuxguitar.app.util.MidiTickUtil;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.graphics.control.TGBeatImpl;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGTransportCache {
	
	private TGContext context;
	
	//Modo reproduccion
	private int playTrack;
	private long playTick;
	private long playStart;
	private long playBeatEnd;
	private boolean playChanges;
	private boolean playUpdate;
	private TGBeatImpl playBeat;
	private TGMeasureImpl playMeasure;
	
	public TGTransportCache(TGContext context){
		this.context = context;
		this.reset();
	}
	
	public void reset() {
		this.playBeat = null;
		this.playMeasure = null;
		this.playUpdate = false;
		this.playChanges = false;
		this.playTrack =  0;
		this.playTick = 0;
		this.playStart = 0;
		this.playBeatEnd = 0;
	}
	
	public void updatePlayMode(){
		this.playUpdate = true;
		this.getPlayBeat();
	}
	
	public TGBeatImpl getPlayBeat(){
		if( this.playUpdate ){
			this.playChanges = false;
			
			MidiPlayer player = MidiPlayer.getInstance(this.context);
			TGSongManager manager = TGDocumentManager.getInstance(this.context).getSongManager();
			if( this.isPlaying() ){
				Caret caret = TablatureEditor.getInstance(this.context).getTablature().getCaret();
				TGTrack track = caret.getTrack();
				
				long tick = player.getTickPosition();
				long start = this.playStart + (tick - this.playTick);
				if( this.playMeasure == null || start < this.playMeasure.getStart() || start > (this.playMeasure.getStart() + this.playMeasure.getLength()) ){
					this.playMeasure = null;
					start = MidiTickUtil.getStart(this.context, tick);
				}
				
				if(this.playMeasure == null || this.playBeatEnd == 0 || start > this.playBeatEnd || start < this.playStart || track.getNumber() != this.playTrack){
					this.playBeat = null;
					this.playBeatEnd = 0;
					this.playChanges = true;
					
					if( this.playMeasure == null || !this.playMeasure.hasTrack(track.getNumber())  || !isPlaying(this.playMeasure) ){
						this.playMeasure = (TGMeasureImpl) manager.getTrackManager().getMeasureAt(track,start);
					}
					if (this.playMeasure != null && !isPlayingCountDown()) {
						this.playBeat = (TGBeatImpl) manager.getMeasureManager().getBeatIn(this.playMeasure, start);
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
	
	public boolean isPlaying(){
		return MidiPlayer.getInstance(this.context).isRunning();
	}
	
	public boolean isPlayingCountDown(){
		return MidiPlayer.getInstance(this.context).getCountDown().isRunning();
	}
	
	public boolean isPlaying(TGMeasure measure){
		return (isPlaying() && this.playMeasure != null && measure.equals(this.playMeasure));
	}
	
	public boolean isPlaying(TGMeasure measure,TGBeat b){
		return (isPlaying(measure) && this.playBeat != null && this.playBeat.getStart() == b.getStart());
	}
}
