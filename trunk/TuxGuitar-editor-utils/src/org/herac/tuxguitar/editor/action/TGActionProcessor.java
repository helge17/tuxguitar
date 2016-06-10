package org.herac.tuxguitar.editor.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorHandler;

public class TGActionProcessor {
	
	private TGContext context;
	private String actionName;
	private Map<String, Object> attributes;
	private Runnable onFinish;
	private TGErrorHandler errorHandler;
	
	public TGActionProcessor(TGContext context, String actionName){
		this.context = context;
		this.actionName = actionName;
		this.attributes = new HashMap<String, Object>();
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public String getActionName() {
		return actionName;
	}
	
	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}
	
	public void clearAttributes() {
		this.attributes.clear();
	}

	public void setOnFinish(Runnable onFinish) {
		this.onFinish = onFinish;
	}

	public void setErrorHandler(TGErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public TGActionContext createActionContext() {
		return TGActionManager.getInstance(getContext()).createActionContext();
	}
	
	public TGActionContext fillActionContext(TGActionContext actionContext, Map<String, Object> customAttributes) {
		this.appendAttributes(actionContext, this.attributes);
		this.appendAttributes(actionContext, customAttributes);
		return actionContext;
	}
	
	public void appendAttributes(TGActionContext actionContext, Map<String, Object> attributes) {
		if( attributes != null ) {
			Iterator<Map.Entry<String, Object>> it = attributes.entrySet().iterator();
			while( it.hasNext() ){
				Map.Entry<String, Object> entry = it.next();
				actionContext.setAttribute(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public void processOnCurrentThread(TGActionContext actionContext, Map<String, Object> customAttributes){
		try {
			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(getActionName(), this.fillActionContext(actionContext, customAttributes));
		
			this.onFinish();
		} catch (TGActionException e) {
			this.onError(e);
		}
	}
	
	public void processOnCurrentThread(TGActionContext actionContext){
		this.processOnCurrentThread(actionContext, null);
	}
	
	public void processOnCurrentThread(Map<String, Object> customAttributes){
		this.processOnCurrentThread(this.createActionContext(), customAttributes);
	}
	
	public void processOnCurrentThread(){
		this.processOnCurrentThread(this.createActionContext());
	}
	
	public void processOnNewThread(final Map<String, Object> customAttributes){
		new Thread(new Runnable() {
			public void run() {
				processOnCurrentThread(customAttributes);
			}
		}).start();
	}
	
	public void processOnNewThread(){
		this.processOnNewThread(null);
	}
	
	public void process(final Map<String, Object> customAttributes){
		this.processOnNewThread(customAttributes);
	}
	
	public void process(){
		this.process(null);
	}
	
	public void onFinish() {
		if( this.onFinish != null ) {
			this.onFinish.run();
		}
	}
	
	public void onError(TGActionException e) {
		if( this.errorHandler != null ) {
			this.errorHandler.handleError(e);
		}
	}
}
