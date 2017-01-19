package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class SVGStylesDialog extends SVGStyles {
	
	private TGContext context;
	
	public SVGStylesDialog(TGContext context){
		this.context = context;
	}
	
	public void configure(final Runnable onSuccess) {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow uiParent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("options"));
		
		//------------------TRACK SELECTION------------------
		UITableLayout trackLayout = new UITableLayout();
		UILegendPanel track = uiFactory.createLegendPanel(dialog);
		track.setLayout(trackLayout);
		track.setText(TuxGuitar.getProperty("track"));
		dialogLayout.set(track, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);
		
		final UILabel trackLabel = uiFactory.createLabel(track);
		trackLabel.setText(TuxGuitar.getProperty("track"));
		trackLayout.set(trackLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<Integer> trackCombo = uiFactory.createDropDownSelect(track);
		TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();
		for(int number = 1; number <= song.countTracks(); number ++){
			trackCombo.addItem(new UISelectItem<Integer>(TuxGuitar.getInstance().getSongManager().getTrack(song, number).getName(), number));
		}
		trackCombo.setSelectedValue(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber());
		trackLayout.set(trackCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		
		final UICheckBox trackAllCheck = uiFactory.createCheckBox(track);
		trackAllCheck.setText(TuxGuitar.getProperty("export.all-tracks"));
		trackAllCheck.setSelected(false);
		trackAllCheck.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				trackLabel.setEnabled( !trackAllCheck.isSelected() );
				trackCombo.setEnabled( !trackAllCheck.isSelected() );
			}
		});
		trackLayout.set(trackAllCheck, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
		
		//------------------CHECK OPTIONS--------------------
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel options = uiFactory.createLegendPanel(dialog);
		options.setLayout(optionsLayout);
		options.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(options, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);
		
		final UICheckBox tablatureEnabled = uiFactory.createCheckBox(options);
		tablatureEnabled.setText(TuxGuitar.getProperty("export.tablature-enabled"));
		tablatureEnabled.setSelected(true);
		optionsLayout.set(tablatureEnabled, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		final UICheckBox scoreEnabled = uiFactory.createCheckBox(options);
		scoreEnabled.setText(TuxGuitar.getProperty("export.score-enabled"));
		scoreEnabled.setSelected(true);
		optionsLayout.set(scoreEnabled, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		final UICheckBox chordNameEnabled = uiFactory.createCheckBox(options);
		chordNameEnabled.setText(TuxGuitar.getProperty("export.chord-name-enabled"));
		chordNameEnabled.setSelected(true);
		optionsLayout.set(chordNameEnabled, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		final UICheckBox chordDiagramEnabled = uiFactory.createCheckBox(options);
		chordDiagramEnabled.setText(TuxGuitar.getProperty("export.chord-diagram-enabled"));
		chordDiagramEnabled.setSelected(true);
		optionsLayout.set(chordDiagramEnabled, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		tablatureEnabled.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if(!tablatureEnabled.isSelected()){
					scoreEnabled.setSelected(true);
				}
			}
		});
		scoreEnabled.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if(!scoreEnabled.isSelected()){
					tablatureEnabled.setSelected(true);
				}
			}
		});
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				Integer selectedTrack = trackCombo.getSelectedValue();
				
				int track = (trackAllCheck.isSelected() || selectedTrack == null ? -1 : selectedTrack);
				boolean showScore = scoreEnabled.isSelected();
				boolean showTablature = tablatureEnabled.isSelected();
				boolean showChordName = chordNameEnabled.isSelected();
				boolean showChordDiagram = chordDiagramEnabled.isSelected();
				
				configure(track, showScore, showTablature, showChordName, showChordDiagram);
				
				dialog.dispose();
				onSuccess.run();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void configure(int track, boolean showScore,boolean showTablature,boolean showChordName,boolean showChordDiagram) {
		this.configureWithDefaults();
		this.setTrack( track );
		this.setFlags( TGLayout.DISPLAY_COMPACT );
		if( showScore ){
			this.setFlags( this.getFlags() | TGLayout.DISPLAY_SCORE );
		}
		if( showTablature ){
			this.setFlags( this.getFlags() | TGLayout.DISPLAY_TABLATURE );
		}
		if( showChordName ){
			this.setFlags( this.getFlags() | TGLayout.DISPLAY_CHORD_NAME );
		}
		if( showChordDiagram ){
			this.setFlags( this.getFlags() | TGLayout.DISPLAY_CHORD_DIAGRAM );
		}
		if( track < 0 ){
			this.setFlags( this.getFlags() | TGLayout.DISPLAY_MULTITRACK );
		}
	}
}
