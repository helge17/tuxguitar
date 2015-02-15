package org.herac.tuxguitar.util.error;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGErrorManager {
	
	private List errorHandlers;
	
	private TGErrorManager(){
		this.errorHandlers = new ArrayList();
	}
	
	public void handleError(Throwable throwable){
		Iterator it = this.errorHandlers.iterator();
		while(it.hasNext()){
			TGErrorHandler tgErrorHandler = (TGErrorHandler)it.next();
			tgErrorHandler.handleError(throwable);
		}
	}
	
	public void addErrorHandler(TGErrorHandler errorHandler){
		if(!this.errorHandlers.contains(errorHandler) ){
			this.errorHandlers.add(errorHandler);
		}
	}
	
	public void removeErrorHandler(TGErrorHandler errorHandler){
		if( this.errorHandlers.contains(errorHandler) ){
			this.errorHandlers.remove(errorHandler);
		}
	}
	
	public List getErrorHandlers() {
		return this.errorHandlers;
	}
	
	public void clear() {
		this.errorHandlers.clear();
	}
	
	public static TGErrorManager getInstance(TGContext context) {
		return (TGErrorManager) TGSingletonUtil.getInstance(context, TGErrorManager.class.getName(), new TGSingletonFactory() {
			public Object createInstance(TGContext context) {
				return new TGErrorManager();
			}
		});
	}
}
