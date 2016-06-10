package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.message.TGMessageDialog;
import org.herac.tuxguitar.app.view.dialog.message.TGMessageDialogController;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGMessageDialogUtil {
	
	public static void infoMessage(TGContext context, String title, String message){
		showMessage(context, TGWindow.getInstance(context).getWindow(), title, message, TGMessageDialog.STYLE_INFO);
	}
	
	public static void infoMessage(TGContext context, UIWindow window, String title, String message){
		showMessage(context, window, title, message, TGMessageDialog.STYLE_INFO);
	}
	
	public static void errorMessage(TGContext context, UIWindow window, String message){
		showMessage(context, window, TuxGuitar.getProperty("error"), message, TGMessageDialog.STYLE_ERROR);
	}
	
	public static void showMessage(TGContext context, UIWindow window, String title, String message, int style) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGMessageDialogController());
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT2, window);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_STYLE, style);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_TITLE, title);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_MESSAGE, message);
		tgActionProcessor.process();
	}
}
