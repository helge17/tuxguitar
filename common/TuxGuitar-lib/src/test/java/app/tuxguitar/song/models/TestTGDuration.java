package app.tuxguitar.song.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import app.tuxguitar.song.factory.TGFactory;
import org.junit.jupiter.api.Test;

public class TestTGDuration {

	private TGFactory factory = new TGFactory();

	@Test
	public void testPreciseDuration() {

		// manually computed, to update if new time divisions are added
		assertEquals(4*64*3*5*7*3*11*13, TGDuration.WHOLE_PRECISE_DURATION);

		TGDuration d = factory.newDuration();

		d.setValue(TGDuration.WHOLE);
		d.setDotted(false);
		d.setDoubleDotted(false);
		d.getDivision().setEnters(1);
		d.getDivision().setTimes(1);
		// check precise time values
		long pt = d.getPreciseTime();
		assertEquals(TGDuration.WHOLE_PRECISE_DURATION, pt);
		// conversion from precise time to TGDuration
		d.setPreciseValue(pt);
		assertEquals(TGDuration.WHOLE, d.getValue());
		assertFalse(d.isDoubleDotted());
		assertFalse(d.isDotted());
		assertEquals(1, d.getDivision().getEnters());
		assertEquals(1, d.getDivision().getTimes());


		d.setValue(TGDuration.HALF);
		pt = d.getPreciseTime();
		assertEquals(TGDuration.WHOLE_PRECISE_DURATION / 2, pt);
		d.setPreciseValue(pt);
		assertEquals(TGDuration.HALF, d.getValue());
		assertFalse(d.isDoubleDotted());
		assertFalse(d.isDotted());
		assertEquals(1, d.getDivision().getEnters());
		assertEquals(1, d.getDivision().getTimes());


		d.setDotted(true);
		pt = d.getPreciseTime();
		assertEquals(TGDuration.WHOLE_PRECISE_DURATION * 3/ 4, pt);
		d.setPreciseValue(pt);
		assertEquals(TGDuration.HALF, d.getValue());
		assertFalse(d.isDoubleDotted());
		assertTrue(d.isDotted());
		assertEquals(1, d.getDivision().getEnters());
		assertEquals(1, d.getDivision().getTimes());


		d.setDotted(false);
		d.setDoubleDotted(true);
		pt = d.getPreciseTime();
		assertEquals(TGDuration.WHOLE_PRECISE_DURATION * 7 / 8, pt);
		d.setPreciseValue(pt);
		assertEquals(TGDuration.HALF, d.getValue());
		assertTrue(d.isDoubleDotted());
		assertFalse(d.isDotted());
		assertEquals(1, d.getDivision().getEnters());
		assertEquals(1, d.getDivision().getTimes());


		d.setDoubleDotted(false);
		d.setValue(TGDuration.EIGHTH);
		d.getDivision().setEnters(3);
		d.getDivision().setTimes(2);
		pt = d.getPreciseTime();
		assertEquals(TGDuration.WHOLE_PRECISE_DURATION / 12, pt);
		d.setPreciseValue(pt);
		assertEquals(TGDuration.EIGHTH, d.getValue());
		assertFalse(d.isDoubleDotted());
		assertFalse(d.isDotted());
		assertEquals(3, d.getDivision().getEnters());
		assertEquals(2, d.getDivision().getTimes());


		d.setValue(TGDuration.SIXTEENTH);
		d.getDivision().setEnters(9);
		d.getDivision().setTimes(8);
		pt = d.getPreciseTime();
		assertEquals(TGDuration.WHOLE_PRECISE_DURATION / 18, pt);
		d.setPreciseValue(pt);
		assertEquals(TGDuration.SIXTEENTH, d.getValue());
		assertFalse(d.isDoubleDotted());
		assertFalse(d.isDotted());
		assertEquals(9, d.getDivision().getEnters());
		assertEquals(8, d.getDivision().getTimes());

		// smallest possible subdivision with 1:1 time division
		d.setValue(TGDuration.SIXTY_FOURTH);
		d.setDotted(false);
		d.setDoubleDotted(true);
		d.getDivision().setEnters(1);
		d.getDivision().setTimes(1);
		pt = d.getPreciseTime();
		assertEquals(TGDuration.WHOLE_PRECISE_DURATION * 7 / (64*4), pt);
		d.setPreciseValue(pt);
		assertEquals(TGDuration.SIXTY_FOURTH, d.getValue());
		assertTrue(d.isDoubleDotted());
		assertFalse(d.isDotted());
		assertEquals(1, d.getDivision().getEnters());
		assertEquals(1, d.getDivision().getTimes());

		// 64th with time division different from 1:1
		d.setValue(TGDuration.SIXTY_FOURTH);
		d.setDotted(false);
		d.setDoubleDotted(false);
		d.getDivision().setEnters(3);
		d.getDivision().setTimes(2);
		pt = d.getPreciseTime();
		assertEquals(TGDuration.WHOLE_PRECISE_DURATION * 2 / (3*64), pt);
		d.setPreciseValue(pt);
		assertEquals(TGDuration.SIXTY_FOURTH, d.getValue());
		assertFalse(d.isDoubleDotted());
		assertFalse(d.isDotted());
		assertEquals(3, d.getDivision().getEnters());
		assertEquals(2, d.getDivision().getTimes());


		// a double-dotted tuplet
		d.setValue(TGDuration.WHOLE);
		d.setDotted(false);
		d.setDoubleDotted(true);
		d.getDivision().setEnters(5);
		d.getDivision().setTimes(4);
		pt = d.getPreciseTime();
		assertEquals(TGDuration.WHOLE_PRECISE_DURATION * 7 / 5, pt);
		d.setPreciseValue(pt);
		assertEquals(TGDuration.WHOLE, d.getValue());
		assertTrue(d.isDoubleDotted());
		assertFalse(d.isDotted());
		assertEquals(5, d.getDivision().getEnters());
		assertEquals(4, d.getDivision().getTimes());

		// with finer time division that the shortest
		d.setValue(TGDuration.SIXTY_FOURTH);
		d.setDotted(true);
		d.setDoubleDotted(false);
		d.getDivision().setEnters(1);
		d.getDivision().setTimes(1);
		pt = d.getPreciseTime();
		d.setPreciseValue(pt);
		assertEquals(TGDuration.SIXTY_FOURTH, d.getValue());
		assertTrue(d.isDotted());
		assertFalse(d.isDoubleDotted());
		assertEquals(1, d.getDivision().getEnters());
		assertEquals(1, d.getDivision().getTimes());

		// exhaustive test, all valid values, convert in both directions
		for(TGDivisionType dt : TGDivisionType.DIVISION_TYPES)
		{
			for (int i = 0; i < 3; i++)
			{
				for (int v = TGDuration.WHOLE; v <= TGDuration.SHORTEST; v *= 2)
				{
					TGDuration duration = factory.newDuration();

					if(i == 1) {
						duration.setDotted(true);
						duration.setDoubleDotted(false);
					}
					else if(i == 2) {
						duration.setDotted(false);
						duration.setDoubleDotted(true);
					}
					else {
						duration.setDotted(false);
						duration.setDoubleDotted(false);
					}

					duration.setValue(v);
					duration.getDivision().setEnters(dt.getEnters());
					duration.getDivision().setTimes(dt.getTimes());

					TGDuration check = factory.newDuration();
					check.setPreciseValue(duration.getPreciseTime());
					// don't directly compare to original duration
					// it cannot be bijective, e.g. duration of a [triplet-dotted-whole] equals this of a [whole]
					assertTrue(check.getPreciseTime() == duration.getPreciseTime());
					assertEquals(check.getTime(), duration.getTime());
				}
			}
		}
	}

	// conversions approximate time <-> precise time
	@Test
	public void testTimePreciseTime() {
		assertEquals(960, TGDuration.toTime(TGDuration.WHOLE_PRECISE_DURATION / 4));
		assertEquals(TGDuration.WHOLE_PRECISE_DURATION / 4, TGDuration.toPreciseTime(960));
		assertEquals(960, TGDuration.getStartingPoint());
		assertEquals(TGDuration.toPreciseTime(960), TGDuration.getPreciseStartingPoint());
	}

	@Test
	public void testSplitDuration() {
		// trivial case, 3 quarters
		List<TGDuration> list = TGDuration.splitPreciseDuration(TGDuration.WHOLE_PRECISE_DURATION*3/4, TGDuration.WHOLE_PRECISE_DURATION/4, factory);
		assertEquals(3, list.size());
		assertEquals(list.get(0).getPreciseTime(), TGDuration.WHOLE_PRECISE_DURATION/4);
		assertEquals(list.get(1).getPreciseTime(), TGDuration.WHOLE_PRECISE_DURATION/4);
		assertEquals(list.get(2).getPreciseTime(), TGDuration.WHOLE_PRECISE_DURATION/4);

		// use case: 1 measures at 8/4, total duration = 2 * whole
		// instantiate 1 note: quarter with time division 9:8, duration = 1/4 * 8/9 = 2/9
		// split the remaining space (=16/9) into rests
		long toSplit = TGDuration.WHOLE_PRECISE_DURATION * 16 / 9;
		list = TGDuration.splitPreciseDuration(toSplit, 2*TGDuration.WHOLE_PRECISE_DURATION, factory);
		long sum = getListSumDuration(list, TGDuration.WHOLE_PRECISE_DURATION);
		assertEquals(sum, toSplit);

		// less trivial
		list = TGDuration.splitPreciseDuration(TGDuration.WHOLE_PRECISE_DURATION*13/15, TGDuration.WHOLE_PRECISE_DURATION, factory);
		assertNotNull(list);
		sum = getListSumDuration(list, TGDuration.WHOLE_PRECISE_DURATION);
		assertEquals(sum, TGDuration.WHOLE_PRECISE_DURATION*13/15);

		// test max
		list = TGDuration.splitPreciseDuration(3*TGDuration.WHOLE_PRECISE_DURATION, TGDuration.WHOLE_PRECISE_DURATION, factory);
		assertNotNull(list);
		assertEquals(3, list.size());
		assertEquals(list.get(0).getPreciseTime(), TGDuration.WHOLE_PRECISE_DURATION);
		assertEquals(list.get(1).getPreciseTime(), TGDuration.WHOLE_PRECISE_DURATION);
		assertEquals(list.get(2).getPreciseTime(), TGDuration.WHOLE_PRECISE_DURATION);

		// something impossible to split
		list = TGDuration.splitPreciseDuration(TGDuration.WHOLE_PRECISE_DURATION/(64*3), TGDuration.WHOLE_PRECISE_DURATION, factory);
		assertNull(list);
		list = TGDuration.splitPreciseDuration(TGDuration.WHOLE_PRECISE_DURATION/17, TGDuration.WHOLE_PRECISE_DURATION, factory);
		assertNull(list);

		// finer division time that the shortest division: dotted 64th = whole/64 *(3/2)
		list = TGDuration.splitPreciseDuration(TGDuration.WHOLE_PRECISE_DURATION*3/128, TGDuration.WHOLE_PRECISE_DURATION, factory);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(TGDuration.SIXTY_FOURTH, list.get(0).getValue());
		assertTrue(list.get(0).isDotted());
		assertFalse(list.get(0).isDoubleDotted());

		// finer division time that the shortest division: double-dotted 32nd = whole/32*(7/4)
		list = TGDuration.splitPreciseDuration(TGDuration.WHOLE_PRECISE_DURATION*7/128, TGDuration.WHOLE_PRECISE_DURATION, factory);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(TGDuration.THIRTY_SECOND, list.get(0).getValue());
		assertFalse(list.get(0).isDotted());
		assertTrue(list.get(0).isDoubleDotted());

		// finer division time that the shortest division: double-dotted 64th = whole/64*(7/4)
		list = TGDuration.splitPreciseDuration(TGDuration.WHOLE_PRECISE_DURATION*7/256, TGDuration.WHOLE_PRECISE_DURATION, factory);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(TGDuration.SIXTY_FOURTH, list.get(0).getValue());
		assertFalse(list.get(0).isDotted());
		assertTrue(list.get(0).isDoubleDotted());
		
		// max not a power of 2
		long max = TGDuration.WHOLE_PRECISE_DURATION*5/16;
		list = TGDuration.splitPreciseDuration(TGDuration.WHOLE_PRECISE_DURATION, max, factory);
		assertNotNull(list);
		sum = getListSumDuration(list, max);
		assertEquals(sum, TGDuration.WHOLE_PRECISE_DURATION);

		// when time to fill is very long
		// https://github.com/helge17/tuxguitar/issues/947
		boolean ok = false;
		try {
			list = TGDuration.splitPreciseDuration(2*TGDuration.WHOLE_PRECISE_DURATION, 9*TGDuration.WHOLE_PRECISE_DURATION/4, factory);
			ok=true;
		}
		catch (Throwable e) {}
		assert(ok);

		// triplet 16th
		toSplit = TGDuration.WHOLE_PRECISE_DURATION*2/3/16;
		list = TGDuration.splitPreciseDuration(toSplit, TGDuration.WHOLE_PRECISE_DURATION, factory);
		assertNotNull(list);
		sum = getListSumDuration(list, null);
		assertEquals(sum, toSplit);

		// limit division to 3, and value to 16, to avoid getting 3 * 9-tuplet of 64th
		list = TGDuration.splitPreciseDuration(toSplit, TGDuration.WHOLE_PRECISE_DURATION, factory,
				16, 3);
		assertNotNull(list);
		sum = getListSumDuration(list, null);
		assertEquals(sum, toSplit);
		for (TGDuration d : list) {
			assertTrue(d.getDivision().getEnters() <= 3);
		}

		// limit division to 3, and value to 32, should give the same result
		list = TGDuration.splitPreciseDuration(toSplit, TGDuration.WHOLE_PRECISE_DURATION, factory,
				32, 3);
		assertNotNull(list);
		sum = getListSumDuration(list, null);
		assertEquals(sum, toSplit);
		for (TGDuration d : list) {
			assertTrue(d.getDivision().getEnters() <= 3);
		}
	}

	// get sum of notes list duration, and check max (if specified)
	private long getListSumDuration(List<TGDuration> list, Long max) {
		long sum = 0;
		for(int i=0; i<list.size(); i++) {
			long preciseTime = list.get(i).getPreciseTime();
			if (max != null) {
				assertTrue(preciseTime <= max, "KO, max " + String.valueOf(max) + " / " + String.valueOf(preciseTime));
			}
			sum += preciseTime;
		}
		return sum;
	}
}
