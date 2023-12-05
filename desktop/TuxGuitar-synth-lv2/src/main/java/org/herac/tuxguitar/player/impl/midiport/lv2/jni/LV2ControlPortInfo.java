package org.herac.tuxguitar.player.impl.midiport.lv2.jni;

public class LV2ControlPortInfo {
	
	private String name;
	private boolean toggled;
	private float defaultValue;
	private float minimumValue;
	private float maximumValue;
	
	public LV2ControlPortInfo(String name, boolean toggled, float defaultValue, float minimumValue, float maximumValue) {
		this.name = name;
		this.toggled = toggled;
		this.defaultValue = defaultValue;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
	}

	public String getName() {
		return name;
	}

	public boolean isToggled() {
		return toggled;
	}

	public float getDefaultValue() {
		return defaultValue;
	}

	public float getMinimumValue() {
		return minimumValue;
	}

	public float getMaximumValue() {
		return maximumValue;
	}
}
