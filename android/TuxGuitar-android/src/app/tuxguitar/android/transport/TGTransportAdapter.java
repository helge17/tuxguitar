package app.tuxguitar.android.transport;

import app.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.impl.sequencer.MidiSequencerProviderImpl;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTransportAdapter {

	private TGContext context;

	private TGTransportAdapter(TGContext context) {
		this.context = context;
	}

	public void initialize() {
		try {
			MidiPlayer midiPlayer = MidiPlayer.getInstance(this.context);
			midiPlayer.addListener(new TGTransportListener(this.context));
			midiPlayer.addSequencerProvider(new MidiSequencerProviderImpl(this.context), true);

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

	public void playBeat( final TGBeat beat ) {
		TGEditorManager.getInstance(this.context).asyncRunLocked(new Runnable() {
			public void run() throws TGException {
				MidiPlayer midiPlayer = MidiPlayer.getInstance(TGTransportAdapter.this.context);
				if(!midiPlayer.isRunning() ){
					midiPlayer.playBeat(beat);
				}
			}
		});
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
