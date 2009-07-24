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
public abstract class TGDivisionType {
	public static final TGDivisionType NORMAL = newDivisionType(1,1);
	
	/**
	 * Cantidad de Duraciones que entran en los tiempos
	 */
	private int enters;
	/**
	 * Tiempos
	 */
	private int times;
	
	public TGDivisionType(){
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
	
	public boolean isEqual(TGDivisionType divisionType){
		return (divisionType.getEnters() == getEnters() && divisionType.getTimes() == getTimes());
	}
	
	public TGDivisionType clone(TGFactory factory){
		TGDivisionType divisionType = factory.newDivisionType();
		copy(divisionType);
		return divisionType;
	}
	
	public void copy(TGDivisionType divisionType){
		divisionType.setEnters(this.enters);
		divisionType.setTimes(this.times);
	}
	
	private static TGDivisionType newDivisionType(int enters,int times){
		TGDivisionType divisionType = new TGFactory().newDivisionType();
		divisionType.setEnters(enters);
		divisionType.setTimes(times);
		return divisionType;
	}
	
}
