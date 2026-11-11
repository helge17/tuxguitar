package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.util.TGContext;

/**
 * Pins how the player picks its output port and sequencer from a list (MidiPlayer.openOutputPort /
 * openSequencer): the device with the chosen key, unless it is already open; else, when asked to, the first device
 * of the list, but only while none is open.
 *
 * Devices log "open key" and "close key"; a device named with a '!' fails to open. A key is chosen the way the
 * application does, through openOutputPort(key, tryFirst) / openSequencer(key, tryFirst), among the devices listed
 * by a registered provider.
 */
public class TestMidiPlayerDevices {

	private final MidiPlayer player = MidiPlayer.getInstance(new TGContext());
	private final List<String> log = new ArrayList<String>();

	private abstract class FakeDevice implements MidiDevice {
		private final String key;

		FakeDevice(String key) {
			this.key = key.replace("!", "");
		}

		public String getKey() { return this.key; }
		public String getName() { return this.key; }
		public void check() { }

		public void open() throws MidiPlayerException {
			if (this.toString().contains("!")) {
				throw new MidiPlayerException("broken " + this.key);
			}
			log.add("open " + this.key);
		}

		public void close() {
			log.add("close " + this.key);
		}
	}

	private class FakePort extends FakeDevice implements MidiOutputPort {
		private final String spec;

		FakePort(String spec) {
			super(spec);
			this.spec = spec;
		}

		public MidiSynthesizer getSynthesizer() { return null; }
		public String toString() { return this.spec; }
	}

	private class FakeSequencer extends FakeDevice implements MidiSequencer {
		private final String spec;

		FakeSequencer(String spec) {
			super(spec);
			this.spec = spec;
		}

		public String toString() { return this.spec; }
		public void start() { }
		public void stop() { }
		public boolean isRunning() { return false; }
		public void setTickPosition(long tickPosition) { }
		public long getTickPosition() { return 0; }
		public long getTickLength() { return 0; }
		public void setTransmitter(MidiTransmitter transmitter) { }
		public MidiSequenceHandler createSequence(int tracks) { return null; }
		public void setSolo(int index, boolean solo) { }
		public void setMute(int index, boolean mute) { }
	}

	private List<MidiOutputPort> ports(String... specs) {
		List<MidiOutputPort> ports = new ArrayList<MidiOutputPort>();
		for (String spec : specs) {
			ports.add(new FakePort(spec));
		}
		return ports;
	}

	private List<MidiSequencer> sequencers(String... specs) {
		List<MidiSequencer> sequencers = new ArrayList<MidiSequencer>();
		for (String spec : specs) {
			sequencers.add(new FakeSequencer(spec));
		}
		return sequencers;
	}

	private List<MidiOutputPort> availablePorts = new ArrayList<MidiOutputPort>();
	private List<MidiSequencer> availableSequencers = new ArrayList<MidiSequencer>();

	public TestMidiPlayerDevices() throws MidiPlayerException {
		player.addOutputPortProvider(new MidiOutputPortProvider() {
			public List<MidiOutputPort> listPorts() { return availablePorts; }
			public void closeAll() { }
		}, false);
		player.addSequencerProvider(new MidiSequencerProvider() {
			public List<MidiSequencer> listSequencers() { return availableSequencers; }
			public void closeAll() { }
		}, false);
	}

	// the log of choosing a key among the available ports, then the key of the open port
	private String openPort(String key, List<MidiOutputPort> ports, boolean tryFirst) {
		availablePorts = ports;
		log.clear();
		player.openOutputPort(key, tryFirst);
		MidiOutputPort open = player.getOutputPort();
		return String.join(", ", log) + " -> " + (open == null ? "none" : open.getKey());
	}

	private String openSequencer(String key, List<MidiSequencer> sequencers, boolean tryFirst) {
		availableSequencers = sequencers;
		log.clear();
		player.openSequencer(key, tryFirst);
		MidiSequencer open = player.getSequencer();
		return String.join(", ", log) + " -> " + (open instanceof MidiSequencerEmpty ? "none" : open.getKey());
	}

	@Test
	public void opensThePortWithTheChosenKey() {
		assertEquals("open b -> b", openPort("b", ports("a", "b", "c"), false));
	}

	@Test
	public void keepsThePortWithTheChosenKeyWhenAlreadyOpen() {
		List<MidiOutputPort> ports = ports("a", "b");
		openPort("b", ports, false);

		assertEquals(" -> b", openPort("b", ports, true));
	}

	@Test
	public void closesTheOpenPortForAnotherKey() {
		List<MidiOutputPort> ports = ports("a", "b");
		openPort("b", ports, false);

		assertEquals("close b, open a -> a", openPort("a", ports, false));
	}

	@Test
	public void triesTheNextPortWithTheChosenKeyWhenOneFails() {
		assertEquals("open b -> b", openPort("b", ports("a", "b!", "b"), false));
	}

	@Test
	public void opensOnlyTheFirstWorkingPortWithTheChosenKey() {
		assertEquals("open b -> b", openPort("b", ports("b", "b"), false));
	}

	@Test
	public void opensNoPortWhenTheChosenOneFails() {
		assertEquals(" -> none", openPort("b", ports("a", "b!"), false));
		assertEquals(" -> none", openPort("z", ports("a", "b"), false));
	}

	@Test
	public void fallsBackToTheFirstPortWhenAsked() {
		assertEquals("open a -> a", openPort("b", ports("a", "b!"), true));
		assertEquals("close a, open c -> c", openPort("z", ports("c", "d"), true));
	}

	@Test
	public void withoutAChosenKeyOpensTheFirstPortOnlyWhenNoneIsOpen() {
		assertEquals(" -> none", openPort(null, ports("a", "b"), false));
		assertEquals("open a -> a", openPort(null, ports("a", "b"), true));
		assertEquals(" -> a", openPort(null, ports("c"), true));
		assertEquals(" -> a", openPort(null, ports(), true));
	}

	@Test
	public void anEmptyListOpensNothing() {
		assertEquals(" -> none", openPort("a", ports(), true));
	}

	@Test
	public void opensTheSequencerWithTheChosenKey() {
		assertEquals("open b -> b", openSequencer("b", sequencers("a", "b", "c"), false));
	}

	@Test
	public void keepsTheSequencerWithTheChosenKeyWhenAlreadyOpen() {
		List<MidiSequencer> sequencers = sequencers("a", "b");
		openSequencer("b", sequencers, false);

		assertEquals(" -> b", openSequencer("b", sequencers, true));
	}

	@Test
	public void closesTheOpenSequencerForAnotherKey() {
		List<MidiSequencer> sequencers = sequencers("a", "b");
		openSequencer("b", sequencers, false);

		assertEquals("close b, open a -> a", openSequencer("a", sequencers, false));
	}

	@Test
	public void triesTheNextSequencerWithTheChosenKeyWhenOneFails() {
		assertEquals("open b -> b", openSequencer("b", sequencers("a", "b!", "b"), false));
	}

	@Test
	public void opensOnlyTheFirstWorkingSequencerWithTheChosenKey() {
		assertEquals("open b -> b", openSequencer("b", sequencers("b", "b"), false));
	}

	@Test
	public void opensNoSequencerWhenTheChosenOneFails() {
		assertEquals(" -> none", openSequencer("b", sequencers("a", "b!"), false));
		assertEquals(" -> none", openSequencer("z", sequencers("a", "b"), false));
	}

	@Test
	public void fallsBackToTheFirstSequencerWhenAsked() {
		assertEquals("open a -> a", openSequencer("b", sequencers("a", "b!"), true));
		assertEquals("close a, open c -> c", openSequencer("z", sequencers("c", "d"), true));
	}

	@Test
	public void withoutAChosenKeyOpensTheFirstSequencerOnlyWhenNoneIsOpen() {
		assertEquals(" -> none", openSequencer(null, sequencers("a", "b"), false));
		assertEquals("open a -> a", openSequencer(null, sequencers("a", "b"), true));
		assertEquals(" -> a", openSequencer(null, sequencers("c"), true));
		assertEquals(" -> a", openSequencer(null, sequencers(), true));
	}

	@Test
	public void aFailureWhileChoosingTheSequencerIsReported() {
		MidiSequencer keyless = new FakeSequencer("a") {
			public String getKey() { return null; }
		};

		openSequencer("b", sequencers(), false);

		assertThrows(MidiPlayerException.class, () -> player.openSequencer(Arrays.asList(keyless), false));
	}

	@Test
	public void aFailureWhileChoosingThePortIsSwallowed() {
		MidiOutputPort keyless = new FakePort("a") {
			public String getKey() { return null; }
		};

		assertEquals(" -> none", openPort("b", Arrays.asList(keyless), true));
	}
}
