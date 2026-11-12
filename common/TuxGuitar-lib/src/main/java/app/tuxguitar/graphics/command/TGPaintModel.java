package app.tuxguitar.graphics.command;
import java.util.function.Function;

import app.tuxguitar.ui.resource.UIPainter;

public class TGPaintModel implements TGPaintCommand {

	private static final boolean UPWARD = true;
	private static final boolean DOWNWARD = false;

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
		return this.getOutermost(TGPaintCommand::getMaximumX, UPWARD);
	}

	public float getMaximumY() {
		return this.getOutermost(TGPaintCommand::getMaximumY, UPWARD);
	}

	public float getMinimumX() {
		return this.getOutermost(TGPaintCommand::getMinimumX, DOWNWARD);
	}

	public float getMinimumY() {
		return this.getOutermost(TGPaintCommand::getMinimumY, DOWNWARD);
	}

	public float getWidth() {
		return (this.getMaximumX() - this.getMinimumX());
	}

	public float getHeight() {
		return (this.getMaximumY() - this.getMinimumY());
	}

	// the bound of the command lying furthest outward, or 0 without commands
	private float getOutermost(Function<TGPaintCommand, Float> bound, boolean upward) {
		Float outermost = null;
		for(TGPaintCommand command : this.commands) {
			Float current = bound.apply(command);
			if( outermost == null || isBeyond(current, outermost, upward) ) {
				outermost = current;
			}
		}
		return outermost != null ? outermost : 0f;
	}

	private static boolean isBeyond(float value, float bound, boolean upward) {
		return (upward ? value > bound : value < bound);
	}
}
