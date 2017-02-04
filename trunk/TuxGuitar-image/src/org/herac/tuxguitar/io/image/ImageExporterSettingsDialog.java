package org.herac.tuxguitar.io.image;

import java.io.File;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.print.TGPrintSettings;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooser;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooserHandler;
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
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class ImageExporterSettingsDialog {
	
	private TGContext context;
	
	public ImageExporterSettingsDialog(TGContext context) {
		this.context = context;
	}
	
	public void openSettingsDialog(final TGSongStreamContext context, final Runnable callback) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGPrintSettings styles = createDefaultStyles(song) ;
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow uiParent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("options"));
		
		//------------------FORMAT SELECTION------------------
		UITableLayout formatLayout = new UITableLayout();
		UILegendPanel formatGroup = uiFactory.createLegendPanel(dialog);
		formatGroup.setLayout(formatLayout);
		formatGroup.setText(TuxGuitar.getProperty("tuxguitar-image.format"));
		dialogLayout.set(formatGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);
		
		UILabel formatLabel = uiFactory.createLabel(formatGroup);
		formatLabel.setText(TuxGuitar.getProperty("tuxguitar-image.format"));
		formatLayout.set(formatLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<ImageFormat> formatCombo = uiFactory.createDropDownSelect(formatGroup);
		for(int i = 0; i < ImageFormat.IMAGE_FORMATS.length; i ++){
			formatCombo.addItem(new UISelectItem<ImageFormat>(ImageFormat.IMAGE_FORMATS[i].getName(), ImageFormat.IMAGE_FORMATS[i]));
		}
		formatCombo.setSelectedValue(ImageFormat.IMAGE_FORMATS[0]);
		formatLayout.set(formatCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------TRACK SELECTION------------------
		UITableLayout trackLayout = new UITableLayout();
		UILegendPanel track = uiFactory.createLegendPanel(dialog);
		track.setLayout(trackLayout);
		track.setText(TuxGuitar.getProperty("track"));
		dialogLayout.set(track, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);
		
		UILabel trackLabel = uiFactory.createLabel(track);
		trackLabel.setText(TuxGuitar.getProperty("track"));
		trackLayout.set(trackLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<Integer> tracks = uiFactory.createDropDownSelect(track);
		for(int number = 1; number <= song.countTracks(); number ++){
			tracks.addItem(new UISelectItem<Integer>(TuxGuitar.getInstance().getSongManager().getTrack(song, number).getName(), number));
		}
		tracks.setSelectedValue(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber());
		trackLayout.set(tracks, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------MEASURE RANGE------------------
		UITableLayout rangeLayout = new UITableLayout();
		UILegendPanel range = uiFactory.createLegendPanel(dialog);
		range.setLayout(rangeLayout);
		range.setText(TuxGuitar.getProperty("print.range"));
		dialogLayout.set(range, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);
		
		final int minSelection = 1;
		final int maxSelection = song.countMeasureHeaders();
		
		UILabel fromLabel = uiFactory.createLabel(range);
		fromLabel.setText(TuxGuitar.getProperty("edit.from"));
		rangeLayout.set(fromLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner fromSpinner = uiFactory.createSpinner(range);
		fromSpinner.setMaximum(maxSelection);
		fromSpinner.setMinimum(minSelection);
		fromSpinner.setValue(minSelection);
		rangeLayout.set(fromSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 60f, null, null);
		
		UILabel toLabel = uiFactory.createLabel(range);
		toLabel.setText(TuxGuitar.getProperty("edit.to"));
		rangeLayout.set(toLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner toSpinner = uiFactory.createSpinner(range);
		toSpinner.setMinimum(minSelection);
		toSpinner.setMaximum(maxSelection);
		toSpinner.setValue(maxSelection);
		rangeLayout.set(toSpinner, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 60f, null, null);
		
		fromSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int fromSelection = fromSpinner.getValue();
				int toSelection = toSpinner.getValue();
				
				if(fromSelection < minSelection){
					fromSpinner.setValue(minSelection);
				}else if(fromSelection > toSelection){
					fromSpinner.setValue(toSelection);
				}
			}
		});
		toSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int toSelection = toSpinner.getValue();
				int fromSelection = fromSpinner.getValue();
				if(toSelection < fromSelection){
					toSpinner.setValue(fromSelection);
				}else if(toSelection > maxSelection){
					toSpinner.setValue(maxSelection);
				}
			}
		});
		//------------------CHECK OPTIONS--------------------
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel options = uiFactory.createLegendPanel(dialog);
		options.setLayout(optionsLayout);
		options.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(options, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);
		
		final UICheckBox tablatureEnabled = uiFactory.createCheckBox(options);
		tablatureEnabled.setText(TuxGuitar.getProperty("export.tablature-enabled"));
		tablatureEnabled.setSelected(true);
		optionsLayout.set(tablatureEnabled, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox scoreEnabled = uiFactory.createCheckBox(options);
		scoreEnabled.setText(TuxGuitar.getProperty("export.score-enabled"));
		scoreEnabled.setSelected(true);
		optionsLayout.set(scoreEnabled, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox chordNameEnabled = uiFactory.createCheckBox(options);
		chordNameEnabled.setText(TuxGuitar.getProperty("export.chord-name-enabled"));
		chordNameEnabled.setSelected(true);
		optionsLayout.set(chordNameEnabled, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox chordDiagramEnabled = uiFactory.createCheckBox(options);
		chordDiagramEnabled.setText(TuxGuitar.getProperty("export.chord-diagram-enabled"));
		chordDiagramEnabled.setSelected(true);
		optionsLayout.set(chordDiagramEnabled, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox blackAndWhite = uiFactory.createCheckBox(options);
		blackAndWhite.setText(TuxGuitar.getProperty("export.black-and-white"));
		blackAndWhite.setSelected(true);
		optionsLayout.set(blackAndWhite, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
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
		dialogLayout.set(buttons, 5, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				ImageFormat imageFormat = formatCombo.getSelectedValue();
				if( imageFormat == null ) {
					imageFormat = ImageFormat.IMAGE_FORMATS[0];
				}
				
				Integer trackNumber = tracks.getSelectedValue();
				if( trackNumber == null ) {
					trackNumber = 1;
				}
				
				int style = 0;
				style |= (scoreEnabled.isSelected() ? TGLayout.DISPLAY_SCORE : 0);
				style |= (tablatureEnabled.isSelected() ? TGLayout.DISPLAY_TABLATURE : 0);
				style |= (chordNameEnabled.isSelected() ? TGLayout.DISPLAY_CHORD_NAME : 0);
				style |= (chordDiagramEnabled.isSelected() ? TGLayout.DISPLAY_CHORD_DIAGRAM : 0);
				style |= (blackAndWhite.isSelected() ? TGLayout.DISPLAY_MODE_BLACK_WHITE : 0);
				styles.setTrackNumber(trackNumber);
				styles.setFromMeasure(fromSpinner.getValue());
				styles.setToMeasure(toSpinner.getValue());
				styles.setStyle(style);
				
				dialog.dispose();
				
				ImageExporterSettings settings = new ImageExporterSettings();
				settings.setStyles(styles);
				settings.setFormat(imageFormat);
				
				openDirectoryDialog(uiFactory, settings, context, callback);
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
	
	public void openDirectoryDialog(final UIFactory uiFactory, final ImageExporterSettings settings, final TGSongStreamContext context, final Runnable callback){
		UIWindow uiWindow = TGWindow.getInstance(this.context).getWindow();
		UIDirectoryChooser uiDirectoryChooser = uiFactory.createDirectoryChooser(uiWindow);
		uiDirectoryChooser.setText(TuxGuitar.getProperty("tuxguitar-image.directory-dialog.title"));
		uiDirectoryChooser.choose(new UIDirectoryChooserHandler() {
			public void onSelectDirectory(File file) {
				settings.setPath(file != null ? file.getAbsolutePath() : null);
				if( settings.getPath() != null ){
					context.setAttribute(ImageExporterSettings.class.getName(), settings);
					callback.run();
				}
			}
		});
	}
	
	public TGPrintSettings createDefaultStyles(TGSong song){
		TGPrintSettings styles = new TGPrintSettings();
		styles.setStyle(TGLayout.DISPLAY_TABLATURE);
		styles.setFromMeasure(1);
		styles.setToMeasure(song.countMeasureHeaders());
		styles.setTrackNumber(1);
		return styles;
	}
}
