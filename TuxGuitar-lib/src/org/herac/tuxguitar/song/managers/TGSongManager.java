package org.herac.tuxguitar.song.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelNames;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGSongManager {
	
	public static final short MAX_CHANNELS = 16;
	
	public static final int[][] DEFAULT_TUNING_VALUES = {
		{43,38,33,28},
		{43,38,33,28,23},
		{64,59,55,50,45,40},
		{64,59,55,50,45,40,35},
	};
	
	private TGFactory factory;
	private TGTrackManager trackManager;
	private TGMeasureManager measureManager;
	
	public TGSongManager(){
		this(new TGFactory());
	}
	
	public TGSongManager(TGFactory factory){
		this.factory = factory;
	}
	
	public TGFactory getFactory(){
		return this.factory;
	}
	
	public void setFactory(TGFactory factory){
		this.factory = factory;
	}
	
	public TGTrackManager getTrackManager(){
		if(this.trackManager == null){
			this.trackManager = new TGTrackManager(this);
		}
		return this.trackManager;
	}
	
	public TGMeasureManager getMeasureManager(){
		if(this.measureManager == null){
			this.measureManager = new TGMeasureManager(this);
		}
		return this.measureManager;
	}
	
	public void setSongName(TGSong song, String name){
		song.setName(name);
	}
	
	public void setProperties(TGSong song, String name,String artist,String album,String author,String date,String copyright,String writer,String transcriber,String comments){
		song.setName(name);
		song.setArtist(artist);
		song.setAlbum(album);
		song.setAuthor(author);
		song.setDate(date);
		song.setCopyright(copyright);
		song.setWriter(writer);
		song.setTranscriber(transcriber);
		song.setComments(comments);
	}

	public void clearSong(TGSong song){
		song.clear();
	}
	
	public void fillSong(TGSong song){
		TGChannel channel = getFactory().newChannel();
		channel.setChannelId(1);
		channel.setName(createDefaultChannelName(song, channel));
		
		TGMeasureHeader header = getFactory().newHeader();
		header.setNumber(1);
		header.setStart(TGDuration.QUARTER_TIME);
		header.getTimeSignature().setNumerator(4);
		header.getTimeSignature().getDenominator().setValue(TGDuration.QUARTER);
		
		TGTrack track = getFactory().newTrack();
		track.setNumber(1);
		track.setName(getDefaultTrackName(track));
		track.setChannelId(channel.getChannelId());
		track.setStrings(createDefaultInstrumentStrings());
		track.addMeasure(getFactory().newMeasure(header));
		track.getColor().copyFrom(TGColor.RED);
		
		song.addChannel(channel);
		song.addMeasureHeader(header);
		song.addTrack(track);
	}
	
	public TGSong newSong(){
		TGSong song = getFactory().newSong();
		
		this.fillSong(song);
		
		return song;
	}
	
	public void copySongFrom(TGSong song, TGSong from){
		song.copyFrom(getFactory(), from);
	}
	
	public TGChannel createChannel(){
		return getFactory().newChannel();
	}
	
	public TGChannel addChannel(TGSong tgSong){
		TGChannel tgChannel = addChannel(tgSong, createChannel());
		tgChannel.setName(this.createDefaultChannelName(tgSong, tgChannel));
		return tgChannel;
	}
	
	public TGChannel addChannel(TGSong song, TGChannel tgChannel){
		if( tgChannel != null ){
			if( tgChannel.getChannelId() <= 0 ){
				tgChannel.setChannelId( getNextChannelId(song) );
			}
			song.addChannel(tgChannel);
		}
		return tgChannel;
	}
	
	public void removeChannel(TGSong song, TGChannel channel){
		if( channel != null ){
			song.removeChannel(channel);
		}
	}
	
	public void removeChannel(TGSong song, int channelId){
		TGChannel channel = getChannel(song, channelId);
		if( channel != null ){
			removeChannel(song, channel);
		}
	}
	
	public void removeAllChannels(TGSong song){
		while( song.countChannels() > 0 ){
			removeChannel(song, song.getChannel(0) );
		}
	}
	
	public TGChannel getChannel(TGSong song, int channelId){
		Iterator<TGChannel> it = song.getChannels();
		while( it.hasNext() ){
			TGChannel channel = it.next();
			if( channel.getChannelId() == channelId ){
				return channel;
			}
		}
		return null;
	}
	
	public List<TGChannel> getChannels(TGSong song){
		List<TGChannel> channels = new ArrayList<TGChannel>();
		
		Iterator<TGChannel> it = song.getChannels();
		while( it.hasNext() ){
			channels.add(it.next());
		}
		
		return channels;
	}
	
	public List<TGChannel> findChannelsByName(TGSong song, String name){
		List<TGChannel> channels = new ArrayList<TGChannel>();
		
		Iterator<TGChannel> it = song.getChannels();
		while( it.hasNext() ){
			TGChannel channel = it.next();
			if( channel.getName().equals(name) ) {
				channels.add(channel);
			}
		}
		
		return channels;
	}
	
	public int getNextChannelId(TGSong song){
		int maximumId = 0;
		
		Iterator<TGChannel> it = song.getChannels();
		while( it.hasNext() ){
			TGChannel channel = it.next();
			if( maximumId < channel.getChannelId() ){
				maximumId = channel.getChannelId();
			}
		}
		
		return (maximumId + 1);
	}
	
	public TGChannel updateChannel(TGSong song, int id,short bnk,short prg,short vol,short bal,short cho,short rev,short pha,short tre,String name){
		TGChannel channel = getChannel(song, id);
		if( channel != null ){
			channel.setBank(bnk);
			channel.setProgram(prg);
			channel.setVolume(vol);
			channel.setBalance(bal);
			channel.setChorus(cho);
			channel.setReverb(rev);
			channel.setPhaser(pha);
			channel.setTremolo(tre);
			channel.setName(name);
		}
		return channel;
	}
	
	public boolean isPercussionChannel(TGSong song, int channelId ){
		TGChannel channel = getChannel(song, channelId);
		if( channel != null ){
			return channel.isPercussionChannel();
		}
		return false;
	}
	
	public boolean isAnyPercussionChannel(TGSong song){
		Iterator<TGChannel> it = song.getChannels();
		while( it.hasNext() ){
			TGChannel channel = it.next();
			if( channel.isPercussionChannel() ){
				return true;
			}
		}
		return false;
	}
	
	public boolean isAnyTrackConnectedToChannel(TGSong song, int channelId ){
		Iterator<TGTrack> it = song.getTracks();
		while( it.hasNext() ){
			TGTrack track = it.next();
			if( track.getChannelId() == channelId ){
				return true;
			}
		}
		return false;
	}
	
	public String createChannelName(TGSong song, TGChannel channel, String prefix) {
		int number = 0;
		
		String unusedName = null;
		while( unusedName == null ) {
			number ++;
			String name = (prefix + " " + number);
			if( this.findChannelsByName(song, name).isEmpty() ) {
				unusedName = name;
			}
		}
		return unusedName;
	}
	
	public String createDefaultChannelName(TGSong song, TGChannel channel) {
		return this.createChannelName(song, channel, "Unnamed");
	}
	
	public String createChannelNameFromProgram(TGSong song, TGChannel channel) {
		if( channel.getProgram() >= 0 && channel.getProgram() < TGChannelNames.DEFAULT_NAMES.length ) {
			return this.createChannelName(song, channel, TGChannelNames.DEFAULT_NAMES[channel.getProgram()]);
		}
		return this.createDefaultChannelName(song, channel);
	}
	
	// -------------------------------------------------------------- // 

	private TGTrack createTrack(TGSong song){
		TGTrack tgTrack = getFactory().newTrack();
		tgTrack.setNumber(getNextTrackNumber(song));
		tgTrack.setName(getDefaultTrackName(tgTrack));
		
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			TGMeasure measure = getFactory().newMeasure(header);
			tgTrack.addMeasure(measure);
		}
		tgTrack.setStrings(createDefaultInstrumentStrings());
		tgTrack.getColor().copyFrom(TGColor.RED);
		
		return tgTrack;
	}
	
	public TGTrack addTrack(TGSong song){
		if( song.isEmpty() ){
			this.fillSong(song);
			return getLastTrack(song);
		}
		TGChannel tgChannel = addChannel(song);
		tgChannel.setName(createChannelNameFromProgram(song, tgChannel));
		
		TGTrack tgTrack = createTrack(song);
		tgTrack.setChannelId(tgChannel.getChannelId());
		addTrack(song, tgTrack);
		return tgTrack;
	}
	
	public void addTrack(TGSong song, TGTrack trackToAdd){
		this.orderTracks(song);
		int addIndex = -1;
		for(int i = 0;i < song.countTracks();i++){
			TGTrack track = song.getTrack(i);
			if( addIndex == -1 && track.getNumber() == trackToAdd.getNumber() ){
				addIndex = i;
			}
			if( addIndex >= 0 ){
				track.setNumber(track.getNumber() + 1);
			}
		}
		if( addIndex < 0 ){
			addIndex = song.countTracks();
		}
		song.addTrack(addIndex,trackToAdd);
	}
	
	public void removeTrack(TGSong song, TGTrack track){
		removeTrack(song, track.getNumber());
	}
	
	public void removeTrack(TGSong song, int number){
		int nextNumber = number;
		TGTrack trackToRemove = null;
		orderTracks(song);
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack currTrack = it.next();
			if(trackToRemove == null && currTrack.getNumber() == nextNumber){
				trackToRemove = currTrack;
			}else if(currTrack.getNumber() == (nextNumber + 1)){
				currTrack.setNumber(nextNumber);
				nextNumber ++;
			}
			
		}
		song.removeTrack(trackToRemove);
	}
	
	private void orderTracks(TGSong song){
		for(int i = 0;i < song.countTracks();i++){
			TGTrack minTrack = null;
			for(int trackIdx = i;trackIdx < song.countTracks();trackIdx++){
				TGTrack track = song.getTrack(trackIdx);
				if(minTrack == null || track.getNumber() < minTrack.getNumber()){
					minTrack = track;
				}
			}
			song.moveTrack(i,minTrack);
		}
	}
	
	public TGTrack getTrack(TGSong song, int number){
		TGTrack track = null;
		for (int i = 0; i < song.countTracks(); i++) {
			TGTrack currTrack = song.getTrack(i);
			if(currTrack.getNumber() == number){
				track = currTrack;
				break;
			}
		}
		return track;
	}
	
	public TGTrack getFirstTrack(TGSong song){
		TGTrack track = null;
		if(!song.isEmpty()){
			track = song.getTrack(0);
		}
		return track;
	}
	
	public TGTrack getLastTrack(TGSong song){
		TGTrack track = null;
		if(!song.isEmpty()){
			track = song.getTrack(song.countTracks() - 1);
		}
		return track;
	}
	
	public int getNextTrackNumber(TGSong song){
		return (song.countTracks() + 1);
	}
	
	public String getDefaultTrackName(TGTrack tgTrack){
		if( tgTrack != null && tgTrack.getNumber() > 0 ){
			return new String("Track " + tgTrack.getNumber());
		}
		return new String();
	}
	
	public TGTrack cloneTrack(TGSong song, TGTrack track){
		TGTrack clone = track.clone(getFactory(),song);
		clone.setNumber(getNextTrackNumber(song));
		addTrack(song, clone);
		return clone;
	}
	
	public boolean moveTrackUp(TGSong song, TGTrack track){
		if(track.getNumber() > 1){
			TGTrack prevTrack = getTrack(song, track.getNumber() - 1);
			prevTrack.setNumber(prevTrack.getNumber() + 1);
			track.setNumber(track.getNumber() - 1);
			orderTracks(song);
			return true;
		}
		return false;
	}
	
	public boolean moveTrackDown(TGSong song, TGTrack track){
		if(track.getNumber() < song.countTracks()){
			TGTrack nextTrack = getTrack(song, track.getNumber() + 1);
			nextTrack.setNumber(nextTrack.getNumber() - 1);
			track.setNumber(track.getNumber() + 1);
			orderTracks(song);
			return true;
		}
		return false;
	}
	
	public void changeTimeSignature(TGSong song, long start,TGTimeSignature timeSignature,boolean toEnd){
		changeTimeSignature(song, getMeasureHeaderAt(song, start),timeSignature,toEnd);
	}
	
	public void changeTimeSignature(TGSong song, TGMeasureHeader header,TGTimeSignature timeSignature,boolean toEnd){
		//asigno el nuevo ritmo
		header.getTimeSignature().copyFrom(timeSignature);
		
		long nextStart = header.getStart() + header.getLength();
		List<TGMeasureHeader> measures = getMeasureHeadersBeforeEnd(song, header.getStart() + 1);
		Iterator<TGMeasureHeader> it = measures.iterator();
		while(it.hasNext()){
			TGMeasureHeader nextHeader = it.next();
			
			long theMove = nextStart - nextHeader.getStart();
			
			//moveMeasureComponents(nextHeader,theMove);
			moveMeasureHeader(nextHeader,theMove,0);
			
			if(toEnd){
				nextHeader.getTimeSignature().copyFrom(timeSignature);
			}
			nextStart = nextHeader.getStart() + nextHeader.getLength();
		}
		moveOutOfBoundsBeatsToNewMeasure(song, header.getStart());
	}
	
	public void moveOutOfBoundsBeatsToNewMeasure(TGSong song, long start){
		Iterator<TGTrack> it = song.getTracks();
		while( it.hasNext() ){
			TGTrack track = it.next();
			getTrackManager().moveOutOfBoundsBeatsToNewMeasure(track, start);
		}
	}
	
	public void changeTripletFeel(TGSong song, long start,int tripletFeel,boolean toEnd){
		changeTripletFeel(song, getMeasureHeaderAt(song, start),tripletFeel,toEnd);
	}
	
	public void changeTripletFeel(TGSong song, TGMeasureHeader header,int tripletFeel,boolean toEnd){
		//asigno el nuevo tripletFeel
		header.setTripletFeel(tripletFeel);
		
		if(toEnd){
			List<TGMeasureHeader> measures = getMeasureHeadersBeforeEnd(song ,header.getStart() + 1);
			Iterator<TGMeasureHeader> it = measures.iterator();
			while(it.hasNext()){
				TGMeasureHeader nextHeader = it.next();
				nextHeader.setTripletFeel(tripletFeel); 
			}
		}
	}
	
	public void changeTempos(TGSong song, long start,TGTempo tempo,boolean toEnd){
		changeTempos(song, getMeasureHeaderAt(song, start),tempo,toEnd);
	}
	
	public void changeTempos(TGSong song, TGMeasureHeader header,TGTempo tempo,boolean toEnd){
		int oldValue = header.getTempo().getValue();
		Iterator<TGMeasureHeader> it = getMeasureHeadersAfter(song, header.getNumber() - 1).iterator();
		while(it.hasNext()){
			TGMeasureHeader nextHeader = it.next();
			if(toEnd || nextHeader.getTempo().getValue() == oldValue){
				changeTempo(nextHeader,tempo);
			}else{
				break;
			}
		}
	}
	
	public void changeTempos(TGSong song, List<TGTempo> tempos){
		int length = tempos.size();
		if(length != song.countMeasureHeaders()){
			return;
		}
		for(int i = 0; i < length; i ++){
			TGTempo tempo = (TGTempo)tempos.get(i);
			TGMeasureHeader header = getMeasureHeader(song, i + 1);
			this.changeTempo(header, tempo);
		}
	}
	
	public void changeTempo(TGMeasureHeader header,TGTempo tempo){
		header.getTempo().copyFrom(tempo);
	}
	
	public void changeOpenRepeat(TGSong song, long start){
		TGMeasureHeader header = getMeasureHeaderAt(song, start);
		header.setRepeatOpen(!header.isRepeatOpen());
	}
	
	public void changeCloseRepeat(TGSong song, long start, int repeatClose){
		TGMeasureHeader header = getMeasureHeaderAt(song, start);
		header.setRepeatClose(repeatClose);
	}
	
	public void changeAlternativeRepeat(TGSong song, long start,int repeatAlternative){
		TGMeasureHeader header = getMeasureHeaderAt(song, start);
		header.setRepeatAlternative(repeatAlternative);
	}
	
	public TGMeasureHeader addNewMeasureBeforeEnd(TGSong song){
		TGMeasureHeader lastHeader = getLastMeasureHeader(song);
		TGMeasureHeader header = getFactory().newHeader();
		header.setNumber((lastHeader.getNumber() + 1));
		header.setStart((lastHeader.getStart() + lastHeader.getLength()));
		header.setRepeatOpen(false);
		header.setRepeatClose(0);
		header.setTripletFeel(lastHeader.getTripletFeel());
		header.getTimeSignature().copyFrom(lastHeader.getTimeSignature());
		header.getTempo().copyFrom(lastHeader.getTempo());
		song.addMeasureHeader(header);
		
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().addNewMeasureBeforeEnd(track,header);
		}
		return header;
	}
	
	public void addNewMeasure(TGSong song, int number){
		//Obtengo un clon para el nuevo Header.
		TGMeasureHeader header = null;
		if(number == 1){
			header = getMeasureHeader(song, number).clone(getFactory());
		}else{
			header = getMeasureHeader(song, (number - 1)).clone(getFactory());
			header.setStart(header.getStart() + header.getLength());
			header.setNumber(header.getNumber() + 1);
		}
		header.setMarker(null);
		header.setRepeatOpen(false);
		header.setRepeatAlternative(0);
		header.setRepeatClose(0);
		
		//Si hay Headers siguientes los muevo
		TGMeasureHeader nextHeader = getMeasureHeader(song, number);
		if(nextHeader != null){
			moveMeasureHeaders(song, getMeasureHeadersBeforeEnd(song, nextHeader.getStart()),header.getLength(),1,true);
		}
		
		//Agrego el header a la lista
		addMeasureHeader(song, (header.getNumber() - 1) ,header);
		
		//Agrego los compases en todas las pistas
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().addNewMeasure(track,header);
		}
	}
	
	public List<TGMeasure> getMeasures(TGSong song, long start){
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			TGMeasure measure = getTrackManager().getMeasureAt(track,start);
			if(measure != null){
				measures.add(measure);
			}
		}
		return measures;
	}
	
	public void copyTrackFrom(TGSong song, TGTrack track, TGTrack from){
		while( song.countMeasureHeaders() < from.countMeasures() ){
			this.addNewMeasureBeforeEnd(song);
		}
		while( song.countMeasureHeaders() > from.countMeasures() ){
			this.removeLastMeasureHeader(song);
		}
		
		track.copyFrom(getFactory(), song, from);
	}
	
	public TGTrack replaceTrack(TGSong song, TGTrack track){
		TGTrack current = getTrack(song, track.getNumber());
		if( current != null ){
			this.copyTrackFrom(song, current, track);
		}
		return current;
	}
	
	public TGMeasureHeader getFirstMeasureHeader(TGSong song){
		TGMeasureHeader firstHeader = null;
		for(int i = 0;i < song.countMeasureHeaders();i++){
			TGMeasureHeader currHeader = song.getMeasureHeader(i);
			if(firstHeader == null || (currHeader.getStart() < firstHeader.getStart())){
				firstHeader = currHeader;
			}
		}
		return firstHeader;
	}
	
	public TGMeasureHeader getLastMeasureHeader(TGSong song){
		int lastIndex = song.countMeasureHeaders() - 1;
		return song.getMeasureHeader(lastIndex);
	}
	
	public TGMeasureHeader getPrevMeasureHeader(TGSong song, TGMeasureHeader header){
		int prevIndex = header.getNumber() - 1;
		if(prevIndex > 0){
			return song.getMeasureHeader(prevIndex - 1);
		}
		return null;
	}
	
	public TGMeasureHeader getNextMeasureHeader(TGSong song, TGMeasureHeader header){
		int nextIndex = header.getNumber();
		if(nextIndex < song.countMeasureHeaders()){
			return song.getMeasureHeader(nextIndex);
		}
		return null;
	}
	
	public TGMeasureHeader getMeasureHeaderAt(TGSong song, long start){
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			long measureStart = header.getStart();
			long measureLength = header.getLength();
			if(start >= measureStart && start < measureStart + measureLength){
				return header;
			}
		}
		return null;
	}
	
	public TGMeasureHeader getMeasureHeader(TGSong song, int number){
		for (int i = 0; i < song.countMeasureHeaders(); i++) {
			TGMeasureHeader header = song.getMeasureHeader(i);
			if(header.getNumber() == number){
				return header;
			}
		}
		return null;
	}
	
	public int getMeasureHeaderIndex(TGSong song, TGMeasureHeader mh){
		for (int i = 0; i < song.countMeasureHeaders(); i++) {
			TGMeasureHeader header = song.getMeasureHeader(i);
			if(header.getNumber() == mh.getNumber()){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGMeasureHeader> getMeasureHeadersBeforeEnd(TGSong song, long fromStart) {
		List<TGMeasureHeader> headers = new ArrayList<TGMeasureHeader>();
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if (header.getStart() >= fromStart) {
				headers.add(header);
			}
		}
		return headers;
	}
	
	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGMeasureHeader> getMeasureHeadersAfter(TGSong song, int number) {
		List<TGMeasureHeader> headers = new ArrayList<TGMeasureHeader>();
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if (header.getNumber() > number) {
				headers.add(header);
			}
		}
		return headers;
	}
	
	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGMeasureHeader> getMeasureHeadersBetween(TGSong song, long p1, long p2) {
		List<TGMeasureHeader> headers = new ArrayList<TGMeasureHeader>();
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if ((header.getStart() + header.getLength()) > p1 && header.getStart() < p2) {
				headers.add(header);
			}
		}
		return headers;
	}
	
	public void removeLastMeasure(TGSong song){
		removeLastMeasureHeader(song);
	}
	
	public void removeMeasure(TGSong song, long start){
		removeMeasureHeader(song, start);
	}
	
	public void removeMeasure(TGSong song, int number){
		removeMeasureHeader(song, number);
	}
	
	/**
	 * Agrega un Compas
	 */
	public void addMeasureHeader(TGSong song, TGMeasureHeader measure){
		song.addMeasureHeader(measure);
	}
	
	/**
	 * Agrega un Compas
	 */
	public void addMeasureHeader(TGSong song, int index,TGMeasureHeader measure){
		song.addMeasureHeader(index, measure);
	}
	
	public void removeMeasureHeaders(TGSong song, int n1, int n2){
		for(int i = n1; i <= n2; i ++){
			TGMeasureHeader measure = getMeasureHeader(song, n1);
			removeMeasureHeader(song,measure);
		}
	}
	
	public void removeLastMeasureHeader(TGSong song){
		removeMeasureHeader(song, getLastMeasureHeader(song));
	}
	
	public void removeMeasureHeader(TGSong song, long start){
		removeMeasureHeader(song, getMeasureHeaderAt(song,start));
	}
	
	public void removeMeasureHeader(TGSong song, int number){
		removeMeasureHeader(song,getMeasureHeader(song, number));
	}
	
	public void removeMeasureHeader(TGSong song, TGMeasureHeader header){
		long start = header.getStart();
		long length = header.getLength();
		
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().removeMeasure(track,start);
		}
		moveMeasureHeaders(song, getMeasureHeadersBeforeEnd(song, start + 1),-length,-1,true);
		song.removeMeasureHeader(header.getNumber() - 1);
	}
	
	public TGMeasureHeader replaceMeasureHeader(TGSong song, TGMeasureHeader newHeader){
		TGMeasureHeader header = getMeasureHeaderAt(song, newHeader.getStart());
		int number = header.getNumber();
		
		this.copyMeasureHeaderFrom(song, header, newHeader);
		
		header.setNumber(number);
		
		return header;
	}
	
	public void copyMeasureHeaderFrom(TGSong song, TGMeasureHeader header, TGMeasureHeader from){
		header.copyFrom(getFactory(), from);
	}
	
	public void moveMeasureHeaders(TGSong song, List<TGMeasureHeader> headers,long theMove,int numberMove,boolean moveComponents) {
		if(moveComponents){
			Iterator<TGMeasureHeader> it = headers.iterator();
			while(it.hasNext()){
				TGMeasureHeader header = it.next();
				moveMeasureComponents(song, header,theMove);
			}
		}
		Iterator<TGMeasureHeader> it = headers.iterator();
		while (it.hasNext()) {
			TGMeasureHeader header = it.next();
			moveMeasureHeader(header,theMove,numberMove);
		}
	}
	
	/**
	 * Mueve el compas
	 */
	public void moveMeasureHeader(TGMeasureHeader header,long theMove,int numberMove){
		header.setNumber(header.getNumber() + numberMove);
		header.setStart(header.getStart() + theMove);
	}
	
	/**
	 * Mueve el compas
	 */
	public void moveMeasureComponents(TGSong song, TGMeasureHeader header,long theMove){
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().moveMeasure(getTrackManager().getMeasure(track,header.getNumber()),theMove);
		}
	}
	
	/** 
	 * Retorna true si el start esta en el rango del compas
	 */
	public boolean isAtPosition(TGMeasureHeader header,long start){
		return (start >= header.getStart() && start < header.getStart() + header.getLength());
	}
	
	public TGMarker updateMarker(TGSong song, int measure,String title,TGColor color){
		TGMeasureHeader header = getMeasureHeader(song, measure);
		if(header != null){
			if(!header.hasMarker()){
				header.setMarker(getFactory().newMarker());
			}
			header.getMarker().setMeasure(measure);
			header.getMarker().setTitle(title);
			header.getMarker().getColor().setR(color.getR());
			header.getMarker().getColor().setG(color.getG());
			header.getMarker().getColor().setB(color.getB());
			return header.getMarker(); 
		}
		return null;
	}
	
	public TGMarker updateMarker(TGSong song, TGMarker marker){
		return updateMarker(song, marker.getMeasure(),marker.getTitle(),marker.getColor());
	}
	
	public void removeMarker(TGSong song, TGMarker marker){
		if( marker != null ){
			removeMarker(song, marker.getMeasure());
		}
	}
	
	public void removeMarker(TGSong song, int number){
		TGMeasureHeader header = getMeasureHeader(song, number);
		if(header != null && header.hasMarker()){
			header.setMarker(null);
		}
	}
	
	public void removeAllMarkers(TGSong song){
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker()){
				header.setMarker(null);
			}
		}
	}
	
	public List<TGMarker> getMarkers(TGSong song){
		List<TGMarker> markers = new ArrayList<TGMarker>();
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker()){
				markers.add(header.getMarker());
			}
		}
		return markers;
	}
	
	public TGMarker getMarker(TGSong song, int number){
		TGMeasureHeader header = getMeasureHeader(song, number);
		if(header != null && header.hasMarker()){
			return header.getMarker();
		}
		return null;
	}
	
	public TGMarker getPreviousMarker(TGSong song, int from){
		TGMeasureHeader previous = null;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker() && header.getNumber() < from){
				if(previous == null || previous.getNumber() < header.getNumber()){
					previous = header;
				}
			}
		}
		return (previous != null)?previous.getMarker():null;
	}
	
	public TGMarker getNextMarker(TGSong song, int from){
		TGMeasureHeader next = null;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker() && header.getNumber() > from){
				if(next == null || next.getNumber() > header.getNumber()){
					next = header;
				}
			}
		}
		return (next != null)?next.getMarker():null;
	}
	
	public TGMarker getFirstMarker(TGSong song){
		TGMeasureHeader first = null;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker()){
				if(first == null || header.getNumber() < first.getNumber()){
					first = header;
				}
			}
		}
		return (first != null)?first.getMarker():null;
	}
	
	public TGMarker getLastMarker(TGSong song){
		TGMeasureHeader next = null;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.hasMarker()){
				if(next == null || header.getNumber() > next.getNumber()){
					next = header;
				}
			}
		}
		return (next != null)?next.getMarker():null;
	}
	
	public void autoCompleteSilences(TGSong song){
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().autoCompleteSilences(track);
		}
	}
	
	public void orderBeats(TGSong song){
		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			getTrackManager().orderBeats(track);
		}
	}
	
	public List<TGString> createDefaultInstrumentStrings(){
		return createDefaultInstrumentStrings(6);
	}
	
	public List<TGString> createDefaultInstrumentStrings(int stringCount){
		return createStrings(stringCount, DEFAULT_TUNING_VALUES);
	}
	
	public List<TGString> createPercussionStrings(int stringCount){
		return createStrings(stringCount, null);
	}
	
	public List<TGString> createStrings(int stringCount, int[][] defaultTunings){
		List<TGString> strings = new ArrayList<TGString>();
		if( defaultTunings != null ) {
			for(int i = 0; i < defaultTunings.length ; i++) {
				if( stringCount == defaultTunings[i].length ) {
					for(int n = 0; n < defaultTunings[i].length ; n ++) {
						strings.add(newString(getFactory(),(n + 1), defaultTunings[i][n]));
					}
					break;
				}
			}
		}
		if( strings.isEmpty() ) {
			for(int i = 1;i <= stringCount; i++){
				strings.add(newString(getFactory(),i, 0));
			}
		}
		return strings;
	}
	
	public static TGString newString(TGFactory factory,int number,int value){
		TGString string = factory.newString();
		string.setNumber(number);
		string.setValue(value);
		return string;
	}
	
	public static long getDivisionLength(TGMeasureHeader header){
		long defaultLength = TGDuration.QUARTER_TIME;
		int denominator = header.getTimeSignature().getDenominator().getValue();
		switch(denominator){
			case TGDuration.EIGHTH:
				if(header.getTimeSignature().getNumerator() % 3 == 0){
					defaultLength += TGDuration.QUARTER_TIME / 2;
				}
				break;
		}
		return defaultLength;
	}
}
