package org.herac.tuxguitar.app.view.dialog.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.helper.TGFileHistory;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatUtils;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooserFormat;
import org.herac.tuxguitar.ui.chooser.UIFileChooserHandler;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGFileChooserDialog {
	
	public static final String ATTRIBUTE_HANDLER = TGFileChooserHandler.class.getName();
	public static final String ATTRIBUTE_STYLE = "style";
	public static final String ATTRIBUTE_FILE_FORMATS = "fileFormats";
	public static final String ATTRIBUTE_FILE_NAME = "fileName";
	public static final String ATTRIBUTE_CHOOSER_PATH = "chooserPath";
	public static final String ATTRIBUTE_DEFAULT_EXTENSION = "defaultExtension";
	
	public static final Integer STYLE_OPEN = 1;
	public static final Integer STYLE_SAVE = 2;
	
	public void show(final TGViewContext context) {
		final Integer style = context.getAttribute(ATTRIBUTE_STYLE);
		final String chooserPath = context.getAttribute(ATTRIBUTE_CHOOSER_PATH);
		final String fileName = context.getAttribute(ATTRIBUTE_FILE_NAME);
		final List<TGFileFormat> formats = context.getAttribute(ATTRIBUTE_FILE_FORMATS);
		
		final UIFactory factory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		
		UIFileChooser dialog = this.createChooser(factory, parent, style);
		if( dialog != null ) {
			File defaultPath = null;
			if( chooserPath != null ) {
				defaultPath = new File(chooserPath);
			}
			if( fileName != null ) {
				defaultPath = new File(defaultPath, fileName);
			}
			
			dialog.setDefaultPath(defaultPath);
			dialog.setSupportedFormats(this.createSupportedFormats(formats));
			
			dialog.choose(new UIFileChooserHandler() {
				public void onSelectFile(File file) {
					if( file != null ) {
						String path = file.getAbsolutePath();
						File fileParent = file.getParentFile();
						if( fileParent != null && fileParent.exists() && fileParent.isDirectory() ){
							TGFileHistory.getInstance(context.getContext()).setChooserPath( fileParent.getAbsolutePath() );
						}
						
						if( STYLE_SAVE.equals(style) && !TGFileFormatUtils.isSupportedFormat(formats, path)) {
							String defaultExtension = context.getAttribute(ATTRIBUTE_DEFAULT_EXTENSION);
							if( defaultExtension != null ) {
								path += defaultExtension;
							}
						}
						
						TGFileChooserDialog.this.handleFileName(context, path);
					}
				}
			});
		}
	}
	
	public UIFileChooser createChooser(UIFactory factory, UIWindow parent, Integer style) {
		if( style != null ) {
			if( STYLE_OPEN.equals(style) ) {
				return factory.createOpenFileChooser(parent);
			}
			if( STYLE_SAVE.equals(style) ) {
				return factory.createSaveFileChooser(parent);
			}
		}
		return null;
	}
	
	public List<UIFileChooserFormat> createSupportedFormats(List<TGFileFormat> formats) {
		List<UIFileChooserFormat> supportedFormats = new ArrayList<UIFileChooserFormat>();
		
		UIFileChooserFormat allSupportedFormats = new UIFileChooserFormat("All Supported Formats");
		supportedFormats.add(allSupportedFormats);
		
		for(TGFileFormat format : formats) {
			supportedFormats.add(new UIFileChooserFormat(format.getName(), Arrays.asList(format.getSupportedFormats())));
			for(String extension : format.getSupportedFormats()) {
				if(!allSupportedFormats.getExtensions().contains(extension)) {
					allSupportedFormats.getExtensions().add(extension);
				}
			}
		}
		supportedFormats.add(new UIFileChooserFormat("All Files", Arrays.asList("*")));
		
		return supportedFormats;
	}
	
	public void handleFileName(TGViewContext context, String fileName) {
		if( STYLE_SAVE.equals(context.getAttribute(ATTRIBUTE_STYLE)) && new File(fileName).exists() ) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(context.getContext(), TGOpenViewAction.NAME);
			tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("file.overwrite-question"));
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO);
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_NO);
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, this.createUpdateFileNameRunnable(context, fileName));
			tgActionProcessor.process();
		} else {
			updateFileName(context, fileName);
		}
	}
	
	public void updateFileName(TGViewContext context, String fileName) {
		TGFileChooserHandler tgFileChooserHandler = context.getAttribute(ATTRIBUTE_HANDLER);
		tgFileChooserHandler.updateFileName(fileName);
	}
	
	public void updateFileNameInNewThread(final TGViewContext context, final String fileName) {
		new Thread(new Runnable() {
			public void run() {
				updateFileName(context, fileName);
			}
		}).start();
	}
	
	public Runnable createUpdateFileNameRunnable(final TGViewContext context, final String fileName) {
		return new Runnable() {
			public void run() {
				updateFileNameInNewThread(context, fileName);
			}
		};
	}
}
