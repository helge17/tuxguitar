package app.tuxguitar.io.midi;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import app.tuxguitar.gm.GMChannelRoute;
import app.tuxguitar.gm.GMChannelRouter;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.io.midi.base.MidiEvent;
import app.tuxguitar.io.midi.base.MidiMessage;
import app.tuxguitar.io.midi.base.MidiSequence;
import app.tuxguitar.io.midi.base.MidiTrack;
import app.tuxguitar.player.base.MidiControllers;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.helpers.tuning.TuningManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGTuning;
import app.tuxguitar.util.TGContext;

public class MidiSongReader extends MidiFileFormat implements TGSongReader {

	private int resolution;
	private List<TGChannel> channels;
	private List<TGMeasureHeader> headers;
	private List<TGTrack> tracks;
	private List<TempNote> tempNotes; // while parsing a track, the list of notes that have started but not yet finished
	private long lastBeatEnd; // while parsing a track, the tick of the end of the last created beat = the start of the next one to create
	private List<TempChannel> tempChannels;
	private List<TrackTuningHelper> trackTuningHelpers;
	private GMChannelRouter channelRouter;
	private MidiSettings settings;
	private TGFactory factory;
	private String sequenceName = "";
	private TGSongManager tgSongManager;
	private TGContext context;

	public MidiSongReader(TGContext context) {
		super();
		this.context = context;
	}

	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		this.settings = null;
		try {
			this.settings = handle.getContext().getAttribute(MidiSettings.class.getName());
		}
		catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		if( this.settings == null ) {
			this.settings = MidiSettings.getDefaults();
		}

		try {
			this.factory = handle.getFactory();

			MidiSequence sequence = new MidiFileReader().getSequence(handle.getInputStream());
			initFields(sequence);
			for(int seqTrackNb = 0; seqTrackNb < sequence.countTracks(); seqTrackNb++){
				this.lastBeatEnd = TGDuration.QUARTER_TIME;
				this.tempNotes.clear();
				MidiTrack track = sequence.getTrack(seqTrackNb);
				int trackNumber = getNextTrackNumber();
				int eventsCount = track.size();
				// group simultaneous events in a list, then process the list
				long previousTick = -1;
				List<MidiEvent> tickEvents = new ArrayList<MidiEvent>();
				for(int eventNb = 0;eventNb < eventsCount;eventNb ++){
					MidiEvent event = track.get(eventNb);
					if (event.getTick() > previousTick) {
						parseEvents(tickEvents, seqTrackNb, trackNumber);
					}
					tickEvents.add(event);
					previousTick = event.getTick();
				}
				parseEvents(tickEvents, seqTrackNb, trackNumber);
			}

			tgSongManager = new TGSongManager(this.factory);
			TGSong tgSong = this.factory.newSong();
			tgSong.setName(sequenceName);

			this.checkAll(tgSongManager);

			Iterator<TGChannel> channels = this.channels.iterator();
			while(channels.hasNext()){
				tgSong.addChannel(channels.next());
			}
			Iterator<TGMeasureHeader> headers = this.headers.iterator();
			while(headers.hasNext()){
				tgSong.addMeasureHeader(headers.next());
			}
			Iterator<TGTrack> tracks = this.tracks.iterator();
			while(tracks.hasNext()){
				tgSong.addTrack(tracks.next());
			}

			handle.setSong(new SongAdjuster(this.factory, tgSong).adjustSong());
		} catch (Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
	}

	// parse list of simultaneous events: parse "note off" events first, to avoid creating false notes
	private void parseEvents(List<MidiEvent> events, int seqTrackNb, int trackNumber) {
		List<MidiEvent> handledEvents = new ArrayList<MidiEvent>();
		// parse noteOff events
		for (MidiEvent event : events) {
			if (isNoteOff(event)) {
				parseMessage(seqTrackNb,trackNumber,event.getTick(),event.getMessage());
				handledEvents.add(event);
			}
		}
		// cleanup list
		for (MidiEvent event : handledEvents) {
			events.remove(event);
		}
		// parse remaining events
		for (MidiEvent event : events) {
			parseMessage(seqTrackNb,trackNumber,event.getTick(),event.getMessage());
		}
		events.clear();
	}

	private boolean isNoteOff(MidiEvent event) {
		MidiMessage message = event.getMessage();
		if(message.getType() == MidiMessage.TYPE_SHORT && message.getCommand() == MidiMessage.NOTE_OFF) {
			return(true);
		}
		if(message.getType() == MidiMessage.TYPE_SHORT && message.getCommand() == MidiMessage.NOTE_ON){
			int length = message.getData().length;
			int velocity = (length > 2)?(message.getData()[2] & 0xFF):0;
			return(velocity == 0);
		}
		return(false);
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

	private void parseMessage(int trackIdx,int trackNumber,long tick,MidiMessage message){
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
		//TRACK NAME
		else if(message.getType() == MidiMessage.TYPE_META && message.getCommand() == MidiMessage.TRACK_NAME){
			parseTrackName(trackIdx, trackNumber, message.getData());
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

	private void parseNoteOn(int track, long tick, byte[] data){
		int length = data.length;
		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):0;
		int value = (length > 1)?(data[1] & 0xFF):0;
		int velocity = (length > 2)?(data[2] & 0xFF):0;
		if(velocity == 0){
			parseNoteOff(track,tick,data);
		}else if(value > 0){
			createTempNotesBefore(tick,track);
			getTempChannel(channel).addTrack(track);
			getTrackTuningHelper(track).checkValue(value + this.settings.getTranspose());
			this.tempNotes.add(new TempNote(channel,value));
		}
	}

	private void parseNoteOff(int track, long tick, byte[] data){
		int length = data.length;

		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):0;
		int value = (length > 1)?(data[1] & 0xFF):0;

		TempNote tempNote = getTempNote(channel, value);
		createTempNotesBefore(tick, track);
		this.tempNotes.remove(tempNote);
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

	/**
	 * If in a format 0 track, or the first track in a format 1 file, the name of the sequence.
	 * Otherwise, the name of the track.
	 */
	private void parseTrackName(int trackIdx, int trackNumber, byte[] data) {
		String name = new String(data, Charset.forName("UTF-8"));
		if (trackIdx == 0) {
			this.sequenceName = name;
		} else {
			TGTrack track = getTrack(trackNumber);
			track.setName(name);
		}
	}

	private void parseTimeSignature(long tick, byte[] data){
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

	private void parseTempo(long tick, byte[] data){
		if(data.length >= 3){
			TGTempo tempo = TGTempo.fromUSQ(this.factory,(data[2] & 0xff) | ((data[1] & 0xff) << 8) | ((data[0] & 0xff) << 16));
			getHeader(tick).setTempo(tempo);
		}
	}

	private TGTrack getTrack(int number){
		Iterator<TGTrack> it = this.tracks.iterator();
		while(it.hasNext()){
			TGTrack track = it.next();
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
			TGMeasureHeader header = it.next();
			if(realTick >= header.getStart() && realTick < header.getStart() + header.getLength()){
				return header;
			}
		}
		TGMeasureHeader last = getLastHeader();
		TGMeasureHeader header = this.factory.newHeader();
		header.setNumber((last != null)?last.getNumber() + 1:1);
		header.setStart((last != null)?(last.getStart() + last.getLength()):TGDuration.QUARTER_TIME);
		header.getTempo().setQuarterValue(  (last != null)?last.getTempo().getQuarterValue():120 );
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
			return this.headers.get(this.headers.size() - 1);
		}
		return null;
	}

	private TGMeasure getMeasure(TGTrack track, long tick){
		long realTick = (tick >= TGDuration.QUARTER_TIME)?tick:TGDuration.QUARTER_TIME;
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = it.next();
			if(realTick >= measure.getStart() && realTick < measure.getStart() + measure.getLength()){
				return measure;
			}
		}
		getHeader(realTick);
		for(int i = 0;i < this.headers.size();i++){
			boolean exist = false;
			TGMeasureHeader header = this.headers.get(i);
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

	@SuppressWarnings({"deprecation"})	// for beat.setStart, can't use preciseStart from an imprecise midi input
	private TGBeat getBeat(TGMeasure measure, long start){
		if (start< measure.getStart()) {
			start = measure.getStart();
		}
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

	private TempNote getTempNote(int channel, int value){
		for(int i = 0;i < this.tempNotes.size();i ++){
			TempNote note = this.tempNotes.get(i);
			if( note.getChannel() == channel && note.getValue() == value ){
				return note;
			}
		}
		return null;
	}

	protected TrackTuningHelper getTrackTuningHelper(int track){
		Iterator<TrackTuningHelper> it = this.trackTuningHelpers.iterator();
		while(it.hasNext()){
			TrackTuningHelper helper = it.next();
			if(helper.getTrack() == track){
				return helper;
			}
		}
		TrackTuningHelper helper = new TrackTuningHelper(track);
		this.trackTuningHelpers.add(helper);

		return helper;
	}

	// fill the space before nEnd
	// with all notes in temporary notes list
	// or with rests if this list is empty
	private void createTempNotesBefore(long nEnd, int track){
		boolean done = false;
		while (!done) {
			TGMeasure measure = getMeasure(getTrack(track), this.lastBeatEnd);
			long measureEnd = measure.getStart() + measure.getLength();
			long coarseTimeToFill = Math.min(nEnd, measureEnd) - this.lastBeatEnd;
			long preciseTimeToFill = TGDuration.toPreciseTime(coarseTimeToFill);
			// try to split exactly
			List<TGDuration> durationList = TGDuration.splitPreciseDuration(preciseTimeToFill, measure.getPreciseLength(), factory,
					MidiSongReader.this.settings.getMaxDurationValue(),
					MidiSongReader.this.settings.getMaxDivision());
			// if it failed, split approximately
			if (durationList == null) {
				durationList = TGDuration.splitPreciseDurationApproximately(preciseTimeToFill, measure.getPreciseLength(), factory,
					MidiSongReader.this.settings.getMaxDurationValue(),
					MidiSongReader.this.settings.getMaxDivision());
			}
			for (TGDuration duration : durationList) {
				long beatEnd = this.lastBeatEnd + duration.getTime();
				if (beatEnd <= measureEnd) {
					TGBeat beat = getBeat(measure, this.lastBeatEnd);
					beat.getVoice(0).getDuration().copyFrom(duration);
					if (this.tempNotes.isEmpty()) {
						// rest beat
						beat.getVoice(0).setEmpty(false);
					}
					else {
						// fill with all notes
						for (TempNote tempNote : this.tempNotes) {
							int nValue = (tempNote.getValue() + this.settings.getTranspose());
							int nVelocity = 64;
							TGNote note = this.factory.newNote();
							note.setValue(nValue);
							note.setString(1);
							note.setVelocity(nVelocity);
							note.setTiedNote(tempNote.shallBeTied());
							tempNote.setShallBeTied();
							beat.getVoice(0).addNote(note);
						}
					}
					this.lastBeatEnd = beatEnd;
				}
				else {
					this.lastBeatEnd = measureEnd;
				}
			}
			done = (nEnd <= measureEnd);
		}
	}

	public TempChannel getTempChannel(int channel){
		Iterator<TempChannel> it = this.tempChannels.iterator();
		while(it.hasNext()){
			TempChannel tempChannel = it.next();
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
			TGTrack track = this.tracks.get(i);

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
			TempChannel tempChannel = this.tempChannels.get( tc );
			if( !tempChannel.getTracks().isEmpty() ){
				boolean channelExists = false;
				for(int c = 0 ; c < this.channels.size() ; c ++ ){
					TGChannel tgChannel = this.channels.get(c);
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
						TempChannel tempChannelAux = this.tempChannels.get( tcAux );
						if( tempChannel.getTracks().equals(tempChannelAux.getTracks()) ){
							if( gmChannelRoute.getChannel2() == gmChannelRoute.getChannel1() ){
								gmChannelRoute.setChannel2( tempChannelAux.getChannel() );
							}else{
								tempChannelAux.clearTracks();
							}
						}
					}

					this.channelRouter.configureRoutes(gmChannelRoute, (tempChannel.getChannel() == 9));

					TGChannelParameter gmChannel1Param = this.factory.newChannelParameter();
					TGChannelParameter gmChannel2Param = this.factory.newChannelParameter();

					gmChannel1Param.setKey(GMChannelRoute.PARAMETER_GM_CHANNEL_1);
					gmChannel1Param.setValue(Integer.toString(gmChannelRoute.getChannel1()));

					gmChannel2Param.setKey(GMChannelRoute.PARAMETER_GM_CHANNEL_2);
					gmChannel2Param.setValue(Integer.toString(gmChannelRoute.getChannel2()));

					tgChannel.addParameter(gmChannel1Param);
					tgChannel.addParameter(gmChannel2Param);

					this.channels.add( tgChannel );
				}
			}
		}
	}

	private void checkTracks(TGSongManager songManager){
		for (TGTrack track : this.tracks) {
			TGChannel trackChannel = null;

			for (TempChannel tempChannel : this.tempChannels) {
				if (tempChannel.getTracks().contains(track.getNumber())) {
					for (TGChannel tgChannel : this.channels) {
						GMChannelRoute gmChannelRoute = this.channelRouter.getRoute(tgChannel.getChannelId());
						if (gmChannelRoute != null && tempChannel.getChannel() == gmChannelRoute.getChannel1()) {
							trackChannel = tgChannel;
						}
					}
				}
			}

			if (trackChannel != null) {
				track.setChannelId(trackChannel.getChannelId());
			}
			if (trackChannel != null && trackChannel.isPercussionChannel()) {
				track.setStrings(songManager.createPercussionStrings(6));
			} else {
				track.setStrings(getTrackTuningHelper(track.getNumber()).getStrings(track.getMaxFret()));
			}
		}
	}

	private class TempNote{
		private int channel;
		private int value;
		private boolean shallBeTied;

		public TempNote(int channel, int value) {
			this.channel = channel;
			this.value = value;
			this.shallBeTied = false;
		}

		public int getChannel() {
			return this.channel;
		}

		public int getValue() {
			return this.value;
		}

		public void setShallBeTied() {
			this.shallBeTied = true;
		}

		public boolean shallBeTied() {
			return this.shallBeTied;
		}
	}

	private class TempChannel{
		private int channel;
		private int instrument;
		private int volume;
		private int balance;
		private Set<Integer> tracks;

		public TempChannel(int channel) {
			this.channel = channel;
			this.instrument = 0;
			this.volume = 127;
			this.balance = 64;
			this.tracks = new HashSet<Integer>();
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

		public Set<Integer> getTracks() {
			return this.tracks;
		}

		public void addTrack(int track) {
			this.tracks.add(track);
		}

		public void clearTracks() {
			this.tracks.clear();
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

		public List<TGString> getStrings(int maxFret) {
			List<TGString> strings = new ArrayList<TGString>();

			TuningManager tuningManager = TuningManager.getInstance(MidiSongReader.this.context);
			List<TGTuning> tunings = tuningManager.getPriorityTgTunings();

			for (TGTuning tuning : tunings) {
				if (tuning.isWithinRange(this.minValue, this.maxValue - maxFret)) {
					int [] notes = tuning.getValues();
					for (int i = 0; i < notes.length; i++)
						strings.add(TGSongManager.newString(MidiSongReader.this.factory, i+1, notes[i]));
					// if we find a tuning in range, we don't need to search any further, break out of the loop.
					break;
				}
			}

			if (strings.isEmpty()) {
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

	private class SongAdjuster {
		private TGFactory factory;
		private TGSong song;

		public SongAdjuster(TGFactory factory, TGSong song) {
			this.factory = factory;
			this.song = song;
		}

		public TGSong adjustSong() {
			Iterator<TGTrack> it = this.song.getTracks();

			while(it.hasNext()){
				TGTrack track = it.next();
				adjustTrack(track);
			}
			return this.song;
		}

		private void adjustTrack(TGTrack track) {
			Iterator<TGMeasure> it = track.getMeasures();
			while(it.hasNext()){
				TGMeasure measure = it.next();
				process(measure, track.isPercussion(), track.getMaxFret());
			}
			tgSongManager.getTrackManager().fixInvalidTiedNotes(track);
		}

		private void process(TGMeasure measure, boolean isPercussionTrack, int maxFret){
			tgSongManager.getMeasureManager().updateBeatsPreciseStart(measure);
			tgSongManager.getMeasureManager().autoCompleteSilences(measure);
			adjustStrings(measure, isPercussionTrack, maxFret);
		}

		private void adjustStrings(TGMeasure measure, boolean isPercussionTrack, int maxFret){
			TGString string = this.factory.newString();
			string.setNumber(1);
			string.setValue(0);
			List<Integer> strings = new ArrayList<Integer>();
			strings.add(string.getValue());
			if (isPercussionTrack) {
				tgSongManager.getTrackManager().allocatePercussionNotesToStrings(strings, measure.getBeats(), measure.getTrack().getStrings());
			} else {
				tgSongManager.getTrackManager().allocateNotesToStrings(strings, measure.getBeats(), measure.getTrack().getStrings(), maxFret);
			}
		}
	}
}
