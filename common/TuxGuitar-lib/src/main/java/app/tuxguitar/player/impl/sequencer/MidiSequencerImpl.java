package app.tuxguitar.player.impl.sequencer;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiReceiver;
import app.tuxguitar.player.base.MidiSampleClock;
import app.tuxguitar.player.base.MidiSequenceHandler;
import app.tuxguitar.player.base.MidiSequencer;
import app.tuxguitar.player.base.MidiTransmitter;
import app.tuxguitar.thread.TGThreadHandler;
import app.tuxguitar.thread.TGThreadLoop;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.thread.TGThreadPriority;
import app.tuxguitar.util.TGContext;

public class MidiSequencerImpl implements MidiSequencer {

	private static final int DEFAULT_TEMPO_IN_USQ = 500000;

	private boolean reset;
	// the next start plays the next pass of a loop
	private boolean looping;
	private boolean running;
	private boolean stopped;
	private Object lock;
	private TGContext context;
	private MidiTransmitter transmitter;
	private MidiTickPlayer midiTickPlayer;
	private MidiEventPlayer midiEventPlayer;
	private MidiEventDispacher midiEventDispacher;
	private MidiTrackController midiTrackController;
	private MidiBlockPlayer midiBlockPlayer;
	// the clock to play on from the next start, and the one played on now
	private volatile MidiSampleClock clock;
	private MidiSampleClock playingClock;
	private final MidiSampleClock.Listener clockListener = (clock, frames) -> this.process(() -> this.midiBlockPlayer.process(clock, frames));
	private final Step timerStep = () -> {
		this.midiTickPlayer.process();
		this.midiEventPlayer.process();
	};

	public MidiSequencerImpl(TGContext context) {
		this.running = false;
		this.stopped = true;
		this.context = context;
		this.lock = new Object();
		this.midiTickPlayer = new MidiTickPlayer();
		this.midiEventPlayer = new MidiEventPlayer(this);
		this.midiEventDispacher = new MidiEventDispacher(this);
		this.midiTrackController = new MidiTrackController(this);
		this.midiBlockPlayer = new MidiBlockPlayer(this);
	}

	public MidiSequenceHandler createSequence(int tracks) throws MidiPlayerException {
		this.clearEvents();
		this.midiTickPlayer.clearTick();
		return new MidiSequenceHandlerImpl(this, tracks);
	}

	public void check() {
		// Not implemented
	}

	public void open() {
		// not implemented
	}

	public void close() throws MidiPlayerException {
		if (this.isRunning()) {
			this.stop();
		}
	}

	public void stop() throws MidiPlayerException {
		this.setRunning(false);
	}

	public void start() throws MidiPlayerException {
		this.setRunning(true);
	}

	public void reset() throws MidiPlayerException {
		synchronized (this.lock) {
			this.getTransmitter().sendAllNotesOff();
			this.getTransmitter().sendPitchBendReset();
		}
	}

	public void sendEvent(MidiEvent event) throws MidiPlayerException {
		synchronized (this.lock) {
			if (!this.reset) {
				this.midiEventDispacher.dispatch(event);
			}
		}
	}

	// an event of a block played on the clock, to the receiver of its frame
	void sendEvent(MidiEvent event, MidiReceiver receiver) throws MidiPlayerException {
		this.midiEventDispacher.dispatch(event, receiver);
	}

	public void addEvent(MidiEvent event) {
		synchronized (this.lock) {
			this.midiEventPlayer.addEvent(event);
			this.midiBlockPlayer.addEvent(event);
			this.midiTickPlayer.notifyTick(event.getTick());
		}
	}

	// only playing on a clock tells plugins the time signature; it is no MIDI message, and does not lengthen the sequence
	void addTimeSignature(MidiEvent event) {
		synchronized (this.lock) {
			this.midiBlockPlayer.addEvent(event);
		}
	}

	// one step of the timer
	protected boolean process() throws MidiPlayerException {
		return this.process(this.timerStep);
	}

	private boolean process(Step step) throws MidiPlayerException {
		synchronized (this.lock) {
			boolean running = this.isRunning();
			if (running) {
				if (this.reset) {
					this.reset();
					this.reset = false;
					this.midiEventPlayer.reset();
					this.midiBlockPlayer.reset(this.looping);
					this.looping = false;
				}
				this.stopped = false;
				step.run();
				if (this.getScheduledTickPosition() > this.getTickLength() && this.getTickPosition() > this.getTickLength()) {
					this.stop();
				}
			} else if (!this.stopped) {
				this.stopped = true;
				this.clearEvents();
				this.reset();
			}
			return running;
		}
	}

	public void setRunning(boolean running) throws MidiPlayerException {
		synchronized (this.lock) {
			this.running = running;
			if (this.running) {
				this.setTempoInUSQ(DEFAULT_TEMPO_IN_USQ);
				this.setTickPosition(this.getTickPosition());
				this.startClock();
			} else {
				this.process();
				this.leaveClock();
			}
		}
	}

	// on the clock when given one, else on the timer
	private void startClock() {
		this.playingClock = this.clock;
		if (this.playingClock != null) {
			this.playingClock.setListener(this.clockListener);
		} else {
			new MidiTimer(this).start();
		}
	}

	private void leaveClock() {
		if (this.playingClock != null) {
			this.playingClock.setListener(null);
			this.playingClock = null;
		}
	}

	public void setClock(MidiSampleClock clock) {
		this.clock = clock;
	}

	private void clearEvents() {
		this.midiEventPlayer.clearEvents();
		this.midiBlockPlayer.clearEvents();
		this.midiTickPlayer.clearTick();
	}

	public void setTempo(int tempo) {
		synchronized (this.lock) {
			this.midiTickPlayer.setTempo(tempo);
		}
	}

	// the timer plays a whole bpm, as it always has; the clock plays the exact tempo
	public void setTempoInUSQ(int usq) {
		synchronized (this.lock) {
			this.setTempo((int) ((60.00 * 1000.00) / (usq / 1000.00)));
			this.midiBlockPlayer.setTempoInUSQ(usq);
		}
	}

	public void setTickPosition(long tickPosition) {
		synchronized (this.lock) {
			this.reset = true;
			this.midiTickPlayer.setTick(tickPosition);
		}
	}

	public void setSolo(int index, boolean solo) throws MidiPlayerException {
		synchronized (this.lock) {
			this.getMidiTrackController().setSolo(index, solo);
		}
	}

	public void setMute(int index, boolean mute) throws MidiPlayerException {
		synchronized (this.lock) {
			this.getMidiTrackController().setMute(index, mute);
		}
	}

	public void setTransmitter(MidiTransmitter transmitter) {
		synchronized (this.lock) {
			this.transmitter = transmitter;
		}
	}

	public boolean isRunning() {
		return this.running;
	}

	MidiTickPlayer getMidiTickPlayer() {
		return this.midiTickPlayer;
	}

	public MidiTrackController getMidiTrackController() {
		return this.midiTrackController;
	}

	public long getTickPosition() {
		return this.midiTickPlayer.getTick();
	}

	// on the clock, the tick at the end of the last block played; on the timer, the position
	public long getScheduledTickPosition() {
		synchronized (this.lock) {
			return (this.playingClock != null ? this.midiBlockPlayer.getScheduledTick() : this.getTickPosition());
		}
	}

	public void loopOnNextStart() {
		synchronized (this.lock) {
			this.looping = true;
		}
	}

	public long getTickLength() {
		return this.midiTickPlayer.getTickLength();
	}

	public MidiTransmitter getTransmitter() {
		return this.transmitter;
	}

	public String getKey() {
		return "tuxguitar.sequencer";
	}

	public String getName() {
		return "TuxGuitar Sequencer";
	}

	public TGContext getContext() {
		return this.context;
	}

	private interface Step {
		void run() throws MidiPlayerException;
	}

	private class MidiTimer implements Runnable {

		private static final long TIMER_DELAY = 10;

		private MidiSequencerImpl sequencer;
		private TGThreadHandler threadHandler;

		public MidiTimer(MidiSequencerImpl sequencer) {
			this.sequencer = sequencer;
			this.threadHandler = TGThreadManager.getInstance(this.sequencer.getContext());
		}

		public void run() {
			this.threadHandler.loop(new TGThreadLoop() {
				public Long process() {
					return (processLoop() ? TIMER_DELAY : BREAK);
				}
			});
		}

		public boolean processLoop() {
			try {
				return this.sequencer.process();
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
			return false;
		}

		public void start() {
			this.threadHandler.start(TGThreadPriority.HIGH, this);
		}
	}
}
