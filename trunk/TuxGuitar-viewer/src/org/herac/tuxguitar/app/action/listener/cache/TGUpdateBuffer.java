package org.herac.tuxguitar.app.action.listener.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.util.TGAbstractContext;

public class TGUpdateBuffer {
	
	private static final Integer UPDATE_CACHE = 1;
	private static final Integer UPDATE_ITEMS = 2;
	
	private Integer updateCache;
	private Boolean updateSong;
	private List<Integer> updateMeasures;
	private List<Runnable> updateRunnables;
	
	public TGUpdateBuffer() {
		this.updateMeasures = new ArrayList<Integer>();
		this.updateRunnables = new ArrayList<Runnable>();
	}
	
	public void clear() {
		this.updateRunnables.clear();
		this.updateMeasures.clear();
		this.updateCache = null;
		this.updateSong = false;
	}
	
	public void apply(TGAbstractContext srcContext) {
		// Update song structure
		this.applyUpdateSong();
		this.applyUpdateMeasures();
		
		// Process runnables after update
		this.applyUpdateRunnables();
		
		// Refresh cache
		this.applyUpdateCache();
	}
	
	public void applyUpdateSong() {
		if( this.updateSong ) {
			TuxGuitar.instance().fireUpdate();
		}
	}
	
	public void applyUpdateMeasures() {
		if(!this.updateSong) {
			Collections.sort(this.updateMeasures);
			
			for(Integer number : this.updateMeasures) {
				TuxGuitar.instance().getTablatureEditor().getTablature().getViewLayout().updateMeasureNumber(number);
			}
		}
	}
	
	public void applyUpdateCache() {
		if( this.updateCache != null && this.updateCache.intValue() >= UPDATE_CACHE ) {
			TuxGuitar tuxguitar = TuxGuitar.instance();
			tuxguitar.updateCache(this.updateCache.equals(UPDATE_ITEMS));
		}
	}
	
	public void applyUpdateRunnables() {
		for(Runnable runnable : this.updateRunnables) {
			runnable.run();
		}
	}
	
	public void requestUpdateCache(Boolean updateItems) {
		if( this.updateCache == null || this.updateCache.intValue() < UPDATE_ITEMS.intValue() ) {
			this.updateCache = (updateItems ? UPDATE_ITEMS : UPDATE_CACHE);
		}
	}
	
	public void requestUpdateMeasure(Integer number) {
		if(!this.updateMeasures.contains(number)) {
			this.updateMeasures.add(number);
		}
	}
	
	public void requestUpdateSong() {
		this.updateSong = true;
	}
	
	public void doPostUpdate(Runnable runnable) {
		this.updateRunnables.add(runnable);
	}
}
