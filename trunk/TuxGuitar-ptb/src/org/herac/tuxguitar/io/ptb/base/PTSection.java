package org.herac.tuxguitar.io.ptb.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PTSection {
	
	private int number;
	private int staffs;
	private List positions;
	
	public PTSection(int number){
		this.number = number;
		this.positions = new ArrayList();
	}
	
	public int getNumber(){
		return this.number;
	}
	
	public int getStaffs() {
		return this.staffs;
	}
	
	public void setStaffs(int staffs) {
		this.staffs = staffs;
	}
	
	public List getPositions(){
		return this.positions;
	}
	
	public PTPosition getPosition(int position){
		Iterator it = getPositions().iterator();
		while(it.hasNext()){
			PTPosition p = (PTPosition)it.next();
			if(p.getPosition() == position){
				return p;
			}
		}
		PTPosition p = new PTPosition(position);
		getPositions().add(p);
		return p;
	}
	
	public int getNextPositionNumber(){
		int next = 0;
		Iterator it = getPositions().iterator();
		while(it.hasNext()){
			PTPosition p = (PTPosition)it.next();
			next = Math.max(next, (p.getPosition() + 1) );
		}
		return next;
	}
	
	public void sort(){
		int count = getPositions().size();
		for(int i = 0;i < count;i++){
			PTPosition minimum = null;
			for(int j = i;j < count;j++){
				PTPosition position = (PTPosition)getPositions().get(j);
				if(minimum == null || position.getPosition() < minimum.getPosition()){
					minimum = position;
				}
			}
			getPositions().remove(minimum);
			getPositions().add(i,minimum);
		}
	}
}
