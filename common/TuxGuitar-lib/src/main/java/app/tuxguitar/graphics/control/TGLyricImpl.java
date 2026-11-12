package app.tuxguitar.graphics.control;

import app.tuxguitar.song.models.TGLyric;
import app.tuxguitar.ui.resource.UIPainter;

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

	public void paintCurrentNoteBeats(UIPainter painter,TGLayout layout,TGMeasureImpl currentMeasure ,float fromX,float fromY){
		int from = currentMeasure.getLyricBeatIndex();
		String[] beats = getLyricBeats();
		if(beats != null && from >= 0){
			int beatIndex = 0;
			for(int i = 0;i < currentMeasure.countBeats();i ++){
				TGBeatImpl beat = (TGBeatImpl)currentMeasure.getBeat(i);
				if(!beat.isRestBeat()){
					if((from + beatIndex) < beats.length){
						paintWord(painter, layout, currentMeasure, beat, beats[from + beatIndex].trim(), fromX, fromY);
					}
					beatIndex ++;
				}
			}
		}
	}

	// the word under its beat, in the play color while the beat plays; an empty word leaves the beat blank
	private void paintWord(UIPainter painter, TGLayout layout, TGMeasureImpl measure, TGBeatImpl beat, String word, float fromX, float fromY){
		if(word.length() > 0){
			float x = (fromX + beat.getPosX() + beat.getSpacing(layout) + 2);
			layout.setLyricStyle(painter,(layout.isPlayModeEnabled() && beat.isPlaying(layout)));
			painter.drawString(word,x + 13,(fromY + measure.getTs().getPosition(TGTrackSpacing.POSITION_LYRIC)));
		}
	}

	public int getHeight(){
		return this.height;
	}
}
