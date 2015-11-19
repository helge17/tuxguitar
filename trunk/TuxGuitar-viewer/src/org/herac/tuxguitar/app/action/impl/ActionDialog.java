package org.herac.tuxguitar.app.action.impl;

import java.awt.GridBagConstraints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public abstract class ActionDialog extends TGActionBase {
	
	public static final int DEFAULT_MARGIN = 2;
	
	public static final int BUTTON_WIDTH = 80;
	public static final int BUTTON_HEIGHT = 35;
	
	public static final int H_GAP = 5;
	public static final int V_GAP = 5;
	
	private JFrame dialog;
	
	public ActionDialog(TGContext context, String name) {
		super(context, name);
	}
	
	protected abstract void openDialog(TGActionContext context);
	
	protected void processAction(TGActionContext context) {
		if( this.dialog != null && this.dialog.isDisplayable() ){
			this.dialog.toFront();
		}else{
			this.openDialog(context);
		}
	}
	
	protected JFrame createDialog(){
		this.dialog = new JFrame();
		this.dialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		this.dialog.addWindowListener( new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				e.getWindow().toFront();
			}
		} );
		return this.dialog;
	}
	
	protected GridBagConstraints getConstraints(int column, int row, float weightx, float weighty, int fill ){
		return getConstraints(column, row, weightx, weighty, fill, 1);
	}
	
	protected GridBagConstraints getConstraints(int column, int row, float weightx, float weighty, int fill , int colspan){
		return getConstraints(column, row, weightx, weighty, fill, colspan, 1);
	}
	
	protected GridBagConstraints getConstraints(int column, int row, float weightx, float weighty, int fill , int colspan, int rowspan){
		return getConstraints(column, row, weightx, weighty, fill, colspan, rowspan, DEFAULT_MARGIN);
	}
	
	protected GridBagConstraints getConstraints(int column, int row, float weightx, float weighty, int fill , int colspan, int rowspan, int margin){
		GridBagConstraints c = new GridBagConstraints();
		c.fill = fill;
		c.weightx = weightx;
		c.weighty = weighty;
		c.gridx = column;
		c.gridy = row;
		c.gridwidth = colspan;
		c.gridheight = rowspan;
		c.insets.set(margin, margin, margin, margin);
		return c;
	}
}
