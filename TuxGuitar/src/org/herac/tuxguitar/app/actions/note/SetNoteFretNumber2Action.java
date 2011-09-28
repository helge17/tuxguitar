package org.herac.tuxguitar.app.actions.note;

public class SetNoteFretNumber2Action extends SetNoteFretNumberAction {
	
	private static final int NUMBER = 2;
	
	public SetNoteFretNumber2Action(){
		super(NUMBER);
	}
	
	public static String getActionName(){
		return SetNoteFretNumberAction.getActionName(NUMBER);
	}
}
