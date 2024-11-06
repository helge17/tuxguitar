/*
 * Created on 29-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.song.factory.TGFactory;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGDuration {
	/**
	 * [ES] tiempo por defecto de la Negra.<br>
	 * [EN] The number of fractions a quarter note can be divided into.
	 *      This value should be equal to the Lowest Common Denominator (LCD)
	 *      of all note durations including all supported tuplets.
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
	 * The shortest possible note (highest value)
	 */
	public static final int SHORTEST = SIXTY_FOURTH;
	
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
	
	/**
	 * Map containing all note durations that are
	 * expected to be supported.<br>
	 * Key = duration time
	 */
	private static final Map<Long, TGDuration> durationMap;
	
	static {
		durationMap = createDurationMap();
	}
	
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
	
	public static TGDuration getShortestDuration(TGFactory factory){
		TGDuration minimum = factory.newDuration();
		minimum.setValue(TGDuration.SHORTEST);
		minimum.setDotted(false);
		minimum.setDoubleDotted(false);
		minimum.getDivision().setEnters(7);
		minimum.getDivision().setTimes(4);
		return minimum;
	}
	
	public static TGDuration fromTime(TGFactory factory, long time){
		TGDuration duration = getShortestDuration(factory);
		return fromTime(factory, time, duration);
	}
	
	public static TGDuration fromTime(TGFactory factory, long time, TGDuration minDuration){
		// The value of `threshold` is dependent on the value of
		// `TGDuration.QUARTER_TIME` and the tuplets that are expected to be supported.
		short threshold = 0;
		
		long quaterTimeConstant = QUARTER_TIME; // Local variable to suppress warnings
		if(quaterTimeConstant == 960) {
			// Patches for when QUARTER_TIME = 960
			if(time < 60) {
				// Notes shorter then the normal 64th note
				threshold = 2;
			}
			else if(time == 95) {
				// Fix going from any 64th tuplet note to a 64th 9-tuplet note
				threshold = 3;
			}
			else if(time == 138) {
				// Fix going from 8th 7-tuplet note to 16th 7-tuplet note in a 4/16 measure
				// also fixes going from 8th 7-tuplet note to 16th 7-tuplet note in a 4/8 measure | if(time == 412 || time == 549)
				// also fixes going from 32th 7-tuplet note to 16th 7-tuplet note | if(time > 822 && time < 2882)
				threshold = 1;
			}
			else if(time == 177) {
				// Fix going from 64th 11-tuplet note to 32th 11-tuplet note
				threshold = 3;
			}
			else if(time == 748) {
				// Fix going from 16th 9-tuplet note to 32th 9-tuplet note
				threshold = 2;
			}
			else if(time == 854) {
				// Fix going from 64th 9-tuplet note to 32th 9-tuplet note
				threshold = 1;
			}
		}
		return fromTime(factory, time, minDuration, threshold);
	}
	
	public static TGDuration fromTime(TGFactory factory, long time, TGDuration minimum, short threshold) {
		TGDuration durationFound = null;
		for (int i = 0; i <= threshold; i++)
		{
			durationFound = durationMap.get(time-i);
			if(durationFound != null) {
				return durationFound.clone(factory);
			}
		}
		
		durationFound = factory.newDuration();
		
		for (int v = TGDuration.WHOLE; v <= SHORTEST; v *= 2)
		{
			durationFound.setValue(v);
			if(time >= durationFound.getTime()) {
				return durationFound.clone(factory);	
			}
		}
		
		return minimum.clone(factory);
	}
	
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
		TGDuration tgDuration = factory.newDuration();
		tgDuration.copyFrom(this);
		return tgDuration;
	}
	
	public void copyFrom(TGDuration duration){
		this.setValue(duration.getValue());
		this.setDotted(duration.isDotted());
		this.setDoubleDotted(duration.isDoubleDotted());
		this.getDivision().copyFrom(duration.getDivision());
	}
	
	private static Map<Long, TGDuration> createDurationMap() {
		TGFactory factory = new TGFactory();
		HashMap<Long, TGDuration> durationHashMap = new HashMap<Long, TGDuration>();
		
		for(TGDivisionType tmpDivisionType : TGDivisionType.DIVISION_TYPES)
		{
			for (int i = 0; i < 3; i++)
			{
				for (int v = TGDuration.WHOLE; v <= SHORTEST; v *= 2)
				{
					TGDuration tmpDuration = factory.newDuration();
					
					if(i == 1) {
						tmpDuration.setDotted(true);
						tmpDuration.setDoubleDotted(false);
					}
					else if(i == 2) {
						tmpDuration.setDotted(false);
						tmpDuration.setDoubleDotted(true);
					}
					else {
						tmpDuration.setDotted(false);
						tmpDuration.setDoubleDotted(false);
					}
					
					tmpDuration.setValue(v);
					tmpDuration.getDivision().setEnters(tmpDivisionType.getEnters());
					tmpDuration.getDivision().setTimes(tmpDivisionType.getTimes());
					
					TGDuration entry = tmpDuration;
					long key = entry.getTime();
					if(!durationHashMap.containsKey(key)) {
						durationHashMap.put(key, entry);
					}
				}
			}
		}
		
		return durationHashMap;
	}
}
