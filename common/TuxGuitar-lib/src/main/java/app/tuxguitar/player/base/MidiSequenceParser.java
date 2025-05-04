package app.tuxguitar.player.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.song.models.effects.TGEffectBend.BendPoint;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar.TremoloBarPoint;

public class MidiSequenceParser {

	public static final int ADD_DEFAULT_CONTROLS = 0x01;
	public static final int ADD_MIXER_MESSAGES = 0x02;
	public static final int ADD_BANK_SELECT = 0x04;
	public static final int ADD_METRONOME = 0x08;
	public static final int ADD_FIRST_TICK_MOVE = 0x10;
	public static final int DEFAULT_PLAY_FLAGS = (ADD_METRONOME);
	public static final int DEFAULT_EXPORT_FLAGS = (ADD_FIRST_TICK_MOVE | ADD_DEFAULT_CONTROLS | ADD_MIXER_MESSAGES);

	private static final int DEFAULT_METRONOME_KEY = 37;
	private static final int DEFAULT_DURATION_PM = 60;
	private static final int DEFAULT_DURATION_DEAD = 30;
	private static final int DEFAULT_BEND = 64;
	private static final float DEFAULT_BEND_SEMI_TONE = 2.75f;

	private TGSong song;
	private TGSongManager songManager;
	private int flags;
	private int infoTrack;
	private int metronomeTrack;
	private int metronomeChannelId;
	private int firstTickMove;
	private int tempoPercent;
	private int transpose;
	private int sHeader;
	private int eHeader;
	private TreeMap<Long, TGTempo> tempoMap;	// ordered map of ticks where tempo changes
	private TreeMap<Long, Long> timestampMap;	// ordered map of timestamps (in ms) where tempo changes

	public MidiSequenceParser(TGSong song, TGSongManager songManager, int flags) {
		this.song = song;
		this.songManager = songManager;
		this.flags = flags;
		this.tempoPercent = 100;
		this.transpose = 0;
		this.sHeader = -1;
		this.eHeader = -1;
		this.firstTickMove = (int) ((flags & ADD_FIRST_TICK_MOVE) != 0 ? -TGDuration.QUARTER_TIME : 0);
		this.tempoMap = new TreeMap<Long, TGTempo>();
		this.timestampMap = new TreeMap<Long, Long>();
	}

	public int getInfoTrack(){
		return this.infoTrack;
	}

	public int getMetronomeTrack(){
		return this.metronomeTrack;
	}

	public TreeMap<Long, TGTempo> getTempoMap() {
		return this.tempoMap;
	}

	public TreeMap<Long, Long> getTimestampMap() {
		return this.timestampMap;
	}

	private long getTick(long tick){
		return (tick + this.firstTickMove);
	}

	public void setSHeader(int header) {
		this.sHeader = header;
	}

	public void setEHeader(int header) {
		this.eHeader = header;
	}

	public void setMetronomeChannelId(int metronomeChannelId) {
		this.metronomeChannelId = metronomeChannelId;
	}

	public void setTempoPercent(int tempoPercent) {
		this.tempoPercent = tempoPercent;
	}

	public void setTranspose(int transpose) {
		this.transpose = transpose;
	}

	public void parse(MidiSequenceHandler sequence) {
		this.infoTrack = 0;
		this.metronomeTrack = (sequence.getTracks() - 1);

		MidiSequenceHelper helper = new MidiSequenceHelper(sequence);
		MidiRepeatController controller = new MidiRepeatController(this.song, this.sHeader, this.eHeader);
		while(!controller.finished()){
			int index = controller.getIndex();
			long move = controller.getRepeatMove();
			controller.process();
			if( controller.shouldPlay()){
				helper.addMeasureHelper(new MidiMeasureHelper(index, move));
			}
		}

		this.addDefaultMessages(helper, this.song);

		boolean existsSoloTrack = false;
		for (int i = 0; i < this.song.countTracks(); i++) {
			existsSoloTrack |= this.song.getTrack(i).isSolo();
		}
		for (int i = 0; i < this.song.countTracks(); i++) {
			TGTrack track = this.song.getTrack(i);
			addTrack(helper, track, !track.isMute() && (!existsSoloTrack || track.isSolo()));
		}
		sequence.notifyFinish();
	}

	private void addDefaultMessages(MidiSequenceHelper sh, TGSong tgSong) {
		if( (this.flags & ADD_DEFAULT_CONTROLS) != 0) {
			Iterator<TGChannel> it = tgSong.getChannels();
			while ( it.hasNext() ){
				int channelId = it.next().getChannelId();
				sh.getSequence().addControlChange(getTick(TGDuration.QUARTER_TIME), getInfoTrack(), channelId, MidiControllers.RPN_MSB, 0);
				sh.getSequence().addControlChange(getTick(TGDuration.QUARTER_TIME), getInfoTrack(), channelId, MidiControllers.RPN_LSB, 0);
				sh.getSequence().addControlChange(getTick(TGDuration.QUARTER_TIME), getInfoTrack(), channelId, MidiControllers.DATA_ENTRY_MSB, 12);
				sh.getSequence().addControlChange(getTick(TGDuration.QUARTER_TIME), getInfoTrack(), channelId, MidiControllers.DATA_ENTRY_LSB, 0);
			}
		}
	}

	private void addTrack(MidiSequenceHelper sh, TGTrack track, boolean shallPlay) {
		TGChannel tgChannel = this.songManager.getChannel(this.song, track.getChannelId() );
		if( tgChannel != null ){
			TGMeasure previous = null;

			this.addBend(sh,track.getNumber(),TGDuration.QUARTER_TIME,DEFAULT_BEND, tgChannel.getChannelId(), -1, false);
			this.makeTrackChannel(sh, tgChannel, track);

			int mCount = sh.getMeasureHelpers().size();
			for( int mIndex = 0 ; mIndex < mCount ; mIndex++ ){
				MidiMeasureHelper mh = sh.getMeasureHelper( mIndex );

				TGMeasure measure = track.getMeasure(mh.getIndex());
				if(track.getNumber() == 1){
					addTimeSignature(sh,measure, previous, mh.getMove());
					addTempo(sh,measure, previous, mh.getMove());
					addMetronome(sh,measure.getHeader(), mh.getMove() );
				}
				if (shallPlay) {
					//agrego los pulsos
					addBeats( sh, tgChannel, track, measure, mIndex, mh.getMove() );
				}
				previous = measure;
			}
		}
	}

	private void addBeats(MidiSequenceHelper sh, TGChannel channel, TGTrack track, TGMeasure measure, int mIndex, long startMove) {
		int[] stroke = new int[track.stringCount()];
		TGBeat previous = null;
		for (int bIndex = 0; bIndex < measure.countBeats(); bIndex++) {
			TGBeat beat = measure.getBeat(bIndex);
			addNotes( sh, channel, track, beat, measure.getTempo(), mIndex, bIndex, startMove, getStroke(beat, previous, stroke) );
			previous = beat;
		}
	}

	private void addNotes( MidiSequenceHelper sh, TGChannel tgChannel, TGTrack track, TGBeat beat, TGTempo tempo, int mIndex,int bIndex, long startMove, int[] stroke) {
		for( int vIndex = 0; vIndex < beat.countVoices(); vIndex ++ ){
			TGVoice voice = beat.getVoice(vIndex);

			MidiTickHelper th = checkTripletFeel(voice,bIndex);
			for (int noteIdx = 0; noteIdx < voice.countNotes(); noteIdx++) {
				TGNote note = voice.getNote(noteIdx);
				if (!note.isTiedNote()) {
					int key = (this.transpose + track.getOffset() + note.getValue() + ((TGString)track.getStrings().get(note.getString() - 1)).getValue());

					long start = applyStrokeStart(note, (th.getStart() + startMove) , stroke);
					long duration = applyStrokeDuration(note, getRealNoteDuration(sh, track, note, tempo, th.getDuration(), mIndex,bIndex), stroke);

					int velocity = getRealVelocity(sh, note, track, tgChannel, mIndex, bIndex);
					int channel = tgChannel.getChannelId();
					int midiVoice = note.getString();
					boolean bendMode = false;

					boolean percussionChannel = tgChannel.isPercussionChannel();
					//---Fade In---
					if(note.getEffect().isFadeIn()){
						addFadeIn(sh,track.getNumber(), start, duration, tgChannel.getVolume(), channel);
					}
					//---Grace---
					if(note.getEffect().isGrace()) {
						bendMode = !percussionChannel;
						int graceKey = track.getOffset() + note.getEffect().getGrace().getFret() + ((TGString)track.getStrings().get(note.getString() - 1)).getValue();
						int graceLength = note.getEffect().getGrace().getDurationTime();
						int graceVelocity = note.getEffect().getGrace().getDynamic();
						long graceDuration = ((note.getEffect().getGrace().isDead())?applyStaticDuration(tempo, DEFAULT_DURATION_DEAD, graceLength):graceLength);

						if(note.getEffect().getGrace().isOnBeat() || (start - graceLength) < TGDuration.QUARTER_TIME){
							start += graceLength;
							duration -= graceLength;
						}
						addNote(sh,track.getNumber(), graceKey,start - graceLength,graceDuration,graceVelocity,channel,midiVoice, bendMode);

					}
					//---Trill---
					if(note.getEffect().isTrill() && !percussionChannel ){
						int trillKey = track.getOffset() + note.getEffect().getTrill().getFret() + ((TGString)track.getStrings().get(note.getString() - 1)).getValue();
						long trillLength = note.getEffect().getTrill().getDuration().getTime();

						boolean realKey = true;
						long tick = start;
						while(true){
							if(tick + 10 >= (start + duration)){
								break ;
							}else if( (tick + trillLength) >= (start + duration)){
								trillLength = (((start + duration) - tick) - 1);
							}
							addNote(sh,track.getNumber(),((realKey)?key:trillKey),tick,trillLength,velocity,channel,midiVoice,bendMode);
							realKey = (!realKey);
							tick += trillLength;
						}
						continue;
					}
					//---Tremolo Picking---
					if(note.getEffect().isTremoloPicking()){
						long tpLength = note.getEffect().getTremoloPicking().getDuration().getTime();
						long tick = start;
						while(true){
							if(tick + 10 >= (start + duration)){
								break ;
							}else if( (tick + tpLength) >= (start + duration)){
								tpLength = (((start + duration) - tick) - 1);
							}
							addNote(sh,track.getNumber(),key,tick,tpLength,velocity,channel,midiVoice,bendMode);
							tick += tpLength;
						}
						continue;
					}

					//---Bend---
					if( note.getEffect().isBend() && !percussionChannel ){
						bendMode = true;
						addBend(sh,track.getNumber(),start,duration,note.getEffect().getBend(),channel,midiVoice,bendMode);
					}
					//---TremoloBar---
					else if( note.getEffect().isTremoloBar() && !percussionChannel ){
						bendMode = true;
						addTremoloBar(sh,track.getNumber(),start,duration,note.getEffect().getTremoloBar(),channel,midiVoice,bendMode);
					}
					//---Slide---
					else if( note.getEffect().isSlide() && !percussionChannel){
						bendMode = true;
						addSlide(sh, note, track, mIndex, bIndex, startMove, channel,midiVoice,bendMode);
					}
					//---Vibrato---
					else if( note.getEffect().isVibrato() && !percussionChannel){
						bendMode = true;
						addVibrato(sh,track.getNumber(),start,duration,channel,midiVoice,bendMode);
					}
					//---Harmonic---
					if( note.getEffect().isHarmonic() && !percussionChannel){
						int orig = key;

						//Natural
						if(note.getEffect().getHarmonic().isNatural()){
							for(int i = 0;i < TGEffectHarmonic.NATURAL_FREQUENCIES.length;i ++){
								if((note.getValue() % 12) ==  (TGEffectHarmonic.NATURAL_FREQUENCIES[i][0] % 12) ){
									key = ((orig + TGEffectHarmonic.NATURAL_FREQUENCIES[i][1]) - note.getValue());
									break;
								}
							}
						}
						//Artificial/Tapped/Pinch/Semi
						else{
							if( note.getEffect().getHarmonic().isSemi() && !percussionChannel ){
								addNote(sh,track.getNumber(),Math.min(127,orig), start, duration,Math.max(TGVelocities.MIN_VELOCITY,velocity - (TGVelocities.VELOCITY_INCREMENT * 3)),channel,midiVoice,bendMode);
							}
							key = (orig + TGEffectHarmonic.NATURAL_FREQUENCIES[note.getEffect().getHarmonic().getData()][1]);

						}
						if( (key - 12) > 0 ){
							int hVelocity = Math.max(TGVelocities.MIN_VELOCITY,velocity - (TGVelocities.VELOCITY_INCREMENT * 4));
							addNote(sh,track.getNumber(),(key - 12), start, duration,hVelocity,channel,midiVoice,bendMode);
						}
					}

					//---Normal Note---
					addNote(sh,track.getNumber(), Math.min(127,key), start, duration, velocity,channel,midiVoice,bendMode);
				}
			}
		}
	}

	private void addNote(MidiSequenceHelper sh,int track, int key, long start, long duration, int velocity, int channel, int midiVoice, boolean bendMode) {
		sh.getSequence().addNoteOn(getTick(start),track,channel,fix(key),fix(velocity), midiVoice, bendMode);
		if( duration > 0 ){
			sh.getSequence().addNoteOff(getTick(start + duration),track,channel,fix(key),fix(velocity), midiVoice, bendMode);
		}
	}

	private void makeTrackChannel(MidiSequenceHelper sh, TGChannel channel, TGTrack track) {
		if((this.flags & ADD_MIXER_MESSAGES) != 0){
			int channelId = channel.getChannelId();
			long tick = getTick(TGDuration.QUARTER_TIME);
			int trackNum = track.getNumber();
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.VOLUME,fix(channel.getVolume()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.BALANCE,fix(channel.getBalance()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.CHORUS,fix(channel.getChorus()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.REVERB,fix(channel.getReverb()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.PHASER,fix(channel.getPhaser()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.TREMOLO,fix(channel.getTremolo()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.EXPRESSION, 127);
			if((this.flags & ADD_BANK_SELECT) != 0) {
				sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.BANK_SELECT, fix(channel.getBank(), 0, 128));
			}
			sh.getSequence().addProgramChange(tick,trackNum,channelId,fix(channel.getProgram()));
			sh.getSequence().addTrackName(tick,trackNum,track.getName());
		}
	}

	public void addMetronome(MidiSequenceHelper sh,TGMeasureHeader header, long startMove){
		if( (this.flags & ADD_METRONOME) != 0) {
			if( this.metronomeChannelId >= 0 ){
				long start = (startMove + header.getStart());
				long length = header.getTimeSignature().getDenominator().getTime();
				for(int i = 1; i <= header.getTimeSignature().getNumerator();i ++){
					addNote(sh,getMetronomeTrack(),DEFAULT_METRONOME_KEY,start,length,TGVelocities.DEFAULT,this.metronomeChannelId,-1,false);
					start += length;
				}
			}
		}
	}


	private void addBend(MidiSequenceHelper sh,int track, long tick,int bend, int channel, int midiVoice, boolean bendMode) {
		sh.getSequence().addPitchBend(getTick(tick), track, channel, fix(bend), midiVoice, bendMode);
	}

	public void addVibrato(MidiSequenceHelper sh,int track,long start, long duration,int channel, int midiVoice, boolean bendMode){
		long nextStart = start;
		long end = nextStart + duration;

		while(nextStart < end){
			nextStart = ((nextStart + 160 > end)?end:nextStart + 160);
			addBend(sh, track, nextStart, DEFAULT_BEND, channel, midiVoice, bendMode);
			nextStart = ((nextStart + 160 > end)?end:nextStart + 160);
			addBend(sh, track, nextStart, DEFAULT_BEND + (int) (DEFAULT_BEND_SEMI_TONE / 2.0f), channel, midiVoice, bendMode);
		}
		addBend(sh, track, nextStart, DEFAULT_BEND, channel, midiVoice, bendMode);
	}

	public void addBend(MidiSequenceHelper sh,int track,long start, long duration, TGEffectBend bend, int channel, int midiVoice, boolean bendMode){
		List<BendPoint> points = bend.getPoints();
		for(int i=0;i<points.size();i++){
			TGEffectBend.BendPoint point = (TGEffectBend.BendPoint)points.get(i);
			long bendStart = start + point.getTime(duration);
			int value = DEFAULT_BEND + (int) (point.getValue() * DEFAULT_BEND_SEMI_TONE / TGEffectBend.SEMITONE_LENGTH);
			value = ((value <= 127)?value:127);
			value = ((value >= 0)?value:0);
			addBend(sh, track, bendStart, value, channel, midiVoice, bendMode);

			if(points.size() > i + 1){
				TGEffectBend.BendPoint nextPoint = (TGEffectBend.BendPoint)points.get(i + 1);
				int nextValue = DEFAULT_BEND + (int) (nextPoint.getValue() * DEFAULT_BEND_SEMI_TONE / TGEffectBend.SEMITONE_LENGTH);
				long nextBendStart = start + nextPoint.getTime(duration);
				if(nextValue != value){
					double width = ( (nextBendStart - bendStart) / Math.abs(  (nextValue - value) ) );
					//ascendente
					if(value < nextValue){
						while(value < nextValue){
							value ++;
							bendStart +=width;
							addBend(sh, track, bendStart,((value <= 127) ? value : 127), channel, midiVoice, bendMode);
						}
						//descendente
					}else if(value > nextValue){
						while(value > nextValue){
							value --;
							bendStart +=width;
							addBend(sh, track, bendStart,((value >= 0) ? value : 0), channel, midiVoice, bendMode);
						}
					}
				}
			}
		}
		addBend(sh, track, start + duration, DEFAULT_BEND, channel, midiVoice, bendMode);
	}

	public void addTremoloBar(MidiSequenceHelper sh,int track,long start, long duration, TGEffectTremoloBar effect, int channel, int midiVoice, boolean bendMode){
		List<TremoloBarPoint> points = effect.getPoints();
		for(int i=0;i<points.size();i++){
			TGEffectTremoloBar.TremoloBarPoint point = (TGEffectTremoloBar.TremoloBarPoint)points.get(i);
			long pointStart = start + point.getTime(duration);
			int value = DEFAULT_BEND + (int) (point.getValue() * (DEFAULT_BEND_SEMI_TONE * 2) );
			value = ((value <= 127)?value:127);
			value = ((value >= 0)?value:0);
			addBend(sh, track, pointStart, value, channel, midiVoice, bendMode);
			if(points.size() > i + 1){
				TGEffectTremoloBar.TremoloBarPoint nextPoint = (TGEffectTremoloBar.TremoloBarPoint)points.get(i + 1);
				int nextValue = DEFAULT_BEND + (int) (nextPoint.getValue() * (DEFAULT_BEND_SEMI_TONE * 2));
				long nextPointStart = start + nextPoint.getTime(duration);
				if(nextValue != value){
					double width = ( (nextPointStart - pointStart) / Math.abs(  (nextValue - value) ) );
					//ascendente
					if(value < nextValue){
						while(value < nextValue){
							value ++;
							pointStart +=width;
							addBend(sh, track, pointStart,((value <= 127) ? value : 127), channel, midiVoice, bendMode);
						}
					//descendente
					}else if(value > nextValue){
						while(value > nextValue){
							value --;
							pointStart += width;
							addBend(sh, track, pointStart,((value >= 0) ? value : 0), channel, midiVoice, bendMode);
						}
					}
				}
			}
		}
		addBend(sh, track, start + duration, DEFAULT_BEND, channel, midiVoice, bendMode);
	}

	private void addSlide(MidiSequenceHelper sh, TGNote note, TGTrack track, int mIndex, int bIndex, long startMove ,int channel, int midiVoice, boolean bendMode){
		MidiNoteHelper nextNote = this.getNextNote(sh, note, track, mIndex, bIndex, true);
		if( nextNote != null ){
			int value1 = note.getValue();
			int value2 = nextNote.getNote().getValue();

			long tick1 = note.getVoice().getBeat().getStart() + startMove;
			long tick2 = nextNote.getNote().getVoice().getBeat().getStart() + nextNote.getMeasure().getMove();

			// Make the Slide
			this.addSlide(sh, track.getNumber(), tick1, value1, tick2, value2, channel, midiVoice, bendMode);
			// Normalize the Bend
			this.addBend(sh,track.getNumber(), tick2 ,DEFAULT_BEND, channel, midiVoice, bendMode);
		}
	}

	public void addSlide(MidiSequenceHelper sh,int track,long tick1,int value1,long tick2,int value2,int channel, int midiVoice, boolean bendMode){
		long distance = (value2 - value1);
		long length = (tick2 - tick1);
		int points = (int) (length / (TGDuration.QUARTER_TIME / 8));
		for(int i = 1;i <= points; i ++){
			float tone = ((((length / points) * (float) i) * distance) / length);
			int bend = (DEFAULT_BEND + (int) (tone * (DEFAULT_BEND_SEMI_TONE * 2)));
			addBend(sh, track, tick1 + ( (length / points) * i), bend, channel, midiVoice, bendMode);
		}
	}

	private void addFadeIn(MidiSequenceHelper sh,int track,long start,long duration,int volume3,int channel){
		int expression = 31;
		int expressionIncrement = 1;
		long tick = start;
		long tickIncrement = (duration / ((127 - expression) / expressionIncrement));
		while( tick < (start + duration) && expression < 127 ) {
			sh.getSequence().addControlChange(getTick(tick),track,channel,MidiControllers.EXPRESSION, fix(expression));
			tick += tickIncrement;
			expression += expressionIncrement;
		}
		sh.getSequence().addControlChange(getTick((start + duration)),track,channel, MidiControllers.EXPRESSION, 127);
	}

	private void addTimeSignature(MidiSequenceHelper sh, TGMeasure currMeasure, TGMeasure prevMeasure, long startMove){
		boolean addTimeSignature = false;
		if (prevMeasure == null) {
			addTimeSignature = true;
		} else {
			int currNumerator = currMeasure.getTimeSignature().getNumerator();
			int currValue = currMeasure.getTimeSignature().getDenominator().getValue();
			int prevNumerator = prevMeasure.getTimeSignature().getNumerator();
			int prevValue = prevMeasure.getTimeSignature().getDenominator().getValue();
			if (currNumerator != prevNumerator || currValue != prevValue) {
				addTimeSignature = true;
			}
		}
		if (addTimeSignature) {
			sh.getSequence().addTimeSignature(getTick(currMeasure.getStart() + startMove), getInfoTrack(), currMeasure.getTimeSignature());
		}
	}

	private void addTempo(MidiSequenceHelper sh,TGMeasure currMeasure, TGMeasure prevMeasure,long startMove){
		boolean addTempo = false;
		if (prevMeasure == null) {
			addTempo = true;
		} else {
			if (currMeasure.getTempo().getInUSQ() != prevMeasure.getTempo().getInUSQ()) {
				addTempo = true;
			}
		}
		if (addTempo) {
			long tick = getTick(currMeasure.getStart() + startMove);
			int usq = (int) (currMeasure.getTempo().getInUSQ() * 100.00 / this.tempoPercent );
			sh.getSequence().addTempoInUSQ(tick, getInfoTrack(), usq);
			if (this.timestampMap.isEmpty()) {
				this.timestampMap.put(tick, 0l);
			} else {
				long t = this.timestampMap.lastEntry().getValue();
				Map.Entry<Long,TGTempo> lastTempoEntry = this.tempoMap.lastEntry();
				t += lastTempoEntry.getValue().getTicksInMillis(tick - lastTempoEntry.getKey());
				this.timestampMap.put(tick,t);
			}
			this.tempoMap.put(tick, currMeasure.getTempo());
		}
	}

	private long getRealNoteDuration(MidiSequenceHelper sh, TGTrack track, TGNote note, TGTempo tempo, long duration,int mIndex, int bIndex) {
		boolean letRing = (note.getEffect().isLetRing());
		boolean letRingBeatChanged = false;
		long lastEnd = (note.getVoice().getBeat().getStart() + note.getVoice().getDuration().getTime() + sh.getMeasureHelper(mIndex).getMove());
		long realDuration = duration;
		int nextBIndex = (bIndex + 1);
		int mCount = sh.getMeasureHelpers().size();
		for (int m = mIndex; m < mCount; m++) {
			MidiMeasureHelper mh = sh.getMeasureHelper( m );
			TGMeasure measure = track.getMeasure( mh.getIndex() );

			int beatCount = measure.countBeats();
			for (int b = nextBIndex; b < beatCount; b++) {
				TGBeat beat = measure.getBeat(b);
				TGVoice voice = beat.getVoice(note.getVoice().getIndex());
				if(!voice.isEmpty()){
					if(voice.isRestVoice()){
						return applyDurationEffects(note, tempo, realDuration);
					}
					int noteCount = voice.countNotes();
					for (int n = 0; n < noteCount; n++) {
						TGNote nextNote = voice.getNote( n );
						if (!nextNote.equals(note) || mIndex != m ) {
							if (letRing) {
								// end of letRing chain?
								if (!nextNote.getEffect().isLetRing()) {
									if ((mIndex == m) || (nextNote.isTiedNote()) ) {
										realDuration += voice.getDuration().getTime();
									}
									return applyDurationEffects(note, tempo, realDuration);
								}
								if ((mIndex != m) && !(nextNote.isTiedNote())) {
									// measure changed, found a non tied note, end of letRing
									return applyDurationEffects(note, tempo, realDuration);
								}
							}
							if (nextNote.getString() == note.getString()) {
								if (nextNote.isTiedNote()) {
									realDuration += (mh.getMove() + beat.getStart() - lastEnd) + (nextNote.getVoice().getDuration().getTime());
									lastEnd = (mh.getMove() + beat.getStart() + voice.getDuration().getTime());
									letRing = (nextNote.getEffect().isLetRing());
									letRingBeatChanged = true;
								} else {
									return applyDurationEffects(note, tempo, realDuration);
								}
							}
						}
					}
					if(letRing && !letRingBeatChanged){
						realDuration += ( voice.getDuration().getTime() );
						lastEnd += voice.getDuration().getTime();
					}
					letRingBeatChanged = false;
				}
			}
			nextBIndex = 0;
		}
		return applyDurationEffects(note, tempo, realDuration);
	}

	private long applyDurationEffects(TGNote note, TGTempo tempo, long duration){
		//dead note
		if(note.getEffect().isDeadNote()){
			return applyStaticDuration(tempo, DEFAULT_DURATION_DEAD, duration);
		}
		//palm mute
		if(note.getEffect().isPalmMute()){
			return applyStaticDuration(tempo, DEFAULT_DURATION_PM, duration);
		}
		//staccato
		if(note.getEffect().isStaccato()){
			return (long)(duration * 50.00 / 100.00);
		}
		return duration;
	}

	private long applyStaticDuration(TGTempo tempo, long duration, long maximum ){
		long value = ( tempo.getQuarterValue() * duration / 60 );
		return (value < maximum ? value : maximum );
	}

	private int getRealVelocity(MidiSequenceHelper sh, TGNote note, TGTrack tgTrack, TGChannel tgChannel, int mIndex,int bIndex){
		int velocity = note.getVelocity();

		//Check for Hammer effect
		if(!tgChannel.isPercussionChannel()){
			MidiNoteHelper previousNote = getPreviousNote(sh, note,tgTrack,mIndex,bIndex,false);
			if(previousNote != null && previousNote.getNote().getEffect().isHammer()){
				velocity = Math.max(TGVelocities.MIN_VELOCITY,(velocity - 25));
			}
		}

		//Check for GhostNote effect
		if(note.getEffect().isGhostNote()){
			velocity = Math.max(TGVelocities.MIN_VELOCITY,(velocity - TGVelocities.VELOCITY_INCREMENT));
		}else if(note.getEffect().isAccentuatedNote()){
			velocity = Math.max(TGVelocities.MIN_VELOCITY,(velocity + TGVelocities.VELOCITY_INCREMENT));
		}else if(note.getEffect().isHeavyAccentuatedNote()){
			velocity = Math.max(TGVelocities.MIN_VELOCITY,(velocity + (TGVelocities.VELOCITY_INCREMENT * 2)));
		}

		return ((velocity > 127)?127:velocity);
	}

	private int[] getStroke(TGBeat beat, TGBeat previous, int[] stroke){
		int direction = beat.getStroke().getDirection();
		if( previous == null || !(direction == TGStroke.STROKE_NONE && previous.getStroke().getDirection() == TGStroke.STROKE_NONE)){
			if( direction == TGStroke.STROKE_NONE ){
				for( int i = 0 ; i < stroke.length ; i ++ ){
					stroke[ i ] = 0;
				}
			}else{
				int stringUseds = 0;
				int stringCount = 0;
				for( int vIndex = 0; vIndex < beat.countVoices(); vIndex ++ ){
					TGVoice voice = beat.getVoice(vIndex);
					for (int nIndex = 0; nIndex < voice.countNotes(); nIndex++) {
						TGNote note = voice.getNote(nIndex);
						if( !note.isTiedNote() ){
							stringUseds |= 0x01 << ( note.getString() - 1 );
							stringCount ++;
						}
					}
				}
				if( stringCount > 0 ){
					int strokeMove = 0;
					int strokeIncrement = beat.getStroke().getIncrementTime(beat);
					for( int i = 0 ; i < stroke.length ; i ++ ){
						int index = ( direction == TGStroke.STROKE_DOWN ? (stroke.length - 1) - i : i );
						if( (stringUseds & ( 0x01 << index ) ) != 0 ){
							stroke[ index ] = strokeMove;
							strokeMove += strokeIncrement;
						}
					}
				}
			}
		}
		return stroke;
	}

	private long applyStrokeStart( TGNote note, long start , int[] stroke){
		return (start + stroke[ note.getString() - 1 ]);
	}

	private long applyStrokeDuration( TGNote note, long duration , int[] stroke){
		return (duration > stroke[note.getString() - 1] ? (duration - stroke[ note.getString() - 1 ]) : duration );
	}

	private MidiTickHelper checkTripletFeel(TGVoice voice,int bIndex){
		long bStart = voice.getBeat().getStart();
		long bDuration =  voice.getDuration().getTime();
		if(voice.getBeat().getMeasure().getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_EIGHTH){
			if(voice.getDuration().isEqual(newDuration(TGDuration.EIGHTH))){
				//first time
				if( (bStart % TGDuration.QUARTER_TIME) == 0){
					TGVoice v = getNextBeat(voice,bIndex);
					if(v == null || ( v.getBeat().getStart() > (bStart + voice.getDuration().getTime()) || v.getDuration().isEqual(newDuration(TGDuration.EIGHTH)))  ){
						TGDuration duration = newDuration(TGDuration.EIGHTH);
						duration.getDivision().setEnters(3);
						duration.getDivision().setTimes(2);
						bDuration = (duration.getTime() * 2);
					}
				}
				//second time
				else if( (bStart % (TGDuration.QUARTER_TIME / 2)) == 0){
					TGVoice v = getPreviousBeat(voice,bIndex);
					if(v == null || ( v.getBeat().getStart() < (bStart - voice.getDuration().getTime())  || v.getDuration().isEqual(newDuration(TGDuration.EIGHTH)) )){
						TGDuration duration = newDuration(TGDuration.EIGHTH);
						duration.getDivision().setEnters(3);
						duration.getDivision().setTimes(2);
						bStart = ( (bStart - voice.getDuration().getTime()) + (duration.getTime() * 2));
						bDuration = duration.getTime();
					}
				}
			}
		}else if(voice.getBeat().getMeasure().getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH){
			if(voice.getDuration().isEqual(newDuration(TGDuration.SIXTEENTH))){
				//first time
				if( (bStart % (TGDuration.QUARTER_TIME / 2)) == 0){
					TGVoice v = getNextBeat(voice,bIndex);
					if(v == null || ( v.getBeat().getStart() > (bStart + voice.getDuration().getTime()) || v.getDuration().isEqual(newDuration(TGDuration.SIXTEENTH)))  ){
						TGDuration duration = newDuration(TGDuration.SIXTEENTH);
						duration.getDivision().setEnters(3);
						duration.getDivision().setTimes(2);
						bDuration = (duration.getTime() * 2);
					}
				}
				//second time
				else if( (bStart % (TGDuration.QUARTER_TIME / 4)) == 0){
					TGVoice v = getPreviousBeat(voice,bIndex);
					if(v == null || ( v.getBeat().getStart() < (bStart - voice.getDuration().getTime())  || v.getDuration().isEqual(newDuration(TGDuration.SIXTEENTH)) )){
						TGDuration duration = newDuration(TGDuration.SIXTEENTH);
						duration.getDivision().setEnters(3);
						duration.getDivision().setTimes(2);
						bStart = ( (bStart - voice.getDuration().getTime()) + (duration.getTime() * 2));
						bDuration = duration.getTime();
					}
				}
			}
		}
		return new MidiTickHelper(bStart, bDuration);
	}

	private TGDuration newDuration(int value){
		TGDuration duration = this.songManager.getFactory().newDuration();
		duration.setValue(value);
		return duration;
	}

	private TGVoice getPreviousBeat(TGVoice beat,int bIndex){
		TGVoice previous = null;
		for (int b = bIndex - 1; b >= 0; b--) {
			TGBeat current = beat.getBeat().getMeasure().getBeat( b );
			if(current.getStart() < beat.getBeat().getStart() && !current.getVoice(beat.getIndex()).isEmpty()){
				if(previous == null || current.getStart() > previous.getBeat().getStart()){
					previous = current.getVoice(beat.getIndex());
				}
			}
		}
		return previous;
	}

	private TGVoice getNextBeat(TGVoice beat,int bIndex){
		TGVoice next = null;
		for (int b = bIndex + 1; b < beat.getBeat().getMeasure().countBeats(); b++) {
			TGBeat current = beat.getBeat().getMeasure().getBeat( b );
			if(current.getStart() > beat.getBeat().getStart() && !current.getVoice(beat.getIndex()).isEmpty()){
				if(next == null || current.getStart() < next.getBeat().getStart()){
					next = current.getVoice(beat.getIndex());
				}
			}
		}
		return next;
	}

	private MidiNoteHelper getNextNote(MidiSequenceHelper sh, TGNote note,TGTrack track, int mIndex, int bIndex, boolean breakAtRest){
		int nextBIndex = (bIndex + 1);
		int measureCount = sh.getMeasureHelpers().size();
		for (int m = mIndex; m < measureCount; m++) {
			MidiMeasureHelper mh = sh.getMeasureHelper( m );

			TGMeasure measure = track.getMeasure( mh.getIndex() );
			int beatCount = measure.countBeats();
			for (int b = nextBIndex; b < beatCount; b++) {
				TGBeat beat = measure.getBeat( b );
				TGVoice voice = beat.getVoice( note.getVoice().getIndex() );
				if( !voice.isEmpty() ){
					int noteCount = voice.countNotes();
					for (int n = 0; n < noteCount; n++) {
						TGNote nextNote = voice.getNote( n );
						if(nextNote.getString() == note.getString()){
							return new MidiNoteHelper(mh,nextNote);
						}
					}
					if( breakAtRest ){
						return null;
					}
				}
			}
			nextBIndex = 0;
		}
		return null;
	}

	private MidiNoteHelper getPreviousNote(MidiSequenceHelper pHelper, TGNote note,TGTrack track, int mIndex, int bIndex, boolean breakAtRest){
		int nextBIndex = bIndex;
		for (int m = mIndex; m >= 0; m--) {
			MidiMeasureHelper mh = pHelper.getMeasureHelper( m );

			TGMeasure measure = track.getMeasure( mh.getIndex() );
			if( this.sHeader == -1 || this.sHeader <= measure.getNumber() ){
				nextBIndex = (nextBIndex < 0 ? measure.countBeats() : nextBIndex);
				for (int b = (nextBIndex - 1); b >= 0; b--) {
					TGBeat beat = measure.getBeat( b );
					TGVoice voice = beat.getVoice( note.getVoice().getIndex() );
					if( !voice.isEmpty() ){
						int noteCount = voice.countNotes();
						for (int n = 0; n < noteCount; n ++) {
							TGNote current = voice.getNote( n );
							if(current.getString() == note.getString()){
								return new MidiNoteHelper(mh,current);
							}
						}
						if( breakAtRest ){
							return null;
						}
					}
				}
			}
			nextBIndex = -1;
		}
		return null;
	}

	private int fix(int value, int minimum, int maximum) {
		return (value >= minimum ? value <= maximum ? value : maximum : minimum);
	}

	private int fix(int value) {
		return this.fix(value, 0, 127);
	}

	private class MidiTickHelper{
		private long start;
		private long duration;

		public MidiTickHelper(long start,long duration){
			this.start = start;
			this.duration = duration;
		}

		public long getDuration() {
			return this.duration;
		}

		public long getStart() {
			return this.start;
		}
	}

	private class MidiNoteHelper {

		private MidiMeasureHelper measure;
		private TGNote note;

		public MidiNoteHelper(MidiMeasureHelper measure, TGNote note){
			this.measure = measure;
			this.note = note;
		}

		public MidiMeasureHelper getMeasure() {
			return this.measure;
		}

		public TGNote getNote() {
			return this.note;
		}
	}

	private class MidiMeasureHelper {

		private int index;
		private long move;

		public MidiMeasureHelper(int index,long move){
			this.index = index;
			this.move = move;
		}

		public int getIndex() {
			return this.index;
		}

		public long getMove() {
			return this.move;
		}
	}

	private class MidiSequenceHelper {

		private List<MidiMeasureHelper> measureHeaderHelpers;
		private MidiSequenceHandler sequence;

		public MidiSequenceHelper(MidiSequenceHandler sequence){
			this.sequence = sequence;
			this.measureHeaderHelpers = new ArrayList<MidiMeasureHelper>();
		}

		public MidiSequenceHandler getSequence(){
			return this.sequence;
		}

		public void addMeasureHelper( MidiMeasureHelper helper ){
			this.measureHeaderHelpers.add( helper );
		}

		public List<MidiMeasureHelper> getMeasureHelpers(){
			return this.measureHeaderHelpers;
		}

		public MidiMeasureHelper getMeasureHelper( int index ){
			return (MidiMeasureHelper)this.measureHeaderHelpers.get( index );
		}

	}
}
