package org.herac.tuxguitar.app.actions.note;

public class SetNoteFretNumber6Action extends SetNoteFretNumberAction {
	
	private static final int NUMBER = 6;
	
	public SetNoteFretNumber6Action(){
		super(NUMBER);
	}
	
	public static String getActionName(){
		return SetNoteFretNumberAction.getActionName(NUMBER);
	}
}
