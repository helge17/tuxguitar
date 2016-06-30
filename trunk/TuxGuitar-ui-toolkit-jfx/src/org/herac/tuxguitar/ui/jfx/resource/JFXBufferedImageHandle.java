package org.herac.tuxguitar.ui.jfx.resource;

import java.util.ArrayList;
import java.util.List;

public class JFXBufferedImageHandle {
	
	private float width;
	private float height;
	private List<JFXBufferedPainterCommand> commands;
	
	public JFXBufferedImageHandle(float width, float height) {
		this.width = width;
		this.height = height;
		this.commands = new ArrayList<JFXBufferedPainterCommand>();
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}

	public List<JFXBufferedPainterCommand> getCommands() {
		return commands;
	}
}
