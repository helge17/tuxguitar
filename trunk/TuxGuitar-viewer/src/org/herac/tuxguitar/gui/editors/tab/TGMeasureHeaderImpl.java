package org.herac.tuxguitar.gui.editors.tab;

import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

public class TGMeasureHeaderImpl extends TGMeasureHeader{
	/**
	 * Espacio por defecto del timeSignature
	 */
	private static final int DEFAULT_TIME_SIGNATURE_SPACING = 30;
	/**
	 * Espacio por defecto a la izquierda
	 */
	private static final int DEFAULT_LEFT_SPACING = 15;
	/**
	 * Espacio por defecto a la derecha
	 */
	private static final int DEFAULT_RIGHT_SPACING = 15;
	
	private static final int PAINT_TEMPO = 0x01;
	
	private static final int PAINT_TRIPLET_FEEL = 0x02;
	
	private static final int PAINT_TIME_SIGNATURE = 0x04;
	
	private int paintFlags;
	
	private int maxQuarterSpacing;
	
	private int maxClefSpacing;
	
	private int maxKeySignatureSpacing;
	
	private int maxWidth;
	
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
	
	public void update(ViewLayout layout, int index) {
		this.reset();
		this.calculateMeasureChanges(layout);
		
		int trackCount = getSong().countTracks();
		for (int trackIdx = 0; trackIdx < trackCount; trackIdx++) {
			TGTrackImpl track = (TGTrackImpl)getSong().getTrack(trackIdx);
			TGMeasureImpl measure = (TGMeasureImpl) track.getMeasure( index );
			measure.calculateMeasureChanges(layout);
		}
	}
	
	public void calculateMeasureChanges(ViewLayout layout) {
		TGMeasureHeader previous = layout.getSongManager().getPrevMeasureHeader(this);
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
	
	public int getMaxQuarterSpacing() {
		return this.maxQuarterSpacing;
	}
	
	public void notifyQuarterSpacing(int spacing) {
		this.maxQuarterSpacing = ((spacing > this.maxQuarterSpacing) ? spacing : this.maxQuarterSpacing );
	}
	
	public int getClefSpacing(ViewLayout layout, TGMeasureImpl measure){
		return (!measure.isPaintClef() && (layout.getStyle() & ViewLayout.DISPLAY_MULTITRACK) == 0 ? 0 : this.maxClefSpacing );
	}
	
	public int getKeySignatureSpacing(ViewLayout layout, TGMeasureImpl measure){
		return (!measure.isPaintKeySignature() && (layout.getStyle() & ViewLayout.DISPLAY_MULTITRACK) == 0 ? 0 : this.maxKeySignatureSpacing );
	}
	
	public int getTempoSpacing(ViewLayout layout){
		return (shouldPaintTempo()? Math.round( 45 * layout.getScale() ):0);
	}
	
	public int getTripletFeelSpacing(ViewLayout layout){
		return (shouldPaintTripletFeel()? Math.round( 55 * layout.getScale() ):0);
	}
	
	public int getTimeSignatureSpacing(ViewLayout layout){
		return (shouldPaintTimeSignature()? Math.round( DEFAULT_TIME_SIGNATURE_SPACING * layout.getScale() ):0);
	}
	
	public int getLeftSpacing(ViewLayout layout){
		return Math.round( DEFAULT_LEFT_SPACING * layout.getScale() );
	}
	
	public int getRightSpacing(ViewLayout layout){
		return Math.round( DEFAULT_RIGHT_SPACING * layout.getScale() );
	}
	
	public int getFirstNoteSpacing(ViewLayout layout, TGMeasureImpl measure){
		int topSpacing = getTempoSpacing(layout) + getTripletFeelSpacing(layout);
		int middleSpacing = getClefSpacing(layout,measure) + getKeySignatureSpacing(layout,measure) + getTimeSignatureSpacing(layout);
		
		return Math.round(Math.max( topSpacing , middleSpacing) + (10f * layout.getScale()));
	}
	
	public void notifyClefSpacing(int spacing){
		this.maxClefSpacing = ((spacing > this.maxClefSpacing)?spacing:this.maxClefSpacing);
	}
	
	public void notifyKeySignatureSpacing(int spacing){
		this.maxKeySignatureSpacing = ((spacing > this.maxKeySignatureSpacing) ? spacing : this.maxKeySignatureSpacing);
	}
	
	public void notifyWidth(int width){
		this.maxWidth = ((width > this.maxWidth)?width:this.maxWidth);
	}
	
	public int getMaxWidth(){
		return this.maxWidth;
	}
}
