package app.tuxguitar.player.base;

public class MidiPercussionKey {

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