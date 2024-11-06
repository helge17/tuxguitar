package org.herac.tuxguitar.app.action.impl.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionContextImpl;
import org.herac.tuxguitar.app.document.TGDocumentFileManager;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.editor.template.TGTemplate;
import org.herac.tuxguitar.editor.template.TGTemplateManager;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.tg.TGStream;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGUserFileUtils;

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
