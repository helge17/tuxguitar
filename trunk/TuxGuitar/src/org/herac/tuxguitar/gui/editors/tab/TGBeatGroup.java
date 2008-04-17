package org.herac.tuxguitar.gui.editors.tab;

import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;


public class TGBeatGroup {
	private static final int SCORE_MIDDLE_KEYS[] = new int[]{55,40,40,50};
	private static final int SCORE_SHARP_POSITIONS[] = new int[]{7,7,6,6,5,4,4,3,3,2,2,1};	
	private static final int SCORE_FLAT_POSITIONS[] = new int[]{7,6,6,5,5,4,3,3,2,2,1,1};
	
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = 2;
	
	private static final int UP_OFFSET = 28;
	private static final int DOWN_OFFSET = 35;
	
	private int direction;
	private TGNoteImpl firstMinNote;
	private TGNoteImpl firstMaxNote;
	private TGNoteImpl lastMinNote;
	private TGNoteImpl lastMaxNote;
	private TGNoteImpl maxNote;
	private TGNoteImpl minNote;
	
	public TGBeatGroup(){
		this.direction = DIRECTION_UP;
		this.firstMinNote = null;
		this.firstMaxNote = null;
		this.lastMinNote = null;
		this.lastMaxNote = null;
		this.maxNote = null;
		this.minNote = null;
	}
	
	public void check(TGNoteImpl note,int clef){
		int value = note.getRealValue();
		
		//FIRST MIN NOTE
		if(this.firstMinNote == null || note.getBeat().getStart() < this.firstMinNote.getBeat().getStart()){			
			this.firstMinNote = note;
		}else if(note.getBeat().getStart() == this.firstMinNote.getBeat().getStart()){
			if(note.getRealValue() < this.firstMinNote.getRealValue()){
				this.firstMinNote = note;
			}
		}
		//FIRST MAX NOTE
		if(this.firstMaxNote == null || note.getBeat().getStart() < this.firstMaxNote.getBeat().getStart()){			
			this.firstMaxNote = note;
		}else if(note.getBeat().getStart() == this.firstMaxNote.getBeat().getStart()){
			if(note.getRealValue() > this.firstMaxNote.getRealValue()){
				this.firstMaxNote = note;
			}
		}
				
		//LAST MIN NOTE
		if(this.lastMinNote == null || note.getBeat().getStart() > this.lastMinNote.getBeat().getStart()){
			this.lastMinNote = note;
		}else if(note.getBeat().getStart() == this.lastMinNote.getBeat().getStart()){
			if(note.getRealValue() < this.lastMinNote.getRealValue()){
				this.lastMinNote = note;
			}
		}	
		//LAST MIN NOTE
		if(this.lastMaxNote == null || note.getBeat().getStart() > this.lastMaxNote.getBeat().getStart()){
			this.lastMaxNote = note;
		}else if(note.getBeat().getStart() == this.lastMaxNote.getBeat().getStart()){
			if(note.getRealValue() > this.lastMaxNote.getRealValue()){
				this.lastMaxNote = note;
			}
		}	
		
		if(this.maxNote == null || value > this.maxNote.getRealValue()){
			this.maxNote = note;
		}
		if(this.minNote == null || value < this.minNote.getRealValue()){
			this.minNote = note;
		}
						
		int max = Math.abs(this.minNote.getRealValue() - (SCORE_MIDDLE_KEYS[clef - 1] + 100));
		int min = Math.abs(this.maxNote.getRealValue() - (SCORE_MIDDLE_KEYS[clef - 1] - 100));		
		if(max > min){
			this.direction = DIRECTION_UP;			
		}else{
			this.direction = DIRECTION_DOWN;
		}
	}

	public int getY1(ViewLayout layout,TGNoteImpl note,int key,int clef){
		double scale = (layout.getScoreLineSpacing() / 2.00);
		int noteValue = note.getRealValue();
		
		int scoreLineY = 0;
		if(key <= 7){
			scoreLineY = (int)((SCORE_SHARP_POSITIONS[noteValue % 12]) * scale - (7 * (noteValue / 12)) * scale);
		}else{
			scoreLineY = (int)((SCORE_FLAT_POSITIONS[noteValue % 12]) * scale - (7 * (noteValue / 12)) * scale);
		}

		scoreLineY += TGMeasureImpl.SCORE_KEY_OFFSETS[clef - 1] * scale; 

		return scoreLineY;
	}
	
	public int getY2(ViewLayout layout,int x,int key,int clef){		
		int maxDistance = 10;
    	float upOffset = TGBeatGroup.getUpOffset(layout);
    	float downOffset = TGBeatGroup.getDownOffset(layout);
		if(this.direction == DIRECTION_DOWN){
			if(this.minNote != this.firstMinNote && this.minNote != this.lastMinNote){
				return (int) (getY1(layout,this.minNote,key,clef) + downOffset);	
			}
			
			int x1 = this.firstMinNote.getPosX() + this.firstMinNote.getBeatImpl().getSpacing();
			int x2 = this.lastMinNote.getPosX() + this.lastMinNote.getBeatImpl().getSpacing();						
			int y1 =  (int) (getY1(layout,this.firstMinNote,key,clef) +  downOffset);
			int y2 =  (int) (getY1(layout,this.lastMinNote,key,clef) +  downOffset);
			
			if(y1 > y2 && (y1 - y2) > maxDistance) y2 = (y1 - maxDistance);
			if(y2 > y1 && (y2 - y1) > maxDistance) y1 = (y2 - maxDistance);			
			int y = (int)((((double)y1 -(double)y2) / ((double)x1 - (double)x2)) * ((double)x1 - (double)x));
			return y1 - y;
		}else if(this.maxNote != this.firstMaxNote && this.maxNote != this.lastMaxNote){
			return (int)(getY1(layout,this.maxNote,key,clef) - upOffset);				
		}else{						
			int x1 = this.firstMaxNote.getPosX() + this.firstMaxNote.getBeatImpl().getSpacing();
			int x2 = this.lastMaxNote.getPosX() + this.lastMaxNote.getBeatImpl().getSpacing();											
			int y1 = (int)(getY1(layout,this.firstMaxNote,key,clef) - upOffset);
			int y2 = (int)(getY1(layout,this.lastMaxNote,key,clef) - upOffset);
			
			if(y1 < y2 && (y2 - y1) > maxDistance) y2 = (y1 + maxDistance);
			if(y2 < y1 && (y1 - y2) > maxDistance) y1 = (y2 + maxDistance);			
			int y = (int)((((double)y1 -(double)y2) / ((double)x1 - (double)x2)) * ((double)x1 - (double)x));
			return y1 - y;			 	
		}
	}

	public int getDirection() {
		return this.direction;
	}
	
	public TGNoteImpl getMinNote(){
		return this.minNote;
	}
	
	public TGNoteImpl getMaxNote(){
		return this.maxNote;
	}

	public static float getUpOffset(ViewLayout layout){
		float scale = (layout.getScoreLineSpacing() / 8.0f);
		return (UP_OFFSET * scale);
	}

	public static float getDownOffset(ViewLayout layout){
		float scale = (layout.getScoreLineSpacing() / 8.0f);
		return (DOWN_OFFSET * scale);
	}
}
