package org.herac.tuxguitar.gui.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.herac.tuxguitar.gui.TuxGuitar;

public class TGTableColumn {
	private TGTable table;
	private CLabel column;
	private List controls;
	
	public TGTableColumn(TGTable table,int align){
		this.table = table;
		this.controls = new ArrayList();
		this.column = new CLabel(this.table.getColumnControl(),align | SWT.SHADOW_OUT);
		this.column.setLayout(new GridLayout());
		this.column.addListener(SWT.Resize,new Listener() {
			public void handleEvent(Event arg0) {
				layout();
			}
		});
		this.column.pack();
		this.appendListeners(this.column);
	}
	
	public CLabel getControl(){
		return this.column;
	}
	
	public void setTitle(String title){
		this.column.setText(title);
	}
	
	public void addControl(Control control){
		this.controls.add(control);
		this.appendListeners(control);
	}
	
	public void appendListeners(Control control){
		TuxGuitar.instance().getkeyBindingManager().appendListenersTo(control);
	}
	
	public void layout(){
		Point location = this.column.getLocation();
		Point size = this.column.getSize();
		
		for(int i = 0; i < this.controls.size(); i ++){
			Control control = (Control)this.controls.get(i);
			if(!control.isDisposed()){
				control.setSize(size.x,this.table.getRowHeight());
				control.setLocation(location.x, 0);
			}
		}
	}
	
	public void notifyRemoved(){
		for(int i = 0; i < this.controls.size(); i ++){
			Control control = (Control)this.controls.get(i);
			if(control.isDisposed()){
				this.controls.remove(i --);
			}
		}
	}
	
}
