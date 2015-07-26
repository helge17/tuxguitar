package org.herac.tuxguitar.app.editors.channel;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.undo.undoables.channel.UndoableChannelGeneric;
import org.herac.tuxguitar.app.undo.undoables.channel.UndoableModifyChannel;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;

public class TGChannelHandle {
	
	public TGChannelHandle(){
		super();
	}
	
	public void addChannel(){
		TGSong song = getDocumentManager().getSong();
		
		// Comienza el Undoable
		UndoableChannelGeneric undoable = UndoableChannelGeneric.startUndo();
		
		getManager().addChannel(song);
		
		// Termina el Undoable
		TuxGuitar.getInstance().getUndoableManager().addEdit( undoable.endUndo() );
		TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
		TuxGuitar.getInstance().updateCache(true);
	}
	
	public void removeChannel(TGChannel channel){
		TGSong song = getDocumentManager().getSong();
		
		// Comienza el Undoable
		UndoableChannelGeneric undoable = UndoableChannelGeneric.startUndo();
		
		getManager().removeChannel(song, channel);
		
		// Termina el Undoable
		TuxGuitar.getInstance().getUndoableManager().addEdit( undoable.endUndo() );
		TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
		TuxGuitar.getInstance().updateCache(true);
	}
	
	public void updateChannel(int id,short bnk,short prg,short vol,short bal,short cho,short rev,short pha,short tre,String name){
		TGSong song = getDocumentManager().getSong();
		TGChannel channel = getManager().getChannel(song, id);
		if( channel != null ){
			boolean programChange = (bnk != channel.getBank() || prg != channel.getProgram());
			
			// Comienza el Undoable
			UndoableModifyChannel undoable = UndoableModifyChannel.startUndo(id);
			
			getManager().updateChannel(song, id, bnk, prg, vol, bal, cho, rev, pha, tre, name);
			
			// Termina el Undoable
			TuxGuitar.getInstance().getUndoableManager().addEdit( undoable.endUndo() );
			TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
			TuxGuitar.getInstance().updateCache(true);
			
			if (TuxGuitar.getInstance().getPlayer().isRunning()) {
				if(programChange){
					TuxGuitar.getInstance().getPlayer().updatePrograms();
				}else{
					TuxGuitar.getInstance().getPlayer().updateControllers();
				}
			}
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
