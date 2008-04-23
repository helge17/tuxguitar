package org.herac.tuxguitar.gui.util;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;

public class TGMusicKeyUtils {
	
	public static final String PREFIX_CHORD = "chord";
	
	public static final String PREFIX_SCALE = "scale";
	
	public static final String PREFIX_TUNING = "tuning";
	
	public static final String PREFIX_FRETBOARD = "fretboard";
	
	public static final String PREFIX_MATRIX = "matrix";
	
	private static final String[][] DEFAULT_KEY_NAMES = new String[][]{
		{"C","C#","Cb"},
		{"D","D#","Db"},
		{"E","E#","Eb"},
		{"F","F#","Fb"},
		{"G","G#","Gb"},
		{"A","A#","Ab"},
		{"B","B#","Bb"}
	};
	
	public static String[] getSharpKeyNames(String prefix){
		return new TGMusicKeyNames(true,prefix).getNames();
	}
	
	public static String[] getFlatKeyNames(String prefix){
		return new TGMusicKeyNames(false,prefix).getNames();
	}
	
	protected static void loadKeyNames(String[] names,String prefix,boolean sharp){
		if(sharp){
			loadSharpKeyNames(names, prefix);
		}else{
			loadFlatKeyNames(names, prefix);
		}
	}
	
	private static void loadSharpKeyNames(String[] names,String prefix){
		names[0] = getName(prefix,0,0);
		names[1] = getName(prefix,0,1);
		names[2] = getName(prefix,1,0);
		names[3] = getName(prefix,1,1);
		names[4] = getName(prefix,2,0);
		names[5] = getName(prefix,3,0);
		names[6] = getName(prefix,3,1);
		names[7] = getName(prefix,4,0);
		names[8] = getName(prefix,4,1);
		names[9] = getName(prefix,5,0);
		names[10] = getName(prefix,5,1);
		names[11] = getName(prefix,6,0);
	}
	
	private static void loadFlatKeyNames(String[] names,String prefix){
		names[0] = getName(prefix,0,0);
		names[1] = getName(prefix,1,2);
		names[2] = getName(prefix,1,0);
		names[3] = getName(prefix,2,2);
		names[4] = getName(prefix,2,0);
		names[5] = getName(prefix,3,0);
		names[6] = getName(prefix,4,2);
		names[7] = getName(prefix,4,0);
		names[8] = getName(prefix,5,2);
		names[9] = getName(prefix,5,0);
		names[10] = getName(prefix,6,2);
		names[11] = getName(prefix,6,0);
	}
	
	private static String getName(String prefix,int key,int signature){
		String resource = ("key." + prefix + "." + key + "." + signature);
		return TuxGuitar.instance().getLanguageManager().getProperty(resource,DEFAULT_KEY_NAMES[key][signature]);
	}
}

class TGMusicKeyNames implements LanguageLoader{
	
	private boolean sharp;
	private String prefix;
	private String[] names;
	
	public TGMusicKeyNames(boolean sharp,String prefix){
		this.sharp = sharp;
		this.prefix = prefix;
		this.names = new String[12];
		this.loadProperties();
		
		TuxGuitar.instance().getLanguageManager().addLoader(this);
	}
	
	public String[] getNames(){
		return this.names;
	}
	
	public void loadProperties() {
		TGMusicKeyUtils.loadKeyNames(this.names, this.prefix, this.sharp);
	}
}
