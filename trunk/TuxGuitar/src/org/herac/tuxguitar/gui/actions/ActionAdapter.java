package org.herac.tuxguitar.gui.actions;

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

public abstract class ActionAdapter implements SelectionListener,MouseListener,MenuListener,ShellListener{
	
	public abstract void process(TypedEvent e);
	
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
		process(e);
	}
	
	public void mouseUp(MouseEvent e) {
		process(e);
	}
	
	public void menuShown(MenuEvent e) {
		process(e);
	}
	
	public void shellClosed(ShellEvent e) {
		e.doit = false;
		process(e);
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
