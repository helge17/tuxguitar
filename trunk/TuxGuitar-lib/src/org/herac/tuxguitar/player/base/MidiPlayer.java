package org.herac.tuxguitar.player.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGLock;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class MidiPlayer{
	
	public static final int MAX_VOLUME = 10;
	
	private static final int TIMER_DELAY = 10;
	
	private TGContext context;
	
	private TGDocumentManager documentManager;
	
	private MidiSequencer sequencer;
	
	private MidiSynthesizerProxy synthesizerProxy;
	
	private MidiChannelRouter channelRouter;
	
	private MidiTransmitter outputTransmitter;
	
	private MidiOutputPort outputPort;
	
	private MidiPlayerMode mode;
	
	private MidiPlayerCountDown countDown;
	
	private String sequencerKey;
	
	private String outputPortKey;
	
	private List<MidiOutputPortProvider> outputPortProviders;
	
	private List<MidiSequencerProvider> sequencerProviders;
	
	private int volume;
	
	private boolean running;
	
	private boolean paused;
	
	private boolean changeTickPosition;
	
	private boolean metronomeEnabled;
	
	private int metronomeTrack;
	
	private int infoTrack;
	
	private int loopSHeader;
	
	private int loopEHeader;
	
	private long loopSPosition;
	
	private boolean anySolo;
	
	protected long tickLength;
	
	protected long tickPosition;
	
	private boolean tryOpenFistDevice;
	
	protected TGLock lock = new TGLock();
	
	private MidiPlayer(TGContext context) {
		this.context = context;
		this.lock = new TGLock();
		this.volume = MAX_VOLUME;
	}
	
	public void lock() {
		this.lock.lock();
	}
	
	public void unlock() {
		this.lock.unlock();
	}
	
	public void init(TGDocumentManager documentManager) {
		try {
			this.lock();
			
			this.documentManager = documentManager;
			this.outputPortProviders = new ArrayList<MidiOutputPortProvider>();
			this.sequencerProviders = new ArrayList<MidiSequencerProvider>();
			this.tryOpenFistDevice = false;
			this.getSequencer();
			this.getMode();
			this.reset();
		} finally {
			this.unlock();
		}
	}
	
	public void reset(){
		try {
			this.lock();
			
			this.stop();
			this.tickPosition = TGDuration.QUARTER_TIME;
			this.setChangeTickPosition(false);
		} finally {
			this.unlock();
		}
	}
	
	public void close(){
		try {
			this.lock();
			
			this.closeSequencer();
			this.closeOutputPort();
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void stopSequencer() {
		try {
			this.lock();
			
			if( this.getSequencer().isRunning() ){
				this.getSequencer().stop();
			}
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void stop(boolean paused) {
		try {
			this.lock();
			
			this.setPaused(paused);
			this.stopSequencer();
			this.setRunning(false);
		} finally {
			this.unlock();
		}
	}
	
	public void stop() {
		try {
			this.lock();
			
			this.stop(false);
		} finally {
			this.unlock();
		}
	}
	
	public void pause(){
		try {
			this.lock();
			
			this.stop(true);
		} finally {
			this.unlock();
		}
	}
	
	public synchronized void play() throws MidiPlayerException{
		try {
			this.lock();
			
			final boolean notifyStarted = (!this.isRunning());
			this.setRunning(true);
			this.stopSequencer();
			this.checkDevices();
			this.updateLoop(true);
			this.addSequence();
			this.updateTracks();
			this.updateChannels();
			this.updatePrograms();
			this.updateControllers();
			this.updateDefaultControllers();
			this.setMetronomeEnabled(isMetronomeEnabled());
			this.getCountDown().setTempoPercent(getMode().getCurrentPercent());
			this.changeTickPosition();
			
			new Thread(new Runnable() {
				public void run() {
					runPlayProcess(notifyStarted);
				}
			}).start();
		}catch (Throwable throwable) {
			this.reset();
			throw new MidiPlayerException(throwable.getMessage(),throwable);
		}finally{
			this.unlock();
		}
	}
	
	public void runPlayProcess(boolean notifyStarted) {
		try {
			// Start
			if( notifyStarted ){
				this.notifyStarted();
			}
			
			if( this.getCountDown().isEnabled() ){
				this.notifyCountDownStarted();
				this.getCountDown().start();
				this.notifyCountDownStopped();
			}
			
			try {
				this.lock();
				if( this.isRunning() ){
					if( this.isChangeTickPosition() ){
						this.changeTickPosition();
					}
					this.getSequencer().start();
				}
				
				this.tickLength = getSequencer().getTickLength();
				this.tickPosition = getSequencer().getTickPosition();
			} finally {
				this.unlock();
			}
			
			// Play loop
			Object sequencerLock = new Object();
			while( this.getSequencer().isRunning() && this.isRunning()) {
				try {
					this.lock();
					
					if( this.isChangeTickPosition()) {
						this.changeTickPosition();
					}
					this.tickPosition = getSequencer().getTickPosition();
				} finally {
					this.unlock();
				}
				synchronized(sequencerLock) {
					sequencerLock.wait( TIMER_DELAY );
				}
			}
			
			// Finish
			try {
				this.lock();
				if( this.isRunning()){
					if( this.tickPosition >= (this.tickLength - (TGDuration.QUARTER_TIME / 2) )){
						this.finish();
					} else {
						this.stop(isPaused());
					}
				}
				
				if(!this.isRunning() ){
					this.notifyStopped();
				}
			} finally {
				this.unlock();
			}
		}catch (Throwable throwable) {
			this.reset();
			
			throwable.printStackTrace();
		}
	}
	
	private void finish() throws MidiPlayerException{
		try {
			this.lock();
			
			if( this.getMode().isLoop()){
				this.stopSequencer();
				this.setTickPosition(TGDuration.QUARTER_TIME);
				this.getMode().notifyLoop();
				this.notifyLoop();
				this.play();
				
				return;
			}
			this.reset();
		} finally {
			this.unlock();
		}
	}
	
	public void updateLoop( boolean force ){
		try {
			this.lock();
			
			if( force || !this.isRunning() ){
				this.loopSHeader = -1;
				this.loopEHeader = -1;
				this.loopSPosition = TGDuration.QUARTER_TIME;
				if( getMode().isLoop() ){
					int hCount = this.getSong().countMeasureHeaders();
					this.loopSHeader = ( getMode().getLoopSHeader() <= hCount ? getMode().getLoopSHeader() : -1 ) ;
					this.loopEHeader = ( getMode().getLoopEHeader() <= hCount ? getMode().getLoopEHeader() : -1 ) ;
					if( this.loopSHeader > 0 && this.loopSHeader <= hCount ){
						TGMeasureHeader header = this.getSongManager().getMeasureHeader( getSong(), this.loopSHeader );
						if( header != null ){
							this.loopSPosition = header.getStart();
						}
					}
				}
			}
		} finally {
			this.unlock();
		}
	}

	public void checkDevices() throws Throwable {
		try {
			this.lock();
			
			this.getSequencer().check();
			if( this.getOutputPort() != null ){
				this.getOutputPort().check();
			}
		} finally {
			this.unlock();
		}
	}
	
	public int getVolume() {
		try {
			this.lock();
			
			return this.volume;
		} finally {
			this.unlock();
		}
	}
	
	public void setVolume(int volume) {
		try {
			this.lock();
			
			this.volume = volume;
			if (this.isRunning()) {
				this.updateControllers();
			}
		} finally {
			this.unlock();
		}
	}
	
	public void setRunning(boolean running) {
		try {
			this.lock();
			
			this.running = running;
		} finally {
			this.unlock();
		}
	}
	
	public boolean isRunning() {
		try {
			this.lock();
			
			return (this.running || this.getSequencer().isRunning());
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
		return false;
	}
	
	public boolean isPaused() {
		try {
			this.lock();
			
			return this.paused;
		} finally {
			this.unlock();
		}
	}
	
	public void setPaused(boolean paused) {
		try {
			this.lock();
			
			this.paused = paused;
		} finally {
			this.unlock();
		}
	}
	
	protected boolean isChangeTickPosition() {
		try {
			this.lock();
			
			return this.changeTickPosition;
		} finally {
			this.unlock();
		}
	}
	
	private void setChangeTickPosition(boolean changeTickPosition) {
		try {
			this.lock();
			
			this.changeTickPosition = changeTickPosition;
		} finally {
			this.unlock();
		}
	}
	
	public void setTickPosition(long position) {
		try {
			this.lock();
			
			this.tickPosition = position;
			this.setChangeTickPosition(true);
		} finally {
			this.unlock();
		}
	}
	
	public long getTickPosition() {
		try {
			this.lock();
			
			return this.tickPosition;
		} finally {
			this.unlock();
		}
	}
	
	protected void changeTickPosition(){
		try {
			this.lock();
			
			if( this.isRunning()){
				if( this.tickPosition < this.getLoopSPosition() ){
					this.tickPosition = this.getLoopSPosition();
				}
				this.getSequencer().setTickPosition(this.tickPosition);
			}
			setChangeTickPosition(false);
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void addSequence() {
		try{
			this.lock();
			
			MidiSequenceParser midiSequenceParser = new MidiSequenceParser(this.documentManager.getSong(), this.documentManager.getSongManager(), MidiSequenceParser.DEFAULT_PLAY_FLAGS);
			midiSequenceParser.setTempoPercent(getMode().getCurrentPercent());
			midiSequenceParser.setSHeader( getLoopSHeader() );
			midiSequenceParser.setEHeader( getLoopEHeader() );
			midiSequenceParser.setMetronomeChannelId(getPercussionChannelId());
			midiSequenceParser.parse(getSequencer().createSequence(this.getSong().countTracks() + 2));
			this.infoTrack = midiSequenceParser.getInfoTrack();
			this.metronomeTrack = midiSequenceParser.getMetronomeTrack();
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void resetChannels() {
		try {
			this.lock();
			
			this.closeChannels();
			this.updateChannels();
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void closeChannels() throws MidiPlayerException{
		try {
			this.lock();
			
			// Remove all channels.
			Iterator<Integer> iterator = getChannelRouter().getMidiChannelIds().iterator();
			while( iterator.hasNext() ){
				int channelId = ((Integer) iterator.next()).intValue();
				
				this.getSynthesizerProxy().closeChannel(getChannelRouter().getMidiChannel(channelId));
				this.getChannelRouter().removeMidiChannel(channelId);
			}
		} finally {
			this.unlock();
		}
	}
	
	public void updateChannels() throws MidiPlayerException {
		try {
			this.lock();
			
			// Remove unused channels.
			List<Integer> oldChannelIds = getChannelRouter().getMidiChannelIds();
			Iterator<Integer> iterator = oldChannelIds.iterator();
			while( iterator.hasNext() ){
				int channelId = ((Integer) iterator.next()).intValue();
				
				boolean removableChannel = ( this.getSongManager().getChannel(getSong(), channelId) == null );
				if(!removableChannel ){
					MidiChannel midiChannel = getChannelRouter().getMidiChannel(channelId);
					if( midiChannel != null ){
						removableChannel = (!this.getSynthesizerProxy().isChannelOpen(midiChannel));
					}
				}
				
				if( removableChannel ){
					this.getSynthesizerProxy().closeChannel(getChannelRouter().getMidiChannel(channelId));
					this.getChannelRouter().removeMidiChannel(channelId);
				}
			}
			
			// Add channels
			List<Integer> newChannelIds = getChannelRouter().getMidiChannelIds();
			Iterator<TGChannel> tgChannels = this.getSong().getChannels();
			while( tgChannels.hasNext() ){
				TGChannel tgChannel = (TGChannel) tgChannels.next();
				if(!newChannelIds.contains(new Integer(tgChannel.getChannelId())) ){
					MidiChannel midiChannel = this.getSynthesizerProxy().openChannel(tgChannel.getChannelId());
					if( midiChannel != null ){
						this.getChannelRouter().addMidiChannel(tgChannel.getChannelId(), midiChannel);
					}
				}
			}
			
			this.updateParameters();
		} finally {
			this.unlock();
		}
	}
	
	public void updateParameters(){
		try {
			this.lock();
			
			Iterator<TGChannel> tgChannels = this.getSong().getChannels();
			while( tgChannels.hasNext() ){
				TGChannel tgChannel = (TGChannel) tgChannels.next();
				this.updateParameters(tgChannel);
			}
		} finally {
			this.unlock();
		}
	}
	
	public void updateParameters(TGChannel tgChannel){
		try {
			this.lock();
			
			Iterator<TGChannelParameter> parameters = tgChannel.getParameters();
			while( parameters.hasNext() ){
				TGChannelParameter tgChannelParameter = (TGChannelParameter) parameters.next();
				this.updateParameters(tgChannel, tgChannelParameter);
			}
		} finally {
			this.unlock();
		}
	}
	
	public void updateParameters(TGChannel tgChannel, TGChannelParameter tgChannelParameter){
		try {
			this.lock();
			
			getOutputTransmitter().sendParameter(tgChannel.getChannelId(), tgChannelParameter.getKey(), tgChannelParameter.getValue());
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	private void updateDefaultControllers(){
		try {
			this.lock();
			
			Iterator<TGChannel> tgChannels = this.getSong().getChannels();
			while( tgChannels.hasNext() ){
				TGChannel tgChannel = (TGChannel) tgChannels.next();
				getOutputTransmitter().sendControlChange(tgChannel.getChannelId(),MidiControllers.RPN_MSB,0);
				getOutputTransmitter().sendControlChange(tgChannel.getChannelId(),MidiControllers.RPN_LSB,0);
				getOutputTransmitter().sendControlChange(tgChannel.getChannelId(),MidiControllers.DATA_ENTRY_MSB,12);
				getOutputTransmitter().sendControlChange(tgChannel.getChannelId(),MidiControllers.DATA_ENTRY_LSB, 0);
			}
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void updatePrograms() {
		try {
			this.lock();
			
			Iterator<TGChannel> it = this.getSong().getChannels();
			while(it.hasNext()){
				updateProgram((TGChannel)it.next());
			}
		} finally {
			this.unlock();
		}
	}
	
	private void updateProgram(TGChannel channel) {
		try {
			this.lock();
			
			this.updateProgram(channel.getChannelId(), channel.getBank(), channel.getProgram());
		} finally {
			this.unlock();
		}
	}
	
	private void updateProgram(int channelId, int bank, int program) {
		try {
			this.lock();
			
			if( bank >= 0 && bank <= 128 ){
				getOutputTransmitter().sendControlChange(channelId, MidiControllers.BANK_SELECT, bank);
			}
			if( program >= 0 && program <= 127 ){
				getOutputTransmitter().sendProgramChange(channelId, program);
			}
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void updateControllers() {
		try{
			this.lock();
			
			Iterator<TGChannel> channelsIt = this.getSong().getChannels();
			while( channelsIt.hasNext() ){
				this.updateController( (TGChannel)channelsIt.next() );
			}
			this.afterUpdate();
		} finally {
			this.unlock();
		}
	}
	
	private void updateController(TGChannel channel) {
		try{
			this.lock();
			
			int volume = (int)((this.getVolume() / 10.00) * channel.getVolume());
			int balance = channel.getBalance();
			int chorus = channel.getChorus();
			int reverb = channel.getReverb();
			int phaser = channel.getPhaser();
			int tremolo = channel.getTremolo();
			
			updateController(channel.getChannelId(),volume,balance,chorus,reverb,phaser,tremolo,127);
		} finally {
			this.unlock();
		}
	}
	
	private void updateController(int channelId,int volume,int balance,int chorus, int reverb,int phaser, int tremolo, int expression) {
		try{
			this.lock();
			
			getOutputTransmitter().sendControlChange(channelId,MidiControllers.VOLUME,volume);
			getOutputTransmitter().sendControlChange(channelId,MidiControllers.BALANCE,balance);
			getOutputTransmitter().sendControlChange(channelId,MidiControllers.CHORUS,chorus);
			getOutputTransmitter().sendControlChange(channelId,MidiControllers.REVERB,reverb);
			getOutputTransmitter().sendControlChange(channelId,MidiControllers.PHASER,phaser);
			getOutputTransmitter().sendControlChange(channelId,MidiControllers.TREMOLO,tremolo);
			getOutputTransmitter().sendControlChange(channelId,MidiControllers.EXPRESSION,expression);
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void updateTracks() {
		try{
			this.lock();
			
			this.anySolo = false;
			
			Iterator<TGTrack> tracksIt = this.getSong().getTracks();
			while( tracksIt.hasNext() ){
				TGTrack track = (TGTrack)tracksIt.next();
				this.updateTrack(track);
				this.anySolo = ((!this.anySolo)?track.isSolo():this.anySolo);
			}
			
			this.afterUpdate();
		} finally {
			this.unlock();
		}
	}
	
	private void updateTrack(TGTrack track) {
		try{
			this.lock();
			
			this.getSequencer().setMute(track.getNumber(),track.isMute());
			this.getSequencer().setSolo(track.getNumber(),track.isSolo());
		}catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	private void afterUpdate(){
		try {
			this.lock();
			
			this.getSequencer().setSolo(this.infoTrack,this.anySolo);
			this.getSequencer().setSolo(this.metronomeTrack,(isMetronomeEnabled() && this.anySolo));
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public boolean isMetronomeEnabled() {
		return this.metronomeEnabled;
	}
	
	public void setMetronomeEnabled(boolean metronomeEnabled) {
		try {
			this.lock();
			
			this.metronomeEnabled = metronomeEnabled;
			this.getSequencer().setMute(this.metronomeTrack,!isMetronomeEnabled());
			this.getSequencer().setSolo(this.metronomeTrack,(isMetronomeEnabled() && this.anySolo));
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void playBeat(TGBeat beat) {
		try {
			this.lock();
			
			List<TGNote> notes = new ArrayList<TGNote>();
			for( int v = 0; v < beat.countVoices(); v ++){
				notes.addAll( beat.getVoice(v).getNotes() );
			}
			this.playBeat(beat.getMeasure().getTrack(), notes);
		} finally {
			this.unlock();
		}
	}
	
	public void playBeat(TGTrack track, List<TGNote> notes) {
		try {
			this.lock();
			
			TGChannel tgChannel = this.getSongManager().getChannel(getSong(), track.getChannelId());
			if( tgChannel != null ){
				final int channelId = tgChannel.getChannelId();
				final int bank = tgChannel.getBank();
				final int program = tgChannel.getProgram();
				final int volume = (int)((this.getVolume() / 10.00) * tgChannel.getVolume());
				final int balance = tgChannel.getBalance();
				final int chorus = tgChannel.getChorus();
				final int reverb = tgChannel.getReverb();
				final int phaser = tgChannel.getPhaser();
				final int tremolo = tgChannel.getTremolo();
				final int size = notes.size();
				final int[][] beat = new int[size][2];
				for(int i = 0; i < size; i ++){
					TGNote note = (TGNote)notes.get(i);
					beat[i][0] = track.getOffset() + (note.getValue() + ((TGString)track.getStrings().get(note.getString() - 1)).getValue());
					beat[i][1] = note.getVelocity();
				}
				
				new Thread(new Runnable() {
					public void run() {
						MidiPlayer.this.playBeat(channelId, bank, program, volume, balance, chorus, reverb, phaser, tremolo, beat);
					}
				}).start();
			}
		} finally {
			this.unlock();
		}
	}
	
	public void playBeat(int channelId, int bank, int program, int volume, int balance, int chorus, int reverb, int phaser, int tremolo, int[][] beat) {
		this.playBeat(channelId, bank, program, volume, balance, chorus, reverb, phaser, tremolo, beat,500,0);
	}
	
	public void playBeat(int channelId, int bank, int program, int volume, int balance, int chorus, int reverb, int phaser, int tremolo, int[][] beat, long duration, int interval) {
		try {
			this.updateChannels();
			
			this.getOutputTransmitter().sendControlChange(channelId,MidiControllers.BANK_SELECT, bank);
			this.getOutputTransmitter().sendControlChange(channelId,MidiControllers.VOLUME, volume);
			this.getOutputTransmitter().sendControlChange(channelId,MidiControllers.BALANCE, balance);
			this.getOutputTransmitter().sendControlChange(channelId,MidiControllers.CHORUS, chorus);
			this.getOutputTransmitter().sendControlChange(channelId,MidiControllers.REVERB, reverb);
			this.getOutputTransmitter().sendControlChange(channelId,MidiControllers.PHASER, phaser);
			this.getOutputTransmitter().sendControlChange(channelId,MidiControllers.TREMOLO, tremolo);
			this.getOutputTransmitter().sendProgramChange(channelId,program);
			
			Object sync = new Object();
			
			for(int i = 0; i < beat.length; i ++){
				this.getOutputTransmitter().sendNoteOn(channelId,beat[i][0], beat[i][1], -1, false);
				
				if( interval > 0 ){
					synchronized (sync) {
						sync.wait(interval);
					}
				}
			}
			
			synchronized (sync) {
				sync.wait(duration);
			}
			
			for(int i = 0; i < beat.length; i ++){
				this.getOutputTransmitter().sendNoteOff(channelId,beat[i][0], beat[i][1], -1, false);
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public int getPercussionChannelId(){
		try {
			this.lock();
			
			Iterator<TGChannel> tgChannels = this.getSong().getChannels();
			while( tgChannels.hasNext() ){
				TGChannel tgChannel = (TGChannel) tgChannels.next();
				if( tgChannel.isPercussionChannel() ){
					return tgChannel.getChannelId();
				}
			}
			return -1;
		} finally {
			this.unlock();
		}
	}
	
	public boolean loadSequencer(MidiSequencer sequencer){
		try {
			this.lock();
			
			this.closeSequencer();
			this.sequencer = sequencer;
			this.sequencer.open();
			this.sequencer.setTransmitter( getOutputTransmitter() );
		} catch(Throwable throwable){
			this.sequencer = null;
			
			return false;
		} finally {
			this.unlock();
		}
		return true;
	}
	
	public boolean loadOutputPort(MidiOutputPort port){
		try {
			this.lock();
			
			this.closeOutputPort();
			this.outputPort = port;
			this.outputPort.open();
			this.getSynthesizerProxy().setMidiSynthesizer(this.outputPort.getSynthesizer());
		} catch(Throwable throwable) {
			this.outputPort = null;
			
			return false;
		} finally {
			this.unlock();
		}
		return true;
	}
	
	public void openOutputPort(String key) {
		try {
			this.lock();
			
			this.openOutputPort(key, false);
		} finally {
			this.unlock();
		}
	}
	
	public void openOutputPort(String key, boolean tryFirst) {
		try {
			this.lock();
		
			this.outputPortKey = key;
			this.openOutputPort(listOutputPorts(),tryFirst);
		} finally {
			this.unlock();
		}
	}
	
	public void openOutputPort(List<MidiOutputPort> ports, boolean tryFirst) {
		try {
			this.lock();
			
			if(this.outputPortKey != null && !this.isOutputPortOpen(this.outputPortKey)){
				this.closeOutputPort();
				for(int i = 0; i < ports.size(); i ++){
					MidiOutputPort port = (MidiOutputPort)ports.get(i);
					if(port.getKey().equals(this.outputPortKey)){
						if(this.loadOutputPort(port)){
							return;
						}
					}
				}
			}
			if(getOutputPort() == null && !ports.isEmpty() && tryFirst){
				this.loadOutputPort( (MidiOutputPort)ports.get(0) );
			}
		} catch(Throwable throwable){
			throwable.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void openSequencer(String key) {
		try {
			this.lock();
			
			this.openSequencer(key, false);
		} finally {
			this.unlock();
		}
	}
	
	public void openSequencer(String key, boolean tryFirst) {
		try {
			this.lock();
			
			this.sequencerKey = key;
			this.openSequencer(listSequencers(),tryFirst);
		} catch(Throwable throwable){
			throwable.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public void openSequencer(List<MidiSequencer> sequencers ,boolean tryFirst) throws MidiPlayerException {
		try {
			this.lock();
			
			if(this.sequencerKey != null && !this.isSequencerOpen(this.sequencerKey)){
				this.closeSequencer();
				for(int i = 0; i < sequencers.size(); i ++){
					MidiSequencer sequencer = (MidiSequencer)sequencers.get(i);
					if(sequencer.getKey().equals(this.sequencerKey)){
						if(this.loadSequencer(sequencer)){
							return;
						}
					}
				}
			}
			
			if(getSequencer() instanceof MidiSequencerEmpty && !sequencers.isEmpty() && tryFirst){
				this.loadSequencer( (MidiSequencer) sequencers.get(0));
			}
			
		} catch(Throwable throwable){
			throw new MidiPlayerException(throwable.getMessage(),throwable);
		} finally {
			this.unlock();
		}
	}
	
	public List<MidiOutputPort> listOutputPorts() {
		try {
			this.lock();
			
			List<MidiOutputPort> ports = new ArrayList<MidiOutputPort>();
			Iterator<MidiOutputPortProvider> it = this.outputPortProviders.iterator();
			while(it.hasNext()){
				try{
					MidiOutputPortProvider provider = (MidiOutputPortProvider)it.next();
					ports.addAll(provider.listPorts());
				}catch(Throwable throwable){
					throwable.printStackTrace();
				}
			}
			return ports;
		} finally {
			this.unlock();
		}
	}
	
	public List<MidiSequencer> listSequencers(){
		try {
			this.lock();
			
			List<MidiSequencer> sequencers = new ArrayList<MidiSequencer>();
			Iterator<MidiSequencerProvider> it = this.sequencerProviders.iterator();
			while(it.hasNext()){
				try{
					MidiSequencerProvider provider = (MidiSequencerProvider)it.next();
					sequencers.addAll(provider.listSequencers());
				}catch(Throwable throwable){
					throwable.printStackTrace();
				}
			}
			return sequencers;
		} finally {
			this.unlock();
		}
	}
	
	public void closeSequencer() throws MidiPlayerException{
		try{
			this.lock();
			
			if( this.isRunning()){
				this.stop();
			}
			if( this.sequencer != null) {
				this.sequencer.close();
				this.sequencer = null;
			}
		} catch(Throwable throwable){
			throw new MidiPlayerException(throwable.getMessage(),throwable);
		} finally {
			this.unlock();
		}
	}
	
	public void closeOutputPort(){
		try {
			this.lock();
			
			if( this.isRunning()){
				this.stop();
			}
			if( this.outputPort != null) {
				this.closeChannels();
				this.getSynthesizerProxy().setMidiSynthesizer(null);
				this.outputPort.close();
				this.outputPort = null;
			}
		} catch(Throwable throwable){
			throwable.printStackTrace();
		} finally {
			this.unlock();
		}
	}
	
	public boolean isSequencerOpen(String key){
		try {
			this.lock();
			
			if( key != null){
				String currentKey = getSequencer().getKey();
				if(currentKey == null){
					return false;
				}
				return currentKey.equals(key);
			}
			return false;
		} finally {
			this.unlock();
		}
	}
	
	public boolean isOutputPortOpen(String key){
		try {
			this.lock();
			
			if( key != null && getOutputPort() != null ){
				String currentKey = getOutputPort().getKey();
				if(currentKey == null){
					return false;
				}
				return currentKey.equals(key);
			}
			return false;
		} finally {
			this.unlock();
		}
	}
	
	public void addOutputPortProvider(MidiOutputPortProvider provider) throws MidiPlayerException {
		try {
			this.lock();
			
			this.addOutputPortProvider(provider, this.isTryOpenFistDevice());
		} finally {
			this.unlock();
		}
	}
	
	public void addOutputPortProvider(MidiOutputPortProvider provider, boolean tryFirst) throws MidiPlayerException {
		try {
			this.lock();
			
			this.outputPortProviders.add(provider);
			this.openOutputPort(provider.listPorts(),tryFirst);
		} finally {
			this.unlock();
		}
	}
	
	public void addSequencerProvider(MidiSequencerProvider provider) throws MidiPlayerException {
		try {
			this.lock();
			
			this.addSequencerProvider(provider, this.isTryOpenFistDevice());
		} finally {
			this.unlock();
		}
	}
	
	public void addSequencerProvider(MidiSequencerProvider provider, boolean tryFirst) throws MidiPlayerException {
		try {
			this.lock();
			
			this.sequencerProviders.add(provider);
			this.openSequencer(provider.listSequencers(), tryFirst);
		} finally {
			this.unlock();
		}
	}
	
	public void removeOutputPortProvider(MidiOutputPortProvider provider) throws MidiPlayerException {
		try {
			this.lock();
			
			this.outputPortProviders.remove(provider);
			
			MidiOutputPort current = getOutputPort();
			if( current != null ){
				Iterator<MidiOutputPort> it = provider.listPorts().iterator();
				while(it.hasNext()){
					MidiOutputPort port = (MidiOutputPort)it.next();
					if(port.getKey().equals(current.getKey())){
						closeOutputPort();
						break;
					}
				}
			}
		} finally {
			this.unlock();
		}
	}
	
	public void removeSequencerProvider(MidiSequencerProvider provider) throws MidiPlayerException {
		try {
			this.lock();
			
			this.sequencerProviders.remove(provider);
			
			MidiSequencer current = getSequencer();
			if(!(current instanceof MidiSequencerEmpty) && current != null){
				Iterator<MidiSequencer> it = provider.listSequencers().iterator();
				while(it.hasNext()){
					MidiSequencer sequencer = (MidiSequencer)it.next();
					if(current.getKey().equals(sequencer.getKey())){
						closeSequencer();
						break;
					}
				}
			}
		} finally {
			this.unlock();
		}
	}
	
	public void addListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(MidiPlayerEvent.EVENT_TYPE, listener);
	}
	
	public void removeListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(MidiPlayerEvent.EVENT_TYPE, listener);
	}
	
	public void notifyStarted(){
		TGEventManager.getInstance(this.context).fireEvent(new MidiPlayerEvent(MidiPlayerEvent.NOTIFY_STARTED));
	}
	
	public void notifyStopped(){
		TGEventManager.getInstance(this.context).fireEvent(new MidiPlayerEvent(MidiPlayerEvent.NOTIFY_STOPPED));
	}
	
	public void notifyCountDownStarted(){
		TGEventManager.getInstance(this.context).fireEvent(new MidiPlayerEvent(MidiPlayerEvent.NOTIFY_COUNT_DOWN_STARTED));
	}
	
	public void notifyCountDownStopped(){
		TGEventManager.getInstance(this.context).fireEvent(new MidiPlayerEvent(MidiPlayerEvent.NOTIFY_COUNT_DOWN_STOPPED));
	}
	
	public void notifyLoop(){
		TGEventManager.getInstance(this.context).fireEvent(new MidiPlayerEvent(MidiPlayerEvent.NOTIFY_LOOP));
	}
	
	public MidiInstrument[] getInstruments(){
		return MidiInstrument.INSTRUMENT_LIST;
	}
	
	public MidiPercussionKey[] getPercussionKeys(){
		return MidiPercussionKey.PERCUSSION_KEY_LIST;
	}
	
	public int getLoopSHeader() {
		return this.loopSHeader;
	}
	
	public int getLoopEHeader() {
		return this.loopEHeader;
	}
	
	public long getLoopSPosition() {
		return this.loopSPosition;
	}
	
	public boolean isTryOpenFistDevice() {
		return tryOpenFistDevice;
	}

	public void setTryOpenFistDevice(boolean tryOpenFistDevice) {
		this.tryOpenFistDevice = tryOpenFistDevice;
	}
	
	public TGSongManager getSongManager(){
		return this.documentManager.getSongManager();
	}
	
	public TGSong getSong(){
		return this.documentManager.getSong();
	}
	
	public MidiPlayerMode getMode(){
		if( this.mode == null){
			this.mode = new MidiPlayerMode();
		}
		return this.mode;
	}
	
	public MidiPlayerCountDown getCountDown(){
		if( this.countDown == null ){
			this.countDown = new MidiPlayerCountDown(this);
		}
		return this.countDown;
	}
	
	public MidiTransmitter getOutputTransmitter(){
		if (this.outputTransmitter == null) {
			this.outputTransmitter = new MidiTransmitter();
			this.outputTransmitter.addReceiver(getChannelRouter().getId(), getChannelRouter());
		}
		return this.outputTransmitter;
	}
	
	public MidiOutputPort getOutputPort(){
		return this.outputPort;
	}
	
	public MidiChannelRouter getChannelRouter(){
		if (this.channelRouter == null) {
			this.channelRouter = new MidiChannelRouter();
		}
		return this.channelRouter;
	}
	
	public MidiSynthesizerProxy getSynthesizerProxy(){
		if (this.synthesizerProxy == null) {
			this.synthesizerProxy = new MidiSynthesizerProxy();
		}
		return this.synthesizerProxy;
	}
	
	public MidiSequencer getSequencer(){
		if (this.sequencer == null) {
			this.sequencer = new MidiSequencerEmpty();
		}
		return this.sequencer;
	}
	
	public static MidiPlayer getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, MidiPlayer.class.getName(), new TGSingletonFactory<MidiPlayer>() {
			public MidiPlayer createInstance(TGContext context) {
				return new MidiPlayer(context);
			}
		});
	}
}
