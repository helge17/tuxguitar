package org.herac.tuxguitar.editor.action.channel;

import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateChannelAction extends TGActionBase{
	
	public static final String NAME = "action.channel.update";
	
	public static final String ATTRIBUTE_BANK = "bank";
	public static final String ATTRIBUTE_PROGRAM = "program";
	public static final String ATTRIBUTE_VOLUME = "volume";
	public static final String ATTRIBUTE_BALANCE = "balance";
	public static final String ATTRIBUTE_CHORUS = "chorus";
	public static final String ATTRIBUTE_REVERB = "reverb";
	public static final String ATTRIBUTE_PHASER = "phaser";
	public static final String ATTRIBUTE_TREMOLO = "tremolo";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_PARAMETERS = "parameters";
	
	public TGUpdateChannelAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGChannel channel = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL);
		TGChannel channelClone = channel.clone(songManager.getFactory());
		
		channelClone.setBank(((Short) findAttribute(context, ATTRIBUTE_BANK, Short.valueOf(channel.getBank()))).shortValue());
		channelClone.setProgram(((Short) findAttribute(context, ATTRIBUTE_PROGRAM, Short.valueOf(channel.getProgram()))).shortValue());
		channelClone.setVolume(((Short) findAttribute(context, ATTRIBUTE_VOLUME, Short.valueOf(channel.getVolume()))).shortValue());
		channelClone.setBalance(((Short) findAttribute(context, ATTRIBUTE_BALANCE, Short.valueOf(channel.getBalance()))).shortValue());
		channelClone.setChorus(((Short) findAttribute(context, ATTRIBUTE_CHORUS, Short.valueOf(channel.getChorus()))).shortValue());
		channelClone.setReverb(((Short) findAttribute(context, ATTRIBUTE_REVERB, Short.valueOf(channel.getReverb()))).shortValue());
		channelClone.setPhaser(((Short) findAttribute(context, ATTRIBUTE_PHASER, Short.valueOf(channel.getPhaser()))).shortValue());
		channelClone.setTremolo(((Short) findAttribute(context, ATTRIBUTE_TREMOLO, Short.valueOf(channel.getTremolo()))).shortValue());
		channelClone.setName((String) findAttribute(context, ATTRIBUTE_NAME, channel.getName()));
		
		List<TGChannelParameter> parameters = context.getAttribute(ATTRIBUTE_PARAMETERS);
		if( parameters != null ) {
			channelClone.removeParameters();
			for(TGChannelParameter parameter : parameters) {
				channelClone.addParameter(parameter);
			}
		}
		
		songManager.updateChannel(song, channelClone);
	}
	
	private Object findAttribute(TGActionContext context, String attribute, Object defaultValue) {
		Object value = context.getAttribute(attribute);
		if( value == null ) {
			value = defaultValue;
		}
		return value;
	}
}
