package app.tuxguitar.app.util;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.dialog.message.TGMessageDialog;
import app.tuxguitar.app.view.dialog.message.TGMessageDialogController;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGMessageDialogUtil {

	public static void infoMessage(TGContext context, String title, String message){
		showMessage(context, TGWindow.getInstance(context).getWindow(), title, message, TGMessageDialog.STYLE_INFO);
	}

	public static void infoMessage(TGContext context, UIWindow window, String title, String message){
		showMessage(context, window, title, message, TGMessageDialog.STYLE_INFO);
	}

	public static void warningMessage(TGContext context, String title, String message){
		showMessage(context, TGWindow.getInstance(context).getWindow(), title, message, TGMessageDialog.STYLE_WARNING);
	}

	public static void errorMessage(TGContext context, UIWindow window, String message){
		showMessage(context, window, TuxGuitar.getProperty("error"), message, TGMessageDialog.STYLE_ERROR);
	}

	public static void showMessage(TGContext context, UIWindow window, String title, String message, int style) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGMessageDialogController());
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT, window);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_STYLE, style);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_TITLE, title);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_MESSAGE, message);
		tgActionProcessor.process();
	}
}
