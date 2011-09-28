package org.herac.tuxguitar.app.actions.note;

public class SetNoteFretNumber4Action extends SetNoteFretNumberAction {
	
	private static final int NUMBER = 4;
	
	public SetNoteFretNumber4Action(){
		super(NUMBER);
	}
	
	public static String getActionName(){
		return SetNoteFretNumberAction.getActionName(NUMBER);
	}
}
