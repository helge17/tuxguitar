package org.herac.tuxguitar.android.view.tablature;

public class TGScrollAxis {
	
	private boolean enabled;
	private float maximum;
	private float minimum;
	private float value;
	
	public TGScrollAxis() {
		super();
	}
	
	public void reset(boolean enabled, float maximum, float minimum, float value) {
		this.enabled = enabled;
		this.maximum = maximum;
		this.minimum = minimum;
		this.value = value;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public float getMaximum() {
		return maximum;
	}

	public void setMaximum(float maximum) {
		this.maximum = maximum;
	}

	public float getMinimum() {
		return minimum;
	}

	public void setMinimum(float minimum) {
		this.minimum = minimum;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
}
