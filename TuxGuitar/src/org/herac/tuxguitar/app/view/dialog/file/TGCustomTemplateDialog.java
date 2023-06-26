package org.herac.tuxguitar.app.view.dialog.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.TGActionContextImpl;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.editor.template.TGTemplate;
import org.herac.tuxguitar.editor.template.TGTemplateManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooserFormat;
import org.herac.tuxguitar.ui.chooser.UIFileChooserHandler;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGUserFileUtils;

public class TGCustomTemplateDialog {
	private UIWindow dialog;
	private UIButton buttonDelete;
	private UILabel labelDelete;
	TGViewContext context;
	
	public void show(final TGViewContext context) {
		this.context = context;
		final UIFactory uiFactory = TGApplication.getInstance(this.context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		
		dialog = uiFactory.createWindow(uiParent, true, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("file.custom-template"));
		
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("file.custom-template"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300.0f, null, null);

		// BUTTON: choose custom template
		final UIButton buttonChoose = uiFactory.createButton(group);
		buttonChoose.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
		buttonChoose.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				chooseCustomTemplate(uiFactory);
			}
		});
		groupLayout.set(buttonChoose, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);
		final UILabel labelChoose = uiFactory.createLabel(group);
		labelChoose.setText(TuxGuitar.getProperty("file.custom-template.dialog.choose"));
		groupLayout.set(labelChoose, 1,2,UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		// BUTTON: delete custom template
		buttonDelete = uiFactory.createButton(group);
		buttonDelete.setImage(TuxGuitar.getInstance().getIconManager().getListRemove());
		buttonDelete.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGUserFileUtils.deleteUserTemplate();
				loadProperties();
			}
		});
		groupLayout.set(buttonDelete, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);
		labelDelete = uiFactory.createLabel(group);
		labelDelete.setText(TuxGuitar.getProperty("file.custom-template.dialog.delete"));
		groupLayout.set(labelDelete, 2,2,UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		// CLOSE BUTTON
		UITableLayout buttonCloseLayout = new UITableLayout();
		UIPanel buttonClosePanel = uiFactory.createPanel(dialog, false);
		buttonClosePanel.setLayout(buttonCloseLayout);
		dialogLayout.set(buttonClosePanel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		final UIButton buttonClose = uiFactory.createButton(buttonClosePanel);
		buttonClose.setDefaultButton();
		buttonClose.setText(TuxGuitar.getProperty("close"));
		buttonClose.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonCloseLayout.set(buttonClose, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		loadProperties();
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void loadProperties() {
		boolean isUserTemplateReadable = TGUserFileUtils.isUserTemplateReadable();
		buttonDelete.setEnabled(isUserTemplateReadable);
		labelDelete.setEnabled(isUserTemplateReadable);
	}
	
	private void chooseCustomTemplate(UIFactory uiFactory) {
		final List<UIFileChooserFormat> supportedFormats = new ArrayList<UIFileChooserFormat>();
		supportedFormats.add(new UIFileChooserFormat("TuxGuitar files", Arrays.asList("tg")));
		UIFileChooser uiFileChooser = uiFactory.createOpenFileChooser(TGCustomTemplateDialog.this.dialog);
		uiFileChooser.setSupportedFormats(supportedFormats);
		uiFileChooser.choose(new UIFileChooserHandler() {
			public void onSelectFile(File file) {
				if (file != null) {
					// check selected template is valid
					TGTemplate userTemplate = new TGTemplate();
					userTemplate.setName(new String());
					userTemplate.setResource(file.getAbsolutePath());
					userTemplate.setUserTemplate();
					TGSong song =  TGTemplateManager.getInstance(context.getContext()).getTemplateAsSong(userTemplate);
					if (song == null) {
						TGMessageDialogUtil.errorMessage(context.getContext(), dialog, TuxGuitar.getProperty("file.custom-template.error"));
					}
					else {
						TGUserFileUtils.setUserTemplate(file);
						TGActionManager tgActionManager = TGActionManager.getInstance(context.getContext());
						tgActionManager.execute(TGLoadTemplateAction.NAME, new TGActionContextImpl());
					}
					loadProperties();
				}
			}
		});
	}
	
}
