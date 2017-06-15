package org.herac.tuxguitar.ui.qt.resource;

import java.util.HashMap;
import java.util.Map;

import com.trolltech.qt.core.Qt.MouseButton;

public class QTMouseButton {
	
	private static final Map<MouseButton, Integer> MOUSE_BUTTON_MAP = QTMouseButton.createMouseButtonMap();
	
	private QTMouseButton() {
		super();
	}
	
	private static Map<MouseButton, Integer> createMouseButtonMap() {
		Map<MouseButton, Integer> cursorMap = new HashMap<MouseButton, Integer>();
		cursorMap.put(MouseButton.NoButton, 0);
		cursorMap.put(MouseButton.LeftButton, 1);
		cursorMap.put(MouseButton.MidButton, 2);
		cursorMap.put(MouseButton.RightButton, 3);
		
		return cursorMap;
	}
	
	public static Integer getMouseButton(MouseButton mouseButton) {
		return MOUSE_BUTTON_MAP.get(mouseButton);
	}
}
