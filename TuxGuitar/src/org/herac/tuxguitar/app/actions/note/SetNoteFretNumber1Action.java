package org.herac.tuxguitar.app.actions.note;

public class SetNoteFretNumber1Action extends SetNoteFretNumberAction {
	
	private static final int NUMBER = 1;
	
	public SetNoteFretNumber1Action(){
		super(NUMBER);
	}
	
	public static String getActionName(){
		return SetNoteFretNumberAction.getActionName(NUMBER);
	}
}
