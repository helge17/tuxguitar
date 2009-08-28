/*
 * Created on 05-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGTupleto {
	public static final TGTupleto NORMAL = newTupleto(1,1);
	
	/**
	 * Cantidad de Duraciones que entran en los tiempos
	 */
	private int enters;
	/**
	 * Tiempos
	 */
	private int times;
	
	public TGTupleto(){
		this.enters = 1;
		this.times = 1;
	}
	
	public int getEnters() {
		return this.enters;
	}
	
	public void setEnters(int enters) {
		this.enters = enters;
	}
	
	public int getTimes() {
		return this.times;
	}
	
	public void setTimes(int times) {
		this.times = times;
	}
	
	public long convertTime(long time){
		return time * this.times / this.enters;
	}
	
	public boolean isEqual(TGTupleto tupleto){
		return (tupleto.getEnters() == getEnters() && tupleto.getTimes() == getTimes());
	}
	
	public TGTupleto clone(TGFactory factory){
		TGTupleto tupleto = factory.newTupleto();
		copy(tupleto);
		return tupleto;
	}
	
	public void copy(TGTupleto tupleto){
		tupleto.setEnters(this.enters);
		tupleto.setTimes(this.times);
	}
	
	private static TGTupleto newTupleto(int enters,int times){
		TGTupleto tupleto = new TGFactory().newTupleto();
		tupleto.setEnters(enters);
		tupleto.setTimes(times);
		return tupleto;
	}
	
}
