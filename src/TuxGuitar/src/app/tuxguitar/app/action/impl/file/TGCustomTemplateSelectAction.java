package app.tuxguitar.app.action.impl.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionContextImpl;
import app.tuxguitar.app.document.TGDocumentFileManager;
import app.tuxguitar.app.util.TGMessageDialogUtil;
import app.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.file.TGReadSongAction;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.template.TGTemplate;
import app.tuxguitar.editor.template.TGTemplateManager;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.tg.TGStream;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGUserFileUtils;

public class TGCustomTemplateSelectAction extends TGActionBase {

	public static final String NAME = "action.custom-template.select";

	public TGCustomTemplateSelectAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext actionContext) {
		List<TGFileFormat> fileFormats = this.createFileFormats(actionContext);

		TGDocumentFileManager tgDocumentFileManager = TGDocumentFileManager.getInstance(getContext());
		tgDocumentFileManager.chooseFileNameForOpen(fileFormats, new TGFileChooserHandler() {
			public void updateFileName(final String fileName) {
				File file = new File(fileName);
				// check selected template is valid
				TGTemplate userTemplate = new TGTemplate();
				userTemplate.setName(new String());
				userTemplate.setResource(file.getAbsolutePath());
				userTemplate.setUserTemplate();
				TGSong song = TGTemplateManager.getInstance(getContext()).getTemplateAsSong(userTemplate);
				if (song == null) {
					UIWindow window = TGWindow.getInstance(getContext()).getWindow();
					TGMessageDialogUtil.errorMessage(getContext(), window , TuxGuitar.getProperty("file.custom-template.error"));
				}
				else {
					TGUserFileUtils.setUserTemplate(file);
					TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
					tgActionManager.execute(TGLoadTemplateAction.NAME, new TGActionContextImpl());
					TGEventManager.getInstance(getContext()).fireEvent(new TGUpdateEvent(TGUpdateEvent.CUSTOM_TEMPLATE_CHANGED, getContext()));
				}
			}
		});

	}

	public List<TGFileFormat> createFileFormats(final TGActionContext context) {
		TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(getContext());
		TGFileFormat fileFormat = context.getAttribute(TGReadSongAction.ATTRIBUTE_FORMAT);
		if( fileFormat == null ) {
			return fileFormatManager.findReadFileFormats(true);
		}

		List<TGFileFormat> fileFormats = new ArrayList<TGFileFormat>();
		fileFormats.add(fileFormat);

		return fileFormats;
	}
}
