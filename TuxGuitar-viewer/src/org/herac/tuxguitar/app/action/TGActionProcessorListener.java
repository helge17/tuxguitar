package org.herac.tuxguitar.app.action;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

public class TGActionProcessorListener extends TGActionProcessor implements ActionListener, MouseListener {
	
	public TGActionProcessorListener(TGContext context, String actionName){
		super(context, actionName);
	}
	
	public void processEvent(AWTEvent e) {
		this.processOnNewThread(this.createRealTimeAttributes(e));
	}
	
	public Map<String, Object> createRealTimeAttributes(AWTEvent e){
		Map<String, Object> realTimeAttributes = new HashMap<String, Object>();
		realTimeAttributes.put(AWTEvent.class.getName(), e);
		return realTimeAttributes;
	}
	
	public void actionPerformed(ActionEvent e){
		this.processEvent(e);
	}
	
    public void mouseClicked(MouseEvent e){
    	this.processEvent(e);
    }
    
    public void mousePressed(MouseEvent e){
		// Not Implemented
    }
    
    public void mouseReleased(MouseEvent e){
		// Not Implemented
    }
    
    public void mouseEntered(MouseEvent e){
		// Not Implemented
    }
    
    public void mouseExited(MouseEvent e){
		// Not Implemented
    }
}
