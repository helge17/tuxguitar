package org.herac.tuxguitar.app.action;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGActionProcessor implements SelectionListener,MouseListener,MenuListener,ShellListener{
	
	public static final String PROPERTY_TYPED_EVENT = "typedEvent";
	
	private String actionName; 
	
	public TGActionProcessor(String actionName){
		this.actionName = actionName;
	}
	
	public void processEvent(TypedEvent e) {
		TGActionContext tgActionContext = TGActionManager.getInstance().createActionContext();
		tgActionContext.setAttribute(PROPERTY_TYPED_EVENT, e);
		
		this.fillWidgetAttributes(tgActionContext, e);
		this.processAction(tgActionContext);
	}
	
	public void processAction(final TGActionContext context){
		final String actionName = this.actionName;
		TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
			public void run() throws TGException {
				TGActionManager.getInstance().execute(actionName, context);
			}
		});
	}
	
	public void fillWidgetAttributes(TGActionContext context, TypedEvent e){
		Object widgetData = (e.widget != null ? e.widget.getData() : null);
		if( widgetData instanceof Map ){
			Iterator it = ((Map)widgetData).entrySet().iterator();
			while( it.hasNext() ){
				Map.Entry entry = (Map.Entry)it.next();
				context.setAttribute(entry.getKey().toString(), entry.getValue());
			}
		}
	}
	
	public void widgetSelected(SelectionEvent e) {
		if(e.widget != null && (e.widget.getStyle() & SWT.RADIO) != 0){
			if(e.widget instanceof Button && !((Button)e.widget).getSelection() ){
				return;
			}
			if(e.widget instanceof ToolItem && !((ToolItem)e.widget).getSelection() ){
				return;
			}
			if(e.widget instanceof MenuItem && !((MenuItem)e.widget).getSelection() ){
				return;
			}
		}
		processEvent(e);
	}
	
	public void mouseUp(MouseEvent e) {
		processEvent(e);
	}
	
	public void menuShown(MenuEvent e) {
		processEvent(e);
	}
	
	public void shellClosed(ShellEvent e) {
		e.doit = false;
		processEvent(e);
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
		//Override me
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		//Override me
	}
	
	public void mouseDown(MouseEvent e) {
		//Override me
	}
	
	public void menuHidden(MenuEvent e) {
		//Override me
	}
	
	public void shellActivated(ShellEvent e) {
		//Override me
	}
	
	public void shellDeactivated(ShellEvent e) {
		//Override me
	}
	
	public void shellDeiconified(ShellEvent e) {
		//Override me
	}
	
	public void shellIconified(ShellEvent e) {
		//Override me
	}	
}
