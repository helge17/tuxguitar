package app.tuxguitar.app.view.toolbar.main;
/** 
 * Tempo indicator
 * defined as a section, not a toolBar item, because it hosts a consistent set of several controls (icon + label)
 */

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import app.tuxguitar.app.action.impl.transport.TGChangeTempoPercentageAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.event.TGRedrawEvent;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIMouseDownListener;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.widget.UIImageView;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarSectionTempo extends TGMainToolBarSection implements TGEventListener {

	private UIImageView tempoImage;
	private UILabel tempoLabel;
	private TGSyncProcessLocked redrawProcess;
	private int currentTempoBase;
	private boolean currentTempoDotted;
	private int currentTempoValue;
	
	private UILabel tempoPercentLabel;
	private UISpinner tempoPercentSpinner;

	public TGMainToolBarSectionTempo(TGContext context, UIPanel parentPanel) {
		super(context);
		UIFactory uiFactory = TGApplication.getInstance(this.getContext()).getFactory();

		this.tempoImage = uiFactory.createImageView(parentPanel);
		this.controls.add(this.tempoImage);
		this.tempoLabel = uiFactory.createLabel(parentPanel);
		this.tempoLabel.addMouseDownListener(new UIMouseDownListener() {
			@Override
			public void onMouseDown(UIMouseEvent event) {
				new TGActionProcessor(TGMainToolBarSectionTempo.this.getContext(), TGOpenTempoDialogAction.NAME)
						.process();
			}
		});
		this.tempoImage.addDisposeListener(new UIDisposeListener() {
			@Override
			public void onDispose(UIDisposeEvent event) {
				TuxGuitar.getInstance().getEditorManager().removeRedrawListener(TGMainToolBarSectionTempo.this);
			}
		});
		this.controls.add(this.tempoLabel);
		
		this.tempoPercentSpinner = uiFactory.createSpinner(parentPanel);
		this.tempoPercentSpinner.setVisible(true);
		this.tempoPercentSpinner.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				int tempoPercent = tempoPercentSpinner.getValue();
				
				TGActionProcessor tgActionProcessor = new TGActionProcessor(getContext(), TGChangeTempoPercentageAction.NAME);
				tgActionProcessor.setAttribute(
					TGChangeTempoPercentageAction.ATTRIBUTE_PERCENTAGE_VALUE, 
					tempoPercent
				);
				tgActionProcessor.process();
			}
		});
		this.controls.add(this.tempoPercentSpinner);
		
		this.tempoPercentLabel = uiFactory.createLabel(parentPanel);
		this.controls.add(this.tempoPercentLabel);
		
		this.loadIcons();
		this.createSyncProcesses();
		this.appendListeners();
	}

	private void createSyncProcesses() {
		this.redrawProcess = new TGSyncProcessLocked(getContext(), new Runnable() {
			public void run() {
				TGMainToolBarSectionTempo.this.updateItems();
			}
		});
	}

	private void appendListeners() {
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
	}

	@Override
	public void processEvent(final TGEvent event) {
		if (TGRedrawEvent.EVENT_TYPE.equals(event.getEventType())) {
			int type = ((Integer) event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
			if (type == TGRedrawEvent.PLAYING_THREAD || type == TGRedrawEvent.PLAYING_NEW_BEAT) {
				this.redrawProcess.process();
			}
		}
	}

	@Override
	// called when skin changes
	public void loadIcons() {
		this.updateTempoIcon();
	}

	private void updateTempoIcon() {
		if (this.currentTempoBase != 0) {
			this.tempoImage.setImage(TuxGuitar.getInstance().getIconManager().getDuration(this.currentTempoBase,
					this.currentTempoDotted));
		} else {
			// still load an icon, to make sure layout is ok (it will be refreshed after)
			this.tempoImage.setImage(TGIconManager.getInstance(getContext()).getImageByName(TGIconManager.QUARTER));
		}
	}

	@Override
	public void loadProperties() {
		if (this.currentTempoValue != 0) {
			this.tempoLabel.setText("= " + String.valueOf(this.currentTempoValue));
		} else {
			// still load an icon, to make sure layout is ok (it will be refreshed after)
			this.tempoLabel.setText("= 120");
		}

		this.tempoPercentSpinner.setMinimum(0);
		this.tempoPercentSpinner.setMaximum(100);
		this.tempoPercentSpinner.setIncrement(1);
		this.tempoPercentSpinner.setValue(100);
		this.tempoPercentSpinner.setEnabled(true);
		
		this.tempoPercentLabel.setText("%");
	}

	@Override
	public void updateItems() {
		TGTempo tempo;

		MidiPlayer midiPlayer = MidiPlayer.getInstance(getContext());
		int tempoPercent = midiPlayer.getMode().getCurrentPercent();
		
		if ((midiPlayer.isRunning() && (midiPlayer.getCurrentTempo() != null))) {
			tempo = midiPlayer.getCurrentTempo();
			// update tempoPercent as midiPlayer is playing
			tempoPercent = midiPlayer.getMode().getCurrentPercent();
			this.tempoLabel.setIgnoreEvents(true);
			this.tempoPercentSpinner.setIgnoreEvents(true);
		} else {
			tempo = TablatureEditor.getInstance(getContext()).getTablature().getCaret().getMeasure().getTempo();
			this.tempoLabel.setIgnoreEvents(false);
			this.tempoPercentSpinner.setIgnoreEvents(false);
		}
		if ((tempo.getBase() != this.currentTempoBase) || (tempo.isDotted() != this.currentTempoDotted)) {
			this.currentTempoBase = tempo.getBase();
			this.currentTempoDotted = tempo.isDotted();
			this.updateTempoIcon();
		}
		int tempoValue = tempo.getRawValue() * tempoPercent / 100;
		if (tempoValue != this.currentTempoValue) {
			this.currentTempoValue = tempoValue;
			this.tempoLabel.setText("= " + String.valueOf(this.currentTempoValue));
		}
		this.tempoPercentSpinner.setValue(tempoPercent);
	}

	@Override
	public void addToolBarItem(TGMainToolBarItemConfig toolBarItemConfig) {
		// nothing to do, content of this section is not configurable
	}
}
