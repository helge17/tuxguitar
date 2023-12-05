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
	
	public static final TGDivisionType TRIPLET = newDivisionType(3,2);
	
	public static final TGDivisionType[] ALTERED_DIVISION_TYPES = new TGDivisionType[]{
		newDivisionType(3,2),
		newDivisionType(5,4),
		newDivisionType(6,4),
		newDivisionType(7,4),
		newDivisionType(9,8),
		newDivisionType(10,8),
		newDivisionType(11,8),
		newDivisionType(12,8),
		newDivisionType(13,8),
	};
	
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
		TGDivisionType tgDivisionType = factory.newDivisionType();
		tgDivisionType.copyFrom(this);
		return tgDivisionType;
	}
	
	public void copyFrom(TGDivisionType divisionType){
		this.setEnters(divisionType.getEnters());
		this.setTimes(divisionType.getTimes());
	}
	
	private static TGDivisionType newDivisionType(int enters,int times){
		TGDivisionType divisionType = new TGFactory().newDivisionType();
		divisionType.setEnters(enters);
		divisionType.setTimes(times);
		return divisionType;
	}
	
}
