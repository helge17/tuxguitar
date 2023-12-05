package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.util.TGContext;

public class TGTransportModeAction extends TGActionBase {
	
	public static final String NAME = "action.transport.mode";
	
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_LOOP = "loop";
	public static final String ATTRIBUTE_SIMPLE_PERCENT = "simplePercent";
	public static final String ATTRIBUTE_CUSTOM_PERCENT_FROM = "customPercentFrom";
	public static final String ATTRIBUTE_CUSTOM_PERCENT_TO = "customPercentTo";
	public static final String ATTRIBUTE_CUSTOM_PERCENT_INCREMENT = "customPercentIncrement";
	
	public static final String ATTRIBUTE_LOOP_S_HEADER = "loopSHeader";
	public static final String ATTRIBUTE_LOOP_E_HEADER = "loopEHeader";
	
	public TGTransportModeAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		MidiPlayerMode mode = MidiPlayer.getInstance(getContext()).getMode();
		
		mode.setType((Integer)context.getAttribute(ATTRIBUTE_TYPE));
		mode.setLoop((Boolean)context.getAttribute(ATTRIBUTE_LOOP));
		mode.setSimplePercent((Integer)context.getAttribute(ATTRIBUTE_SIMPLE_PERCENT));
		mode.setCustomPercentFrom((Integer)context.getAttribute(ATTRIBUTE_CUSTOM_PERCENT_FROM));
		mode.setCustomPercentTo((Integer)context.getAttribute(ATTRIBUTE_CUSTOM_PERCENT_TO));
		mode.setCustomPercentIncrement((Integer)context.getAttribute(ATTRIBUTE_CUSTOM_PERCENT_INCREMENT));
		mode.setLoopSHeader( ( mode.isLoop() ? (Integer)context.getAttribute(ATTRIBUTE_LOOP_S_HEADER) : -1 ) );
		mode.setLoopEHeader( ( mode.isLoop() ? (Integer)context.getAttribute(ATTRIBUTE_LOOP_E_HEADER) : -1 ) );
		mode.reset();
	}
}