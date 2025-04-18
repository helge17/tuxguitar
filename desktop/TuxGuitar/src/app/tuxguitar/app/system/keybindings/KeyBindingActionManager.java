package app.tuxguitar.app.system.keybindings;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.keybindings.xml.KeyBindingReader;
import app.tuxguitar.app.system.keybindings.xml.KeyBindingWriter;
import app.tuxguitar.app.util.TGFileUtils;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.ui.resource.UIKeyCombination;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

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
		this.keyBindingsActions.addAll(this.merge(KeyBindingActionDefaults.getDefaultKeyBindings(this.context), KeyBindingReader.getKeyBindings(getUserFileName())));
		this.listener = new KeyBindingListener(this);
	}

	public List<KeyBindingAction>merge(List<KeyBindingAction> defaultsBindings, List<KeyBindingAction> userBindings) {
		if (userBindings == null) {
			return defaultsBindings;
		}
		List<KeyBindingAction> list = userBindings;
		// add new default bindings, if no conflict with user-defined ones
		for (KeyBindingAction defaultAction : defaultsBindings) {
			boolean conflict = false;
			for (KeyBindingAction userAction : userBindings) {
				conflict |= (userAction.getAction().equals(defaultAction.getAction()));
				conflict |= (userAction.getCombination().equals(defaultAction.getCombination()));
				if (conflict) break;
			}
			if (!conflict) {
				list.add(defaultAction);
			}
		}


		// remove actions with empty key combinations
		List<KeyBindingAction> toRemove = new ArrayList<KeyBindingAction>();
		for (KeyBindingAction action : list) {
			if (action.getCombination().getKeys().isEmpty()) {
				toRemove.add(action);
			}
		}
		for (KeyBindingAction action : toRemove) {
			list.remove(action);
		}
		return list;
	}

	private String getUserFileName(){
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "shortcuts.xml";
	}

	public String getActionForKeyBinding(UIKeyCombination kb){
		Iterator<KeyBindingAction> it = this.keyBindingsActions.iterator();
		while(it.hasNext()){
			KeyBindingAction keyBindingAction = (KeyBindingAction)it.next();
			if( keyBindingAction.getCombination() != null && kb.equals( keyBindingAction.getCombination() )){
				if( isKeyBindingAvailable(keyBindingAction) ){
					return keyBindingAction.getAction();
				}
			}
		}
		return null;
	}

	public UIKeyCombination getKeyBindingForAction(String action){
		Iterator<KeyBindingAction> it = this.keyBindingsActions.iterator();
		while(it.hasNext()){
			KeyBindingAction keyBindingAction = (KeyBindingAction)it.next();
			if(action.equals( keyBindingAction.getAction() )){
				if( isKeyBindingAvailable(keyBindingAction) ){
					return keyBindingAction.getCombination();
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

	public void processKeyBinding(UIKeyCombination kb){
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
