package org.herac.tuxguitar.player.base;

public class MidiPercussion {
	
	public static final MidiPercussion[] PERCUSSION_LIST = new MidiPercussion[]{
		new MidiPercussion(35,"Acoustic Bass Drum"),
		new MidiPercussion(36,"Bass Drum 1"),
		new MidiPercussion(37,"Side Stick"),
		new MidiPercussion(38,"Acoustic Snare"),
		new MidiPercussion(39,"Hand Clap"),
		new MidiPercussion(40,"Electric Snare"),
		new MidiPercussion(41,"Low Floor Tom"),
		new MidiPercussion(42,"Closed Hi Hat"),
		new MidiPercussion(43,"High Floor Tom"),
		new MidiPercussion(44,"Pedal Hi-Hat"),
		new MidiPercussion(45,"Low Tom"),
		new MidiPercussion(46,"Open Hi-Hat"),
		new MidiPercussion(47,"Low-Mid Tom"),
		new MidiPercussion(48,"Hi-Mid Tom"),
		new MidiPercussion(49,"Crash Cymbal 1"),
		new MidiPercussion(50,"High Tom"),
		new MidiPercussion(51,"Ride Cymbal 1"),
		new MidiPercussion(52,"Chinese Cymbal"),
		new MidiPercussion(53,"Ride Bell"),
		new MidiPercussion(54,"Tambourine"),
		new MidiPercussion(55,"Splash Cymbal"),
		new MidiPercussion(56,"Cowbell"),
		new MidiPercussion(57,"Crash Cymbal 2"),
		new MidiPercussion(58,"Vibraslap"),
		new MidiPercussion(59,"Ride Cymbal 2"),
		new MidiPercussion(60,"Hi Bongo"),
		new MidiPercussion(61,"Low Bongo"),
		new MidiPercussion(62,"Mute Hi Conga"),
		new MidiPercussion(63,"Open Hi Conga"),
		new MidiPercussion(64,"Low Conga"),
		new MidiPercussion(65,"High Timbale"),
		new MidiPercussion(66,"Low Timbale"),
		new MidiPercussion(67,"High Agogo"),
		new MidiPercussion(68,"Low Agogo"),
		new MidiPercussion(69,"Cabasa"),
		new MidiPercussion(70,"Maracas"),
		new MidiPercussion(71,"Short Whistle"),
		new MidiPercussion(72,"Long Whistle"),
		new MidiPercussion(73,"Short Guiro"),
		new MidiPercussion(74,"Long Guiro"),
		new MidiPercussion(75,"Claves"),
		new MidiPercussion(76,"Hi Wood Block"),
		new MidiPercussion(77,"Low Wood Block"),
		new MidiPercussion(78,"Mute Cuica"),
		new MidiPercussion(79,"Open Cuica"),
		new MidiPercussion(80,"Mute Triangle"),
		new MidiPercussion(81,"Open Triangle"),
	};
	
	private int value;
	private String name;
	
	public MidiPercussion(int value,String name){
		this.value = value;
		this.name = name;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public String getName(){
		return this.name;
	}
}