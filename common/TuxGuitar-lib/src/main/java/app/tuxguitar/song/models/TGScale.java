package app.tuxguitar.song.models;

import app.tuxguitar.util.TGMusicKeyUtils;

public abstract class TGScale {
	private final boolean[] notes = new boolean[12];

	private int key;
	private int alteration;

	public TGScale(){
		this.clear();
	}

	public void setKeyName(String name){
		String shortName = name.substring(0,1);
		this.key = 0;
		for (int i=0; i<TGMusicKeyUtils.sharpKeyNames.length; i++) {
			if (shortName.equals(TGMusicKeyUtils.sharpKeyNames[i])) {
				this.key = i;
			}
		}
		this.alteration = TGMusicKeyUtils.NONE;
		if (name.contains("#")) {
			this.key += 1;
			this.alteration = TGMusicKeyUtils.SHARP;
		}
		else if (name.contains("b")) {
			this.key -= 1;
			this.alteration = TGMusicKeyUtils.FLAT;
		}
	}

	public int getKey(){
		return this.key;
	}

	public int getAlteration(){
		return this.alteration;
	}

	public void setNote(int note,boolean on){
		this.notes[note] = on;
	}

	public boolean getNote(int note){
		return this.notes[((note + (12 - this.key)) % 12)];
	}

	public void clear(){
		this.setKeyName("C");
		for(int i = 0; i < this.notes.length; i++){
			this.setNote(i,false);
		}
	}

}
