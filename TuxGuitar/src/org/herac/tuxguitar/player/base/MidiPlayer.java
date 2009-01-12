package org.herac.tuxguitar.player.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGLock;

public class MidiPlayer{
	
	private static final int MAX_CHANNELS = 16;
	
	public static final int MAX_VOLUME = 10;
	
	private static final int TIMER_DELAY = 10;
	
	private TGSongManager songManager;
	
	private MidiSequencer sequencer;
	
	private MidiTransmitter outputTransmitter;
	
	private MidiOutputPort outputPort;
	
	private MidiPlayerMode mode;
	
	private String sequencerKey;
	
	private String outputPortKey;
	
	private List outputPortProviders;
	
	private List sequencerProviders;
	
	private int volume;
	
	private boolean running;
	
	private boolean paused;
	
	private boolean changeTickPosition;
		
	private boolean metronomeEnabled;
	
	private int metronomeTrack;
	
	private int infoTrack;
	
	private boolean anySolo;
	
	protected long tickPosition;
	
	protected boolean starting;
	
	protected TGLock lock = new TGLock();
	
	public MidiPlayer() {
		this.lock = new TGLock();
		this.volume = MAX_VOLUME;
	}
	
	/**
	 * Inicia el Secuenciador y Sintetizador
	 * @throws MidiUnavailableException 
	 */
	
	public void init(TGSongManager songManager) {
		this.songManager = songManager;
		this.outputPortProviders = new ArrayList();
		this.sequencerProviders = new ArrayList();
		this.getSequencer();
		this.getMode();
		this.reset();
	}
	
	/**
	 * Retorna una lista de instrumentos
	 */
	public MidiInstrument[] getInstruments(){
		return MidiInstrument.INSTRUMENT_LIST;
	}
	
	/**
	 * Retorna una lista de instrumentos
	 */
	public MidiPercussion[] getPercussions(){
		return MidiPercussion.PERCUSSION_LIST;
	}
	
	/**
	 * Resetea los valores
	 */
	public void reset(){
		this.stop();
		this.lock.lock();
		this.tickPosition = TGDuration.QUARTER_TIME;
		this.setChangeTickPosition(false);
		this.lock.unlock();
	}
	
	/**
	 * Cierra el Secuenciador y Sintetizador
	 * @throws MidiUnavailableException 
	 */
	public void close(){
		try {
			this.closeSequencer();
			this.closeOutputPort();
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Para la reproduccion
	 * @throws MidiUnavailableException 
	 */
	public void stop(boolean paused) {
		try{
			this.setPaused(paused);
			if(this.isRunning()){
				this.getSequencer().stop();
			}
			this.setRunning(false);
		}catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Para la reproduccion
	 * @throws MidiUnavailableException 
	 */
	public void stop() {
		this.stop(false);
	}
	
	public void pause(){
		this.stop(true);
	}
	
	/**
	 * Inicia la reproduccion
	 * @throws MidiPlayerException 
	 * @throws MidiUnavailableException 
	 */
	public synchronized void play() throws MidiPlayerException{
		try {
			this.setStarting(true);
			this.stop();
			this.lock.lock();
			this.checkOutput();
			this.systemReset();
			this.addSecuence();
			this.updatePrograms();
			this.updateControllers();
			this.updateDefaultControllers();
			this.setMetronomeEnabled(isMetronomeEnabled());
			this.changeTickPosition();
			this.setRunning(true);
			this.getSequencer().start();
			new Thread(new Runnable() {
				public synchronized void run() {
					try {
						MidiPlayer.this.lock.lock();
						
						setStarting(false);
						
						MidiPlayer.this.tickPosition = getSequencer().getTickPosition();
						while (getSequencer().isRunning() && isRunning()) {
							synchronized(getSequencer()) {
								if (isChangeTickPosition()) {
									changeTickPosition();
								}
								MidiPlayer.this.tickPosition = getSequencer().getTickPosition();
								
								getSequencer().wait( TIMER_DELAY );
							}
						}
						
						//FINISH
						if(isRunning()){
							if(MidiPlayer.this.tickPosition >= (getSequencer().getTickLength() - (TGDuration.QUARTER_TIME / 2) )){
								finish();
							}else {
								stop(isPaused());
							}
						}
					}catch (Throwable throwable) {
						setStarting(false);
						reset();
						throwable.printStackTrace();
					}finally{
						MidiPlayer.this.lock.unlock();
					}
				}
			}).start();
		}catch (Throwable throwable) {
			this.setStarting(false);
			this.reset();
			throw new MidiPlayerException(throwable.getMessage(),throwable);
		}finally{
			this.lock.unlock();
		}
	}
	
	protected void finish(){
		try {
			if(this.getMode().isLoop()){
				this.setStarting(true);
				this.reset();
				this.getMode().notifyLoop();
				this.play();
				return;
			}
			this.reset();
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void checkOutput() throws Throwable {
		if( getOutputPort() != null ){
			getOutputPort().check();
		}
	}
	
	public int getVolume() {
		return this.volume;
	}
	
	public void setVolume(int volume) {
		this.volume = volume;
		if (this.isRunning()) {
			this.updateControllers();
		}
	}
	
	protected boolean isStarting() {
		return this.starting;
	}
	
	protected void setStarting(boolean starting) {
		this.starting = starting;
	}
	
	/**
	 * Asigna el valor a running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	/**
	 * Retorna True si esta reproduciendo
	 */
	public boolean isRunning() {
		try {
			return (this.running || this.getSequencer().isRunning() || this.isStarting());
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isPaused() {
		return this.paused;
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	/**
	 * Retorna True si hay cambios en la posicion
	 */
	protected boolean isChangeTickPosition() {
		return this.changeTickPosition;
	}
	
	/**
	 * Asigna los cambios de la posicion
	 */
	private void setChangeTickPosition(boolean changeTickPosition) {
		this.changeTickPosition = changeTickPosition;
	}
	
	/**
	 * Indica la posicion del secuenciador
	 * @throws MidiUnavailableException 
	 */
	public void setTickPosition(long position) {
		this.tickPosition = position;
		this.setChangeTickPosition(true);
	}
	
	/**
	 * Retorna el tick de la nota que esta reproduciendo
	 */
	public long getTickPosition() {
		return this.tickPosition;
	}
	
	protected void changeTickPosition(){
		try{
			if(isRunning()){
				this.getSequencer().setTickPosition(this.tickPosition);
			}
			setChangeTickPosition(false);
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void systemReset(){
		try {
			this.getOutputTransmitter().sendSystemReset();
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Agrega la Secuencia
	 * @throws MidiUnavailableException 
	 */
	public void addSecuence() {
		try{
			MidiSequenceParser parser = new MidiSequenceParser(this.songManager,MidiSequenceParser.DEFAULT_PLAY_FLAGS,getMode().getCurrentPercent(),0);		
			MidiSequenceHandler sequence = getSequencer().createSequence(this.songManager.getSong().countTracks() + 2);
			parser.parse(sequence);
			this.infoTrack = sequence.getInfoTrack();
			this.metronomeTrack = sequence.getMetronomeTrack();
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	private void updateDefaultControllers(){
		try{
			for(int channel = 0; channel < MAX_CHANNELS;channel ++){
				getOutputTransmitter().sendControlChange(channel,MidiControllers.RPN_MSB,0);
				getOutputTransmitter().sendControlChange(channel,MidiControllers.RPN_LSB,0);
				getOutputTransmitter().sendControlChange(channel,MidiControllers.DATA_ENTRY_MSB,12);
				getOutputTransmitter().sendControlChange(channel,MidiControllers.DATA_ENTRY_LSB, 0);
			}
		}
		catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void updatePrograms() {
		try{
			Iterator it = this.songManager.getSong().getTracks();
			while(it.hasNext()){
				TGTrack track = (TGTrack)it.next();
				getOutputTransmitter().sendProgramChange(track.getChannel().getChannel(),track.getChannel().getInstrument());
				if(track.getChannel().getChannel() != track.getChannel().getEffectChannel()){
					getOutputTransmitter().sendProgramChange(track.getChannel().getEffectChannel(),track.getChannel().getInstrument());
				}
			}
		}catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void updateControllers() {
		this.anySolo = false;
		boolean percusionUpdated = false;
		Iterator it = this.songManager.getSong().getTracks();
		while(it.hasNext()){
			TGTrack track = (TGTrack)it.next();
			this.updateController(track);
			this.anySolo = ((!this.anySolo)?track.isSolo():this.anySolo);
			percusionUpdated = (percusionUpdated || track.isPercussionTrack());
		}
		if(!percusionUpdated && isMetronomeEnabled()){
			int volume = (int)((this.getVolume() / 10.00) * TGChannel.DEFAULT_VOLUME);
			int balance = TGChannel.DEFAULT_BALANCE;
			int chorus = TGChannel.DEFAULT_CHORUS;
			int reverb = TGChannel.DEFAULT_REVERB;
			int phaser = TGChannel.DEFAULT_PHASER;
			int tremolo = TGChannel.DEFAULT_TREMOLO;
			updateController(9,volume,balance,chorus,reverb,phaser,tremolo);
		}
		this.afterUpdate();
	}
	
	private void updateController(TGTrack track) {
		try{
			int volume = (int)((this.getVolume() / 10.00) * track.getChannel().getVolume());
			int balance = track.getChannel().getBalance();
			int chorus = track.getChannel().getChorus();
			int reverb = track.getChannel().getReverb();
			int phaser = track.getChannel().getPhaser();
			int tremolo = track.getChannel().getTremolo();
			
			updateController(track.getChannel().getChannel(),volume,balance,chorus,reverb,phaser,tremolo);
			if(track.getChannel().getChannel() != track.getChannel().getEffectChannel()){
				updateController(track.getChannel().getEffectChannel(),volume,balance,chorus,reverb,phaser,tremolo);
			}
			getSequencer().setMute(track.getNumber(),track.isMute());
			getSequencer().setSolo(track.getNumber(),track.isSolo());
		}catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	private void updateController(int channel,int volume,int balance,int chorus, int reverb,int phaser, int tremolo) {
		try{
			getOutputTransmitter().sendControlChange(channel,MidiControllers.VOLUME,volume);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.BALANCE,balance);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.CHORUS,chorus);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.REVERB,reverb);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.PHASER,phaser);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.TREMOLO,tremolo);
		}catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	private void afterUpdate(){
		try{
			getSequencer().setSolo(this.infoTrack,this.anySolo);
			getSequencer().setSolo(this.metronomeTrack,(isMetronomeEnabled() && this.anySolo));
		}catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isMetronomeEnabled() {
		return this.metronomeEnabled;
	}
	
	public void setMetronomeEnabled(boolean metronomeEnabled) {
		try{
			this.metronomeEnabled = metronomeEnabled;
			this.getSequencer().setMute(this.metronomeTrack,!isMetronomeEnabled());
			this.getSequencer().setSolo(this.metronomeTrack,(isMetronomeEnabled() && this.anySolo));
		}catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void playBeat(final TGTrack track,final List notes) {
		int channel = track.getChannel().getChannel();
		int program = track.getChannel().getInstrument();
		int volume = (int)((this.getVolume() / 10.00) * track.getChannel().getVolume());
		int balance = track.getChannel().getBalance();
		int chorus = track.getChannel().getChorus();
		int reverb = track.getChannel().getReverb();
		int phaser = track.getChannel().getPhaser();
		int tremolo = track.getChannel().getTremolo();
		int size = notes.size();
		int[][] beat = new int[size][2];
		for(int i = 0; i < size; i ++){
			TGNote note = (TGNote)notes.get(i);
			beat[i][0] = track.getOffset() + (note.getValue() + ((TGString)track.getStrings().get(note.getString() - 1)).getValue());
			beat[i][1] = note.getVelocity();
		}
		playBeat(channel,program,volume,balance,chorus,reverb,phaser,tremolo,beat);
	}
	
	public void playBeat(int channel,int program,int volume,int balance,int chorus, int reverb,int phaser,int tremolo,int[][] beat) {
		playBeat(channel, program, volume, balance, chorus, reverb, phaser, tremolo, beat,500,0);
	}
	
	public void playBeat(int channel,int program,int volume,int balance,int chorus, int reverb,int phaser,int tremolo,int[][] beat,long duration,int interval) {
		try {
			getOutputTransmitter().sendProgramChange(channel,program);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.VOLUME,volume);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.BALANCE,balance);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.CHORUS,chorus);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.REVERB,reverb);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.PHASER,phaser);
			getOutputTransmitter().sendControlChange(channel,MidiControllers.TREMOLO,tremolo);
				
			for(int i = 0; i < beat.length; i ++){
				getOutputTransmitter().sendNoteOn(channel,beat[i][0], beat[i][1]);
				if(interval > 0){
					Thread.sleep(interval);
				}
			}
			Thread.sleep(duration);
			for(int i = 0; i < beat.length; i ++){
				getOutputTransmitter().sendNoteOff(channel,beat[i][0], beat[i][1]);
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public MidiPlayerMode getMode(){
		if(this.mode == null){
			this.mode = new MidiPlayerMode();
		}
		return this.mode;
	}
	
	public MidiTransmitter getOutputTransmitter(){
		if (this.outputTransmitter == null) {
			this.outputTransmitter = new MidiTransmitter();
		}
		return this.outputTransmitter;
	}
	
	/**
	 * Retorna el Puerto Midi
	 */
	public MidiOutputPort getOutputPort(){
		return this.outputPort;
	}
	
	/**
	 * Retorna el Sequenciador 
	 */
	public MidiSequencer getSequencer(){
		if (this.sequencer == null) {
			this.sequencer = new MidiSequencerEmpty();
		}
		return this.sequencer;
	}
	
	public boolean loadSequencer(MidiSequencer sequencer){
		try{
			this.closeSequencer();
			this.sequencer = sequencer;
			this.sequencer.open();
			this.sequencer.setTransmitter( getOutputTransmitter() );
		}catch(Throwable throwable){
			this.sequencer = null;
			return false;
		}
		return true;
	}
	
	public boolean loadOutputPort(MidiOutputPort port){
		try{
			this.closeOutputPort();
			this.outputPort = port;
			this.outputPort.open();
			this.getOutputTransmitter().addReceiver(this.outputPort.getKey(), this.outputPort.getReceiver() );
		}catch(Throwable throwable){
			this.outputPort = null;
			return false;
		}
		return true;
	}
	
	public void openOutputPort(String key) {
		this.outputPortKey = key;
		this.openOutputPort(listOutputPorts(),false);
	}
	
	public void openOutputPort(List ports, boolean tryFirst) {
		try{
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
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	public void openSequencer(String key) {
		try{
			this.sequencerKey = key;
			this.openSequencer(listSequencers(),false);
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	public void openSequencer(List sequencers ,boolean tryFirst) throws MidiPlayerException {
		try{
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
			
		}catch(Throwable throwable){
			throw new MidiPlayerException(throwable.getMessage(),throwable);
		}
	}
	
	public List listOutputPorts() {
		List ports = new ArrayList();
		Iterator it = this.outputPortProviders.iterator();
		while(it.hasNext()){
			try{
				MidiOutputPortProvider provider = (MidiOutputPortProvider)it.next();
				ports.addAll(provider.listPorts());
			}catch(Throwable throwable){
				throwable.printStackTrace();
			}
		}
		return ports;
	}
	
	public List listSequencers(){
		List sequencers = new ArrayList();
		Iterator it = this.sequencerProviders.iterator();
		while(it.hasNext()){
			try{
				MidiSequencerProvider provider = (MidiSequencerProvider)it.next();
				sequencers.addAll(provider.listSequencers());
			}catch(Throwable throwable){
				throwable.printStackTrace();
			}
		}
		return sequencers;
	}
	
	public void closeSequencer() throws MidiPlayerException{
		try{
			if(this.isRunning()){
				this.stop();
			}
			this.lock.lock();
			if (this.sequencer != null) {
				this.sequencer.close();
				this.sequencer = null;
			}
			this.lock.unlock();
		}catch(Throwable throwable){
			throw new MidiPlayerException(throwable.getMessage(),throwable);
		}
	}
	
	public void closeOutputPort(){
		try{
			if(this.isRunning()){
				this.stop();
			}
			this.lock.lock();
			if (this.outputPort != null) {
				this.getOutputTransmitter().removeReceiver(this.outputPort.getKey());
				this.outputPort.close();
				this.outputPort = null;
			}
			this.lock.unlock();
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	public boolean isSequencerOpen(String key){
		if(key != null){
			String currentKey = getSequencer().getKey();
			if(currentKey == null){
				return false;
			}
			return currentKey.equals(key);
		}
		return false;
	}
	
	public boolean isOutputPortOpen(String key){
		if(key != null && getOutputPort() != null ){
			String currentKey = getOutputPort().getKey();
			if(currentKey == null){
				return false;
			}
			return currentKey.equals(key);
		}
		return false;
	}
	
	public void addOutputPortProvider(MidiOutputPortProvider provider) throws MidiPlayerException {
		this.outputPortProviders.add(provider);
		this.openOutputPort(provider.listPorts(),true);
	}
	
	public void addSequencerProvider(MidiSequencerProvider provider) throws MidiPlayerException {
		this.sequencerProviders.add(provider);
		this.openSequencer(provider.listSequencers(),true);
	}
	
	public void removeOutputPortProvider(MidiOutputPortProvider provider) throws MidiPlayerException {
		this.outputPortProviders.remove(provider);
		
		MidiOutputPort current = getOutputPort();
		if( current != null ){
			Iterator it = provider.listPorts().iterator();
			while(it.hasNext()){
				MidiOutputPort port = (MidiOutputPort)it.next();
				if(port.getKey().equals(current.getKey())){
					closeOutputPort();
					break;
				}
			}
		}
	}
	
	public void removeSequencerProvider(MidiSequencerProvider provider) throws MidiPlayerException {
		this.sequencerProviders.remove(provider);
		
		MidiSequencer current = getSequencer();
		if(!(current instanceof MidiSequencerEmpty) && current != null){
			Iterator it = provider.listSequencers().iterator();
			while(it.hasNext()){
				MidiSequencer sequencer = (MidiSequencer)it.next();
				if(current.getKey().equals(sequencer.getKey())){
					closeSequencer();
					break;
				}
			}
		}
	}	
}
