package app.tuxguitar.app.transport;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.util.MidiTickUtil;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

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
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(this.context, player.getTickPosition()));
		if(header != null){
			gotoMeasure(getSongManager().getNextMeasureHeader(getSong(), header),true);
		}
	}

	public void gotoPrevious(){
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(this.context, player.getTickPosition()));
		if(header != null){
			gotoMeasure(getSongManager().getPrevMeasureHeader(getSong(), header),true);
		}
	}

	public void gotoCaretPosition() {
		gotoMeasure(TablatureEditor.getInstance(this.context).getTablature().getCaret().getMeasure().getHeader(), false);
	}

	public void gotoMeasure(TGMeasureHeader header,boolean moveCaret){
		if(header != null){
			TGMeasure playingMeasure = null;
			MidiPlayer player = MidiPlayer.getInstance(this.context);
			if( player.isRunning() ){
				getCache().updatePlayMode();
				playingMeasure = getCache().getPlayMeasure();
			}
			if( playingMeasure == null || playingMeasure.getHeader().getNumber() != header.getNumber() ){
				player.setTickPosition(MidiTickUtil.getTick(this.context, header.getStart()));
				if( moveCaret){
					this.goToTickPosition();
				}
			}
		}
	}

	public void gotoPlayerPosition(){
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(this.context, player.getTickPosition()));
		if( header != null){
			player.setTickPosition(MidiTickUtil.getTick(this.context, header.getStart()));
		}

		this.goToTickPosition();
	}

	public void goToTickPosition() {
		TablatureEditor.getInstance(this.context).getTablature().getCaret().goToTickPosition();
		TuxGuitar.getInstance().updateCache(true);
	}

	public void playPause(TGBeat start, TGBeat end){
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		if(!player.isRunning()){
			if (start!=null) {
				player.setSelection(start,end);
			}
			this.play();
		}else{
			this.pause();
		}
	}
	public void playStop(TGBeat start, TGBeat end){
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		if(!player.isRunning()){
			if (start!=null) {
				player.setSelection(start,end);
			}
			this.play();
		}else{
			this.stop();
		}
	}
	private void play() {
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		try{
			gotoCaretPosition();
			player.getMode().reset();
			player.play();
		}catch(MidiPlayerException e){
			TGErrorManager.getInstance(this.context).handleError(e);
		}
	}
	private void pause() {
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		gotoPlayerPosition();
		player.pause();
	}
	public void stop(){
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		if(player.isRunning()){
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
