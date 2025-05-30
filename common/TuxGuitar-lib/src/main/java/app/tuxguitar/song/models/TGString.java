/*
 * Created on 30-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.tuxguitar.song.models;

import app.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGString {

	private int number;
	private int value;

	public TGString(){
		this.number = 0;
		this.value = 0;
	}

	public int getNumber() {
		return this.number;
	}

	public int getValue() {
		return this.value;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isEqual(TGString string){
		return (this.getNumber() == string.getNumber() && this.getValue() == string.getValue());
	}

	public TGString clone(TGFactory factory){
		TGString tgString = factory.newString();
		tgString.copyFrom(this);
		return tgString;
	}

	public void copyFrom(TGString string){
		this.setNumber(string.getNumber());
		this.setValue(string.getValue());
	}
}
