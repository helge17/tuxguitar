package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UIProgressBar;

public class JFXProgressBar extends JFXControl<ProgressBar> implements UIProgressBar {
	
	private int value;
	private int maximum;
	private int minimum;
	
	public JFXProgressBar(JFXContainer<? extends Region> parent) {
		super(new ProgressBar(), parent);
	}

	public int getValue() {
		this.value = Math.round(this.minimum + ((this.maximum - this.minimum) * (float) this.getControl().getProgress()));
		
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
		
		double range = (this.maximum - this.minimum);
		double progress = (this.value - this.minimum);
		
		this.getControl().setProgress((range > 0 && progress > 0 ? (progress / range) : 0));
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
		if( this.minimum > this.maximum ) {
			this.minimum = this.maximum;
		}
		this.setValue((this.value <= this.maximum ? this.value : this.maximum ));
	}

	public int getMinimum() {
		return minimum;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
		if( this.maximum < this.minimum ) {
			this.maximum = this.minimum;
		}
		this.setValue((this.value >= this.minimum ? this.value : this.minimum ));
	}
}
