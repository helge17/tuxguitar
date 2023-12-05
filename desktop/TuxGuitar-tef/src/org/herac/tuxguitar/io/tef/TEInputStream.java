package org.herac.tuxguitar.io.tef;

import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.tef.base.TEChord;
import org.herac.tuxguitar.io.tef.base.TEComponentChord;
import org.herac.tuxguitar.io.tef.base.TEComponentNote;
import org.herac.tuxguitar.io.tef.base.TEInfo;
import org.herac.tuxguitar.io.tef.base.TEPercussion;
import org.herac.tuxguitar.io.tef.base.TERepeat;
import org.herac.tuxguitar.io.tef.base.TERhythm;
import org.herac.tuxguitar.io.tef.base.TESong;
import org.herac.tuxguitar.io.tef.base.TETempo;
import org.herac.tuxguitar.io.tef.base.TEText;
import org.herac.tuxguitar.io.tef.base.TETimeSignature;
import org.herac.tuxguitar.io.tef.base.TETimeSignatureChange;
import org.herac.tuxguitar.io.tef.base.TETrack;

public class TEInputStream {
	
	private TESong song;
	private InputStream stream;
	
	public TEInputStream(InputStream stream){
		this.stream = stream;
	}
	
	public TESong readSong(){
		this.song = new TESong();
		
		this.readInfo();
		
		this.song.setMeasures((this.readByte() & 0xff));
		
		this.skip(1);
		this.readTimeSignature();
		
		this.skip(15);
		this.readTempo();
		
		this.song.setRepeats( (this.readByte() & 0xff) );
		
		this.skip(5);
		this.song.setTexts((this.readByte() & 0xff));
		
		this.skip(5);
		this.song.setPercussions((this.readByte() & 0xff));
		this.song.setRhythms((this.readByte() & 0xff));
		
		this.song.setChords((this.readByte() & 0xff));
		
		this.skip(1);
		boolean notes = ((this.readByte() & 0xff) > 0);
		
		this.skip(1);
		this.song.setStrings((this.readByte() & 0xff));
		
		this.song.setTracks((this.readByte() & 0xff) + 1);
		
		this.skip(14);
		this.readComponents();
		this.readRepeats();
		this.readTexts();
		
		this.readPercussions();
		
		this.readChords();
		
		
		this.readRhythms();
		this.readNotes(notes);
		this.readTracks();
		
		this.close();
		
		return this.song;
	}
	
	private void readInfo(){
		byte[] info = this.readBytes(200);
		int offset = 0;
		String strings[] = new String[3];
		for(int i = 0; i < strings.length; i ++){
			int length = 0;
			while( (offset + length) < info.length && info[ (offset + length)  ] != 0 ){
				length ++;
			}
			strings[i] = new String(info,offset,length);
			offset += length + 1;
		}
		this.song.setInfo(new TEInfo(strings[0],strings[1],strings[2]));
	}
	
	private void readNotes(boolean notes){
		if(notes){
			int length = this.readShort();
			this.song.getInfo().setNotes( new String(this.readBytes(length),1,(length -1)) );
		}
	}
	
	private void readTempo(){
		int value = this.readShort();
		this.song.setTempo(new TETempo(value));
	}
	
	private void readTimeSignature(){
		int numerator = this.readByte();
		this.skip(1);
		int denominator = this.readByte();
		this.song.setTimeSignature(new TETimeSignature(numerator,denominator));
	}
	
	private void readRepeats(){
		for(int i = 0; i < this.song.getRepeats().length; i ++){
			int data1 = this.readByte();
			int data2 = this.readByte();
			this.song.setRepeat(i,new TERepeat(data1,data2));
		}
	}
	
	private void readTexts(){
		for(int i = 0; i < this.song.getTexts().length; i ++){
			int length = this.readByte();
			byte[] text = this.readBytes(length);
			this.song.setText(i,new TEText(new String(text,1,(length -1))));
			this.skip(1);
		}
	}
	
	private void readChords(){
		for(int i = 0; i < this.song.getChords().length; i ++){
			byte[] strings = this.readBytes(14);
			byte[] name = this.readBytes(16);
			
			this.song.setChord(i,new TEChord(strings,new String(name)));
			this.skip(2);
		}
	}
	
	private void readPercussions(){
		if(this.song.getPercussions().length > 0){
			for(int i = 0; i < this.song.getPercussions().length; i ++){
				this.skip(96);
				String name = new String(this.readBytes(8) );
				this.skip(1);
				int volume = (this.readByte() & 0xff);
				this.song.setPercussion(i,new TEPercussion(name,volume));
				this.skip(2);
			}
			this.skip(this.song.getMeasures());
		}
	}
	
	private void readRhythms(){
		if(this.song.getRhythms().length > 0){
			for(int i = 0; i < this.song.getRhythms().length; i ++){
				this.skip(96);
				String name = new String(this.readBytes(8) );
				this.skip(1);
				int volume = (this.readByte() & 0xff);
				int instrument = (this.readByte() & 0xff);
				this.song.setRhythm(i,new TERhythm(name,volume,instrument));
				this.skip(1);
			}
			this.skip(this.song.getMeasures());
		}
	}
	
	private void readTracks(){
		for(int i = 0; i < this.song.getTracks().length; i ++){
			int[] strings = new int[this.readByte()];
			
			this.skip(5);
			int type = this.readByte();
			
			this.skip(1);
			int instrument = this.readByte();
			
			this.skip(3);
			int capo = this.readByte();
			
			this.skip(1);
			
			int clefType = this.readByte();
			int clefNumber = this.readByte();
			
			this.skip(1);
			
			int pan = this.readByte();
			int volume = this.readByte();
			int flags = this.readByte();
			
			for(int string = 0; string < strings.length; string ++){
				strings[string] = (this.readByte() & 0xff);
			}
			this.skip(12 - strings.length);
			
			String name = new String(this.readBytes(16));
			
			this.song.setTrack(i,new TETrack( (type == 98),instrument,capo, clefType, clefNumber, pan, volume, flags, strings, name));
			this.skip(2);
		}
	}
	
	private void readComponents(){
		int tsSize = ( (256 * this.song.getTimeSignature().getNumerator()) / this.song.getTimeSignature().getDenominator() );
		int tsMove = 0;
		int mIndex = 0;
		int mData = 0;
		int count = this.readShort();
		for(int i = 0; i < count; i ++){
			byte[] data = this.readBytes(6);
			
			int location = ( (data[0] & 0xff) + (256 *  (mData + (data[1] & 0xff))));
			if( ( location / ( tsSize * this.song.getStrings() ) ) < mIndex ){
				mData += 256;
				location = ((data[0] & 0xff) + (256 *  (mData + (data[1] & 0xff))));
			}
			int position = (location  %  tsSize);
			int string = ( (location / tsSize)  % this.song.getStrings()) ;
			int measure = ( location / ( tsSize * this.song.getStrings() ) );
			
			tsMove = (mIndex == measure)?tsMove:0;
			position -= tsMove;
			
			if( ((data[2] & 0xff) & 0x1f) > 0  && ((data[2] & 0xff) & 0x1f) <= 25 ){
				int duration = (data[3] & 0xf);
				int dynamic =  (data[3] >> 4);
				int effect = data[4];
				int fret = (((data[2] & 0xff) & 0x1f) - 1);
				if((((data[2] & 0xff) >> 5) & 0x01) != 0 ) {
					fret += (data[5] & 0xff);
				}
				this.song.getComponents().add( new TEComponentNote(position, measure, string ,fret,duration,dynamic,effect ) );
			}
			else if( ((data[2] & 0xff) & 0x1f) == 27 ){
				//TIME SIGNATURE CHANGE
				tsMove = (4 * (data[3] & 0xff ) );
				int denominator = (int)( ( Math.pow( 2  , (  ( data[2] & 0xff ) >> 5 ) ) / 2 ) );
				int numerator = ((( (tsSize / 4) - (data[3] & 0xff) ) * denominator) / 64);
				this.song.addTimeSignatureChange(new TETimeSignatureChange(measure, new TETimeSignature(numerator,denominator)));
			}
			else if( ((data[2] & 0xff) & 0x1f) == 28 && (data[2] & 0x20) != 0){
				//SCALE DIAGRAM
			}
			else if( ((data[2] & 0xff) & 0x1f) == 28){
				//CHORD
				this.song.getComponents().add( new TEComponentChord(position, measure, string , data[3]) );
			}
			else if( ((data[2] & 0xff) & 0x1f) == 29 ){
				//SCALE DIAGRAM | SPECIAL CHAR | SYNCOPATION CHANGE
			}
			else if( ((data[2] & 0xff) & 0x1f) == 30 ){
				//TEMPO CHANGE | VOICE CHANGE | DRUM EVENT | CRESCENDO ACCENT | REPEAT/ENDING
			}
			mIndex = measure;
		}
	}
	
	//-----------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------//	
	protected byte[] readBytes(int length){
		byte[] bytes = new byte[length];
		try {
			this.stream.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	protected int readByte(){
		try {
			return this.stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	protected int readShort(){
		try {
			byte[] b = new byte[2];
			this.stream.read(b);
			return ((b[1] & 0xff) << 8) | (b[0] & 0xff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	protected void skip(int count){
		for(int i = 0; i < count; i++){
			readByte();
		}
	}
	
	protected void close(){
		try {
			this.stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
