package org.herac.tuxguitar.ui.swt.resource;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.herac.tuxguitar.ui.resource.UICursor;

public class SWTCursor {
	
	private static final Map<UICursor, Integer> CURSOR_MAP = SWTCursor.createCursorMap();
	
	private SWTCursor() {
		super();
	}
	
	private static Map<UICursor, Integer> createCursorMap() {
		Map<UICursor, Integer> cursorMap = new HashMap<UICursor, Integer>();
		cursorMap.put(UICursor.NORMAL, SWT.CURSOR_ARROW);
		cursorMap.put(UICursor.WAIT, SWT.CURSOR_WAIT);
		cursorMap.put(UICursor.HAND, SWT.CURSOR_HAND);
		cursorMap.put(UICursor.SIZEWE, SWT.CURSOR_SIZEWE);
		cursorMap.put(UICursor.SIZENS, SWT.CURSOR_SIZENS);
		return cursorMap;
	}
	
	public static Integer getCursorStyle(UICursor cursor) {
		if( CURSOR_MAP.containsKey(cursor) ) {
			return CURSOR_MAP.get(cursor);
		}
		return SWT.CURSOR_ARROW;
	}
	
	public static Cursor getCursor(Control control, UICursor cursor) {
		return control.getDisplay().getSystemCursor(SWTCursor.getCursorStyle(cursor));
	}
}
