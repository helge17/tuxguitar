package org.herac.tuxguitar.app.view.component.table;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.TGContinousControlSelectionListener;
import org.herac.tuxguitar.app.util.TGContinuousControl;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScale;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.util.TGContext;

public class TGTableMixer {
	private TGContext context;
	private UIPanel panel;
	private UITableLayout layout;
	private UIFactory uiFactory;
	private List<VolumeControl> volumeControls;

	public TGTableMixer(UIScrollBarPanel parent, UIFactory uiFactory, TGContext context) {
		this.uiFactory = uiFactory;
		this.context = context;
		this.panel = this.uiFactory.createPanel(parent, true);
		this.layout = new UITableLayout();
		this.panel.setLayout(layout);
		this.volumeControls = new ArrayList<VolumeControl>();
	}

	public void update(TGSong song) {
		TGSongManager songManager = TuxGuitar.getInstance().getSongManager();

		// list of channels present in song
		List<TGChannel> newChannels = new ArrayList<TGChannel>();
		for(int nTrack = 0; nTrack < song.countTracks(); nTrack ++){
			int channelId = song.getTrack(nTrack).getChannelId();
			TGChannel channel = songManager.getChannel(song, channelId);
			if (!newChannels.contains(channel) && channel!=null) {
				newChannels.add(channel);
			}
		}

		// need to update list of volume controls?
		boolean listControlChanged = (newChannels.size() != this.volumeControls.size());
		if (!listControlChanged) {
			for (int i=0; i<this.volumeControls.size(); i++) {
				if (!newChannels.get(i).equals(this.volumeControls.get(i).getChannel())) {
					listControlChanged = true;
				}
			}
		}

		if (listControlChanged) {
			// recreate everything
			for (UIControl control : this.panel.getChildren()) {
				control.dispose();
			}
			this.volumeControls.clear();
			int nRow = 1;
			for(TGChannel channel : newChannels) {
				VolumeControl volumeControl = new VolumeControl(channel, context, this.uiFactory, this.panel);
				this.layout.set(volumeControl.getLabel(), nRow,1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false, 1,1, null, null, null);
				this.layout.set(volumeControl.getScale(), UITableLayout.MAXIMUM_PACKED_WIDTH, 80f);
				this.layout.set(volumeControl.getScale(), nRow,2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false, 1,1, null, null, null);
				nRow++;
				this.volumeControls.add(volumeControl);
			}
			this.panel.layout();
		}
		else {
			// unchanged list of controls, just update volumes
			for (VolumeControl control : this.volumeControls) {
				control.updateVolume();
			}
		}
	}

	public void updateInstrumentsNames() {
		for (VolumeControl control : this.volumeControls) {
			control.instrumentLabel.setText(control.getChannel().getName());
		}
	}

	private class VolumeControl implements TGContinuousControl {
		private UIScale volumeScale;
		private UILabel instrumentLabel;
		private TGChannel channel;
		private TGContext context;
		private TGContinousControlSelectionListener volumeListener;

		VolumeControl(TGChannel channel, TGContext context, UIFactory uiFactory, UIContainer parent) {
			this.channel=channel;
			this.context = context;

			this.instrumentLabel = uiFactory.createLabel(parent);
			this.instrumentLabel.setText(channel.getName());

			this.volumeScale = uiFactory.createHorizontalScale(parent);
			this.volumeScale.setMinimum(TGChannel.MIN_VOLUME);
			this.volumeScale.setMaximum(TGChannel.MAX_VOLUME);
			this.volumeScale.setValue(channel.getVolume());
			updateToolTipText();

			this.volumeScale.addSelectionListener(new UISelectionListener() {
				@Override
				public void onSelect(UISelectionEvent event) {
					VolumeControl.this.updateToolTipText();
					volumeListener.onSelect(null);
				}
			});
			volumeListener = new TGContinousControlSelectionListener(this, 250);
		}

		private void updateToolTipText() {
			int percent = 100*(VolumeControl.this.volumeScale.getValue() - TGChannel.MIN_VOLUME) / (TGChannel.MAX_VOLUME - TGChannel.MIN_VOLUME);
			this.volumeScale.setToolTipText(String.valueOf(percent) + "%");
		}

		TGChannel getChannel() {
			return this.channel;
		}

		UIControl getScale() {
			return this.volumeScale;
		}

		UIControl getLabel() {
			return this.instrumentLabel;
		}

		void updateVolume() {
			this.volumeScale.setValue(this.channel.getVolume());
		}

		@Override
		public void doActionWhenStable() {
			if (channel!=null) {
				short newVolume = (short) (volumeScale.getValue());
				TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGUpdateChannelAction.NAME);
				tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, TuxGuitar.getInstance().getDocumentManager().getSong());
				tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
				tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_BANK, channel.getBank());
				tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PROGRAM, channel.getProgram());
				tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_VOLUME, newVolume);
				tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_BALANCE, channel.getBalance());
				tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_CHORUS, channel.getChorus());
				tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_REVERB, channel.getReverb());
				tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PHASER, channel.getPhaser());
				tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_TREMOLO, channel.getTremolo());
				tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_NAME, channel.getName());
				tgActionProcessor.process();
			}
		}

		@Override
		public TGContext getContext() {
			return this.context;
		}
	}

}
