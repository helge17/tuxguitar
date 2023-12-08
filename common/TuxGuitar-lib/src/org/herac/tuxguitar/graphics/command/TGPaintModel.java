package org.herac.tuxguitar.graphics.command;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class TGPaintModel implements TGPaintCommand {
	
	private TGPaintCommand[] commands;
	
	public TGPaintModel(TGPaintCommand... commands) {
		this.commands = commands;
	}
	
	public void paint(UIPainter painter, float x, float y, float scale) {
		for(TGPaintCommand command : this.commands) {
			command.paint(painter, x, y, scale);
		}
	}
	
	public float getMaximumX() {
		Float maximum = null;
		for(TGPaintCommand command : this.commands) {
			Float current = command.getMaximumX();
			if( maximum == null || current > maximum ) {
				maximum = current;
			}
		}
		return maximum != null ? maximum : 0f;
	}
	
	public float getMaximumY() {
		Float maximum = null;
		for(TGPaintCommand command : this.commands) {
			Float current = command.getMaximumY();
			if( maximum == null || current > maximum ) {
				maximum = current;
			}
		}
		return maximum != null ? maximum : 0f;
	}
	
	public float getMinimumX() {
		Float minimum = null;
		for(TGPaintCommand command : this.commands) {
			Float current = command.getMinimumX();
			if( minimum == null || current < minimum ) {
				minimum = current;
			}
		}
		return minimum != null ? minimum : 0f;
	}
	
	public float getMinimumY() {
		Float minimum = null;
		for(TGPaintCommand command : this.commands) {
			Float current = command.getMinimumY();
			if( minimum == null || current < minimum ) {
				minimum = current;
			}
		}
		return minimum != null ? minimum : 0f;
	}
	
	public float getWidth() {
		return (this.getMaximumX() - this.getMinimumX());
	}
	
	public float getHeight() {
		return (this.getMaximumY() - this.getMinimumY());
	}
}
