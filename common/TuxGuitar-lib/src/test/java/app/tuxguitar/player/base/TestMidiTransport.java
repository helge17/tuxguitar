package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * The transport a sample clock's owner writes before each block: spans of the block, each from a frame of it.
 */
public class TestMidiTransport {

	private static String read(MidiTransport transport) {
		StringBuilder read = new StringBuilder();
		for (int i = 0; i < transport.count(); i++) {
			MidiTransport.Span span = transport.get(i);
			read.append("@" + span.getFrame() + " " + span.isPlaying() + " " + span.getTempo() + " " + span.getNumerator() + "/" + span.getDenominator() + " q"
				+ span.getQuarters() + " s" + span.getSamples() + "; ");
		}
		return read.toString();
	}

	@Test
	public void aNewTransportHoldsOneStoppedSpan() {
		assertEquals("@0 false 120.0 4/4 q0.0 s0; ", read(new MidiTransport()));
	}

	@Test
	public void spansAreAddedAfterAClearAndStoppingLeavesOneStoppedSpan() {
		MidiTransport transport = new MidiTransport();
		transport.clear();
		transport.add(0, true, 96, 3, 4, 2.5, 1000);
		transport.add(200, true, 60, 6, 8, 3, 2000);
		String added = read(transport);
		transport.stop();
		assertEquals("@0 true 96.0 3/4 q2.5 s1000; @200 true 60.0 6/8 q3.0 s2000; | @0 false 120.0 4/4 q0.0 s0; ", added + "| " + read(transport));
	}

	// a transport is live until its owner says it waits for each block, and stays so whatever it is told
	@Test
	public void aTransportIsLiveUntilItsOwnerSaysItIsOffline() {
		MidiTransport transport = new MidiTransport();
		boolean live = transport.isOffline();
		transport.setOffline(true);
		transport.clear();
		transport.stop();
		boolean offline = transport.isOffline();
		transport.setOffline(false);
		assertEquals("false true false", live + " " + offline + " " + transport.isOffline());
	}

	// a span from the frame the last one starts on replaces it: no span is empty
	@Test
	public void aSpanFromTheLastOnesFrameReplacesIt() {
		MidiTransport transport = new MidiTransport();
		transport.add(0, true, 96, 3, 4, 2.5, 1000);
		transport.add(300, true, 96, 3, 4, 3, 1300);
		transport.add(300, true, 60, 6, 8, 3, 1300);
		assertEquals("@0 true 96.0 3/4 q2.5 s1000; @300 true 60.0 6/8 q3.0 s1300; ", read(transport));
	}

	// a span past the most a block holds is left out: the last one runs on
	@Test
	public void aSpanPastTheMostIsLeftOut() {
		MidiTransport transport = new MidiTransport();
		transport.clear();
		for (int i = 0; i <= MidiTransport.MAX_SPANS; i++) {
			transport.add(i * 10, true, 120, 4, 4, i, i);
		}
		assertEquals(MidiTransport.MAX_SPANS, transport.count());
		assertEquals(70, transport.get(MidiTransport.MAX_SPANS - 1).getFrame());
	}
}
