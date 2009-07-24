package org.herac.tuxguitar.gui.editors.tab;

import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.song.models.TGLyric;

public class TGLyricImpl extends TGLyric{
	
	private int height;
	private int nextIndex = 0;
	
	public TGLyricImpl(){
		this.height = 0;
	}
	
	public void setFrom(int from) {
		super.setFrom(from);
		this.update();
	}
	
	public void setLyrics(String lyrics) {
		super.setLyrics(lyrics);
		this.update();
	}
	
	private void update(){
		this.height = (this.isEmpty()?0:10);
	}
	
	public void start(){
		this.start(0);
	}
	
	public void start(int index){
		this.nextIndex = index;
	}
	
	public void setCurrentMeasure(TGMeasureImpl measure){
		if(measure.getNumber() >= getFrom()){
			measure.setLyricBeatIndex(this.nextIndex);
			this.nextIndex += (measure.getNotEmptyBeats());
		}else{
			measure.setLyricBeatIndex(-1);
			this.start();
		}
	}
	
	public void paintCurrentNoteBeats(TGPainter painter,ViewLayout layout,TGMeasureImpl currentMeasure ,int fromX,int fromY){
		int from = currentMeasure.getLyricBeatIndex();
		String[] beats = getLyricBeats();
		if(beats != null && from >= 0 && from < beats.length){
			int beatIndex = 0;
			for(int i = 0;i < currentMeasure.countBeats();i ++){
				TGBeatImpl beat = (TGBeatImpl)currentMeasure.getBeat(i);
				if(!beat.isRestBeat()){
					if((from + beatIndex) < beats.length){
						String str = beats[from + beatIndex].trim();
						if(str.length() > 0){
							int x = (fromX + beat.getPosX() + beat.getSpacing() + 2);
							layout.setLyricStyle(painter,(layout.isPlayModeEnabled() && beat.isPlaying(layout)));
							painter.drawString(str,x + 13,(fromY + currentMeasure.getTs().getPosition(TGTrackSpacing.POSITION_LYRIC)));
						}
					}
					beatIndex ++;
				}
			}
		}
	}
	
	public int getHeight(){
		return this.height;
	}
}
