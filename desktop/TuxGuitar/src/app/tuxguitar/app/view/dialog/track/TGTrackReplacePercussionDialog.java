package app.tuxguitar.app.view.dialog.track;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.track.TGTrackReplacePercussionAction;
import app.tuxguitar.player.base.MidiPercussionKey;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;

public class TGTrackReplacePercussionDialog {

	private MidiPercussionKey[] percussions;
	private TGViewContext context;
	private UIWindow dialog;
	private UIDropDownSelect<Integer> findSelect;
	private UIDropDownSelect<Integer> replaceSelect;

	public TGTrackReplacePercussionDialog(TGViewContext context) {
		this.context = context;
	}

	public void show() {
		TGTrack track = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGNote note = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		this.percussions = TuxGuitar.getInstance().getPlayer().getPercussionKeys();

		UIFactory factory = TGApplication.getInstance(this.context.getContext()).getFactory();
		UIWindow parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		UITableLayout dialogLayout = new UITableLayout();

		this.dialog = factory.createWindow(parent, true, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("track.replace-percussion"));

		// ------------- TRACK, find, replace ------------
		UITableLayout trackLayout = new UITableLayout();
		UILegendPanel trackLegendPanel = factory.createLegendPanel(this.dialog);
		trackLegendPanel.setLayout(trackLayout);
		trackLegendPanel.setText(track.getName());
		dialogLayout.set(trackLegendPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel findLabel = factory.createLabel(trackLegendPanel);
		findLabel.setText(TuxGuitar.getProperty("track.replace-percussion.find") + ":");
		trackLayout.set(findLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		this.findSelect = factory.createDropDownSelect(trackLegendPanel);
		this.updateSelect(findSelect, note);
		trackLayout.set(findSelect, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		UILabel replaceLabel = factory.createLabel(trackLegendPanel);
		replaceLabel.setText(TuxGuitar.getProperty("track.replace-percussion.replace") + ":");
		trackLayout.set(replaceLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		this.replaceSelect = factory.createDropDownSelect(trackLegendPanel);
		this.updateSelect(replaceSelect, note);
		trackLayout.set(replaceSelect, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		// ---------- BUTTONS ----------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = factory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		final UIButton buttonApply = factory.createButton(buttons);
		buttonApply.setText(TuxGuitar.getProperty("apply"));
		buttonApply.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				doReplace();
			}
		});
		buttonsLayout.set(buttonApply, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f,
				25f, null);

		final UIButton buttonOK = factory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				doReplace();
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f,
				25f, null);

		UIButton buttonCancel = factory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f,
				25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);

		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private void updateSelect(UIDropDownSelect<Integer> select, TGNote note) {
		select.setIgnoreEvents(true);
		select.removeItems();
		for (int i = 0; i < this.percussions.length; i++) {
			MidiPercussionKey key = this.percussions[i];
			select.addItem(
					new UISelectItem<Integer>(String.valueOf(key.getValue()) + " - " + key.getName(), key.getValue()));
			if ((note != null) && (key.getValue() == note.getValue())) {
				select.setSelectedValue(key.getValue());
			}
		}
	}

	private void doReplace() {
		int find = 0;
		int replace = 0;
		try {
			find = this.findSelect.getSelectedValue();
			replace = this.replaceSelect.getSelectedValue();
		} catch (Throwable e) {
			// nothing was selected, do nothing
		}
		if ((find != 0) && (replace != 0)) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(),
					TGTrackReplacePercussionAction.NAME);
			tgActionProcessor.setAttribute(TGTrackReplacePercussionAction.ATTRIBUTE_PERCUSSION_FIND, find);
			tgActionProcessor.setAttribute(TGTrackReplacePercussionAction.ATTRIBUTE_PERCUSSION_REPLACE, replace);
			tgActionProcessor.process();
		}
	}
}