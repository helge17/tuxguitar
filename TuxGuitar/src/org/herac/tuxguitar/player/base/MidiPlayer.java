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
	
	private static final int TIMER_DELAY = 10;
	
	private TGSongManager songManager;
	
	private MidiSequencer sequencer;
	
	private MidiPort port;
	
	private MidiPlayerMode mode;
	
	private String sequencerKey;
	
	private String portKey;
	
	private List portProviders;
	
	private List sequencerProviders;
	
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
	}
	
	/**
	 * Inicia el Secuenciador y Sintetizador
	 * @throws MidiUnavailableException 
	 */
	
	public void init(TGSongManager songManager) {
		this.songManager = songManager;
		this.portProviders = new ArrayList();
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
			this.closePort();
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
			this.getMidiPort().check();
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
		if(!isRunning()){
			this.changeTickPosition();
		}
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
			this.getMidiPort().out().sendSystemReset();
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
				getMidiPort().out().sendControlChange(channel,MidiControllers.RPN_MSB,0);
				getMidiPort().out().sendControlChange(channel,MidiControllers.RPN_LSB,0);
				getMidiPort().out().sendControlChange(channel,MidiControllers.DATA_ENTRY_MSB,12);
				getMidiPort().out().sendControlChange(channel,MidiControllers.DATA_ENTRY_LSB, 0);
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
				getMidiPort().out().sendProgramChange(track.getChannel().getChannel(),track.getChannel().getInstrument());
				if(track.getChannel().getChannel() != track.getChannel().getEffectChannel()){
					getMidiPort().out().sendProgramChange(track.getChannel().getEffectChannel(),track.getChannel().getInstrument());
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
			this.anySolo = ((!this.anySolo)?track.getChannel().isSolo():this.anySolo);
			percusionUpdated = (percusionUpdated || track.isPercussionTrack());
		}
		if(!percusionUpdated && isMetronomeEnabled()){
			updateController(9,(int)((this.songManager.getSong().getVolume() / 10.00) * TGChannel.DEFAULT_VOLUME),TGChannel.DEFAULT_BALANCE);
		}
		this.afterUpdate();
	}
	
	private void updateController(TGTrack track) {
		try{
			int volume = (int)((this.songManager.getSong().getVolume() / 10.00) * track.getChannel().getVolume());
			int balance = track.getChannel().getBalance();
			
			updateController(track.getChannel().getChannel(),volume,balance);
			if(track.getChannel().getChannel() != track.getChannel().getEffectChannel()){
				updateController(track.getChannel().getEffectChannel(),volume,balance);
			}
			getSequencer().setMute(track.getNumber(),track.getChannel().isMute());
			getSequencer().setSolo(track.getNumber(),track.getChannel().isSolo());
		}catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	private void updateController(int channel,int volume,int balance) {
		try{
			getMidiPort().out().sendControlChange(channel,MidiControllers.VOLUME,volume);
			getMidiPort().out().sendControlChange(channel,MidiControllers.BALANCE,balance);
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
		int volume = (int)((this.songManager.getSong().getVolume() / 10.00) * track.getChannel().getVolume());
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
			getMidiPort().out().sendProgramChange(channel,program);
			getMidiPort().out().sendControlChange(channel,MidiControllers.VOLUME,volume);
			getMidiPort().out().sendControlChange(channel,MidiControllers.BALANCE,balance);
			getMidiPort().out().sendControlChange(channel,MidiControllers.CHORUS,chorus);
			getMidiPort().out().sendControlChange(channel,MidiControllers.REVERB,reverb);
			getMidiPort().out().sendControlChange(channel,MidiControllers.PHASER,phaser);
			getMidiPort().out().sendControlChange(channel,MidiControllers.TREMOLO,tremolo);
				
			for(int i = 0; i < beat.length; i ++){
				getMidiPort().out().sendNoteOn(channel,beat[i][0], beat[i][1]);
				if(interval > 0){
					Thread.sleep(interval);
				}
			}
			Thread.sleep(duration);
			for(int i = 0; i < beat.length; i ++){
				getMidiPort().out().sendNoteOff(channel,beat[i][0], beat[i][1]);
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
	
	/**
	 * Retorna el Puerto Midi
	 */
	public MidiPort getMidiPort(){
		if (this.port == null) {
			this.port = new MidiPortEmpty();
		}
		return this.port;
	}
	
	/**
	 * Retorna el Sequenciador 
	 */
	protected MidiSequencer getSequencer(){
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
			this.sequencer.setMidiPort(getMidiPort());
		}catch(Throwable throwable){
			this.sequencer = null;
			return false;
		}
		return true;
	}
	
	public boolean loadPort(MidiPort port){
		try{
			this.closePort();
			this.port = port;
			this.port.open();
		}catch(Throwable throwable){
			this.port = null;
			return false;
		}finally{
			try {
				this.getSequencer().setMidiPort(getMidiPort());
			} catch (MidiPlayerException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public void openPort(String key) {
		this.portKey = key;
		this.openPort(listPorts(),false);
	}
	
	public void openPort(List ports, boolean tryFirst) {
		try{
			if(this.portKey != null && !this.isMidiPortOpen(this.portKey)){
				//if(this.isRunning()){
				//	this.stop();
				//}
				this.closePort();
				for(int i = 0; i < ports.size(); i ++){
					MidiPort port = (MidiPort)ports.get(i);
					if(port.getKey().equals(this.portKey)){
						if(this.loadPort(port)){
							return;
						}
					}
				}
			}
			
			if(getMidiPort() instanceof MidiPortEmpty && !ports.isEmpty() && tryFirst){
				this.loadPort( (MidiPort)ports.get(0) );
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
				//if(this.isRunning()){
				//	this.stop();
				//}
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
	
	public List listPorts() {
		List ports = new ArrayList();
		Iterator it = this.portProviders.iterator();
		while(it.hasNext()){
			try{
				MidiPortProvider provider = (MidiPortProvider)it.next();
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
	
	public void closePort(){
		try{
			if(this.isRunning()){
				this.stop();
			}
			this.lock.lock();
			if (this.port != null) {
				this.port.close();
				this.port = null;
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
	
	public boolean isMidiPortOpen(String key){
		if(key != null){
			String currentKey = getMidiPort().getKey();
			if(currentKey == null){
				return false;
			}
			return currentKey.equals(key);
		}
		return false;
	}
	
	public void addPortProvider(MidiPortProvider provider) throws MidiPlayerException {
		this.portProviders.add(provider);
		this.openPort(provider.listPorts(),true);
	}
	
	public void addSequencerProvider(MidiSequencerProvider provider) throws MidiPlayerException {
		this.sequencerProviders.add(provider);
		this.openSequencer(provider.listSequencers(),true);
	}
	
	public void removePortProvider(MidiPortProvider provider) throws MidiPlayerException {
		this.portProviders.remove(provider);
		
		MidiPort current = getMidiPort();
		if(!(current instanceof MidiPortEmpty) && current != null ){
			Iterator it = provider.listPorts().iterator();
			while(it.hasNext()){
				MidiPort port = (MidiPort)it.next();
				if(port.getKey().equals(current.getKey())){
					closePort();
					getSequencer().setMidiPort(getMidiPort());
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
