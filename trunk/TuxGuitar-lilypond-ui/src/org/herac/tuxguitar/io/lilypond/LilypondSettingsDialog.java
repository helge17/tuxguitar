package org.herac.tuxguitar.io.lilypond;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
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
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class LilypondSettingsDialog {
	
	private TGContext context;
	
	private TGSong song;
	
	public LilypondSettingsDialog(TGContext context, TGSong song){
		this.context = context;
		this.song = song;
	}
	
	public void open(final LilypondSettings settings, final Runnable onSuccess) {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow uiParent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("lilypond.options"));
		
		UITableLayout columnLeftLayout = new UITableLayout(0f);
		UIPanel columnLeft = uiFactory.createPanel(dialog, false);
		columnLeft.setLayout(columnLeftLayout);
		dialogLayout.set(columnLeft, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, 200f, null, 0f);
		
		UITableLayout columnRightLayout = new UITableLayout(0f);
		UIPanel columnRight = uiFactory.createPanel(dialog, false);
		columnRight.setLayout(columnRightLayout);
		dialogLayout.set(columnRight, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, 200f, null, 0f);
		
		//------------------TRACK SELECTION------------------
		UITableLayout trackLayout = new UITableLayout();
		UILegendPanel trackGroup = uiFactory.createLegendPanel(columnLeft);
		trackGroup.setLayout(trackLayout);
		trackGroup.setText(TuxGuitar.getProperty("lilypond.options.select-track.tip"));
		columnLeftLayout.set(trackGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UILabel trackLabel = uiFactory.createLabel(trackGroup);
		trackLabel.setText(TuxGuitar.getProperty("lilypond.options.select-track") + ":");
		trackLayout.set(trackLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<Integer> trackCombo = uiFactory.createDropDownSelect(trackGroup);
		for(int number = 1; number <= this.song.countTracks(); number ++){
			trackCombo.addItem(new UISelectItem<Integer>(TuxGuitar.getInstance().getSongManager().getTrack(this.song, number).getName(), number));
		}
		trackCombo.setSelectedValue(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber());
		trackCombo.setEnabled( settings.getTrack() != LilypondSettings.ALL_TRACKS );
		trackLayout.set(trackCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 1, 120f, null, null);
		
		final UICheckBox trackAllCheck = uiFactory.createCheckBox(trackGroup);
		trackAllCheck.setText(TuxGuitar.getProperty("lilypond.options.select-all-tracks"));
		trackAllCheck.setSelected( settings.getTrack() == LilypondSettings.ALL_TRACKS );
		trackLayout.set(trackAllCheck, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 2);
		
		//------------------MEASURE RANGE------------------
		UITableLayout measureRangeLayout = new UITableLayout();
		UILegendPanel measureRange = uiFactory.createLegendPanel(columnLeft);
		measureRange.setLayout(measureRangeLayout);
		measureRange.setText(TuxGuitar.getProperty("lilypond.options.measure-range.tip"));
		columnLeftLayout.set(measureRange, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final int minSelection = 1;
		final int maxSelection = this.song.countMeasureHeaders();
		
		UILabel measureFromLabel = uiFactory.createLabel(measureRange);
		measureFromLabel.setText(TuxGuitar.getProperty("lilypond.options.measure-range.from"));
		measureRangeLayout.set(measureFromLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UISpinner measureFromSpinner = uiFactory.createSpinner(measureRange);
		measureFromSpinner.setMaximum(maxSelection);
		measureFromSpinner.setMinimum(minSelection);
		measureFromSpinner.setValue(minSelection);
		measureRangeLayout.set(measureFromSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 60f, null, null);
		
		UILabel measureToLabel = uiFactory.createLabel(measureRange);
		measureToLabel.setText(TuxGuitar.getProperty("lilypond.options.measure-range.to"));
		measureRangeLayout.set(measureToLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UISpinner measureToSpinner = uiFactory.createSpinner(measureRange);
		measureToSpinner.setMinimum(minSelection);
		measureToSpinner.setMaximum(maxSelection);
		measureToSpinner.setValue(maxSelection);
		measureRangeLayout.set(measureToSpinner, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 60f, null, null);
		
		measureFromSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int fromSelection = measureFromSpinner.getValue();
				int toSelection = measureToSpinner.getValue();
				
				if(fromSelection < minSelection){
					measureFromSpinner.setValue(minSelection);
				}else if(fromSelection > toSelection){
					measureFromSpinner.setValue(toSelection);
				}
			}
		});
		measureToSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int toSelection = measureToSpinner.getValue();
				int fromSelection = measureFromSpinner.getValue();
				if(toSelection < fromSelection){
					measureToSpinner.setValue(fromSelection);
				}else if(toSelection > maxSelection){
					measureToSpinner.setValue(maxSelection);
				}
			}
		});
		
		//------------------VERSION OPTIONS------------------
		UITableLayout versionLayout = new UITableLayout();
		UILegendPanel versionGroup = uiFactory.createLegendPanel(columnRight);
		versionGroup.setLayout(versionLayout);
		versionGroup.setText(TuxGuitar.getProperty("lilypond.options.format-version"));
		columnRightLayout.set(versionGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UITextField lilyVersion = uiFactory.createTextField(versionGroup);
		lilyVersion.setText(settings.getLilypondVersion());
		versionLayout.set(lilyVersion, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//------------------LAYOUT OPTIONS------------------
		
		UITableLayout layoutGroupLayout = new UITableLayout();
		UILegendPanel layoutGroup = uiFactory.createLegendPanel(columnRight);
		layoutGroup.setLayout(layoutGroupLayout);
		layoutGroup.setText(TuxGuitar.getProperty("lilypond.options.layout.tip"));
		columnRightLayout.set(layoutGroup, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox scoreCheck = uiFactory.createCheckBox(layoutGroup);
		scoreCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-score"));
		scoreCheck.setSelected(settings.isScoreEnabled());
		layoutGroupLayout.set(scoreCheck, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		final UICheckBox tablatureCheck = uiFactory.createCheckBox(layoutGroup);
		tablatureCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-tablature"));
		tablatureCheck.setSelected(settings.isTablatureEnabled());
		layoutGroupLayout.set(tablatureCheck, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		final UICheckBox trackGroupCheck = uiFactory.createCheckBox(layoutGroup);
		trackGroupCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-track-groups"));
		trackGroupCheck.setSelected(settings.isTrackGroupEnabled());
		trackGroupCheck.setEnabled(settings.getTrack() == LilypondSettings.ALL_TRACKS);
		layoutGroupLayout.set(trackGroupCheck, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		final UICheckBox trackNameCheck = uiFactory.createCheckBox(layoutGroup);
		trackNameCheck.setSelected(settings.isTrackNameEnabled());
		trackNameCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-track-names"));
		layoutGroupLayout.set(trackNameCheck, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		final UICheckBox lyricsCheck = uiFactory.createCheckBox(layoutGroup);
		lyricsCheck.setSelected(settings.isLyricsEnabled());
		lyricsCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-lyrics"));
		layoutGroupLayout.set(lyricsCheck, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		final UICheckBox textsCheck = uiFactory.createCheckBox(layoutGroup);
		textsCheck.setSelected(settings.isTextEnabled());
		textsCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-texts"));
		layoutGroupLayout.set(textsCheck, 6, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		final UICheckBox chordDiagramsCheck = uiFactory.createCheckBox(layoutGroup);
		chordDiagramsCheck.setSelected(settings.isChordDiagramEnabled());
		chordDiagramsCheck.setText(TuxGuitar.getProperty("lilypond.options.layout.enable-chord-diagrams"));
		layoutGroupLayout.set(chordDiagramsCheck, 7, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		tablatureCheck.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if(!tablatureCheck.isSelected()){
					scoreCheck.setSelected(true);
				}
			}
		});
		scoreCheck.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if(!scoreCheck.isSelected()){
					tablatureCheck.setSelected(true);
				}
			}
		});
		trackAllCheck.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				trackLabel.setEnabled( !trackAllCheck.isSelected() );
				trackCombo.setEnabled( !trackAllCheck.isSelected() );
				trackGroupCheck.setEnabled( trackAllCheck.isSelected() );
			}
		});
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true, 1, 2);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				Integer selectedTrack = trackCombo.getSelectedValue();
				
				settings.setTrack(trackAllCheck.isSelected() || selectedTrack == null ? LilypondSettings.ALL_TRACKS : selectedTrack);
				settings.setTrackGroupEnabled( trackAllCheck.isSelected()? trackGroupCheck.isSelected() : false);
				settings.setTrackNameEnabled( trackNameCheck.isSelected() );
				settings.setMeasureFrom(measureFromSpinner.getValue());
				settings.setMeasureTo(measureToSpinner.getValue());
				settings.setScoreEnabled(scoreCheck.isSelected());
				settings.setTablatureEnabled(tablatureCheck.isSelected());
				settings.setChordDiagramEnabled(chordDiagramsCheck.isSelected());
				settings.setLyricsEnabled(lyricsCheck.isSelected());
				settings.setTextEnabled(textsCheck.isSelected());
				settings.setLilypondVersion(lilyVersion.getText());
				settings.check();
				
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
}
