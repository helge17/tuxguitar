/*
 * Created on 29-nov-2005
 *
 */
package org.herac.tuxguitar.song.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.song.factory.TGFactory;
/**
 * @author julian
 *
 */
public abstract class TGDuration implements Comparable<TGDuration> {
	/**
	 * [ES] tiempo por defecto de la Negra.<br>
	 * [EN] The number of fractions a quarter note can be divided into.
	 *      unit for imprecise time duration (legacy)
	 *      exposed to rounding errors for some time divisions (tuplets)
	 */
	public static final long QUARTER_TIME = 960;
	/**
	 * the number of fractions a whole note can be divided into, without any rounding issue
	 * unit for precise duration = least common multiple of all possible time divisions (fractions of time)
	 * to make sure computation of durations is performed on integers for all supported tuplets
	 */
	public static long WHOLE_PRECISE_DURATION;
	/**
	 * Possible duration values, as fractions of a whole
	 */
	public static final int WHOLE = 1;
	public static final int HALF = 2;
	public static final int QUARTER = 4;
	public static final int EIGHTH = 8;
	public static final int SIXTEENTH = 16;
	public static final int THIRTY_SECOND = 32;
	public static final int SIXTY_FOURTH = 64;
	
	/**
	 * The shortest possible note (highest value)
	 */
	public static final int SHORTEST = SIXTY_FOURTH;
	
	/**
	 * Valor.
	 * Value, shall be one of the constants defined above
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

	private static List<TGDivisionType> divisionTypes;
	
	/**
	 * Map containing all note durations that are
	 * expected to be supported.<br>
	 * Key = duration precise time
	 */
	private static final Map<Long, TGDuration> durationMap;
	
	static {
		WHOLE_PRECISE_DURATION = SHORTEST;
		divisionTypes = new ArrayList<TGDivisionType>();
		for (TGDivisionType dt : TGDivisionType.DIVISION_TYPES) {
			WHOLE_PRECISE_DURATION = lcm(WHOLE_PRECISE_DURATION, dt.getEnters());
			// list of division types: only consider "prime" values (e.g. do not consider 6:4)
			if (gcd(dt.getEnters(), dt.getTimes()) == 1) {
				divisionTypes.add(dt);
			}
		}
		// for dotted and double-dotted, enable finer grain division
		WHOLE_PRECISE_DURATION *= 4;
		// sort division types, shortest division first (=highest .enters value)
		Collections.sort(divisionTypes,
				(TGDivisionType dt1, TGDivisionType dt2) -> (Integer.valueOf(dt2.getEnters()).compareTo(Integer.valueOf(dt1.getEnters()))));
		durationMap = createDurationMap();
	}
	
	// precise to approximate time (possible rounding error)
	public static long toTime(long preciseTime) {
		return QUARTER_TIME * QUARTER * preciseTime / WHOLE_PRECISE_DURATION;
	}
	
	// approximate to precise time
	public static long toPreciseTime(long ticks) {
		return ticks * WHOLE_PRECISE_DURATION / (QUARTER_TIME * QUARTER) ;
	}
	
	// convention: starting point of song is 1 quarter
	public static Long getStartingPoint() {
		return QUARTER_TIME;
	}
	
	public static Long getPreciseStartingPoint() {
		return toPreciseTime(getStartingPoint());
	}

	
	// split duration into a list of valid durations
	// all computations done on precise durations
	public static List<TGDuration> splitPreciseDuration(long timeToSplit, long maxTime, TGDuration preferredDuration, TGFactory factory) {
		if ((preferredDuration != null) && (preferredDuration.getPreciseTime() <=maxTime)) {
			long preferredTime = preferredDuration.getPreciseTime();
			// fill with preferred duration, then try to fill with anything that can fit
			Long remainingTime = timeToSplit;
			List<TGDuration> list = new ArrayList<TGDuration>();
			while ( remainingTime >= preferredTime ) {
				list.add(preferredDuration.clone(factory));
				remainingTime -= preferredTime;
			}
			// if something remains, try to split with no preferred duration constraint
			List<TGDuration> remainingList = splitPreciseDuration(remainingTime, maxTime, factory);
			if (remainingList != null) {
				list.addAll(remainingList);
				return list;
			}
		}
		// if could not split with preferred duration, try without this constraint
		return splitPreciseDuration(timeToSplit, maxTime, factory);
	}
	
	// objective: split input precise duration D into a list of durations di such as:
	// - sum of precise durations of all di == D
	// - each di is less than or equal to specified max
	// - for one time division, all di are identical. E.g. [3*eighths triplet] instead  of [1 quarter triplet + 1 eighth triplet]
	// - di with highest denominator of time division appear first in returned list (i.e. quintuplets before triplets)
	//
	// if not all criteria can be fulfilled, null is returned
	public static List<TGDuration> splitPreciseDuration(long timeToSplit, long max, TGFactory factory) {
		long D = timeToSplit;
		List<TGDuration> list = new ArrayList<TGDuration>();
		
		// look for all division types, starting with longest divisions
		for (TGDivisionType dt : divisionTypes) {
			if ((dt.getEnters()==1) || (D % dt.getEnters() != 0)) {
				// D contains notes with this time division
				boolean foundDurationWithTimeDivision = false;
				
				// try successively to match with no dotted/no double-dotted, then with dotted, then with double-dotted
				for (int subDivision = 1; subDivision <= 4 && !foundDurationWithTimeDivision; subDivision*=2) {
					long base = TGDuration.WHOLE_PRECISE_DURATION * dt.getTimes() / (dt.getEnters() * SHORTEST);  // shortest possible duration for this time division
					base /= subDivision;
					
					if (base > max)  {
						break;
					}
					long toSubtract;
					if (dt.getEnters() == 1) {
						toSubtract = D;
					} else {
						// subtract as many occurrences of base to D so that D does not contain any more duration with this time division
						toSubtract = base;
						while( ((D-toSubtract) % dt.getEnters() != 0) && (toSubtract <= D)) {
							toSubtract += base;
						}
					}
					// if the amount of time to subtract removes the contribution of time division, then let's go
					if ((toSubtract % base == 0) && (toSubtract <= D)) {
						// number of occurrences of base duration
						long nBase = toSubtract / base;
						// look for longest note duration with this time division that can fit (successive powers of 2)
						long n=1;
						while ((nBase % 2 == 0) && (n*base <= max)) {
							n *= 2;
							nBase /= 2;
						}
						// handle subDivision: no dot / dotted / double-dotted
						boolean ok = false;
						boolean dotted = false;
						boolean doubleDotted = false;
						switch (subDivision) {
						case 1:
							// base =  shortest
							ok = true;
							break;
						case 2:
							// base = shortest /2, only a multiple of 3 is valid
							// n * 3 * base = n * shortest * 3/2 = n * dotted shortest
							ok = (nBase % 3 == 0); 
							dotted = true;
							nBase /= 3;
							base *= 2;
							break;
						case 4:
							// base = shortest /4, only a multiple of 7 is valid
							// n * 7 * base = n * shortest * 7/4 =  n * double-dotted shortest
							ok = (nBase % 7 == 0);
							doubleDotted = true;
							nBase /= 7;
							base *=4 ;
							break;
						default:
							return null;
						}
						
						if (ok) {
							for (int i=0; i<nBase; i++) {
								TGDuration duration = factory.newDuration();
								duration.setPreciseValue(n* base);
								duration.setDotted(dotted);
								duration.setDoubleDotted(doubleDotted);
								list.add(duration);
							}
							D -= toSubtract;
							foundDurationWithTimeDivision = true;
						}
					}
				}
			}
		}
		if (D != 0) {
			return null;
		}
		return list;
	}
	
	private static long gcd(long a, long b) {
		// Euclid algorithm
		if (b == 0) return a;
		return gcd(b, a % b);
	}
	private static long lcm(long a, long b) {
		return a * b / gcd(a,b);
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
	
	public void setPreciseValue(Long preciseValue) {
		TGDuration duration = durationMap.get(preciseValue);
		if (duration == null) {
			// invalid!
			throw new IllegalArgumentException();
		}
		this.copyFrom(duration);
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
	
	public long getPreciseTime() {
		long preciseDuration = WHOLE_PRECISE_DURATION / this.value;
		if (this.dotted) {
			preciseDuration = preciseDuration * 3 / 2;
		}
		else if (this.doubleDotted) {
			preciseDuration = preciseDuration * 7 / 4;
		}
		preciseDuration = preciseDuration * this.divisionType.getTimes() / this.divisionType.getEnters();
		return preciseDuration;
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

	@Override
	public int compareTo(TGDuration duration) {
		if (duration == null) return 1;
		if ((this.getPreciseTime() >= 0) && (duration.getPreciseTime() >= 0)) {
			return Long.valueOf(this.getPreciseTime()).compareTo(Long.valueOf(duration.getPreciseTime()));
		}
		return (Long.valueOf(this.getTime()).compareTo(Long.valueOf(duration.getTime())));
	}

	
	private static Map<Long, TGDuration> createDurationMap() {
		TGFactory factory = new TGFactory();
		HashMap<Long, TGDuration> durationHashMap = new HashMap<Long, TGDuration>();
		
		// several valid TGDuration instances may have the exact same duration
		// priority : not dotted, then dotted, then double-dotted
		
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
						// default (executed first)
						tmpDuration.setDotted(false);
						tmpDuration.setDoubleDotted(false);
					}
					
					tmpDuration.setValue(v);
					tmpDuration.getDivision().setEnters(tmpDivisionType.getEnters());
					tmpDuration.getDivision().setTimes(tmpDivisionType.getTimes());
					
					TGDuration entry = tmpDuration;
					long key = entry.getPreciseTime();
					if(!durationHashMap.containsKey(key)) {
						durationHashMap.put(key, entry);
					}
				}
			}
		}
		
		return durationHashMap;
	}
}
