package app.tuxguitar.io.abc.base;

public class ABCRepeat {

	private int data1;

	private int data2;

	public ABCRepeat(int data1,int data2){
		this.data1 = data1;
		this.data2 = data2;
	}

	public int getData1() {
		return this.data1;
	}

	public int getData2() {
		return this.data2;
	}

	public String toString(){
		String string = new String("[REPEAT]     ");
		string += (this.getData1() + "-" + this.getData2());
		return string;
	}
}
