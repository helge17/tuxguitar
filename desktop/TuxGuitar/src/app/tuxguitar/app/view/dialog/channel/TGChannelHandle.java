package app.tuxguitar.app.view.dialog.channel;

import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.channel.TGAddNewChannelAction;
import app.tuxguitar.editor.action.channel.TGRemoveChannelAction;
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGChannelHandle {

	private TGContext context;

	public TGChannelHandle(TGContext context){
		this.context = context;
	}

	public void addChannel(){
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGAddNewChannelAction.NAME);
		tgActionProcessor.process();
	}

	public void removeChannel(TGChannel channel){
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGRemoveChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		tgActionProcessor.process();
	}

	public void updateChannel(int id, short bnk, short prg, short vol, short bal, short cho, short rev, short pha, short tre, String name){
		TGSong song = getDocumentManager().getSong();
		TGChannel channel = getManager().getChannel(song, id);
		if( channel != null ){
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGUpdateChannelAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
			tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_BANK, bnk);
			tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PROGRAM, prg);
			tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_VOLUME, vol);
			tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_BALANCE, bal);
			tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_CHORUS, cho);
			tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_REVERB, rev);
			tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PHASER, pha);
			tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_TREMOLO, tre);
			tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_NAME, name);
			tgActionProcessor.process();
		}
	}

	public List<TGChannel> getChannels(){
		return getManager().getChannels(getDocumentManager().getSong());
	}

	public boolean isAnyTrackConnectedToChannel(TGChannel channel){
		return getManager().isAnyTrackConnectedToChannel(getDocumentManager().getSong(), channel.getChannelId() );
	}

	public boolean isAnyPercussionChannel(){
		return getManager().isAnyPercussionChannel(getDocumentManager().getSong());
	}

	public boolean isPlayerRunning(){
		return TuxGuitar.getInstance().getPlayer().isRunning();
	}

	private TGSongManager getManager(){
		return TuxGuitar.getInstance().getSongManager();
	}

	private TGDocumentManager getDocumentManager(){
		return TuxGuitar.getInstance().getDocumentManager();
	}
}
