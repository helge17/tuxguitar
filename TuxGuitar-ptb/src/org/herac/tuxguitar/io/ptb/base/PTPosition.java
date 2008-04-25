package org.herac.tuxguitar.io.ptb.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PTPosition {
	
	private int position;
	private List components;
	
	public PTPosition(int position){
		this.position = position;
		this.components = new ArrayList();
	}
	
	public int getPosition() {
		return this.position;
	}
	
	public List getComponents() {
		return this.components;
	}
	
	public void addComponent(PTComponent component){
		this.components.add(component);
	}
	
	public void sort(){
		Collections.sort( getComponents(), new Comparator() {
			public int compare(Object o1, Object o2) {
				if( o1 instanceof PTDirection ){
					return 1;
				}
				if( o2 instanceof PTDirection ){
					return -1;
				}
				return 0;
			}
		});
	}
}
