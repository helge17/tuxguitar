package org.herac.tuxguitar.io.gpx.score;

public class GPXDrumkit {
	
	public static final GPXDrumkit[] DRUMKITS = new GPXDrumkit[]{
		new GPXDrumkit(36, 0 , 0),
		new GPXDrumkit(36, 0 , 0),
		new GPXDrumkit(37, 1 , 2),
		new GPXDrumkit(38, 1 , 0),
		new GPXDrumkit(41, 5 ,0),
		new GPXDrumkit(42, 10 ,0),
		new GPXDrumkit(43, 6 ,0),
		new GPXDrumkit(44, 11 ,0),
		new GPXDrumkit(45, 7 ,0),
		new GPXDrumkit(46, 10 ,2),
		new GPXDrumkit(47, 8 ,0),
		new GPXDrumkit(48, 9 ,0),
		new GPXDrumkit(49, 12 ,0),
		new GPXDrumkit(50, 9 ,0),
		new GPXDrumkit(51, 15 ,0),
		new GPXDrumkit(52, 16 ,0),
		new GPXDrumkit(53, 15 ,2),
		new GPXDrumkit(55, 14 ,0),
		new GPXDrumkit(56, 3 ,0),
		new GPXDrumkit(57, 13 ,0),
		new GPXDrumkit(59, 15 ,1),
	};
	
	private int element;
	private int variation;
	private int midiValue;
	
	public GPXDrumkit(int midiValue, int element, int variation){
		this.midiValue = midiValue;
		this.element = element;
		this.variation = variation;
	}

	public int getElement() {
		return element;
	}

	public int getVariation() {
		return variation;
	}

	public int getMidiValue() {
		return midiValue;
	}
}
