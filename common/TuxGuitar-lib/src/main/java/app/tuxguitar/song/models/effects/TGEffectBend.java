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
 * update guiv 2023-04-02, store amplitudes and directions of bend movements, for graphical representation
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGEffectBend {

	public static final int SEMITONE_LENGTH = 1;
	public static final int MAX_POSITION_LENGTH = 12;
	public static final int MAX_VALUE_LENGTH = (SEMITONE_LENGTH * 12);

	private List<BendPoint> points;
	// list of movements to be displayed
	// -1 means: release from 1*1/4
	// +2 means: bend towards 2*1/4
	// empty list means 'hold'
	private List<Integer> movements = new ArrayList<>();

	public TGEffectBend(){
		this.points = new ArrayList<BendPoint>();
	}

	public void addPoint(int position,int value){
		this.points.add(new BendPoint(position,value));
		updateMovements();
	}

	public List<BendPoint> getPoints(){
		return this.points;
	}

	public List<Integer> getMovements()  {
		return this.movements;
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

	private void updateMovements()  {
		movements.clear();
		if (points.size() < 2) return ;
		int start = points.get(0).getValue();
		int current = points.get(1).getValue();
		int i;
		for (i=2; i<points.size();i++)  {
			int next = points.get(i).getValue();
			if ((next-current) * (current-start) < 0) {
				// direction changed
				if (current-start > 0) {
					// end of bend movement
					movements.add(current);
				} else {
					// end of release movement
					movements.add(-start);
				}
				start = current;
			}
			current = next;
		}
		// last movement
		if (current-start > 0) {
			// end of bend movement
			movements.add(current);
		} else if (current-start<0) {
			// end of release movement
			movements.add(-start);
		}
	}
}
