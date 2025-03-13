package app.tuxguitar.io.tef2.base;

public class TETempo {

	private int value;

	public TETempo(int value){
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public String toString(){
		String string = new String("[TEMPO]");
		string += "\n     Value:       " + getValue();
		return string;
	}
}
