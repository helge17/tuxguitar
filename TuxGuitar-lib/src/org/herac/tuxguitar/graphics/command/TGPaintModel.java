package org.herac.tuxguitar.graphics.command;
import org.herac.tuxguitar.graphics.TGPainter;

public class TGPaintModel implements TGPaintCommand {
	
	private TGPaintCommand[] commands;
	
	public TGPaintModel(TGPaintCommand... commands) {
		this.commands = commands;
	}
	
	public void paint(TGPainter painter, float x, float y, float scale) {
		for(TGPaintCommand command : this.commands) {
			command.paint(painter, x, y, scale);
		}
	}
}
