package org.herac.tuxguitar.io.abc.base;

import org.herac.tuxguitar.io.abc.ABCSongReader;
import org.herac.tuxguitar.song.models.TGMeasure;

public class ABCTrack {
	
	public static final int FLAG_DOUBLE_STRINGS = 0x01;
	public static final int FLAG_LET_RING = 0x02;
	public static final int FLAG_PEDAL_SABCEL_GUITAR = 0x04;
	public static final int FLAG_EFFECT_CHANNEL_DISABLED = 0x08;
	public static final int FLAG_RHYTHM_TRACK = 0x10;
	private boolean percussion;
	private int instrument;
	private int capo;
	private int clefType;
	private int clefNumber;
	private int pan;
	private int volume;
	private int flags;
	private int[] strings;
	private String name;
	private String declaration;
	private boolean bagpipe;
	
	public ABCTrack(String declaration) {
		this.declaration=declaration;
		name=declaration.split("\\s+")[0];
		percussion = declaration.endsWith("\tdrums");
		bagpipe = declaration.endsWith("\tdrone"); 
		instrument=0;
		capo=0;
		clefType=TGMeasure.DEFAULT_CLEF;
		clefNumber=0;
		pan=(int) (Math.random()*16);
		volume=-1;
		flags=0; 
		strings=null;
		if(declaration.toLowerCase().indexOf("clef=")>0) {
			if(declaration.toLowerCase().indexOf("clef=treble")>0) clefType=TGMeasure.CLEF_TREBLE;
			if(declaration.toLowerCase().indexOf("clef=bass")>0) clefType=TGMeasure.CLEF_BASS;
			if(declaration.toLowerCase().indexOf("clef=alto")>0) clefType=TGMeasure.CLEF_ALTO;
			if(declaration.toLowerCase().indexOf("clef=tenor")>0) clefType=TGMeasure.CLEF_TENOR;
		}
		switch(clefType) {
		case TGMeasure.CLEF_ALTO:
			strings=new int[4];
			strings[0]=67;
			strings[1]=62;
			strings[2]=57;
			strings[3]=52;
			break;
		case TGMeasure.CLEF_BASS:
			strings=new int[4];
			strings[0]=67;
			strings[1]=62;
			strings[2]=57;
			strings[3]=52;
			break;
		case TGMeasure.CLEF_TENOR:
			strings=new int[6];
			strings[0]=76;
			strings[1]=71;
			strings[2]=67;
			strings[3]=62;
			strings[4]=57;
			strings[5]=52;
			break;
		case TGMeasure.CLEF_TREBLE:
			strings=new int[6];
			strings[0]=88;
			strings[1]=83;
			strings[2]=79;
			strings[3]=74;
			strings[4]=69;
			strings[5]=64;
			break;
		}
		if(percussion) {
			for(int i=0;i<strings.length;i++) 
				strings[i]=ABCSongReader.PERCUSSION_TUNINGS[strings.length-1][i];
		}
	}
	
	public int getCapo() {
		return this.capo;
	}
	
	public int getClefNumber() {
		return this.clefNumber;
	}
	
	public int getClefType() {
		return this.clefType;
	}
	
	public int getFlags() {
		return this.flags;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getPan() {
		return this.pan;
	}
	
	public boolean isPercussion() {
		return this.percussion;
	}
	
	public int[] getStrings() {
		return this.strings;
	}
	
	public int getVolume() {
		return this.volume;
	}
	
	public int getInstrument() {
		return this.instrument;
	}
	
	public String toString(){
		String string = new String("[TRACK]");
		string += "\n     "  + this.getName();
		
		for(int i = 0; i < this.strings.length; i ++){
			string += "\n     String " + i + ": " + (96 - this.strings[i]);
		}
		return string;
	}

	public String getDeclaration() {
		// TODO Auto-generated method stub
		return declaration;
	}

	/**
	 * @param capo the capo to set
	 */
	public void setCapo(int capo) {
		this.capo = capo;
	}

	/**
	 * @param clefNumber the clefNumber to set
	 */
	public void setClefNumber(int clefNumber) {
		this.clefNumber = clefNumber;
	}

	/**
	 * @param clefType the clefType to set
	 */
	public void setClefType(int clefType) {
		transposeStrings(this.clefType,clefType);
		this.clefType = clefType;
	}

	private void transposeStrings(int from, int to) {
		int transpose = tuning(to) - tuning(from);
		for(int i=0;i<strings.length;i++) strings[i] += transpose;
	}

	private int tuning(int c) {
		switch(c) {
		case TGMeasure.CLEF_ALTO:	return 52;
		case TGMeasure.CLEF_BASS:	return 52;
		case TGMeasure.CLEF_TENOR:	return 52;
		case TGMeasure.CLEF_TREBLE:	return 64;
		}
		return 0;
	}

	/**
	 * @param instrument the instrument to set
	 */
	public void setInstrument(int instrument) {
		this.instrument = instrument;
	}

	/**
	 * @param pan the pan to set
	 */
	public void setPan(int pan) {
		this.pan = pan;
	}

	/**
	 * @param volume the volume to set
	 */
	public void setVolume(int volume) {
		this.volume = volume;
	}

	/**
	 * @param flags the flags to set
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	public ABCTrack clone(String name, int numstrings) {
		ABCTrack trk=new ABCTrack(this.declaration);
		trk.name=name;
		trk.percussion = name.endsWith("\tdrums");
		trk.bagpipe = name.endsWith("\tdrone"); 
		if(numstrings==strings.length && !trk.percussion) 
			return trk;
		trk.strings=new int[numstrings];
		if(trk.percussion) {
			for(int i=0;i<numstrings;i++) 
				trk.strings[i]=ABCSongReader.PERCUSSION_TUNINGS[numstrings-1][i];
		}
		else if(strings.length>numstrings) {
			for(int i=0;i<numstrings;i++) {
				trk.strings[i]=strings[i+strings.length-numstrings];
			}
		}
		else { // strings.length < numstrings
			trk.strings[numstrings-1]=strings[strings.length-1]-5*(numstrings-strings.length);
			for(int i=numstrings-1;i>0;i--) {
				trk.strings[i-1]=(i==2?4:5)+trk.strings[i];
			}
		}
		return trk;
	}

	public void setStrings(int[] s) {
		this.strings=new int[s.length];
		for(int i=0;i<s.length;i++)
			this.strings[i]=s[i];
	}

	public boolean isBagpipe() {
		return bagpipe;
	}
}
