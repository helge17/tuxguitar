package org.herac.tuxguitar.ui.qt.resource;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.ui.resource.UICursor;

import com.trolltech.qt.core.Qt.CursorShape;
import com.trolltech.qt.gui.QCursor;

public class QTCursor {
	
	private static final Map<UICursor, QCursor> CURSOR_MAP = QTCursor.createCursorMap();
	
	private QTCursor() {
		super();
	}
	
	private static Map<UICursor, QCursor> createCursorMap() {
		Map<UICursor, QCursor> cursorMap = new HashMap<UICursor, QCursor>();
		cursorMap.put(UICursor.NORMAL, new QCursor(CursorShape.ArrowCursor));
		cursorMap.put(UICursor.WAIT, new QCursor(CursorShape.WaitCursor));
		cursorMap.put(UICursor.HAND, new QCursor(CursorShape.OpenHandCursor));
		cursorMap.put(UICursor.SIZEWE, new QCursor(CursorShape.SizeHorCursor));
		cursorMap.put(UICursor.SIZENS, new QCursor(CursorShape.SizeVerCursor));
		return cursorMap;
	}
	
	public static QCursor getCursor(UICursor cursor) {
		return CURSOR_MAP.get(cursor);
	}
}
