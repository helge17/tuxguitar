package org.herac.tuxguitar.app.system.keybindings;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.keybindings.xml.KeyBindingReader;
import org.herac.tuxguitar.app.system.keybindings.xml.KeyBindingWriter;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.resource.UIKeyConvination;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class KeyBindingActionManager {
	
	private TGContext context;
	private List<KeyBindingAction> keyBindingsActions;
	private KeyBindingListener listener;
	
	private KeyBindingActionManager(TGContext context){
		this.context = context;
		this.keyBindingsActions = new ArrayList<KeyBindingAction>();
		this.init();
	}
	
	public void init(){
		List<KeyBindingAction> enabled = KeyBindingReader.getKeyBindings(getUserFileName());
		this.keyBindingsActions.addAll( (enabled != null ? enabled : KeyBindingActionDefaults.getDefaultKeyBindings(this.context)) );
		this.listener = new KeyBindingListener(this);
	}
	
	private String getUserFileName(){
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "shortcuts.xml";
	}
	
	public String getActionForKeyBinding(UIKeyConvination kb){
		Iterator<KeyBindingAction> it = this.keyBindingsActions.iterator();
		while(it.hasNext()){
			KeyBindingAction keyBindingAction = (KeyBindingAction)it.next();
			if( keyBindingAction.getConvination() != null && kb.equals( keyBindingAction.getConvination() )){
				if( isKeyBindingAvailable(keyBindingAction) ){
					return keyBindingAction.getAction();
				}
			}
		}
		return null;
	}
	
	public UIKeyConvination getKeyBindingForAction(String action){
		Iterator<KeyBindingAction> it = this.keyBindingsActions.iterator();
		while(it.hasNext()){
			KeyBindingAction keyBindingAction = (KeyBindingAction)it.next();
			if(action.equals( keyBindingAction.getAction() )){
				if( isKeyBindingAvailable(keyBindingAction) ){
					return keyBindingAction.getConvination();
				}
			}
		}
		return null;
	}
	
	public boolean isKeyBindingAvailable(KeyBindingAction keyBindingAction){
		String actionId = keyBindingAction.getAction();
		if( actionId != null ){
			return TuxGuitar.getInstance().getActionAdapterManager().getKeyBindingActionIds().hasActionId(actionId);
		}
		return false;
	}
	
	public void reset(List<KeyBindingAction> keyBindings){
		this.keyBindingsActions.clear();
		this.keyBindingsActions.addAll(keyBindings);
	}
	
	public List<KeyBindingAction> getKeyBindingActions(){
		return this.keyBindingsActions;
	}
	
	public void saveKeyBindings(){
		KeyBindingWriter.setBindings(getKeyBindingActions(),getUserFileName());
	}
	
	public void appendListenersTo(UIControl control){
		control.addKeyPressedListener(this.listener);
	}
	
	public void processKeyBinding(UIKeyConvination kb){
		final String actionId = getActionForKeyBinding(kb);
		if( actionId != null ){
			new TGActionProcessor(this.context, actionId).process();
		}
	}
	
	public static KeyBindingActionManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, KeyBindingActionManager.class.getName(), new TGSingletonFactory<KeyBindingActionManager>() {
			public KeyBindingActionManager createInstance(TGContext context) {
				return new KeyBindingActionManager(context);
			}
		});
	}
}
