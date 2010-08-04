package org.herac.tuxguitar.app.actions;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class ActionAdapter implements ActionListener, MouseListener{
	
	public abstract void process(AWTEvent e);
	
	private void doProcess(final AWTEvent e){
		new Thread(new Runnable() {
			public void run() {
				process(e);
			}
		}).start();
	}
	
	public void actionPerformed(final ActionEvent e){
		this.doProcess(e);
	}
	
    public void mouseClicked(final MouseEvent e){
    	this.doProcess(e);
    }
    
    public void mousePressed(final MouseEvent e){
		// Not Implemented
    }
    
    public void mouseReleased(final MouseEvent e){
		// Not Implemented
    }
    
    public void mouseEntered(final MouseEvent e){
		// Not Implemented
    }
    
    public void mouseExited(final MouseEvent e){
		// Not Implemented
    }
}
