package org.herac.tuxguitar.jack.sequencer;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSequenceHandler;
import org.herac.tuxguitar.player.base.MidiSequencer;
import org.herac.tuxguitar.player.base.MidiTransmitter;
import org.herac.tuxguitar.util.TGContext;

public class JackSequencer implements MidiSequencer{
	
	private long transportUID;
	private long transportTryCount;
	private long transportTryNumber;
	private boolean reset;
	private boolean running;
	private boolean stopped;
	private boolean transportRunning;
	private boolean transportLockTick;
	private MidiTransmitter transmitter;
	private Object jackLock;
	private JackTickController jackTickController;
	private JackEventController jackEventController;
	private JackEventDispacher jackEventDispacher;
	private JackTrackController jackTrackController;
	private JackTimer jackTimer;
	private JackClient jackClient;
	private JackMidiPlayerStarter jackMidiPlayerStarter;
	
	public JackSequencer(TGContext context, JackClient jackClient){
		this.stopped = true;
		this.running = false;
		this.transportRunning = false;
		this.transportUID = -1;
		this.transportTryCount = 10;
		this.transportTryNumber = 0;
		this.jackClient = jackClient;
		this.jackLock = new Object();
		this.jackTickController = new JackTickController(this);
		this.jackEventController = new JackEventController(this);
		this.jackEventDispacher = new JackEventDispacher(this);
		this.jackTrackController = new JackTrackController(this);
		this.jackTimer = new JackTimer(this);
		this.jackMidiPlayerStarter = new JackMidiPlayerStarter(context, this);
	}
	
	public JackClient getJackClient(){
		return this.jackClient;
	}
	
	public JackTickController getJackTickController(){
		return this.jackTickController;
	}
	
	public JackEventController getJackEventController(){
		return this.jackEventController;
	}
	
	public JackTrackController getJackTrackController(){
		return this.jackTrackController;
	}
	
	public void setTempo(int tempo){
		this.jackTickController.setTempo(tempo);
	}
	
	public long getTickPosition(){
		return Math.round(this.jackTickController.getTick());
	}
	
	public void setTickPosition(long tickPosition){
		this.setTickPosition(tickPosition, !this.transportLockTick );
		this.transportLockTick = false;
	}
	
	public void setTickPosition(long tickPosition, boolean transportUpdate ){
		this.reset = true;
		this.jackTickController.setTick(tickPosition , transportUpdate);
	}
	
	public long getTickLength(){
		return this.jackTickController.getTickLength();
	}
	
	public void sendEvent(JackEvent event) throws MidiPlayerException{
		if(!this.reset){
			this.jackEventDispacher.dispatch(event);
		}
	}
	
	public void addEvent(JackEvent event){
		this.jackEventController.addEvent(event);
		this.jackTickController.notifyTick(event.getTick());
	}
	
	public void clearEvents() {
		this.jackEventController.clearEvents();
		this.jackTickController.clearTick();
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public void start() throws MidiPlayerException{
		this.start( true );
	}
	
	public void start( boolean startTransport ) throws MidiPlayerException{
		synchronized ( this.jackLock ) {
			if(!this.running ){
				this.setTempo(120);
				this.running = true;
				if( startTransport ){
					this.jackClient.setTransportStart();
				}
			}
		}
	}
	
	public void stop() throws MidiPlayerException{
		this.stop( true );
	}
	
	public void stop( boolean stopTransport ) throws MidiPlayerException{
		synchronized ( this.jackLock ) {
			if( this.running ){
				this.running = false;
				if( stopTransport ){
					this.jackClient.setTransportStop();
				}
			}
		}
	}
	
	public void stopAndClearEvents() throws MidiPlayerException{
		synchronized ( this.jackLock ) {
			this.stop( true );
			this.reset();
			this.clearEvents();
			this.stopped = true;
		}
	}
	
	public void reset()  throws MidiPlayerException{
		this.getTransmitter().sendAllNotesOff();
		this.getTransmitter().sendPitchBendReset();
	}
	
	protected void startPlayer(){
		// Make sure sequencer was already initialized.
		if( this.transmitter != null ){
			this.transportLockTick = true;
			this.jackMidiPlayerStarter.start();
		}
	}
	
	public MidiTransmitter getTransmitter() {
		return this.transmitter;
	}
	
	public void setTransmitter(MidiTransmitter transmitter) {
		this.transmitter = transmitter;
	}
	
	public void open() {
		if(!this.jackClient.isOpen()){
			this.jackClient.open();
		}
		if(!this.jackClient.isTransportOpen() ){
			this.jackClient.openTransport();
		}
		this.jackMidiPlayerStarter.open();
		this.jackTimer.setRunning( true );
	}
	
	public void close() throws MidiPlayerException {
		this.jackTimer.setRunning( false );
		if(this.isRunning()){
			this.stop();
		}
		this.jackMidiPlayerStarter.close();
		if( this.jackClient.isTransportOpen() ){
			this.jackClient.closeTransport();
		}
	}
	
	public void check() throws MidiPlayerException {
		if(!this.jackClient.isOpen() || !this.jackClient.isTransportOpen() ){
			this.open();
			if(!this.jackClient.isOpen() || !this.jackClient.isTransportOpen() ){
				throw new MidiPlayerException("Jack server not running?");
			}
		}
	}
	
	public MidiSequenceHandler createSequence(int tracks) throws MidiPlayerException{
		this.stopAndClearEvents();
		
		return new JackSequenceHandler(this,tracks);
	}
	
	public void setSolo(int index,boolean solo) throws MidiPlayerException{
		this.getJackTrackController().setSolo(index, solo);
	}
	
	public void setMute(int index,boolean mute) throws MidiPlayerException{
		this.getJackTrackController().setMute(index, mute);
	}
	
	public String getKey() {
		return "tuxguitar-jack";
	}
	
	public String getName() {
		return "Jack Sequencer";
	}
	
	protected void process() throws MidiPlayerException{
		synchronized ( this.jackLock ) {
			boolean transportRunning = this.jackClient.isTransportRunning();
			
			// Check if transport was started
			if( transportRunning && !this.transportRunning && !this.running ){
				this.startPlayer();
			}
			else{
				if( this.running && transportRunning ){
					long transportUID = this.jackClient.getTransportUID();
					if( this.transportUID != transportUID ){
						this.setTickPosition( 0 , false );
					}
					if(this.reset){
						this.reset();
						this.reset = false;
						this.jackEventController.reset();
					}
					this.stopped = false;
					this.jackTickController.process();
					this.jackEventController.process();
					if(this.getTickPosition() > this.getTickLength()){
						this.stopAndClearEvents();
					}
					this.transportUID = transportUID;
				}
				else if( this.running ){
					// Check if transport was stopped.
					if( this.transportTryNumber++ > this.transportTryCount ){
						this.running = false;
						this.transportTryNumber = 0;
					}
				}
				else if(!this.stopped){
					this.stopAndClearEvents();
				}
			}
			this.transportRunning = transportRunning;
		}
	}
	
	private class JackTimer implements Runnable{
		
		private static final int TIMER_DELAY = 10;
		
		private Object sequencerSync;
		private JackSequencer sequencer;
		private boolean running;
		
		public JackTimer(JackSequencer sequencer){
			this.sequencerSync = new Object();
			this.sequencer = sequencer;
			this.running = false;
		}
		
		public void setRunning( boolean running ){
			this.running = running;
			if( this.running ){
				new Thread( this ).start();
			}
		}
		
		public void run() {
			try {
				synchronized(this.sequencerSync) {
					while( this.running ){
						this.sequencer.process();
						this.sequencerSync.wait( TIMER_DELAY );
					}
				}
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}
}
