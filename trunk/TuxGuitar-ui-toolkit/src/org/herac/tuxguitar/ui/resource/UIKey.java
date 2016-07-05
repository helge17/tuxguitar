package org.herac.tuxguitar.ui.resource;

public class UIKey {
	
	public static final UIKey F1 = new UIKey("F1");
	public static final UIKey F2 = new UIKey("F2");
	public static final UIKey F3 = new UIKey("F3");
	public static final UIKey F4 = new UIKey("F4");
	public static final UIKey F5 = new UIKey("F5");
	public static final UIKey F6 = new UIKey("F6");
	public static final UIKey F7 = new UIKey("F7");
	public static final UIKey F8 = new UIKey("F8");
	public static final UIKey F9 = new UIKey("F9");
	public static final UIKey F10 = new UIKey("F10");
	public static final UIKey F11 = new UIKey("F11");
	public static final UIKey F12 = new UIKey("F12");
	public static final UIKey ESC = new UIKey("ESC");
	public static final UIKey PAUSE = new UIKey("Pause");
	public static final UIKey PRINT_SCREEN = new UIKey("PrintScreen");
	public static final UIKey INSERT = new UIKey("Ins");
	public static final UIKey DELETE = new UIKey("Del");
	public static final UIKey HOME = new UIKey("Home");
	public static final UIKey PAGE_UP = new UIKey("PgUp");
	public static final UIKey PAGE_DOWN = new UIKey("PgDn");
	public static final UIKey END = new UIKey("End");
	public static final UIKey ALT = new UIKey("Alt");
	public static final UIKey CONTROL = new UIKey("Control");
	public static final UIKey COMMAND = new UIKey("Command");
	public static final UIKey SHIFT = new UIKey("Shift");
	public static final UIKey TAB = new UIKey("Tab");
	public static final UIKey BACKSPACE = new UIKey("BackSpace");
	public static final UIKey SPACE = new UIKey("Space");
	public static final UIKey ENTER = new UIKey("Enter");
	public static final UIKey UP = new UIKey("Up");
	public static final UIKey DOWN = new UIKey("Down");
	public static final UIKey LEFT = new UIKey("Left");
	public static final UIKey RIGHT = new UIKey("Right");
	
	private String code;
	
	public UIKey(String code) {
		this.code = code;
	}
	
	public boolean equals(Object o) {
		if( o instanceof UIKey ) {
			return this.toString().equals(o.toString());
		}
		return false;
	}
	
	public String toString() {
		return this.code;
	}
}
