package org.herac.tuxguitar.app.system.keybindings;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.keybindings.xml.KeyBindingReader;
import org.herac.tuxguitar.app.system.keybindings.xml.KeyBindingWriter;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class KeyBindingActionManager {
	
	private List keyBindingsActions;
	private KeyBindingListener listener;
	
	public KeyBindingActionManager(){
		this.keyBindingsActions = new ArrayList();
		this.init();
	}
	
	public void init(){
		List enabled = KeyBindingReader.getKeyBindings(getUserFileName());
		this.keyBindingsActions.addAll( (enabled != null ? enabled : KeyBindingActionDefaults.getDefaultKeyBindings()) );
		this.listener = new KeyBindingListener(this);
	}
	
	private String getUserFileName(){
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "shortcuts.xml";
	}
	
	public String getActionForKeyBinding(KeyBinding kb){
		Iterator it = this.keyBindingsActions.iterator();
		while(it.hasNext()){
			KeyBindingAction keyBindingAction = (KeyBindingAction)it.next();
			if( keyBindingAction.getKeyBinding() != null && kb.isSameAs( keyBindingAction.getKeyBinding() )){
				if( isKeyBindingAvailable(keyBindingAction) ){
					return keyBindingAction.getAction();
				}
			}
		}
		return null;
	}
	
	public KeyBinding getKeyBindingForAction(String action){
		Iterator it = this.keyBindingsActions.iterator();
		while(it.hasNext()){
			KeyBindingAction keyBindingAction = (KeyBindingAction)it.next();
			if(action.equals( keyBindingAction.getAction() )){
				if( isKeyBindingAvailable(keyBindingAction) ){
					return keyBindingAction.getKeyBinding();
				}
			}
		}
		return null;
	}
	
	public boolean isKeyBindingAvailable(KeyBindingAction keyBindingAction){
		String actionId = keyBindingAction.getAction();
		if( actionId != null ){
			return TuxGuitar.instance().getActionAdapterManager().getKeyBindingActionIds().hasActionId(actionId);
		}
		return false;
	}
	
	public void reset(List keyBindings){
		this.keyBindingsActions.clear();
		this.keyBindingsActions.addAll(keyBindings);
	}
	
	public List getKeyBindingActions(){
		return this.keyBindingsActions;
	}
	
	public void saveKeyBindings(){
		KeyBindingWriter.setBindings(getKeyBindingActions(),getUserFileName());
	}
	
	public void appendListenersTo(Control control){
		control.addKeyListener(this.listener);
	}
	
	public void processKeyBinding(KeyBinding kb){
		final String actionId = getActionForKeyBinding(kb);
		if( actionId != null ){
			TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
				public void run() throws TGException {
					TuxGuitar.instance().getActionManager().execute(actionId);
				}
			});
		}
	}
}
