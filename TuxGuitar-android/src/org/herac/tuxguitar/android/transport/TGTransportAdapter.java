package org.herac.tuxguitar.android.transport;

import org.herac.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.impl.sequencer.MidiSequencerProviderImpl;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTransportAdapter {
	
	private TGContext context;

	private TGTransportAdapter(TGContext context) {
		this.context = context;
	}
	
	public void initialize() {
		try {
			MidiPlayer midiPlayer = MidiPlayer.getInstance(this.context);
			midiPlayer.init(TGDocumentManager.getInstance(this.context));
			midiPlayer.addListener(new TGTransportListener(this.context));
			midiPlayer.addSequencerProvider(new MidiSequencerProviderImpl(), true);
			
			this.appendListeners();
			this.callLoadSettings();
		} catch (MidiPlayerException e) {
			TGErrorManager.getInstance(this.context).handleError(e);
		}
	}

	public void callLoadSettings() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGTransportLoadSettingsAction.NAME);
		tgActionProcessor.process();
	}

	public void appendListeners() {
		TGEditorManager.getInstance(this.context).addDestroyListener(new TGTransportDestroyListener(this));
	}
	
	public void destroy() {
		MidiPlayer.getInstance(this.context).close();
	}

	public void playBeat( final TGBeat beat ){
		new Thread(new Runnable() {
			public void run() throws TGException {
				MidiPlayer midiPlayer = MidiPlayer.getInstance(TGTransportAdapter.this.context);
				if(!midiPlayer.isRunning() ){
					midiPlayer.playBeat(beat);
				}
			}
		}).start();
	}

	public void loadSettings() {
		TGTransportProperties tgTransportProperties = new TGTransportProperties(this.context);
		tgTransportProperties.load();

		String outputPortKey = tgTransportProperties.getMidiOutputPort();
		MidiPlayer.getInstance(this.context).openOutputPort(outputPortKey, (outputPortKey == null));
	}

	public static TGTransportAdapter getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGTransportAdapter.class.getName(), new TGSingletonFactory<TGTransportAdapter>() {
			public TGTransportAdapter createInstance(TGContext context) {
				return new TGTransportAdapter(context);
			}
		});
	}
}
