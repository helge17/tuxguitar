package org.herac.tuxguitar.gm.port;

public class GMProgram {
	
	private Integer channel1;
	private Integer channel2;
	private Integer program;
	private Integer[] controllers;
	
	public GMProgram() {
		this.controllers = new Integer[128];
	}

	public Integer getChannel1() {
		return channel1;
	}

	public void setChannel1(Integer channel1) {
		this.channel1 = channel1;
	}

	public Integer getChannel2() {
		return channel2;
	}

	public void setChannel2(Integer channel2) {
		this.channel2 = channel2;
	}

	public Integer getProgram() {
		return program;
	}

	public void setProgram(Integer program) {
		this.program = program;
	}

	public Integer[] getControllers() {
		return controllers;
	}

	public void setController(Integer controller, Integer value) {
		if( controller != null && controller >= 0 && controller < this.controllers.length ) {
			this.controllers[controller] = value;
		}
	}
	
	public Integer getController(Integer controller) {
		if( controller != null && controller >= 0 && controller < this.controllers.length ) {
			return this.controllers[controller];
		}
		return null;
	}
	
	public boolean isSameChannel(Integer channel1, Integer channel2) {
		if( this.channel1 == null || this.channel2 == null || channel1 == null || channel2 == null ) {
			return false;
		}
		return (this.channel1.equals(channel1) && this.channel2.equals(channel2));
	}
}
