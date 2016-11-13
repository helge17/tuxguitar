package org.herac.tuxguitar.android.action.listener.error;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionErrorEvent;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.action.TGActionPreExecutionEvent;
import org.herac.tuxguitar.android.action.TGActionAsyncProcessFinishEvent;
import org.herac.tuxguitar.android.action.TGActionAsyncProcessErrorEvent;
import org.herac.tuxguitar.android.action.TGActionAsyncProcessStartEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventException;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorHandler;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGActionErrorHandler implements TGEventListener {
	
	public static final String ATTRIBUTE_ACTION_LEVEL= (TGActionErrorHandler.class.getName() + "-level");
	public static final String ATTRIBUTE_ERROR_HANDLER = TGErrorHandler.class.getName();

	private static final String[] PRE_EXEC_EVENTS = {
			TGActionPreExecutionEvent.EVENT_TYPE,
			TGActionAsyncProcessStartEvent.EVENT_TYPE
	};

	private static final String[] POST_EXEC_EVENTS = {
			TGActionPostExecutionEvent.EVENT_TYPE,
			TGActionAsyncProcessFinishEvent.EVENT_TYPE
	};

	private static final String[] ERROR_EVENTS = {
			TGActionErrorEvent.EVENT_TYPE,
			TGActionAsyncProcessErrorEvent.EVENT_TYPE
	};

	private TGContext context;
	
	public TGActionErrorHandler(TGContext context) {
		this.context = context;
	}
	
	public void incrementLevel(TGActionContext context) {
		Integer level = this.getLevel(context);
		context.setAttribute(ATTRIBUTE_ACTION_LEVEL, (level != null ? level.intValue() + 1 : 1));
	}

	public void decrementLevel(TGActionContext context) {
		Integer level = this.getLevel(context);
		context.setAttribute(ATTRIBUTE_ACTION_LEVEL, (level != null ? level.intValue() - 1 : 0));
	}
	
	public Integer getLevel(TGActionContext context) {
		return context.getAttribute(ATTRIBUTE_ACTION_LEVEL);
	}
	
	public TGActionContext findActionContext(TGEvent event) {
		return event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);
	}

	public boolean isEventType(TGEvent event, String[] eventTypes) {
		for(String eventType : eventTypes) {
			if( eventType.equals(event.getEventType())) {
				return true;
			}
		}
		return false;
	}

	public void processError(Throwable throwable, TGErrorHandler errorHandler) {
		if( errorHandler != null ) {
			errorHandler.handleError(throwable);
		} else {
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}

	public void processErrorEvent(TGEvent event) {
		TGActionContext actionContext = this.findActionContext(event);
		Integer level = this.getLevel(actionContext);
		if( level == null || level.intValue() == 0 ) {
			Throwable throwable = event.getAttribute(TGActionErrorEvent.PROPERTY_ACTION_ERROR);
			TGErrorHandler errorHandler = actionContext.getAttribute(ATTRIBUTE_ERROR_HANDLER);

			this.processError(throwable, errorHandler);
		}
	}

	public void processEvent(TGEvent event) throws TGEventException {
		if( this.isEventType(event, PRE_EXEC_EVENTS) ) {
			this.incrementLevel(this.findActionContext(event));
		}
		else if( this.isEventType(event, POST_EXEC_EVENTS) ) {
			this.decrementLevel(this.findActionContext(event));
		}
		else if( this.isEventType(event, ERROR_EVENTS) ) {
			this.decrementLevel(this.findActionContext(event));
			this.processErrorEvent(event);
		}
	}
}
