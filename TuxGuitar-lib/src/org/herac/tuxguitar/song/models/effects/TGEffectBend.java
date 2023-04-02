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
 * update guiv 2023-04-01, store amplitude and direction of first movement, for graphical representation
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGEffectBend {
	
	public static final int SEMITONE_LENGTH = 1;
	public static final int MAX_POSITION_LENGTH = 12;
	public static final int MAX_VALUE_LENGTH = (SEMITONE_LENGTH * 12);
	
	private List<BendPoint> points;
	private int firstMovementAmplitude = 0;	// bend: positive, release: negative, hold: zero
	
	public TGEffectBend(){
		this.points = new ArrayList<BendPoint>();
	}
	
	public void addPoint(int position,int value){
		this.points.add(new BendPoint(position,value));
		updateFirstAmplitude();
	}
	
	public List<BendPoint> getPoints(){
		return this.points;
	}
	
	public int getFirstMovementAmplitude()  {
		return this.firstMovementAmplitude;
	}
	
	public TGEffectBend clone(TGFactory factory){
		TGEffectBend effect = factory.newEffectBend();
		Iterator<BendPoint> it = getPoints().iterator();
		while(it.hasNext()){
			BendPoint point = it.next();
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
	
	private void updateFirstAmplitude()  {
		if (points.size() < 2) return;
		// bend amplitude to display: 1st max if bending, init value if releasing
		boolean up = false;
		boolean down = false;
		int amplitude = points.get(0).getValue();
		for (BendPoint nextPoint : points)  {
			if (nextPoint.getValue() < amplitude)  {
				if (!up)  {
					// initial release
					down = true;
				}
				break;
			} else if (nextPoint.getValue() > amplitude)  {
				// (still) bending
				up = true;
				amplitude = nextPoint.getValue();
			}
		}
		if (up == down) firstMovementAmplitude = 0;
		else if (up) firstMovementAmplitude = amplitude;
		else firstMovementAmplitude = (-amplitude);
	}
}
