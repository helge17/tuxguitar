package org.herac.tuxguitar.ui.resource;

public class UIKeyLabel {
	
	public static final UIKeyLabel[] LABELS = new UIKeyLabel[] {
		new UIKeyLabel(UIKey.F1, "F1"),
		new UIKeyLabel(UIKey.F2, "F2"),
		new UIKeyLabel(UIKey.F3,"F3"),
		new UIKeyLabel(UIKey.F4,"F4"),
		new UIKeyLabel(UIKey.F5,"F5"),
		new UIKeyLabel(UIKey.F6,"F6"),
		new UIKeyLabel(UIKey.F7,"F7"),
		new UIKeyLabel(UIKey.F8,"F8"),
		new UIKeyLabel(UIKey.F9,"F9"),
		new UIKeyLabel(UIKey.F10,"F10"),
		new UIKeyLabel(UIKey.F11,"F11"),
		new UIKeyLabel(UIKey.F12,"F12"),
		new UIKeyLabel(UIKey.ESC,"Esc"),
		new UIKeyLabel(UIKey.PAUSE,"Pause"),
		new UIKeyLabel(UIKey.PRINT_SCREEN,"Print"),
		new UIKeyLabel(UIKey.INSERT,"Ins"),
		new UIKeyLabel(UIKey.DELETE,"Del"),
		new UIKeyLabel(UIKey.HOME,"Home"),
		new UIKeyLabel(UIKey.PAGE_UP,"PgUp"),
		new UIKeyLabel(UIKey.PAGE_DOWN,"PgDn"),
		new UIKeyLabel(UIKey.END,"End"),
		new UIKeyLabel(UIKey.UP,"Up"),
		new UIKeyLabel(UIKey.DOWN,"Down"),
		new UIKeyLabel(UIKey.LEFT,"Left"),
		new UIKeyLabel(UIKey.RIGHT,"Right"),
		new UIKeyLabel(UIKey.TAB,"Tab"),
		new UIKeyLabel(UIKey.SPACE,"Space"),
		new UIKeyLabel(UIKey.ENTER,"Enter"),
		new UIKeyLabel(UIKey.BACKSPACE,"BackSpace"),
		new UIKeyLabel(UIKey.CONTROL, "Control"),
		new UIKeyLabel(UIKey.SHIFT, "Shift"),
		new UIKeyLabel(UIKey.ALT,"Alt"),
		new UIKeyLabel(UIKey.COMMAND,"\u2318"),
	};
	
	private UIKey key;
	private String label;
	
	public UIKeyLabel(UIKey key, String label){
		this.key = key;
		this.label = label;
	}
	
	public UIKey getKey(){
		return this.key;
	}
	
	public String getLabel(){
		return this.label;
	}
}
