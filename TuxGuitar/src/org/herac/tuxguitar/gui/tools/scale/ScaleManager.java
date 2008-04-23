package org.herac.tuxguitar.gui.tools.scale;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.tools.scale.xml.ScaleReader;
import org.herac.tuxguitar.gui.util.TGMusicKeyUtils;
import org.herac.tuxguitar.gui.util.TGFileUtils;
import org.herac.tuxguitar.song.models.TGScale;

public class ScaleManager {
	private static final String[] KEY_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_SCALE);
	
	private static final String KEY_SEPARATOR = ",";
	
	public static final int NONE_SELECTION = -1;
	
	private List scales;
	
	private TGScale scale;
	
	private int selectionIndex;
	
	private int selectionKey;
	
	public ScaleManager(){
		this.scales = new ArrayList();
		this.scale = TuxGuitar.instance().getSongManager().getFactory().newScale();
		this.selectionKey = 0;
		this.selectionIndex = NONE_SELECTION;
		this.loadScales();
	}
	
	public TGScale getScale() {
		return this.scale;
	}
	
	public void selectScale(int index,int key){
		if(index == NONE_SELECTION){
			getScale().clear();
		}
		else if(index >= 0 && index < this.scales.size()){
			getScale().clear();
			ScaleInfo info = (ScaleInfo)this.scales.get(index);
			String[] keys = info.getKeys().split(KEY_SEPARATOR);
			for (int i = 0; i < keys.length; i ++){
				int note = (Integer.parseInt(keys[i]) - 1);
				if(note >= 0 && note < 12){
					getScale().setNote(note,true);
				}
			}
			getScale().setKey(key);
		}
		this.selectionIndex = index;
		this.selectionKey = key;
	}
	
	public String[] getScaleNames(){
		String[] names = new String[this.scales.size()];
		for(int i = 0;i < this.scales.size();i ++){
			ScaleInfo info = (ScaleInfo)this.scales.get(i);
			names[i] = info.getName();
		}
		return names;
	}
	
	public String[] getKeyNames(){
		return KEY_NAMES;
	}
	
	public int getSelectionIndex() {
		return this.selectionIndex;
	}
	
	public int getSelectionKey() {
		return this.selectionKey;
	}
	
	private void loadScales(){
		try{
			new ScaleReader().loadScales(this.scales,getScalesFileName());
		} catch (Throwable e) {
			e.printStackTrace();
		} 
	}
	
	private String getScalesFileName(){
		return TGFileUtils.PATH_SCALES + File.separator + "scales.xml";
	}
}
