package app.tuxguitar.app.tools.scale;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.tools.scale.xml.ScaleReader;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.song.models.TGScale;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class ScaleManager {

	private static final String[] KEY_NAMES = new String[]    {"C", "C#", "Db", "D", "D#", "Eb", "E", "F", "F#", "Gb", "G", "G#", "Ab", "A",  "A#", "Bb", "B"};

	private static final String KEY_SEPARATOR = ",";

	public static final int NONE_SELECTION = -1;

	private TGContext context;

	private List<ScaleInfo> scales;

	private TGScale scale;

	private int scaleIndex;

	private int selectionKeyIndex;

	private ScaleManager(TGContext context){
		this.context = context;
		this.scales = new ArrayList<ScaleInfo>();
		this.scale = TuxGuitar.getInstance().getSongManager().getFactory().newScale();
		this.selectionKeyIndex = 0;
		this.scaleIndex = NONE_SELECTION;
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

	public void selectScale(int scaleIndex, int keyIndex){
		if( scaleIndex == NONE_SELECTION ){
			getScale().clear();
		}
		else if(scaleIndex >= 0 && scaleIndex < this.scales.size()){
			getScale().clear();
			ScaleInfo info = this.scales.get(scaleIndex);
			String[] keys = info.getKeys().split(KEY_SEPARATOR);
			for (int i = 0; i < keys.length; i ++){
				int note = (Integer.parseInt(keys[i]) - 1);
				if( note >= 0 && note < 12 ){
					getScale().setNote(note,true);
				}
			}
			getScale().setKeyName(KEY_NAMES[keyIndex]);
		}
		this.scaleIndex = scaleIndex;
		this.selectionKeyIndex = keyIndex;
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
			return this.scales.get(index).getName();
		}
		return null;
	}

	public String getScaleKeys(int index) {
		if(index >= 0 && index < this.scales.size()) {
			return this.scales.get(index).getKeys();
		}
		return null;
	}

	public String[] getScaleNames(){
		String[] names = new String[this.scales.size()];
		for(int i = 0;i < this.scales.size();i ++){
			ScaleInfo info = this.scales.get(i);
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

	public int getScaleIndex() {
		return this.scaleIndex;
	}

	public int getSelectionKeyIndex() {
		return this.selectionKeyIndex;
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
