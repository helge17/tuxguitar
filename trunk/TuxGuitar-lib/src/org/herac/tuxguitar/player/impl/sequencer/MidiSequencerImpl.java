package org.herac.tuxguitar.player.impl.sequencer;

import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSequenceHandler;
import org.herac.tuxguitar.player.base.MidiSequencer;
import org.herac.tuxguitar.player.base.MidiTransmitter;
import org.herac.tuxguitar.thread.TGThreadLoop;
import org.herac.tuxguitar.thread.TGThreadManager;
import org.herac.tuxguitar.util.TGContext;

public class MidiSequencerImpl implements MidiSequencer {

	private boolean reset;
	private boolean running;
	private boolean stopped;
	private Object lock;
	private TGContext context;
	private MidiTransmitter transmitter;
	private MidiTickPlayer midiTickPlayer;
	private MidiEventPlayer midiEventPlayer;
	private MidiEventDispacher midiEventDispacher;
	private MidiTrackController midiTrackController;

	public MidiSequencerImpl(TGContext context) {
		this.running = false;
		this.stopped = true;
		this.context = context;
		this.lock = new Object();
		this.midiTickPlayer = new MidiTickPlayer();
		this.midiEventPlayer = new MidiEventPlayer(this);
		this.midiEventDispacher = new MidiEventDispacher(this);
		this.midiTrackController = new MidiTrackController(this);
	}

	public MidiSequenceHandler createSequence(int tracks) throws MidiPlayerException {
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

	public void addEvent(MidiEvent event) {
		synchronized (this.lock) {
			this.midiEventPlayer.addEvent(event);
			this.midiTickPlayer.notifyTick(event.getTick());
		}
	}

	protected boolean process() throws MidiPlayerException {
		synchronized (this.lock) {
			boolean running = this.isRunning();
			if (running) {
				if (this.reset) {
					this.reset();
					this.reset = false;
					this.midiEventPlayer.reset();
				}
				this.stopped = false;
				this.midiTickPlayer.process();
				this.midiEventPlayer.process();
				if (this.getTickPosition() > this.getTickLength()) {
					this.stop();
				}
			} else if (!this.stopped) {
				this.stopped = true;
				this.midiEventPlayer.clearEvents();
				this.midiTickPlayer.clearTick();
				this.reset();
			}
			return running;
		}
	}

	public void setRunning(boolean running) throws MidiPlayerException {
		synchronized (this.lock) {
			this.running = running;
			if (this.running) {
				this.setTempo(120);
				this.setTickPosition(this.getTickPosition());
				new MidiTimer(this).start();
			} else {
				this.process();
			}
		}
	}

	public void setTempo(int tempo) {
		synchronized (this.lock) {
			this.midiTickPlayer.setTempo(tempo);
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

	public MidiTrackController getMidiTrackController() {
		return this.midiTrackController;
	}

	public long getTickPosition() {
		return this.midiTickPlayer.getTick();
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

	private class MidiTimer implements Runnable {

		private static final long TIMER_DELAY = 15;

		private MidiSequencerImpl sequencer;

		public MidiTimer(MidiSequencerImpl sequencer) {
			this.sequencer = sequencer;
		}

		public void run() {
			TGThreadManager.getInstance(this.sequencer.getContext()).loop(new TGThreadLoop() {
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
			TGThreadManager.getInstance(this.sequencer.getContext()).start(this);
		}
	}
}
