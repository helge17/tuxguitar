package org.herac.tuxguitar.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.util.TGLock;

public class TGActionManager {
	
	private static TGActionManager instance;
	
	private TGLock lock;
	private Map actions;
	private List interceptors;
	private List preExecutionListeners;
	private List postExecutionListeners;
	private TGActionContextFactory actionContextFactory;
	
	private TGActionManager(){
		this.lock = new TGLock();
		this.actions = new HashMap();
		this.interceptors = new ArrayList();
		this.preExecutionListeners = new ArrayList();
		this.postExecutionListeners = new ArrayList();
		this.actionContextFactory = null;
	}
	
	public static TGActionManager getInstance(){
		synchronized (TGActionManager.class) {
			if( instance == null ){
				instance = new TGActionManager();
			}
			return instance;
		}
	}
	
	public void mapAction(String id, TGAction action){
		this.actions.put(id,action);
	}
	
	public void unmapAction(String id){
		if( this.actions.containsKey(id) ){
			this.actions.remove(id);
		}
	}
	
	public TGAction getAction(String id){
		if( this.actions.containsKey(id) ){
			return (TGAction)this.actions.get(id);
		}
		return null;
	}
	
	public Map getActions(){
		return this.actions;
	}
	
	public TGActionContext createActionContext() throws TGActionException{
		if( this.actionContextFactory != null ){
			return this.actionContextFactory.createActionContext();
		}
		return null;
	}
	
	public void execute(String id) throws TGActionException{
		this.execute(id, createActionContext());
	}
	
	public void execute(String id, TGActionContext context) throws TGActionException{
		try {
			this.lock.lock();
			
			TGAction action = getAction(id);
			if( action != null ){
				if(!this.intercept(id, context)){
					this.doPreExecution(id, context);
					
					action.execute(context);
					
					this.doPostExecution(id, context);
				}
			}
		} finally {
			this.lock.unlock();
		}
	}
	
	public boolean intercept(String id, TGActionContext context) throws TGActionException{
		Iterator it = this.interceptors.iterator();
		while( it.hasNext() ){
			TGActionInterceptor interceptor = (TGActionInterceptor) it.next();
			if( interceptor.intercept(id, context) ) {
				return true;
			}
		}
		return false;
	}
	
	public void doPreExecution(String id, TGActionContext context) throws TGActionException{
		Iterator it = this.preExecutionListeners.iterator();
		while( it.hasNext() ){
			TGActionPreExecutionListener listener = (TGActionPreExecutionListener) it.next();
			listener.doPreExecution(id, context);
		}
	}
	
	public void doPostExecution(String id, TGActionContext context) throws TGActionException{
		Iterator it = this.postExecutionListeners.iterator();
		while( it.hasNext() ){
			TGActionPostExecutionListener listener = (TGActionPostExecutionListener) it.next();
			listener.doPostExecution(id, context);
		}
	}
	
	public void addInterceptor(TGActionInterceptor interceptor){
		if(!this.interceptors.contains(interceptor)){
			this.interceptors.add(interceptor);
		}
	}
	
	public void removeInterceptor(TGActionInterceptor interceptor){
		if( this.interceptors.contains(interceptor) ){
			this.interceptors.remove(interceptor);
		}
	}
	
	public void addPreExecutionListener(TGActionPreExecutionListener listener){
		if(!this.preExecutionListeners.contains(listener)){
			this.preExecutionListeners.add(listener);
		}
	}
	
	public void removePreExecutionListener(TGActionPreExecutionListener listener){
		if( this.preExecutionListeners.contains(listener) ){
			this.preExecutionListeners.remove(listener);
		}
	}
	
	public void addPostExecutionListener(TGActionPostExecutionListener listener){
		if(!this.postExecutionListeners.contains(listener)){
			this.postExecutionListeners.add(listener);
		}
	}
	
	public void removePostExecutionListener(TGActionPostExecutionListener listener){
		if( this.postExecutionListeners.contains(listener) ){
			this.postExecutionListeners.remove(listener);
		}
	}
	
	public void setActionContextFactory(TGActionContextFactory actionContextFactory) {
		this.actionContextFactory = actionContextFactory;
	}
}
