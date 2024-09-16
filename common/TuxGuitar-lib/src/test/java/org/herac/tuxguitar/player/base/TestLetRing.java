package org.herac.tuxguitar.player.base;

/* test of "letRing" duration
 * this test heavily depends from a test .tg file
 * take care to update this file whenever .tg file format changes (just save under new format)
 * this file needs to be readable by tuxguitar-lib
 * i.e. without dependency to old formats in tuxguitar-compat
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.io.tg.TestFileFormat20;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.junit.jupiter.api.Test;
import org.herac.tuxguitar.player.impl.sequencer.MidiEvent;
import org.herac.tuxguitar.player.impl.sequencer.MidiSequenceHandlerImpl;
import org.herac.tuxguitar.player.impl.sequencer.MidiSequencerImpl;

public class TestLetRing {
	
	private List<MidiEvent> events;
	private List<NoteTiming> notes;
	
	// load test song, and fill MIDI events list
	public TestLetRing() throws IOException {
		TGContext context = new TGContext();
		TGSong song = new TestFileFormat20().readSong("letRing.tg", true).getSong();
		MidiSequenceParser parser = new MidiSequenceParser(song, new TGSongManager(), 0);
		MidiSequencerImplForTest sequencer = new MidiSequencerImplForTest(context);
		MidiSequenceHandlerImpl seqHandler = new MidiSequenceHandlerImpl(sequencer,1);
		parser.parse(seqHandler);
		events = sequencer.getEvents();
	}
	
	// 4 quarters, all with letRing, following measure is empty
	@Test
	public void testMeasure1() throws MidiPlayerException {
		this.notes = this.getNotesTiming(1);
		assertEquals(4, notes.size());
		assertNoteTiming(0, 1, 4);	// first note: position 1, duration 4
		assertNoteTiming(1, 2, 3);	// second note: position 2, duration 3
		assertNoteTiming(2, 3, 2);	// ...
		assertNoteTiming(3, 4, 1);
	}
	
	// 4 quarters, 1st and 3rd with letRing, following measure is empty
	@Test
	public void testMeasure3() throws MidiPlayerException {
		this.notes = this.getNotesTiming(3);
		assertEquals(4, notes.size());
		assertNoteTiming(0, 1, 2);	// first note stops together with second
		assertNoteTiming(1, 2, 1);
		assertNoteTiming(2, 3, 2);	// 3rd and 4th notes stop together
		assertNoteTiming(3, 4, 1);
	}
	
	// 2 measures with 4 quarters. Seven first notes with letRing
	// 3 first notes of second measure are tied
	// Following measure starts without tied
	// all notes stop together at end of second measure
	@Test
	public void testMeasures5_6() {
		this.notes = this.getNotesTiming(5);
		assertEquals(4, notes.size());
		assertNoteTiming(0, 1, 8);
		assertNoteTiming(1, 2, 7);
		assertNoteTiming(2, 3, 6);
		assertNoteTiming(3, 4, 5);
		this.notes = this.getNotesTiming(6);
		assertEquals(0, notes.size());	// no note starts in measure
	}
	
	// 3 quarters (2 first notes with letRing), then rest
	// all notes stop together
	@Test
	public void testMeasure8() {
		this.notes = this.getNotesTiming(8);
		assertEquals(3, notes.size());
		assertNoteTiming(0, 1, 3);
		assertNoteTiming(1, 2, 2);
		assertNoteTiming(2, 3, 1);
	}
	
	// 3 quarters, all with letRing, then rest
	// all notes stop together
	@Test
	public void testMeasure9() {
		this.notes = this.getNotesTiming(9);
		assertEquals(3, notes.size());
		assertNoteTiming(0, 1, 3);
		assertNoteTiming(1, 2, 2);
		assertNoteTiming(2, 3, 1);
	}
	
	// measure 11 test: 
	// 2 simultaneous quarters: one with letRing and the other one without
	// then 1 quarter with letRing, then 1 without, then rest
	// expected behavior in this case is not so clear => NOT TESTED

	
	// 4 quarters, 3 first with letRing
	// 3rd quarter on same string than first
	@Test
	public void testMeasure12() {
		this.notes = this.getNotesTiming(12);
		assertEquals(4, notes.size());
		assertNoteTiming(0, 1, 2);	// 1st note stops when 3rd starts (same string)
		assertNoteTiming(1, 2, 3);
		assertNoteTiming(2, 3, 2);
		assertNoteTiming(3, 4, 1);
	}
	
	// 4 quarters with letRing, first note of following measure not tied
	// all notes stop together with 4th
	@Test
	public void testMeasure14() {
		this.notes = this.getNotesTiming(14);
		assertEquals(4, notes.size());
		assertNoteTiming(0, 1, 4);
		assertNoteTiming(1, 2, 3);
		assertNoteTiming(2, 3, 2);
		assertNoteTiming(3, 4, 1);
	}
	
	// measure with 4 quarters, all with letRing, and then a repeat
	// take care here, nMeasure is increased by one after repeat
	// nMeasure=17 is 2nd pass on measure #16
	@Test
	public void testMeasure16() {
		this.notes = this.getNotesTiming(16);
		assertEquals(4, notes.size());
		assertNoteTiming(0, 1, 4);
		assertNoteTiming(1, 2, 3);
		assertNoteTiming(2, 3, 2);
		assertNoteTiming(3, 4, 1);
		this.notes = this.getNotesTiming(17);
		assertEquals(4, notes.size());
		assertNoteTiming(0, 1, 4);
		assertNoteTiming(1, 2, 3);
		assertNoteTiming(2, 3, 2);
		assertNoteTiming(3, 4, 1);
	}
	
	private void assertNoteTiming(int index, long start, long duration) {
		assertEquals(start, this.notes.get(index).getStart());
		assertEquals(duration, this.notes.get(index).getDuration());
	}
	
	
	// override MidiSequencerImpl to retrieve list of MIDI events
	private class MidiSequencerImplForTest extends MidiSequencerImpl {
		private List<MidiEvent> events = new ArrayList<MidiEvent>();
		public MidiSequencerImplForTest(TGContext context) {
			super(context);
		}
		@Override
		public void addEvent(MidiEvent event) {
			events.add(event);
		}
		public List<MidiEvent> getEvents() {
			return this.events;
		}
	}
	
	// used by test: list of notes per measure, with [start, duration] attributes, measured in quarters
	// start: 1 corresponds to 1st beat of measure
	private class NoteTiming {
		private long start;
		private long duration;
		public NoteTiming(int nMeasure, long startTick, long durationTicks) {
			this.start = 1 + (startTick - getMeasureTick(nMeasure)) / TGDuration.QUARTER_TIME;
			this.duration = durationTicks / TGDuration.QUARTER_TIME;
		}
		public long getStart() { return start; }
		public long getDuration() { return duration; }
	}

	// warning, this method is well suited ONLY for the test .tg file
	// assumptions: all notes are quarters, 4/4 time signature, ...
	// measure numbers: from 1 to n
	private List<NoteTiming> getNotesTiming(int nMeasure) {
		List<NoteTiming> notes = new ArrayList<NoteTiming>();
		int nEventNoteOn=0;
		// look for 1st note of measure
		MidiEvent eventNoteOn = this.events.getFirst();
		while ((nEventNoteOn < this.events.size()) && 
				((eventNoteOn.getTick()<(getMeasureTick(nMeasure)) || (eventNoteOn.getType()!=MidiEvent.MIDI_EVENT_NOTEON))) ) {
			nEventNoteOn ++;
			eventNoteOn = this.events.get(nEventNoteOn);
		}
		if (nEventNoteOn >= this.events.size()) {
			return notes;
		}
		while ((eventNoteOn.getTick() < (getMeasureTick(nMeasure+1)))
				&& ((nEventNoteOn < this.events.size()))) {
			// look for corresponding noteOff (may not be in same measure)
			int pitch = eventNoteOn.getData()[1];
			int nEventNoteOff = nEventNoteOn+1;
			MidiEvent eventNoteOff = this.events.get(nEventNoteOff);
			while ((eventNoteOff.getType() != MidiEvent.MIDI_EVENT_NOTEOFF) || (eventNoteOff.getData()[1] != pitch)) {
				// assuming note off will always be found (or exception is raised and test fails)
				nEventNoteOff ++;
				eventNoteOff = this.events.get(nEventNoteOff);
			}
			notes.add(new NoteTiming(nMeasure, eventNoteOn.getTick(), eventNoteOff.getTick() - eventNoteOn.getTick()));
			// next noteOn (if any)
			nEventNoteOn ++;
			eventNoteOn = this.events.get(nEventNoteOn);
			while ((nEventNoteOn < this.events.size()-1) && 
					(eventNoteOn.getType()!=MidiEvent.MIDI_EVENT_NOTEON) ) {
				nEventNoteOn ++;
				eventNoteOn = this.events.get(nEventNoteOn);
			}
		}
		return notes;
	}
	
	private long getMeasureTick(int nMeasure) {
		return TGDuration.QUARTER_TIME * (4 * (nMeasure-1) + 1);
	}

}
