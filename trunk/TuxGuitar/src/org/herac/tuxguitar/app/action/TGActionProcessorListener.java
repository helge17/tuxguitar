package org.herac.tuxguitar.app.action;

import java.util.HashMap;
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
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

public class TGActionProcessorListener extends TGActionProcessor implements SelectionListener,MouseListener,MenuListener,ShellListener{
	
	public static final String PROPERTY_TYPED_EVENT = "typedEvent";
	
	public TGActionProcessorListener(TGContext context, String actionName){
		super(context, actionName);
	}
	
	public void processEvent(TypedEvent e) {
		this.processOnNewThread(this.createWidgetAttributes(e));
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> createWidgetAttributes(TypedEvent e){
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(PROPERTY_TYPED_EVENT, e);
		
		Object widgetData = (e.widget != null ? e.widget.getData() : null);
		if( widgetData instanceof Map ){
			attributes.putAll((Map<String, Object>)widgetData);
		}
		return attributes;
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
