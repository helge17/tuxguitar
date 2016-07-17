package org.herac.tuxguitar.ui.qt.resource;

import org.herac.tuxguitar.ui.resource.UIKey;
import org.herac.tuxguitar.ui.resource.UIKeyConvination;

import com.trolltech.qt.core.Qt.Key;
import com.trolltech.qt.core.Qt.KeyboardModifier;
import com.trolltech.qt.core.Qt.KeyboardModifiers;
import com.trolltech.qt.gui.QKeyEvent;

public class QTKey {
	
	private static final QTKeyMap[] KEY_MAP = new QTKeyMap[] {
		new QTKeyMap(Key.Key_F1, UIKey.F1),
		new QTKeyMap(Key.Key_F2, UIKey.F2),
		new QTKeyMap(Key.Key_F3, UIKey.F3),
		new QTKeyMap(Key.Key_F4, UIKey.F4),
		new QTKeyMap(Key.Key_F5, UIKey.F5),
		new QTKeyMap(Key.Key_F6, UIKey.F6),
		new QTKeyMap(Key.Key_F7, UIKey.F7),
		new QTKeyMap(Key.Key_F8, UIKey.F8),
		new QTKeyMap(Key.Key_F9, UIKey.F9),
		new QTKeyMap(Key.Key_F10, UIKey.F10),
		new QTKeyMap(Key.Key_F11, UIKey.F11),
		new QTKeyMap(Key.Key_F12, UIKey.F12),
		new QTKeyMap(Key.Key_Escape, UIKey.ESC),
		new QTKeyMap(Key.Key_Pause, UIKey.PAUSE),
		new QTKeyMap(Key.Key_Print, UIKey.PRINT_SCREEN),
		new QTKeyMap(Key.Key_Insert, UIKey.INSERT),
		new QTKeyMap(Key.Key_Delete, UIKey.DELETE),
		new QTKeyMap(Key.Key_HomePage, UIKey.HOME),
		new QTKeyMap(Key.Key_PageUp, UIKey.PAGE_UP),
		new QTKeyMap(Key.Key_PageDown, UIKey.PAGE_DOWN),
		new QTKeyMap(Key.Key_End, UIKey.END),
		new QTKeyMap(Key.Key_Alt, UIKey.ALT),
		new QTKeyMap(Key.Key_AltGr, UIKey.ALT),
		new QTKeyMap(Key.Key_Control, UIKey.CONTROL),
		new QTKeyMap(Key.Key_Meta, UIKey.COMMAND),
		new QTKeyMap(Key.Key_Shift, UIKey.SHIFT),
		new QTKeyMap(Key.Key_Tab, UIKey.TAB),
		new QTKeyMap(Key.Key_Backspace, UIKey.BACKSPACE),
		new QTKeyMap(Key.Key_Space, UIKey.SPACE),
		new QTKeyMap(Key.Key_Enter, UIKey.ENTER),
		new QTKeyMap(Key.Key_Up, UIKey.UP),
		new QTKeyMap(Key.Key_Down, UIKey.DOWN),
		new QTKeyMap(Key.Key_Left, UIKey.LEFT),
		new QTKeyMap(Key.Key_Right, UIKey.RIGHT),
		new QTKeyMap(Key.Key_Plus, new UIKey("+")),
		new QTKeyMap(Key.Key_Minus, new UIKey("-")),
		new QTKeyMap(Key.Key_Comma, new UIKey(",")),
		new QTKeyMap(Key.Key_Period, new UIKey(".")),
		new QTKeyMap(Key.Key_Slash, new UIKey("/")),
		new QTKeyMap(Key.Key_multiply, new UIKey("*")),
		new QTKeyMap(Key.Key_Less, new UIKey("<")),
		new QTKeyMap(Key.Key_Greater, new UIKey(">"))
	};
	
	public static UIKey getKey(QKeyEvent keyEvent) {
		int keyCode = keyEvent.key();
		for(QTKeyMap keyMap : KEY_MAP) {
			if( keyMap.getCode().value() == keyCode ) {
				return keyMap.getKey();
			}
		}
		
		return new UIKey(Character.toString((char) (keyEvent.key() & 0xffff)).toLowerCase());
	}
	
	public static UIKeyConvination getConvination(QKeyEvent keyEvent) {
		UIKeyConvination keyConvination = new UIKeyConvination();
		
		KeyboardModifiers modifiers = keyEvent.modifiers();
		if( modifiers.isSet(KeyboardModifier.AltModifier)) {
			keyConvination.getKeys().add(UIKey.ALT);
		}
		if( modifiers.isSet(KeyboardModifier.ShiftModifier)) {
			keyConvination.getKeys().add(UIKey.SHIFT);
		}
		if( modifiers.isSet(KeyboardModifier.ControlModifier)) {
			keyConvination.getKeys().add(UIKey.CONTROL);
		}
		if( modifiers.isSet(KeyboardModifier.MetaModifier)) {
			keyConvination.getKeys().add(UIKey.COMMAND);
		}
		
		UIKey principalKey = QTKey.getKey(keyEvent);
		if(!keyConvination.contains(principalKey)) {
			keyConvination.getKeys().add(principalKey);
		}
		
		return keyConvination;
	}
	
	private static class QTKeyMap {
		
		private Key code;
		private UIKey key;
		
		public QTKeyMap(Key code, UIKey key) {
			this.code = code;
			this.key = key;
		}
		
		public Key getCode() {
			return this.code;
		}
		
		public UIKey getKey() {
			return this.key;
		}
	}
}
