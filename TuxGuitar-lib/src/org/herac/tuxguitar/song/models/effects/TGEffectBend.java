/*
 * Created on 26-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models.effects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGEffectBend {
	public static final int SEMITONE_LENGTH = 1;
	public static final int MAX_POSITION_LENGTH = 12;
	public static final int MAX_VALUE_LENGTH = (SEMITONE_LENGTH * 12);
	
	private List points;
	
	public TGEffectBend(){
		this.points = new ArrayList();
	}
	
	public void addPoint(int position,int value){
		this.points.add(new BendPoint(position,value));
	}
	
	public List getPoints(){
		return this.points;
	}
	
	public TGEffectBend clone(TGFactory factory){
		TGEffectBend effect = factory.newEffectBend();
		Iterator it = getPoints().iterator();
		while(it.hasNext()){
			BendPoint point = (BendPoint)it.next();
			effect.addPoint(point.getPosition(),point.getValue());
		}
		return effect;
	}
	
	public class BendPoint{
		private int position;
		private int value;
		
		public BendPoint(int position,int value){
			this.position = position;
			this.value = value;
		}
		
		public int getPosition() {
			return this.position;
		}
		
		public int getValue() {
			return this.value;
		}
		
		public long getTime(long duration){
			return (duration * getPosition() / MAX_POSITION_LENGTH);
		}
		
		public Object clone(){
			return new BendPoint(getPosition(),getValue());
		}
	}
	
}
