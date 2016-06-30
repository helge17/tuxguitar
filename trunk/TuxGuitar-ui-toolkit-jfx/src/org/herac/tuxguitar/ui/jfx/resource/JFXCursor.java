package org.herac.tuxguitar.ui.jfx.resource;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Cursor;

import org.herac.tuxguitar.ui.resource.UICursor;

public class JFXCursor {
	
	private static final Map<UICursor, Cursor> CURSOR_MAP = JFXCursor.createCursorMap();
	
	private JFXCursor() {
		super();
	}
	
	private static Map<UICursor, Cursor> createCursorMap() {
		Map<UICursor, Cursor> cursorMap = new HashMap<UICursor, Cursor>();
		cursorMap.put(UICursor.NORMAL, Cursor.DEFAULT);
		cursorMap.put(UICursor.WAIT, Cursor.WAIT);
		cursorMap.put(UICursor.HAND, Cursor.HAND);
		cursorMap.put(UICursor.SIZEWE, Cursor.H_RESIZE);
		cursorMap.put(UICursor.SIZENS, Cursor.V_RESIZE);
		return cursorMap;
	}
	
	public static Cursor getCursor(UICursor cursor) {
		return CURSOR_MAP.get(cursor);
	}
}
