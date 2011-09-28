package org.herac.tuxguitar.app.actions.note;

public class SetNoteFretNumber7Action extends SetNoteFretNumberAction {
	
	private static final int NUMBER = 7;
	
	public SetNoteFretNumber7Action(){
		super(NUMBER);
	}
	
	public static String getActionName(){
		return SetNoteFretNumberAction.getActionName(NUMBER);
	}
}
