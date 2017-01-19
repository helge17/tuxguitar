package org.herac.tuxguitar.io.midi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.herac.tuxguitar.io.midi.base.MidiEvent;
import org.herac.tuxguitar.io.midi.base.MidiMessage;
import org.herac.tuxguitar.io.midi.base.MidiSequence;
import org.herac.tuxguitar.io.midi.base.MidiTrack;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;

public class MidiSongReader extends MidiFileFormat implements TGSongReader {
	
	private static final int MIN_DURATION_VALUE = TGDuration.SIXTY_FOURTH;
	
	private int resolution;
	private List<TGChannel> channels;
	private List<TGMeasureHeader> headers;
	private List<TGTrack> tracks;
	private List<TempNote> tempNotes;
	private List<TempChannel> tempChannels;
	private List<TrackTuningHelper> trackTuningHelpers;
	private GMChannelRouter channelRouter;
	private MidiSettings settings;
	private TGFactory factory;
	
	public MidiSongReader() {
		super();
	}
	
	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		try {
			this.factory = handle.getFactory();
			this.settings = handle.getContext().getAttribute(MidiSettings.class.getName());
			if( this.settings == null ) {
				this.settings = MidiSettings.getDefaults();
			}
			
			MidiSequence sequence = new MidiFileReader().getSequence(handle.getInputStream());
			initFields(sequence);
			for(int i = 0; i < sequence.countTracks(); i++){
				MidiTrack track = sequence.getTrack(i);
				int trackNumber = getNextTrackNumber();
				int events = track.size();
				for(int j = 0;j < events;j ++){
					MidiEvent event = track.get(j);
					parseMessage(trackNumber,event.getTick(),event.getMessage());
				}
			}
			
			TGSongManager tgSongManager = new TGSongManager(this.factory);
			TGSong tgSong = this.factory.newSong();
			
			this.checkAll(tgSongManager);
			
			Iterator<TGChannel> channels = this.channels.iterator();
			while(channels.hasNext()){
				tgSong.addChannel((TGChannel)channels.next());
			}
			Iterator<TGMeasureHeader> headers = this.headers.iterator();
			while(headers.hasNext()){
				tgSong.addMeasureHeader((TGMeasureHeader)headers.next());
			}
			Iterator<TGTrack> tracks = this.tracks.iterator();
			while(tracks.hasNext()){
				tgSong.addTrack((TGTrack)tracks.next());
			}
			
			handle.setSong(new SongAdjuster(this.factory, tgSong).adjustSong());
		} catch (Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
	}
	
	private void initFields(MidiSequence sequence){
		this.resolution = sequence.getResolution();
		this.channels = new ArrayList<TGChannel>();
		this.headers = new ArrayList<TGMeasureHeader>();
		this.tracks = new ArrayList<TGTrack>();
		this.tempNotes = new ArrayList<TempNote>();
		this.tempChannels = new ArrayList<TempChannel>();
		this.trackTuningHelpers = new ArrayList<TrackTuningHelper>();
		this.channelRouter = new GMChannelRouter();
	}
	
	private int getNextTrackNumber(){
		return (this.tracks.size() + 1);
	}
	
	private void parseMessage(int trackNumber,long tick,MidiMessage message){
		long parsedTick = parseTick(tick + this.resolution);
		
		//NOTE ON
		if(message.getType() == MidiMessage.TYPE_SHORT && message.getCommand() == MidiMessage.NOTE_ON){
			parseNoteOn(trackNumber,parsedTick,message.getData());
		}
		//NOTE OFF
		else if(message.getType() == MidiMessage.TYPE_SHORT && message.getCommand() == MidiMessage.NOTE_OFF){
			parseNoteOff(trackNumber,parsedTick,message.getData());
		}
		//PROGRAM CHANGE
		else if(message.getType() == MidiMessage.TYPE_SHORT && message.getCommand() == MidiMessage.PROGRAM_CHANGE){
			parseProgramChange(message.getData());
		}
		//CONTROL CHANGE
		else if(message.getType() == MidiMessage.TYPE_SHORT && message.getCommand() == MidiMessage.CONTROL_CHANGE){
			parseControlChange(message.getData());
		}
		//TIME SIGNATURE
		else if(message.getType() == MidiMessage.TYPE_META && message.getCommand() == MidiMessage.TIME_SIGNATURE_CHANGE){
			parseTimeSignature(parsedTick,message.getData());
		}
		//TEMPO
		else if(message.getType() == MidiMessage.TYPE_META && message.getCommand() == MidiMessage.TEMPO_CHANGE){
			parseTempo(parsedTick,message.getData());
		}
	}
	
	private long parseTick(long tick){
		return Math.abs(TGDuration.QUARTER_TIME * tick / this.resolution);
	}
	
	private void parseNoteOn(int track,long tick,byte[] data){
		int length = data.length;
		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):0;
		int value = (length > 1)?(data[1] & 0xFF):0;
		int velocity = (length > 2)?(data[2] & 0xFF):0;
		if(velocity == 0){
			parseNoteOff(track,tick,data);
		}else if(value > 0){
			makeTempNotesBefore(tick,track);
			getTempChannel(channel).setTrack(track);
			getTrackTuningHelper(track).checkValue(value);
			this.tempNotes.add(new TempNote(track,channel,value,tick));
		}
	}
	
	private void parseNoteOff(int track,long tick,byte[] data){
		int length = data.length;
		
		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):0;
		int value = (length > 1)?(data[1] & 0xFF):0;
		
		makeNote(tick,track,channel,value);
	}
	
	private void parseProgramChange(byte[] data){
		int length = data.length;
		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):-1;
		int instrument = (length > 1)?(data[1] & 0xFF):-1;
		if(channel != -1 && instrument != -1){
			getTempChannel(channel).setInstrument(instrument);
		}
	}
	
	private void parseControlChange(byte[] data){
		int length = data.length;
		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):-1;
		int control = (length > 1)?(data[1] & 0xFF):-1;
		int value = (length > 2)?(data[2] & 0xFF):-1;
		if(channel != -1 && control != -1 && value != -1){
			if(control == MidiControllers.VOLUME){
				getTempChannel(channel).setVolume(value);
			}
			else if(control == MidiControllers.BALANCE){
				getTempChannel(channel).setBalance(value);
			}
		}
	}
	
	private void parseTimeSignature(long tick,byte[] data){
		if(data.length >= 2){
			TGTimeSignature timeSignature = this.factory.newTimeSignature();
			timeSignature.setNumerator(data[0]);
			timeSignature.getDenominator().setValue(TGDuration.QUARTER);
			if (data[1] == 0) {
				timeSignature.getDenominator().setValue(TGDuration.WHOLE);
			} else if (data[1] == 1) {
				timeSignature.getDenominator().setValue(TGDuration.HALF);
			} else if (data[1] == 2) {
				timeSignature.getDenominator().setValue(TGDuration.QUARTER);
			} else if (data[1] == 3) {
				timeSignature.getDenominator().setValue(TGDuration.EIGHTH);
			} else if (data[1] == 4) {
				timeSignature.getDenominator().setValue(TGDuration.SIXTEENTH);
			} else if (data[1] == 5) {
				timeSignature.getDenominator().setValue(TGDuration.THIRTY_SECOND);
			}
			getHeader(tick).setTimeSignature(timeSignature);
		}
	}
	
	private void parseTempo(long tick,byte[] data){
		if(data.length >= 3){
			TGTempo tempo = TGTempo.fromUSQ(this.factory,(data[2] & 0xff) | ((data[1] & 0xff) << 8) | ((data[0] & 0xff) << 16));
			getHeader(tick).setTempo(tempo);
		}
	}
	
	private TGTrack getTrack(int number){
		Iterator<TGTrack> it = this.tracks.iterator();
		while(it.hasNext()){
			TGTrack track = (TGTrack)it.next();
			if(track.getNumber() == number){
				return track;
			}
		}
		
		TGTrack track = this.factory.newTrack();
		track.setNumber(number);
		track.setChannelId(-1);
		track.getColor().copyFrom(TGColor.RED);
		
		this.tracks.add(track);
		return track;
	}
	
	private TGMeasureHeader getHeader(long tick){
		long realTick = (tick >= TGDuration.QUARTER_TIME)?tick:TGDuration.QUARTER_TIME;
		
		Iterator<TGMeasureHeader> it = this.headers.iterator();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			if(realTick >= header.getStart() && realTick < header.getStart() + header.getLength()){
				return header;
			}
		}
		TGMeasureHeader last = getLastHeader();
		TGMeasureHeader header = this.factory.newHeader();
		header.setNumber((last != null)?last.getNumber() + 1:1);
		header.setStart((last != null)?(last.getStart() + last.getLength()):TGDuration.QUARTER_TIME);
		header.getTempo().setValue(  (last != null)?last.getTempo().getValue():120 );
		if(last != null){
			header.getTimeSignature().copyFrom(last.getTimeSignature());
		}else{
			header.getTimeSignature().setNumerator(4);
			header.getTimeSignature().getDenominator().setValue(TGDuration.QUARTER);
		}
		this.headers.add(header);
		
		if(realTick >= header.getStart() && realTick < header.getStart() + header.getLength()){
			return header;
		}
		return getHeader(realTick);
	}
	
	private TGMeasureHeader getLastHeader(){
		if(!this.headers.isEmpty()){
			return (TGMeasureHeader)this.headers.get(this.headers.size() - 1);
		}
		return null;
	}
	
	private TGMeasure getMeasure(TGTrack track,long tick){
		long realTick = (tick >= TGDuration.QUARTER_TIME)?tick:TGDuration.QUARTER_TIME;
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			if(realTick >= measure.getStart() && realTick < measure.getStart() + measure.getLength()){
				return measure;
			}
		}
		getHeader(realTick);
		for(int i = 0;i < this.headers.size();i++){
			boolean exist = false;
			TGMeasureHeader header = (TGMeasureHeader)this.headers.get(i);
			int measureCount = track.countMeasures();
			for(int j = 0;j < measureCount;j++){
				TGMeasure measure = track.getMeasure(j);
				if(measure.getHeader().equals(header)){
					exist = true;
				}
			}
			if(!exist){
				TGMeasure measure = this.factory.newMeasure(header);
				track.addMeasure(measure);
			}
		}
		return getMeasure(track,realTick);
	}
	
	private TGBeat getBeat(TGMeasure measure, long start){
		int beatCount = measure.countBeats();
		for( int i = 0 ; i < beatCount ; i ++){
			TGBeat beat = measure.getBeat( i );
			if( beat.getStart() == start){
				return beat;
			}
		}
		
		TGBeat beat = this.factory.newBeat();
		beat.setStart(start);
		measure.addBeat(beat);
		return beat;
	}
	
	private TempNote getTempNote(int track,int channel,int value,boolean purge){
		for(int i = 0;i < this.tempNotes.size();i ++){
			TempNote note = (TempNote)this.tempNotes.get(i);
			if(note.getTrack() == track && note.getChannel() == channel && note.getValue() == value){
				if(purge){
					this.tempNotes.remove(i);
				}
				return note;
			}
		}
		return null;
	}
	
	protected TrackTuningHelper getTrackTuningHelper(int track){
		Iterator<TrackTuningHelper> it = this.trackTuningHelpers.iterator();
		while(it.hasNext()){
			TrackTuningHelper helper = (TrackTuningHelper)it.next();
			if(helper.getTrack() == track){
				return helper;
			}
		}
		TrackTuningHelper helper = new TrackTuningHelper(track);
		this.trackTuningHelpers.add(helper);
		
		return helper;
	}
	
	private void makeTempNotesBefore(long tick,int track){
		long nextTick = tick;
		boolean check = true;
		while(check){
			check = false;
			for(int i = 0;i < this.tempNotes.size();i ++){
				TempNote note = (TempNote)this.tempNotes.get(i);
				if(note.getTick() < nextTick && note.getTrack() == track){
					nextTick = note.getTick() + (TGDuration.QUARTER_TIME * 5); //First beat + 4/4 measure;
					makeNote(nextTick,track,note.getChannel(),note.getValue());
					check = true;
					break;
				}
			}
		}
	}
	
	private void makeNote(long tick,int track,int channel,int value){
		TempNote tempNote = getTempNote(track,channel,value,true);
		if(tempNote != null){
			int nString = 0;
			int nValue = (tempNote.getValue() + this.settings.getTranspose());
			int nVelocity = 64;
			long nStart = tempNote.getTick();
			TGDuration minDuration = newDuration(MIN_DURATION_VALUE);
			TGDuration nDuration = TGDuration.fromTime(this.factory,tick - tempNote.getTick(),minDuration);
			
			TGMeasure measure = getMeasure(getTrack(track),tempNote.getTick());
			TGBeat beat = getBeat(measure, nStart);
			beat.getVoice(0).getDuration().copyFrom(nDuration);
			
			TGNote note = this.factory.newNote();
			note.setValue(nValue);
			note.setString(nString);
			note.setVelocity(nVelocity);
			
			beat.getVoice(0).addNote(note);
		}
	}
	
	public TempChannel getTempChannel(int channel){
		Iterator<TempChannel> it = this.tempChannels.iterator();
		while(it.hasNext()){
			TempChannel tempChannel = (TempChannel)it.next();
			if(tempChannel.getChannel() == channel){
				return tempChannel;
			}
		}
		TempChannel tempChannel = new TempChannel(channel);
		this.tempChannels.add(tempChannel);
		
		return tempChannel;
	}
	
	private void checkAll(TGSongManager songManager) throws Exception{
		checkChannels();
		checkTracks(songManager);
		
		int headerCount = this.headers.size();
		for(int i = 0;i < this.tracks.size();i ++){
			TGTrack track = (TGTrack)this.tracks.get(i);
			
			while(track.countMeasures() < headerCount){
				long start = TGDuration.QUARTER_TIME;
				TGMeasure lastMeasure = ((track.countMeasures() > 0)?track.getMeasure(track.countMeasures() - 1) :null);
				if(lastMeasure != null){
					start = (lastMeasure.getStart() + lastMeasure.getLength());
				}
				
				track.addMeasure(this.factory.newMeasure(getHeader(start)));
			}
		}
		
		if(this.headers.isEmpty() || this.tracks.isEmpty()){
			throw new Exception("Empty Song");
		}
	}
	
	private void checkChannels(){
		for(int tc = 0 ; tc < this.tempChannels.size() ; tc ++ ){
			TempChannel tempChannel = (TempChannel)this.tempChannels.get( tc );
			if( tempChannel.getTrack() > 0 ){
				boolean channelExists = false;
				for(int c = 0 ; c < this.channels.size() ; c ++ ){
					TGChannel tgChannel = (TGChannel) this.channels.get(c);
					GMChannelRoute gmChannelRoute = this.channelRouter.getRoute(tgChannel.getChannelId());
					if( gmChannelRoute != null ){
						if( gmChannelRoute.getChannel1() == tempChannel.getChannel() || gmChannelRoute.getChannel2() == tempChannel.getChannel() ){
							channelExists = true;
						}
					}
				}
				
				if(!channelExists){
					TGChannel tgChannel = this.factory.newChannel();
					tgChannel.setChannelId(this.channels.size() + 1);
					tgChannel.setProgram((short)tempChannel.getInstrument());
					tgChannel.setVolume((short)tempChannel.getVolume());
					tgChannel.setBalance((short)tempChannel.getBalance());
					tgChannel.setName(("#" + tgChannel.getChannelId()));
					tgChannel.setBank(tempChannel.getChannel() == 9 ? TGChannel.DEFAULT_PERCUSSION_BANK : TGChannel.DEFAULT_BANK);
					
					GMChannelRoute gmChannelRoute = new GMChannelRoute(tgChannel.getChannelId());
					gmChannelRoute.setChannel1(tempChannel.getChannel());
					gmChannelRoute.setChannel2(tempChannel.getChannel());
					
					for(int tcAux = (tc + 1) ; tcAux < this.tempChannels.size() ; tcAux ++ ){
						TempChannel tempChannelAux = (TempChannel)this.tempChannels.get( tcAux );
						if( tempChannel.getTrack() == tempChannelAux.getTrack() ){
							if( gmChannelRoute.getChannel2() == gmChannelRoute.getChannel1() ){
								gmChannelRoute.setChannel2( tempChannelAux.getChannel() );
							}else{
								tempChannelAux.setTrack(-1);
							}
						}
					}
					
					this.channelRouter.configureRoutes(gmChannelRoute, (tempChannel.getChannel() == 9));
					this.channels.add( tgChannel );
				}
			}
		}
	}
	
	private void checkTracks(TGSongManager songManager){
		Iterator<TGTrack> it = this.tracks.iterator();
		while(it.hasNext()){
			TGTrack track = (TGTrack)it.next();
			TGChannel trackChannel = null;
			
			Iterator<TempChannel> tcIt = this.tempChannels.iterator();
			while(tcIt.hasNext()){
				TempChannel tempChannel = (TempChannel)tcIt.next();
				if( tempChannel.getTrack() == track.getNumber() ){
					Iterator<TGChannel> channelIt = this.channels.iterator();
					while( channelIt.hasNext() ){
						TGChannel tgChannel = (TGChannel)channelIt.next();
						GMChannelRoute gmChannelRoute = this.channelRouter.getRoute(tgChannel.getChannelId());
						if( gmChannelRoute != null && tempChannel.getChannel() == gmChannelRoute.getChannel1() ){
							trackChannel = tgChannel;
						}
					}
				}
			}
			if( trackChannel != null ){
				track.setChannelId( trackChannel.getChannelId() );
			}
			if( trackChannel != null && trackChannel.isPercussionChannel() ){
				track.setStrings(songManager.createPercussionStrings(6));
			}else{
				track.setStrings(getTrackTuningHelper(track.getNumber()).getStrings());
			}
		}
	}
	
	protected TGDuration newDuration(int value){
		TGDuration duration = this.factory.newDuration();
		duration.setValue(value);
		return duration;
	}
	
	private class TempNote{
		private int track;
		private int channel;
		private int value;
		private long tick;
		
		public TempNote(int track, int channel, int value,long tick) {
			this.track = track;
			this.channel = channel;
			this.value = value;
			this.tick = tick;
		}
		
		public int getChannel() {
			return this.channel;
		}
		
		public long getTick() {
			return this.tick;
		}
		
		public int getTrack() {
			return this.track;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	private class TempChannel{
		private int channel;
		private int instrument;
		private int volume;
		private int balance;
		private int track;
		
		public TempChannel(int channel) {
			this.channel = channel;
			this.instrument = 0;
			this.volume = 127;
			this.balance = 64;
			this.track = -1;
		}
		
		public int getBalance() {
			return this.balance;
		}
		
		public void setBalance(int balance) {
			this.balance = balance;
		}
		
		public int getChannel() {
			return this.channel;
		}
		
		public int getInstrument() {
			return this.instrument;
		}
		
		public void setInstrument(int instrument) {
			this.instrument = instrument;
		}
		
		public int getTrack() {
			return this.track;
		}
		
		public void setTrack(int track) {
			this.track = track;
		}
		
		public int getVolume() {
			return this.volume;
		}
		
		public void setVolume(int volume) {
			this.volume = volume;
		}
		
	}
	
	private class TrackTuningHelper{
		private int track;
		private int maxValue;
		private int minValue;
		
		public TrackTuningHelper(int track){
			this.track = track;
			this.maxValue = -1;
			this.minValue = -1;
		}
		
		public void checkValue(int value){
			if(this.minValue < 0 || value < this.minValue){
				this.minValue = value;
			}
			if(this.maxValue < 0 || value > this.maxValue){
				this.maxValue = value;
			}
		}
		
		public List<TGString> getStrings() {
			List<TGString> strings = new ArrayList<TGString>();
			
			int maxFret = 24;
			
			if(this.minValue >= 40 && this.maxValue <= 64 + maxFret){
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,1, 64));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,2, 59));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,3, 55));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,4, 50));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,5, 45));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,6, 40));
			}
			else if(this.minValue >= 38 && this.maxValue <= 64 + maxFret){
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,1, 64));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,2, 59));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,3, 55));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,4, 50));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,5, 45));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,6, 38));
			}
			else if(this.minValue >= 35 && this.maxValue <= 64 + maxFret){
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,1, 64));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,2, 59));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,3, 55));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,4, 50));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,5, 45));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,6, 40));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,7, 35));
			}
			else if(this.minValue >= 28 && this.maxValue <= 43 + maxFret){
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,1, 43));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,2, 38));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,3, 33));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,4, 28));
			}
			else if(this.minValue >= 23 && this.maxValue <= 43 + maxFret){
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,1, 43));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,2, 38));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,3, 33));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,4, 28));
				strings.add(TGSongManager.newString(MidiSongReader.this.factory,5, 23));
			}else{
				int stringCount = 6;
				int stringSpacing = ((this.maxValue - (maxFret - 4) - this.minValue) / stringCount);
				if(stringSpacing > 5){
					stringCount = 7;
					stringSpacing = ((this.maxValue - (maxFret - 4) - this.minValue) / stringCount);
				}
				
				int maxStringValue = (this.minValue + (stringCount * stringSpacing));
				while(strings.size() < stringCount){
					maxStringValue -= stringSpacing;
					strings.add(TGSongManager.newString(MidiSongReader.this.factory,strings.size() + 1,maxStringValue));
				}
			}
			
			return strings;
		}
		
		public int getTrack() {
			return this.track;
		}
		
	}
}

class SongAdjuster{
	private TGFactory factory;
	private TGSong song;
	private long minDurationTime;
	
	public SongAdjuster(TGFactory factory,TGSong song){
		this.factory = factory;
		this.song = song;
		this.minDurationTime = 40;
	}
	
	public TGSong adjustSong(){
		Iterator<TGTrack> it = this.song.getTracks();
		
		while(it.hasNext()){
			TGTrack track = (TGTrack)it.next();
			adjustTrack(track);
		}
		return this.song;
	}
	
	private void adjustTrack(TGTrack track){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			process(measure);
		}
	}
	
	public void process(TGMeasure measure){
		orderBeats(measure);
		joinBeats(measure);
		adjustStrings(measure);
	}
	
	public void joinBeats(TGMeasure measure){
		TGBeat previous = null;
		boolean finish = true;
		
		long measureStart = measure.getStart();
		long measureEnd = (measureStart + measure.getLength());
		for(int i = 0;i < measure.countBeats();i++){
			TGBeat beat = measure.getBeat( i );
			long beatStart = beat.getStart();
			long beatLength = beat.getVoice(0).getDuration().getTime();
			if(previous != null){
				long previousStart = previous.getStart();
				long previousLength = previous.getVoice(0).getDuration().getTime();
				
				//if(previousStart == beatStart){
				if(beatStart >= previousStart && (previousStart + this.minDurationTime) > beatStart ){
					// add beat notes to previous
					for(int n = 0;n < beat.getVoice(0).countNotes();n++){
						TGNote note = beat.getVoice(0).getNote( n );
						previous.getVoice(0).addNote( note );
					}
					
					// add beat chord to previous
					if(!previous.isChordBeat() && beat.isChordBeat()){
						previous.setChord( beat.getChord() );
					}
					
					// add beat text to previous
					if(!previous.isTextBeat() && beat.isTextBeat()){
						previous.setText( beat.getText() );
					}
					
					// set the best duration
					if(beatLength > previousLength && (beatStart + beatLength) <= measureEnd){
						previous.getVoice(0).getDuration().copyFrom(beat.getVoice(0).getDuration());
					}
					
					measure.removeBeat(beat);
					finish = false;
					break;
				}
				
				else if(previousStart < beatStart && (previousStart + previousLength) > beatStart){
					if(beat.getVoice(0).isRestVoice()){
						measure.removeBeat(beat);
						finish = false;
						break;
					}
					TGDuration duration = TGDuration.fromTime(this.factory, (beatStart - previousStart) );
					previous.getVoice(0).getDuration().copyFrom( duration );
				}
			}
			if( (beatStart + beatLength) > measureEnd ){
				if(beat.getVoice(0).isRestVoice()){
					measure.removeBeat(beat);
					finish = false;
					break;
				}
				TGDuration duration = TGDuration.fromTime(this.factory, (measureEnd - beatStart) );
				beat.getVoice(0).getDuration().copyFrom( duration );
			}
			
			previous = beat;
		}
		if(!finish){
			joinBeats(measure);
		}
	}
	
	public void orderBeats(TGMeasure measure){
		for(int i = 0;i < measure.countBeats();i++){
			TGBeat minBeat = null;
			for(int j = i;j < measure.countBeats();j++){
				TGBeat beat = measure.getBeat(j);
				if(minBeat == null || beat.getStart() < minBeat.getStart()){
					minBeat = beat;
				}
			}
			measure.moveBeat(i, minBeat);
		}
	}
	
	private void adjustStrings(TGMeasure measure){
		for(int i = 0;i < measure.countBeats();i++){
			TGBeat beat = measure.getBeat( i );
			adjustStrings(beat);
		}
	}
	
	private void adjustStrings(TGBeat beat){
		TGTrack track = beat.getMeasure().getTrack();
		List<TGString> freeStrings = new ArrayList<TGString>( track.getStrings() );
		List<TGNote> notesToRemove = new ArrayList<TGNote>();
		
		//ajusto las cuerdas
		Iterator<TGNote> it = beat.getVoice(0).getNotes().iterator();
		while(it.hasNext()){
			TGNote note = (TGNote)it.next();
			
			int string = getStringForValue(freeStrings,note.getValue());
			for(int j = 0;j < freeStrings.size();j ++){
				TGString tempString = (TGString)freeStrings.get(j);
				if(tempString.getNumber() == string){
					note.setValue(note.getValue() - tempString.getValue());
					note.setString(tempString.getNumber());
					freeStrings.remove(j);
					break;
				}
			}
			
			//Cannot have more notes on same string 
			if(note.getString() < 1){
				notesToRemove.add( note );
			}
		}
		
		// Remove notes
		while( notesToRemove.size() > 0 ){
			beat.getVoice(0).removeNote( (TGNote)notesToRemove.get( 0 ) );
			notesToRemove.remove( 0 );
		}
	}
	
	private int getStringForValue(List<TGString> strings,int value){
		int minFret = -1;
		int stringForValue = 0;
		for(int i = 0;i < strings.size();i++){
			TGString string = (TGString)strings.get(i);
			int fret = value - string.getValue();
			if(minFret < 0 || (fret >= 0 && fret < minFret)){
				stringForValue = string.getNumber();
				minFret = fret;
			}
		}
		return stringForValue;
	}
}