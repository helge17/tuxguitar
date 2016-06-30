package org.herac.tuxguitar.ui.jfx.resource;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.input.MouseButton;

public class JFXMouseButton {
	
	private static final Map<MouseButton, Integer> MOUSE_BUTTON_MAP = JFXMouseButton.createMouseButtonMap();
	
	private JFXMouseButton() {
		super();
	}
	
	private static Map<MouseButton, Integer> createMouseButtonMap() {
		Map<MouseButton, Integer> cursorMap = new HashMap<MouseButton, Integer>();
		cursorMap.put(MouseButton.NONE, 0);
		cursorMap.put(MouseButton.PRIMARY, 1);
		cursorMap.put(MouseButton.MIDDLE, 2);
		cursorMap.put(MouseButton.SECONDARY, 3);
		
		return cursorMap;
	}
	
	public static Integer getMouseButton(MouseButton mouseButton) {
		return MOUSE_BUTTON_MAP.get(mouseButton);
	}
}
