package org.herac.tuxguitar.app.actions;

import java.awt.AWTEvent;
import java.awt.GridBagConstraints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public abstract class ActionDialog extends Action{
	
	public static final int DEFAULT_MARGIN = 2;
	
	public static final int BUTTON_WIDTH = 80;
	public static final int BUTTON_HEIGHT = 35;
	
	public static final int H_GAP = 5;
	public static final int V_GAP = 5;
	
	private JFrame dialog;
	
	public ActionDialog(String name, int flags) {
		super(name, flags);
	}
	
	protected abstract void openDialog();
	
	protected int execute(AWTEvent e){
		if( this.dialog != null && this.dialog.isDisplayable() ){
			this.dialog.toFront();
		}else{
			this.openDialog();
		}
		return 0;
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
