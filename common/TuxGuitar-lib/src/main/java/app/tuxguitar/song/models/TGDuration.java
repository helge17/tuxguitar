/*
 * Created on 29-nov-2005
 *
 */
package app.tuxguitar.song.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.song.factory.TGFactory;
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
	// - each di is longer than min duration (value smaller than maxValue) if specified
	// - each di has time division.enters smaller than or equal to maxDivision if specified
	// - if approximate is set, list closest to target is returned
	//
	// if not all criteria can be fulfilled, null is returned
	
/*
 * principle of the algorithm:
 * All valid durations are subdivisions of a whole
 * a whole's duration is encoded by the product of all prime numbers of possible divisions: 3*5*7*...
 * so a whole's duration is divisible by all possible values of TGDivision.enters
 * (warning: 9 is not a prime)
 * when splitting D into multiple TGDuration di:
 * - if none of these di use a division 8/13, then the sum shall remain divisible by 13
 * - logically: if D is NOT divisible by 13, then it must contain some TGDuration di with TGDivision 8/13
 * - if so, compute how many instances of TGDuration with the lowest possible duration and this division
 *          shall be subtracted from D to make sure the remaining value becomes divisible by 13
 * - when it's done, look if the remaining value of D is divisible by 11
 * - etc
 * 
 */
	
	public static List<TGDuration> splitPreciseDuration(long timeToSplit, long max, TGFactory factory) {
		return splitPreciseDuration(timeToSplit, max, factory, null, null);
	}
	public static List<TGDuration> splitPreciseDuration(long timeToSplit, long max, TGFactory factory,
			Integer maxDurationValue, Integer maxDivision) {

		List<TGDuration> list = new ArrayList<TGDuration>();
		if (timeToSplit == 0) {
			return list;
		}
		SplitLimits limits = new SplitLimits(max, maxDurationValue, maxDivision);
		long remaining = timeToSplit;
		// look for all division types, starting with longest divisions, except excluded ones
		for (TGDivisionType dt : divisionTypes) {
			if (limits.mustSplitWith(dt, remaining)) {
				List<TGDuration> durations = splitDivision(dt, remaining, limits, factory);
				list.addAll(durations);
				remaining -= totalPreciseTime(durations);
			}
		}
		return (remaining == 0 ? list : null);
	}

	// durations with this time division to take out of remaining, so that what is left contains none of them
	// try successively to match with no dotted/no double-dotted, then with dotted, then with double-dotted
	private static List<TGDuration> splitDivision(TGDivisionType dt, long remaining, SplitLimits limits, TGFactory factory) {
		for (int subDivision = 1; subDivision <= 4; subDivision *= 2) {
			long base = limits.shortestTime(dt) / subDivision;
			if ((base > limits.maxBase) || ((subDivision != 1) && !limits.allowsDots())) {
				break;
			}
			List<TGDuration> durations = splitSubDivision(dt, remaining, base, subDivision, limits, factory);
			if (durations != null) {
				return durations;
			}
		}
		return Collections.emptyList();
	}

	// subDivision: 1 = no dot (base = shortest), 2 = dotted (base = shortest/2), 4 = double-dotted (base = shortest/4)
	// returns null when this subDivision cannot remove the contribution of the time division
	private static List<TGDuration> splitSubDivision(TGDivisionType dt, long remaining, long base, int subDivision,
			SplitLimits limits, TGFactory factory) {
		long toSubtract = timeToSubtract(dt, remaining, base, limits.modulus(dt));
		if ((toSubtract % base != 0) || (toSubtract > remaining)) {
			return null;
		}
		long baseCount = toSubtract / base;
		long merged = mergedPowerOfTwo(baseCount, base, limits.max);
		// n * 3 * (shortest/2) = n * dotted shortest, n * 7 * (shortest/4) = n * double-dotted shortest
		long dotFactor = 2 * subDivision - 1;
		long count = baseCount / merged;
		if (count % dotFactor != 0) {
			return null;
		}
		long durationCount = count / dotFactor;
		long undottedTime = merged * base * subDivision;
		// specific case for 9-uplets: not ok if number of instances can be divided by 3
		if ((dt.getEnters() == 9) && (durationCount % 3 == 0)) {
			undottedTime *= 3;
			durationCount /= 3;
		}
		return newDurations(durationCount, undottedTime, (subDivision == 2), (subDivision == 4), factory);
	}

	// subtract as many occurrences of base from remaining so that it does not contain any more duration with this time division
	// (the result is larger than remaining when that is impossible)
	private static long timeToSubtract(TGDivisionType dt, long remaining, long base, int modulus) {
		if ((dt.getEnters() == 1) || isPowerOfTwoMultiple(remaining, base)) {
			return remaining;
		}
		long toSubtract = base;
		while (((remaining - toSubtract) % modulus != 0) && (toSubtract <= remaining)) {
			toSubtract += base;
		}
		return toSubtract;
	}

	private static boolean isPowerOfTwoMultiple(long time, long base) {
		long count = time / base;
		return (time % base == 0) && ((count & (count - 1)) == 0);
	}

	// largest power of 2 dividing baseCount, such that this many base durations still fit in max
	private static long mergedPowerOfTwo(long baseCount, long base, long max) {
		long merged = 1;
		while (((baseCount / merged) % 2 == 0) && (merged * 2 * base <= max)) {
			merged *= 2;
		}
		return merged;
	}

	private static List<TGDuration> newDurations(long count, long undottedTime, boolean dotted, boolean doubleDotted,
			TGFactory factory) {
		List<TGDuration> list = new ArrayList<TGDuration>();
		for (long i = 0; i < count; i++) {
			TGDuration duration = factory.newDuration();
			duration.setPreciseValue(undottedTime);
			duration.setDotted(dotted);
			duration.setDoubleDotted(doubleDotted);
			list.add(duration);
		}
		return list;
	}

	private static long totalPreciseTime(List<TGDuration> durations) {
		long total = 0;
		for (TGDuration duration : durations) {
			total += duration.getPreciseTime();
		}
		return total;
	}

	public static List<TGDuration> splitPreciseDurationApproximately(long timeToSplit, long max, TGFactory factory,
			Integer maxDurationValue, Integer maxDivision) {

		if (timeToSplit == 0) {
			return new ArrayList<TGDuration>();
		}
		// try to split exactly, then approximately, and take the "best" solution
		List<TGDuration> exactSplit = splitPreciseDuration(timeToSplit, max, factory, maxDurationValue, maxDivision);
		SplitLimits limits = new SplitLimits(max, maxDurationValue, maxDivision);
		List<TGDuration> approximateSplit = closestSingleDuration(timeToSplit, limits, factory);
		if (approximateSplit == null) {
			// if no single duration found, try a combination of several
			approximateSplit = closestCombination(timeToSplit, limits, factory);
		}
		return preferredSplit(exactSplit, approximateSplit);
	}

	// look for a single duration with an acceptable error
	// returns an empty list if zero is the best acceptable approximation, null if nothing is acceptable
	private static List<TGDuration> closestSingleDuration(long timeToSplit, SplitLimits limits, TGFactory factory) {
		boolean zeroAcceptable = (timeToSplit <= TGDuration.WHOLE_PRECISE_DURATION / limits.shortest);
		TGDuration bestDuration = closestDuration(timeToSplit, limits, (zeroAcceptable ? timeToSplit : -1));
		if (bestDuration != null) {
			List<TGDuration> list = new ArrayList<TGDuration>();
			list.add(bestDuration.clone(factory));
			return list;
		}
		return (zeroAcceptable ? new ArrayList<TGDuration>() : null);
	}

	// zeroError: error of an empty split, negative if an empty split is not acceptable
	private static TGDuration closestDuration(long timeToSplit, SplitLimits limits, long zeroError) {
		long bestError = zeroError;
		TGDuration bestDuration = null;
		for (TGDuration d : durationMap.values()) {
			long error = Math.abs(d.getPreciseTime() - timeToSplit);
			if (limits.acceptsApproximation(d, error) && d.isBetterApproximation(error, bestDuration, bestError)) {
				bestDuration = d;
				bestError = error;
			}
		}
		return bestDuration;
	}

	// simpler than the best so far, or as simple and closer
	private boolean isBetterApproximation(long error, TGDuration bestDuration, long bestError) {
		int complexity = this.moreComplexThan(bestDuration);
		return (bestError < 0) || (complexity < 0) || ((complexity == 0) && (error < bestError));
	}

	// closest sum of durations with a single time division, looking for all division types, starting with longest divisions
	private static List<TGDuration> closestCombination(long timeToSplit, SplitLimits limits, TGFactory factory) {
		List<TGDuration> closest = new ArrayList<TGDuration>();
		// error of the empty list
		long bestError = timeToSplit;
		for (TGDivisionType dt : divisionTypes) {
			if (limits.allows(dt)) {
				long base = limits.shortestTime(dt);
				if (base > limits.maxBase) {
					break;
				}
				List<TGDuration> list = combination(timeToSplit, base, limits.max, factory);
				long error = Math.abs(timeToSplit - totalPreciseTime(list));
				if (closest.isEmpty() || (error <= bestError)) {
					closest = list;
					bestError = error;
				}
			}
		}
		return closest;
	}

	// rounded number of base durations, merged by powers of 2 as long as they fit in max, longest first
	private static List<TGDuration> combination(long timeToSplit, long base, long max, TGFactory factory) {
		List<TGDuration> list = new ArrayList<TGDuration>();
		// apply -1 offset, because Math.round(1.5)==2, and 1 is preferred
		// (better a bit too short than too long)
		long baseCount = Math.round((float)(timeToSplit-1) / (float)base);
		while (baseCount > 0) {
			if (2 * base > max) {
				list.addAll(newDurations(baseCount, base, false, false, factory));
				baseCount = 0;
			}
			else if ((baseCount % 2) != 0) {
				list.addAll(newDurations(1, base, false, false, factory));
				baseCount --;
			}
			else {
				baseCount /= 2;
				base *= 2;
			}
		}
		Collections.reverse(list);
		return list;
	}

	// compare exact split with approximate (arbitrary criteria)
	private static List<TGDuration> preferredSplit(List<TGDuration> exactSplit, List<TGDuration> approximateSplit) {
		if ((exactSplit == null) || hasMixedDivisions(exactSplit) || (approximateSplit.size() < exactSplit.size())) {
			return approximateSplit;
		}
		return exactSplit;
	}

	private static boolean hasMixedDivisions(List<TGDuration> durations) {
		for (TGDuration d : durations) {
			if (!d.getDivision().isEqual(durations.get(0).getDivision())) {
				return true;
			}
		}
		return false;
	}

	// constraints on the durations a split may produce
	private static final class SplitLimits {
		// longest allowed duration, no longer than a whole
		private final long max;
		// longest power of 2 fraction of a whole, no longer than max
		private final long maxBase;
		// shortest allowed duration value (highest denominator)
		private final int shortest;
		private final Integer maxDurationValue;
		private final Integer maxDivision;

		private SplitLimits(long max, Integer maxDurationValue, Integer maxDivision) {
			this.max = Math.min(max, TGDuration.WHOLE_PRECISE_DURATION);
			long maxBase = TGDuration.WHOLE_PRECISE_DURATION;
			while (this.max < maxBase) {
				maxBase /= 2;
			}
			this.maxBase = maxBase;
			this.shortest = (maxDurationValue == null ? SHORTEST : maxDurationValue);
			this.maxDurationValue = maxDurationValue;
			this.maxDivision = maxDivision;
		}

		// shortest possible duration for this time division
		private long shortestTime(TGDivisionType dt) {
			return TGDuration.WHOLE_PRECISE_DURATION * dt.getTimes() / (dt.getEnters() * this.shortest);
		}

		private boolean allows(TGDivisionType dt) {
			return (this.maxDivision == null) || (dt.getEnters() <= this.maxDivision);
		}

		// dotted and double-dotted durations are only used when the shortest value is not limited
		private boolean allowsDots() {
			return (this.maxDurationValue == null);
		}

		// specific case if maxDivision < 9, since 9 is not a prime number
		private int modulus(TGDivisionType dt) {
			boolean ninesExcluded = (this.maxDivision != null) && (this.maxDivision < 9);
			return ((ninesExcluded && (dt.getEnters() == 3)) ? 9 : dt.getEnters());
		}

		// the remaining time contains durations with this time division
		private boolean mustSplitWith(TGDivisionType dt, long remaining) {
			return (dt.getEnters() == 1) || ((remaining % modulus(dt) != 0) && allows(dt));
		}

		// exclude double-dotted: purely arbitrary criterion
		private boolean acceptsApproximation(TGDuration d, long error) {
			return (d.getValue() <= this.shortest) && allows(d.getDivision()) && !d.isDoubleDotted()
					&& (error <= shortestTime(d.getDivision()));
		}
	}

	// arbitrary complexity criteria
	// returns a positive value if more complex, negative if less complex, zero if equivalent
	// (approximations are never double-dotted: see acceptsApproximation)
	private int moreComplexThan(TGDuration d) {
		if (d == null) {
			// zero duration is "as simple as" no dotted, no time division
			return (isPlain() ? 0 : 1);
		}
		int byDivision = Integer.compare(this.getDivision().getEnters(), d.getDivision().getEnters());
		if (byDivision != 0) {
			return byDivision;
		}
		return ((this.isDotted() && !d.isDotted()) ? 1 : 0);
	}

	// not dotted, no time division
	private boolean isPlain() {
		return !this.dotted && !this.doubleDotted && this.getDivision().isEqual(TGDivisionType.NORMAL);
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
			throw new IllegalArgumentException("invalid duration " +String.valueOf(preciseValue) + " /whole=" + String.valueOf(TGDuration.WHOLE_PRECISE_DURATION));
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
		return fromTime(factory, time, minDuration, (short) 0);
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
