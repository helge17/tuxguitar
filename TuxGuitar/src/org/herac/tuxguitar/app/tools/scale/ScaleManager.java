package org.herac.tuxguitar.app.tools.scale;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.scale.xml.ScaleReader;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.song.models.TGScale;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class ScaleManager {
	
	private static final String[] KEY_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_SCALE);
	
	private static final String KEY_SEPARATOR = ",";
	
	public static final int NONE_SELECTION = -1;
	
	private TGContext context;
	
	private List<ScaleInfo> scales;
	
	private TGScale scale;
	
	private int selectionIndex;
	
	private int selectionKey;
	
	private ScaleManager(TGContext context){
		this.context = context;
		this.scales = new ArrayList<ScaleInfo>();
		this.scale = TuxGuitar.getInstance().getSongManager().getFactory().newScale();
		this.selectionKey = 0;
		this.selectionIndex = NONE_SELECTION;
		this.loadScales();
	}
	
	public void addListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(ScaleEvent.EVENT_TYPE, listener);
	}
	
	public void removeListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(ScaleEvent.EVENT_TYPE, listener);
	}
	
	public void fireListeners(){
		TGEventManager.getInstance(this.context).fireEvent(new ScaleEvent());
	}
	
	public void selectScale(int index, int key){
		if( index == NONE_SELECTION ){
			getScale().clear();
		}
		else if(index >= 0 && index < this.scales.size()){
			getScale().clear();
			ScaleInfo info = (ScaleInfo)this.scales.get(index);
			String[] keys = info.getKeys().split(KEY_SEPARATOR);
			for (int i = 0; i < keys.length; i ++){
				int note = (Integer.parseInt(keys[i]) - 1);
				if( note >= 0 && note < 12 ){
					getScale().setNote(note,true);
				}
			}
			getScale().setKey(key);
		}
		this.selectionIndex = index;
		this.selectionKey = key;
		this.fireListeners();
	}
	
	public TGScale getScale() {
		return this.scale;
	}
	
	public int countScales() {
		return this.scales.size();
	}
	
	public String getScaleName(int index) {
		if(index >= 0 && index < this.scales.size()) {
			return (((ScaleInfo)this.scales.get(index)).getName());
		}
		return null;
	}
	
	public String getScaleKeys(int index) {
		if(index >= 0 && index < this.scales.size()) {
			return(((ScaleInfo)this.scales.get(index)).getKeys());
		}
		return null;
	}
	
	public String[] getScaleNames(){
		String[] names = new String[this.scales.size()];
		for(int i = 0;i < this.scales.size();i ++){
			ScaleInfo info = (ScaleInfo)this.scales.get(i);
			names[i] = info.getName();
		}
		return names;
	}
	
	public String getKeyName(int index){
		if( index >=0 && index < KEY_NAMES.length){
			return KEY_NAMES[ index ];
		}
		return null;
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
			new ScaleReader().loadScales(this.scales, TGResourceManager.getInstance(this.context).getResourceAsStream("scales/scales.xml") );
		} catch (Throwable e) {
			TGErrorManager.getInstance(this.context).handleError(e);
		} 
	}
	
	public static ScaleManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, ScaleManager.class.getName(), new TGSingletonFactory<ScaleManager>() {
			public ScaleManager createInstance(TGContext context) {
				return new ScaleManager(context);
			}
		});
	}
}
