package org.herac.tuxguitar.app.action.listener.cache;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateContext {

	private TGUpdateBuffer buffer;
	private Integer level;
	
	public TGUpdateContext(TGContext context){
		this.buffer = new TGUpdateBuffer(context);
		this.level = 0;
	}
	
	public void incrementLevel() {
		this.level ++;
	}
	
	public void decrementLevel() {
		this.level --;
	}
	
	public Integer getLevel() {
		return level;
	}

	public TGUpdateBuffer getBuffer() {
		return buffer;
	}

	public static TGUpdateContext getInstance(TGContext context, TGActionContext actionContext) {
		synchronized (TGUpdateContext.class) {
			String key = TGUpdateContext.class.getName();
			if( actionContext.hasAttribute(key) ) {
				return actionContext.getAttribute(key);
			}
			actionContext.setAttribute(key, new TGUpdateContext(context));
			
			return getInstance(context, actionContext);
		}
	}
}
