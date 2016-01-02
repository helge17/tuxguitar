package org.herac.tuxguitar.app.editors;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfig;
import org.herac.tuxguitar.app.util.TGResourceUtils;

public class TGScrollBar {
	
	public static final int DEFAULT_INCREMENT = 50;
	
	private float value;
	private float maximum;
	
	private JButton buttonUp;
	private JButton buttonDown;
	
	public TGScrollBar(){
		
	}
	
	public Component getComponent(){
		this.buttonUp = new JButton();
		this.buttonUp.setIcon(TGResourceUtils.loadIcon("scroll_up.png"));
		this.buttonUp.setPressedIcon(TGResourceUtils.loadIcon("scroll_up_pressed.png"));
		this.buttonUp.setBorderPainted( false );
		this.buttonUp.setContentAreaFilled( false );
		this.buttonUp.setFocusPainted( false );
		this.buttonUp.setMargin( new Insets(0,0,0,0) );
		this.buttonUp.addMouseListener( new TGScrollBarAction( this, -1) );
		
		this.buttonDown = new JButton();
		this.buttonDown.setIcon(TGResourceUtils.loadIcon("scroll_down.png"));
		this.buttonDown.setPressedIcon(TGResourceUtils.loadIcon("scroll_down_pressed.png"));
		this.buttonDown.setBorderPainted( false );
		this.buttonDown.setContentAreaFilled( false );
		this.buttonDown.setFocusPainted( false );
		this.buttonDown.setMargin( new Insets(0,0,0,0) );
		this.buttonDown.addMouseListener( new TGScrollBarAction( this, 1) );
		
		JPanel panel = new JPanel();
		panel.setBackground( TGConfig.COLOR_WIDGET_BACKGROUND );
		panel.setLayout( new BoxLayout( panel, BoxLayout.PAGE_AXIS) );
		panel.add( Box.createVerticalStrut( 100 ));
		panel.add( this.buttonUp );
		panel.add( Box.createVerticalGlue());
		panel.add( this.buttonDown );
		panel.add( Box.createVerticalStrut( 100 ));
		
		return panel;
	}
	
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
		if( this.value < 0 ){
			this.value = 0;
		} else if( this.value > this.maximum ){
			this.value = this.maximum;
		}
	}
	
	public float getMaximum() {
		return maximum;
	}
	
	public void setMaximum(float maximum) {
		this.maximum = maximum;
	}
	
	private class TGScrollBarAction extends MouseAdapter implements Runnable {
		
		private boolean pressed;
		private int direction;
		private TGScrollBar scrollBar;
		
		public TGScrollBarAction(TGScrollBar scrollBar, int direction){
			this.scrollBar = scrollBar;
			this.direction = direction;
		}
		
		public void process() {
			if( TuxGuitar.instance().getTablatureEditor().isStarted() ){
				this.scrollBar.setValue( this.scrollBar.getValue() + ( this.direction * DEFAULT_INCREMENT ) );
				TuxGuitar.instance().getTablatureEditor().repaint();
			}
		}
		
	    public void mousePressed(MouseEvent e) {
	    	synchronized ( this ) {
	    		if( TuxGuitar.instance().getTablatureEditor().isStarted() ){
		    		if( !this.pressed ){
		    			this.pressed = true;
		    			new Thread( this ).start();
		    		}
	    		}
	    	}
	    }
	    
	    public void mouseReleased(MouseEvent e) {
	    	synchronized ( this ) {
	    		if( TuxGuitar.instance().getTablatureEditor().isStarted() ){
		    		this.pressed = false;
		    		this.notifyAll();
	    		}
	    	}
	    }
	    
		public void run() {
			try {
				synchronized ( this ) {
					while( this.pressed ){
						this.process();
						this.wait( 50 );
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
