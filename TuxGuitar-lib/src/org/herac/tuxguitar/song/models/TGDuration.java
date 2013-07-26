/*
 * Created on 29-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGDuration {
	/**
	 * tiempo por defecto de la Negra.
	 */
	public static final long QUARTER_TIME = 960;
	/**
	 * Redonda.
	 */
	public static final int WHOLE = 1;
	
	/**
	 * Blanca.
	 */
	public static final int HALF = 2;
	
	/**
	 * Negra.
	 */
	public static final int QUARTER = 4;
	
	/**
	 * Corchea.
	 */
	public static final int EIGHTH = 8;
	
	/**
	 * Semi-Corchea.
	 */
	public static final int SIXTEENTH = 16;
	
	/**
	 * Fusa.
	 */
	public static final int THIRTY_SECOND = 32;
	
	/**
	 * Semi-Fusa.
	 */
	public static final int SIXTY_FOURTH = 64;
	/**
	 * Valor.
	 */
	private int value;
	/**
	 * Puntillo.
	 */
	private boolean dotted;
	/**
	 * Doble Puntillo.
	 */
	private boolean doubleDotted;
	/**
	 * DivisionType.
	 */
	private TGDivisionType divisionType;
	
	public TGDuration(TGFactory factory){
		this.value = QUARTER;
		this.dotted = false;
		this.doubleDotted = false;
		this.divisionType = factory.newDivisionType();
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean isDotted() {
		return this.dotted;
	}
	
	public void setDotted(boolean dotted) {
		this.dotted = dotted;
	}
	
	public boolean isDoubleDotted() {
		return this.doubleDotted;
	}
	
	public void setDoubleDotted(boolean doubleDotted) {
		this.doubleDotted = doubleDotted;
	}
	
	public TGDivisionType getDivision(){
		return this.divisionType;
	}
	
	public long getTime(){
		long time = (long)( QUARTER_TIME * ( 4.0f / this.value ) ) ;
		if(this.dotted){
			time += time / 2;
		}else if(this.doubleDotted){
			time += ((time / 4) * 3);
		}
		return this.divisionType.convertTime(time);
	}
	
	public static TGDuration fromTime(TGFactory factory,long time){
		TGDuration duration = factory.newDuration();
		duration.setValue(TGDuration.SIXTY_FOURTH);
		duration.setDotted(false);
		duration.setDoubleDotted(false);
		duration.getDivision().setEnters(3);
		duration.getDivision().setTimes(2);
		return fromTime(factory,time,duration);
	}
	
	public static TGDuration fromTime(TGFactory factory,long time,TGDuration minDuration){
		return fromTime(factory, time, minDuration, 10);
	}
	
	public static TGDuration fromTime(TGFactory factory,long time,TGDuration minimum, int diff){
		TGDuration duration = minimum.clone(factory);
		TGDuration tmpDuration = factory.newDuration();
		tmpDuration.setValue(TGDuration.WHOLE);
		tmpDuration.setDotted(true);
		boolean finish = false;
		while(!finish){
			long tmpTime = tmpDuration.getTime();
			if(tmpTime - diff <= time){
				//if(tmpTime > duration.getTime()){
				if(Math.abs( tmpTime - time ) < Math.abs( duration.getTime() - time ) ){
					duration = tmpDuration.clone(factory);
				}
			}
			if(tmpDuration.isDotted()){
				tmpDuration.setDotted(false);
			}else if(tmpDuration.getDivision().isEqual(TGDivisionType.NORMAL)){
				tmpDuration.getDivision().setEnters(3);
				tmpDuration.getDivision().setTimes(2);
			}else{
				tmpDuration.setValue(tmpDuration.getValue() * 2);
				tmpDuration.setDotted(true);
				tmpDuration.getDivision().setEnters(1);
				tmpDuration.getDivision().setTimes(1);
			}
			if(tmpDuration.getValue() > TGDuration.SIXTY_FOURTH){
				finish = true;
			}
		}
		return duration;
	}
	/*
	public int log2(){
		return (int)((Math.log(getValue() ) / Math.log(2.0)) + 0.5f );
	}
	*/
	public int getIndex(){
		int index = 0;
		int value = this.value;
		while( ( value = ( value >> 1 ) ) > 0 ){
			index ++;
		}
		return index;
	}
	
	public boolean isEqual(TGDuration d){
		return (getValue() == d.getValue() && isDotted() == d.isDotted() && isDoubleDotted() == d.isDoubleDotted() && getDivision().isEqual(d.getDivision()));
	}
	
	public TGDuration clone(TGFactory factory){
		TGDuration duration = factory.newDuration();
		copy(duration);
		return duration;
	}
	
	public void copy(TGDuration duration){
		duration.setValue(getValue());
		duration.setDotted(isDotted());
		duration.setDoubleDotted(isDoubleDotted());
		getDivision().copy(duration.getDivision());
	}
	
}
