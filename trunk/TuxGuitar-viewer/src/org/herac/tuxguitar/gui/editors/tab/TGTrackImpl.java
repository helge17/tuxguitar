/*
 * Created on 29-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.tab;

import java.util.Iterator;

import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TGTrackImpl extends TGTrack{
	
	private int tabHeight;
	private int scoreHeight;
	
	public TGTrackImpl(TGFactory factory) {
		super(factory);
	}
	
	public void update(ViewLayout layout){
		this.calculateTabHeight(layout);
		this.calculateScoreHeight(layout);
	}
	/**
	 * Calcula el el ancho de la tablatura
	 */
	public void calculateTabHeight(ViewLayout layout) {
		this.tabHeight = ((layout.getStyle() & ViewLayout.DISPLAY_TABLATURE) != 0 ?((stringCount() - 1) * layout.getStringSpacing()):0);
	}
	
	/**
	 * Calcula el el ancho de la partitura
	 */
	public void calculateScoreHeight(ViewLayout layout) {
		this.scoreHeight = ((layout.getStyle() & ViewLayout.DISPLAY_SCORE) != 0 ?(layout.getScoreLineSpacing() * 4):0);
	}
	
	public boolean hasCaret(ViewLayout layout){
		return (this.equals(layout.getTablature().getCaret().getTrack()));
	}
	
	public int getTabHeight() {
		return this.tabHeight;
	}
	
	public int getScoreHeight() {
		return this.scoreHeight;
	}
	
	public void setTabHeight(int tabHeight) {
		this.tabHeight = tabHeight;
	}
	
	public void clear(){
		Iterator measures = getMeasures();
		while(measures.hasNext()){
			TGMeasureImpl measure = (TGMeasureImpl)measures.next();
			if(!measure.isDisposed()){
				measure.dispose();
			}
		}
		super.clear();
	}
	
	public void removeMeasure(int index){
		TGMeasureImpl measure = (TGMeasureImpl)getMeasure(index);
		if(!measure.isDisposed()){
			measure.dispose();
		}
		super.removeMeasure(index);
	}
}