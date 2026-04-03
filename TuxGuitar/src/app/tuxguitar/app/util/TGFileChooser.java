package app.tuxguitar.app.util;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.helper.TGFileHistory;
import app.tuxguitar.app.view.dialog.file.TGFileChooserDialog;
import app.tuxguitar.app.view.dialog.file.TGFileChooserDialogController;
import app.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatUtils;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGFileChooser {

	public static final String DEFAULT_OPEN_FILENAME = null;

	public static TGFileFormat ALL_FORMATS = new TGFileFormat(TuxGuitar.getProperty("file.all-files"), "*/*", new String[]{"*"});

	private TGContext context;

	private TGFileChooser(TGContext context) {
		this.context = context;
	}

	public void openChooser(TGFileChooserHandler handler, List<TGFileFormat> formats, Integer style, String fileName, String chooserPath, String defaultExtension) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGFileChooserDialogController());
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_HANDLER, handler);
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_STYLE, style);
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_CHOOSER_PATH, chooserPath);
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_FILE_FORMATS, formats);
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_FILE_NAME, fileName);
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_DEFAULT_EXTENSION, defaultExtension);
		tgActionProcessor.process();
	}

	public void openChooser(TGFileChooserHandler handler, TGFileFormat format, Integer style, String fileName, String chooserPath, String defaultExtension) {
		this.openChooser(handler, toFileFormatList(format), style, fileName, chooserPath, defaultExtension);
	}

	public void openChooser(TGFileChooserHandler handler, List<TGFileFormat> formats, Integer style, String fileName, String chooserPath) {
		this.openChooser(handler, formats, style, fileName, chooserPath, null);
	}

	public void openChooser(TGFileChooserHandler handler, TGFileFormat format, Integer style, String fileName, String chooserPath) {
		this.openChooser(handler, toFileFormatList(format), style, fileName, chooserPath);
	}

	public void openChooser(TGFileChooserHandler handler, List<TGFileFormat> formats, Integer style, String fileName) {
		this.openChooser(handler, formats, style, fileName, getDefaultChooserPath());
	}

	public void openChooser(TGFileChooserHandler handler, TGFileFormat format, Integer style, String fileName) {
		this.openChooser(handler, format, style, fileName, getDefaultChooserPath());
	}

	public void openChooser(TGFileChooserHandler handler, List<TGFileFormat> formats, Integer style) {
		this.openChooser(handler, formats, style, null);
	}

	public void openChooser(TGFileChooserHandler handler, TGFileFormat format, Integer style) {
		this.openChooser(handler, format, style, null);
	}

	public List<TGFileFormat> toFileFormatList(TGFileFormat format) {
		List<TGFileFormat> formats = new ArrayList<TGFileFormat>();
		formats.add(format);
		return formats;
	}

	public String getDefaultChooserPath() {
		return TGFileHistory.getInstance(this.context).getChooserPath();
	}

	public static TGFileChooser getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGFileChooser.class.getName(), new TGSingletonFactory<TGFileChooser>() {
			public TGFileChooser createInstance(TGContext context) {
				return new TGFileChooser(context);
			}
		});
	}

	public static String getDefaultSaveFileName() {
		return TuxGuitar.getProperty("file.save.default-name") + TGFileFormatUtils.DEFAULT_EXTENSION;
	}

}
