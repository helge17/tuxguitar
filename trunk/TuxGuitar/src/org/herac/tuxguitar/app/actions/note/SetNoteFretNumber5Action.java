package org.herac.tuxguitar.app.actions.note;

public class SetNoteFretNumber5Action extends SetNoteFretNumberAction {
	
	private static final int NUMBER = 5;
	
	public SetNoteFretNumber5Action(){
		super(NUMBER);
	}
	
	public static String getActionName(){
		return SetNoteFretNumberAction.getActionName(NUMBER);
	}
}
