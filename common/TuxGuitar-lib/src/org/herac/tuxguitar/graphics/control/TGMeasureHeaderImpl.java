package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

public class TGMeasureHeaderImpl extends TGMeasureHeader{
	
	private static final float DEFAULT_TEMPO_WIDTH = 45;
	
	private static final float DEFAULT_TRIPLET_FEEL_WIDTH = 55;
	
	private static final float DEFAULT_TIME_SIGNATURE_WIDTH = 10;
	
	private static final int PAINT_TEMPO = 0x01;
	
	private static final int PAINT_TRIPLET_FEEL = 0x02;
	
	private static final int PAINT_TIME_SIGNATURE = 0x04;
	
	private int paintFlags;
	
	private float maxQuarterSpacing;
	
	private float maxClefSpacing;
	
	private float maxKeySignatureSpacing;
	
	private float maxWidth;
	
	public TGMeasureHeaderImpl(TGFactory factory){
		super(factory);
	}
	
	public void reset() {
		this.maxWidth = 0;
		this.paintFlags = 0;
		this.maxQuarterSpacing = 0;
		this.maxClefSpacing = 0;
		this.maxKeySignatureSpacing = 0;
	}
	
	public void update(TGLayout layout, int index) {
		this.reset();
		this.calculateMeasureChanges(layout);
		
		int trackCount = getSong().countTracks();
		for (int trackIdx = 0; trackIdx < trackCount; trackIdx++) {
			TGTrackImpl track = (TGTrackImpl)getSong().getTrack(trackIdx);
			TGMeasureImpl measure = (TGMeasureImpl) track.getMeasure( index );
			measure.calculateMeasureChanges(layout);
		}
	}
	
	public void calculateMeasureChanges(TGLayout layout) {
		TGMeasureHeader previous = layout.getSongManager().getPrevMeasureHeader(layout.getSong(), this);
		if(previous == null){
			this.paintFlags |= PAINT_TEMPO; 
			this.paintFlags |= ((this.getTripletFeel() != TGMeasureHeader.TRIPLET_FEEL_NONE)?PAINT_TRIPLET_FEEL:0);
			this.paintFlags |= PAINT_TIME_SIGNATURE;
		}else{
			//Tempo
			if(this.getTempo().getValue() != previous.getTempo().getValue()){
				this.paintFlags |= PAINT_TEMPO; 
			}
			//Triplet Feel
			if(this.getTripletFeel() != previous.getTripletFeel()){
				this.paintFlags |= PAINT_TRIPLET_FEEL;
			}
			//Time Signature
			int thisNumerator = this.getTimeSignature().getNumerator();
			int thisValue = this.getTimeSignature().getDenominator().getValue();
			int prevNumerator = previous.getTimeSignature().getNumerator();
			int prevValue = previous.getTimeSignature().getDenominator().getValue();
			if(thisNumerator != prevNumerator || thisValue != prevValue){
				this.paintFlags |= PAINT_TIME_SIGNATURE;
			}
		}
	}
	
	public boolean shouldPaintTempo(){
		return ( (this.paintFlags & PAINT_TEMPO) != 0 );
	}
	
	public boolean shouldPaintTripletFeel(){
		return ( (this.paintFlags & PAINT_TRIPLET_FEEL) != 0 );
	}
	
	public boolean shouldPaintTimeSignature(){
		return ( (this.paintFlags & PAINT_TIME_SIGNATURE) != 0 );
	}
	
	public float getMaxQuarterSpacing() {
		return this.maxQuarterSpacing;
	}
	
	public void notifyQuarterSpacing(float spacing) {
		this.maxQuarterSpacing = ((spacing > this.maxQuarterSpacing) ? spacing : this.maxQuarterSpacing );
	}
	
	public float getClefSpacing(TGLayout layout, TGMeasureImpl measure){
		return (!measure.isPaintClef() && (layout.getStyle() & TGLayout.DISPLAY_MULTITRACK) == 0 ? 0 : this.maxClefSpacing );
	}
	
	public float getKeySignatureSpacing(TGLayout layout, TGMeasureImpl measure){
		return (!measure.isPaintKeySignature() && (layout.getStyle() & TGLayout.DISPLAY_MULTITRACK) == 0 ? 0 : this.maxKeySignatureSpacing );
	}
	
	public float getTempoSpacing(TGLayout layout){
		return (shouldPaintTempo() ? Math.round(DEFAULT_TEMPO_WIDTH * layout.getScale()) : 0);
	}
	
	public float getTripletFeelSpacing(TGLayout layout){
		return (shouldPaintTripletFeel() ? Math.round(DEFAULT_TRIPLET_FEEL_WIDTH * layout.getScale()) : 0);
	}
	
	public float getTimeSignatureSpacing(TGLayout layout){
		return (shouldPaintTimeSignature() ? Math.round((DEFAULT_TIME_SIGNATURE_WIDTH * layout.getScale()) + layout.getTimeSignatureSpacing()) : 0);
	}
	
	public float getLeftSpacing(TGLayout layout){
		return layout.getMeasureLeftSpacing();
	}
	
	public float getRightSpacing(TGLayout layout){
		return layout.getMeasureRightSpacing();
	}
	
	public float getFirstNoteSpacing(TGLayout layout, TGMeasureImpl measure){
		float topSpacing = (getTempoSpacing(layout) + getTripletFeelSpacing(layout));
		float middleSpacing = (getClefSpacing(layout,measure) + getKeySignatureSpacing(layout,measure) + getTimeSignatureSpacing(layout) + layout.getFirstNoteSpacing());
		
		return Math.round(Math.max(topSpacing , middleSpacing));
	}
	
	public void notifyClefSpacing(float spacing){
		this.maxClefSpacing = ((spacing > this.maxClefSpacing) ? spacing : this.maxClefSpacing);
	}
	
	public void notifyKeySignatureSpacing(float spacing){
		this.maxKeySignatureSpacing = ((spacing > this.maxKeySignatureSpacing) ? spacing : this.maxKeySignatureSpacing);
	}
	
	public void notifyWidth(float width){
		this.maxWidth = ((width > this.maxWidth)?width:this.maxWidth);
	}
	
	public float getMaxWidth(){
		return this.maxWidth;
	}
}
