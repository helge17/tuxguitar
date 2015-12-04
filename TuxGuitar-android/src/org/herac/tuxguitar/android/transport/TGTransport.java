package org.herac.tuxguitar.android.transport;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.util.MidiTickUtil;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTransport {
	
	private TGContext context;
	private TGTransportCache cache;
	
	public TGTransport(TGContext context) {
		this.context = context;
		this.cache = new TGTransportCache(context);
	}
	
	public TGTransportCache getCache() {
		return this.cache;
	}

	public TGSongManager getSongManager(){
		return TGDocumentManager.getInstance(this.context).getSongManager();
	}
	
	public TGSong getSong(){
		return TGDocumentManager.getInstance(this.context).getSong();
	}
	
	public void gotoFirst(){
		gotoMeasure(getSongManager().getFirstMeasureHeader(getSong()), true);
	}
	
	public void gotoLast(){
		gotoMeasure(getSongManager().getLastMeasureHeader(getSong()), true) ;
	}
	
	public void gotoNext(){
		MidiPlayer player = TuxGuitar.getInstance(this.context).getPlayer();
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(this.context, player.getTickPosition()));
		if(header != null){
			gotoMeasure(getSongManager().getNextMeasureHeader(getSong(), header),true);
		}
	}
	
	public void gotoPrevious(){
		MidiPlayer player = TuxGuitar.getInstance(this.context).getPlayer();
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(this.context, player.getTickPosition()));
		if(header != null){
			gotoMeasure(getSongManager().getPrevMeasureHeader(getSong(), header),true);
		}
	}
	
	public void gotoMeasure(TGMeasureHeader header){
		gotoMeasure(header, false);
	}
	
	public void gotoCaretPosition() {
		gotoMeasure(TGSongViewController.getInstance(this.context).getCaret().getMeasure().getHeader(), false);
	}
	
	public void gotoMeasure(TGMeasureHeader header, boolean moveCaret){
		if(header != null){
			TGMeasure playingMeasure = null;
			TuxGuitar tuxguitar = TuxGuitar.getInstance(this.context);
			if( tuxguitar.getPlayer().isRunning() ){
				getCache().updatePlayMode();
				playingMeasure = getCache().getPlayMeasure();
			}
			if( playingMeasure == null || playingMeasure.getHeader().getNumber() != header.getNumber() ){
				tuxguitar.getPlayer().setTickPosition(MidiTickUtil.getTick(this.context, header.getStart()));
				if( moveCaret ){
					TGSongViewController.getInstance(this.context).getCaret().goToTickPosition();
					tuxguitar.updateCache(true);
				}
			}
		}
	}
	
	public void gotoPlayerPosition() {
		TuxGuitar tuxguitar = TuxGuitar.getInstance(this.context);
		
		MidiPlayer player = tuxguitar.getPlayer();
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(this.context, player.getTickPosition()));
		if( header != null ){
			player.setTickPosition(MidiTickUtil.getTick(this.context, header.getStart()));
		}
		TGSongViewController.getInstance(this.context).getCaret().goToTickPosition();
		
		tuxguitar.updateCache(true);
	}
	
	public void play(){
		MidiPlayer player = TuxGuitar.getInstance(this.context).getPlayer();
		if(!player.isRunning()){
			try{
				this.gotoCaretPosition();
				
				player.getMode().reset();
				player.play();
			}catch(MidiPlayerException exception){
				TGErrorManager.getInstance(this.context).handleError(exception);
			}
		}else{
			player.pause();
		}
	}
	
	public void stop(){
		MidiPlayer player = TuxGuitar.getInstance(this.context).getPlayer();
		if(!player.isRunning()){
			player.reset();
			this.gotoPlayerPosition();
		}else{
			player.reset();
		}
	}
	
	public static TGTransport getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGTransport.class.getName(), new TGSingletonFactory<TGTransport>() {
			public TGTransport createInstance(TGContext context) {
				return new TGTransport(context);
			}
		});
	}
}
