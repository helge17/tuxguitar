package app.tuxguitar.app.view.dialog.backingtrack;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.backingtrack.TGBackingTrackManager;
import app.tuxguitar.app.document.TGDocumentFileManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGSetBackingTrackAction;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIReadOnlyTextField;
import app.tuxguitar.ui.widget.UISlider;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGBackingTrackDialog {

	private TGContext context;
	private UIWindow window;
	private UIReadOnlyTextField fileField;
	private UISlider volumeSlider;
	private UILabel volumeLabel;
	private String selectedPath;
	private float originalVolume;

	public TGBackingTrackDialog(TGContext context) {
		this.context = context;
	}

	public void show() {
		UIFactory factory = TGApplication.getInstance(this.context).getFactory();
		TGSong song = TGDocumentManager.getInstance(this.context).getSong();
		float currentVolume = (song != null) ? song.getBackingTrackVolume() : 1.0f;
		this.originalVolume = currentVolume;
		this.selectedPath = (song != null) ? song.getBackingTrack() : null;

		this.window = factory.createWindow(TGWindow.getInstance(this.context).getWindow(), true, false);
		this.window.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		this.window.setLayout(new UITableLayout());
		this.window.setText(TuxGuitar.getProperty("backing-track.dialog.title"));

		UITableLayout layout = (UITableLayout) this.window.getLayout();

		//--FILE ROW--
		UILabel fileLabel = factory.createLabel(this.window);
		fileLabel.setText(TuxGuitar.getProperty("backing-track.dialog.file"));
		layout.set(fileLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);

		this.fileField = factory.createReadOnlyTextField(this.window);
		this.fileField.setText((this.selectedPath != null && !this.selectedPath.trim().isEmpty())
				? this.selectedPath : TuxGuitar.getProperty("backing-track.dialog.file.none"));
		layout.set(this.fileField, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

		UIButton browseButton = factory.createButton(this.window);
		browseButton.setText(TuxGuitar.getProperty("backing-track.dialog.browse"));
		browseButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGBackingTrackDialog.this.openFileChooser();
			}
		});
		layout.set(browseButton, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);

		//--VOLUME ROW--
		UILabel volumeText = factory.createLabel(this.window);
		volumeText.setText(TuxGuitar.getProperty("backing-track.dialog.volume"));
		layout.set(volumeText, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);

		this.volumeSlider = factory.createHorizontalSlider(this.window);
		this.volumeSlider.setMinimum(0);
		this.volumeSlider.setMaximum(100);
		this.volumeSlider.setIncrement(1);
		this.volumeSlider.setValue(Math.round(currentVolume * 100f));
		layout.set(this.volumeSlider, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

		this.volumeLabel = factory.createLabel(this.window);
		this.volumeLabel.setText(this.getVolumeLabelText());
		layout.set(this.volumeLabel, 2, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);

		this.volumeSlider.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGBackingTrackDialog.this.updateVolumePreview();
			}
		});

		//--BUTTONS ROW--
		UIButton okButton = factory.createButton(this.window);
		okButton.setText(TuxGuitar.getProperty("backing-track.dialog.ok"));
		okButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGBackingTrackDialog.this.applyAndClose();
			}
		});
		layout.set(okButton, 3, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);

		UIButton cancelButton = factory.createButton(this.window);
		cancelButton.setText(TuxGuitar.getProperty("backing-track.dialog.cancel"));
		cancelButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGBackingTrackDialog.this.cancelAndClose();
			}
		});
		layout.set(cancelButton, 3, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);

		TGDialogUtil.openDialog(this.window, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private void openFileChooser() {
		TGFileFormat fileFormat = new TGFileFormat(TuxGuitar.getProperty("file.backing-track.audio"), "audio/*",
				new String[] {"mp3", "wav", "aif", "aiff", "au"});
		TGDocumentFileManager.getInstance(this.context).chooseFileNameForOpen(fileFormat, new TGFileChooserHandler() {
			public void updateFileName(final String fileName) {
				TGBackingTrackDialog.this.selectedPath = fileName;
				TGBackingTrackDialog.this.fileField.setText(fileName);
			}
		});
	}

	private String getVolumeLabelText() {
		return TuxGuitar.getProperty("backing-track.dialog.volume.value",
				new String[] {String.valueOf(this.volumeSlider.getValue())});
	}

	private void updateVolumePreview() {
		this.volumeLabel.setText(this.getVolumeLabelText());
		TGBackingTrackManager.getInstance(this.context).setVolume(this.volumeSlider.getValue() / 100f);
	}

	private void applyAndClose() {
		TGSong song = TGDocumentManager.getInstance(this.context).getSong();
		if( song != null ) {
			float volume = this.volumeSlider.getValue() / 100f;
			song.setBackingTrackVolume(volume);
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGSetBackingTrackAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
			tgActionProcessor.setAttribute(TGSetBackingTrackAction.ATTRIBUTE_REMOVE, Boolean.FALSE);
			tgActionProcessor.setAttribute(TGSetBackingTrackAction.ATTRIBUTE_PATH,
					(this.selectedPath != null) ? this.selectedPath : song.getBackingTrack());
			tgActionProcessor.setAttribute(TGSetBackingTrackAction.ATTRIBUTE_VOLUME, Float.valueOf(volume));
			tgActionProcessor.processOnNewThread();
		}
		this.window.dispose();
	}

	private void cancelAndClose() {
		TGBackingTrackManager.getInstance(this.context).setVolume(this.originalVolume);
		this.window.dispose();
	}
}
