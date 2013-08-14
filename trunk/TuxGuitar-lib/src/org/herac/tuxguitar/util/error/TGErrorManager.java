package org.herac.tuxguitar.util.error;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGErrorManager {
	
	private static TGErrorManager instance;
	
	private List errorHandlers;
	
	private TGErrorManager(){
		this.errorHandlers = new ArrayList();
	}
	
	public static TGErrorManager getInstance(){
		synchronized (TGErrorManager.class) {
			if( instance == null ){
				instance = new TGErrorManager();
			}
			return instance;
		}
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
}
