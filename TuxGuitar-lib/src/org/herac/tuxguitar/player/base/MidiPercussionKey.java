package org.herac.tuxguitar.player.base;

public class MidiPercussionKey {
	
	public static final MidiPercussionKey[] PERCUSSION_KEY_LIST = new MidiPercussionKey[]{
		new MidiPercussionKey(35,"Acoustic Bass Drum"),
		new MidiPercussionKey(36,"Bass Drum 1"),
		new MidiPercussionKey(37,"Side Stick"),
		new MidiPercussionKey(38,"Acoustic Snare"),
		new MidiPercussionKey(39,"Hand Clap"),
		new MidiPercussionKey(40,"Electric Snare"),
		new MidiPercussionKey(41,"Low Floor Tom"),
		new MidiPercussionKey(42,"Closed Hi Hat"),
		new MidiPercussionKey(43,"High Floor Tom"),
		new MidiPercussionKey(44,"Pedal Hi-Hat"),
		new MidiPercussionKey(45,"Low Tom"),
		new MidiPercussionKey(46,"Open Hi-Hat"),
		new MidiPercussionKey(47,"Low-Mid Tom"),
		new MidiPercussionKey(48,"Hi-Mid Tom"),
		new MidiPercussionKey(49,"Crash Cymbal 1"),
		new MidiPercussionKey(50,"High Tom"),
		new MidiPercussionKey(51,"Ride Cymbal 1"),
		new MidiPercussionKey(52,"Chinese Cymbal"),
		new MidiPercussionKey(53,"Ride Bell"),
		new MidiPercussionKey(54,"Tambourine"),
		new MidiPercussionKey(55,"Splash Cymbal"),
		new MidiPercussionKey(56,"Cowbell"),
		new MidiPercussionKey(57,"Crash Cymbal 2"),
		new MidiPercussionKey(58,"Vibraslap"),
		new MidiPercussionKey(59,"Ride Cymbal 2"),
		new MidiPercussionKey(60,"Hi Bongo"),
		new MidiPercussionKey(61,"Low Bongo"),
		new MidiPercussionKey(62,"Mute Hi Conga"),
		new MidiPercussionKey(63,"Open Hi Conga"),
		new MidiPercussionKey(64,"Low Conga"),
		new MidiPercussionKey(65,"High Timbale"),
		new MidiPercussionKey(66,"Low Timbale"),
		new MidiPercussionKey(67,"High Agogo"),
		new MidiPercussionKey(68,"Low Agogo"),
		new MidiPercussionKey(69,"Cabasa"),
		new MidiPercussionKey(70,"Maracas"),
		new MidiPercussionKey(71,"Short Whistle"),
		new MidiPercussionKey(72,"Long Whistle"),
		new MidiPercussionKey(73,"Short Guiro"),
		new MidiPercussionKey(74,"Long Guiro"),
		new MidiPercussionKey(75,"Claves"),
		new MidiPercussionKey(76,"Hi Wood Block"),
		new MidiPercussionKey(77,"Low Wood Block"),
		new MidiPercussionKey(78,"Mute Cuica"),
		new MidiPercussionKey(79,"Open Cuica"),
		new MidiPercussionKey(80,"Mute Triangle"),
		new MidiPercussionKey(81,"Open Triangle"),
	};
	
	private int value;
	private String name;
	
	public MidiPercussionKey(int value,String name){
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