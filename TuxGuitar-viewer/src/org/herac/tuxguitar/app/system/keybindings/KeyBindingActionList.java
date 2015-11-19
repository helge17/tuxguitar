package org.herac.tuxguitar.app.system.keybindings;

import org.herac.tuxguitar.app.action.impl.measure.GoNextMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.GoPreviousMeasureAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportPlayAction;

public class KeyBindingActionList {
	
	private static KeyBindingAction[] KB_ACTIONS = new KeyBindingAction[]{
		new KeyBindingAction(GoPreviousMeasureAction.NAME,new KeyBinding(KeyBindingConstants.LEFT,0)),
		new KeyBindingAction(GoNextMeasureAction.NAME,new KeyBinding(KeyBindingConstants.RIGHT,0)),
		new KeyBindingAction(TransportPlayAction.NAME,new KeyBinding(KeyBindingConstants.SPACE,0)),

	};
	
	public static boolean isReserved(KeyBinding kb){
		for(int i = 0;i < KB_ACTIONS.length;i++){
			if(kb.isSameAs(KB_ACTIONS[i].getKeyBinding())){
				return true;
			}
		}
		return false;
	}
	
	public static String getActionForKeyBinding(KeyBinding kb){
		for(int i = 0;i < KB_ACTIONS.length;i++){
			if(kb.isSameAs(KB_ACTIONS[i].getKeyBinding())){
				return KB_ACTIONS[i].getAction();
			}
		}
		return null;
	}
	
	public static KeyBinding getKeyBindingForAction(String action){
		for(int i = 0;i < KB_ACTIONS.length;i++){
			if(action.equals(KB_ACTIONS[i].getAction())){
				return KB_ACTIONS[i].getKeyBinding();
			}
		}
		return null;
	}
}
