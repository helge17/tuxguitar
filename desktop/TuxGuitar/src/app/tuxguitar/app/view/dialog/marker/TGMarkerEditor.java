package app.tuxguitar.app.view.dialog.marker;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.marker.TGModifyMarkerAction;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.chooser.UIColorChooser;
import app.tuxguitar.ui.chooser.UIColorChooserHandler;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.ui.widget.UIWindow;

public class TGMarkerEditor {

	private static final float MINIMUM_CONTROL_WIDTH = 180;
	private static final float MINIMUM_BUTTON_WIDTH = 80;
	private static final float MINIMUM_BUTTON_HEIGHT = 25;

	private TGViewContext context;
	private TGMarker marker;
	private UIWindow dialog;
	private UISpinner measureSpinner;
	private UITextField titleText;
	private UIButton colorButton;
	private UIColor colorButtonValue;

	public TGMarkerEditor(TGViewContext context) {
		this.context = context;
	}

	public void show() {
		this.createEditableMarker();

		final UIFactory uiFactory = TGApplication.getInstance(this.context.getContext()).getFactory();
		final UIWindow uiParent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();

		this.dialog = uiFactory.createWindow(uiParent, true, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("marker"));

		// ----------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(this.dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("marker"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		// Measure Number
		final int measureCount = TuxGuitar.getInstance().getDocumentManager().getSong().countMeasureHeaders();
		UILabel measureLabel = uiFactory.createLabel(group);
		measureLabel.setText(TuxGuitar.getProperty("measure") + ":");
		groupLayout.set(measureLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);

		this.measureSpinner = uiFactory.createSpinner(group);
		this.measureSpinner.setMinimum(1);
		this.measureSpinner.setMaximum(measureCount);
		this.measureSpinner.setValue(this.marker.getMeasure());
		this.measureSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int selection = TGMarkerEditor.this.measureSpinner.getValue();
				if (selection < 1) {
					TGMarkerEditor.this.measureSpinner.setValue(1);
				} else if (selection > measureCount) {
					TGMarkerEditor.this.measureSpinner.setValue(measureCount);
				}
			}
		});
		groupLayout.set(this.measureSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);

		// Title
		UILabel titleLabel = uiFactory.createLabel(group);
		titleLabel.setText(TuxGuitar.getProperty("title") + ":");
		groupLayout.set(titleLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);

		this.titleText = uiFactory.createTextField(group);
		this.titleText.setText(this.marker.getTitle());
		groupLayout.set(this.titleText, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);
        this.titleText.setFocus();

		// Color
		UILabel colorLabel = uiFactory.createLabel(group);
		colorLabel.setText(TuxGuitar.getProperty("color") + ":");
		groupLayout.set(colorLabel, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);

		this.colorButton = uiFactory.createButton(group);
		this.colorButton.setText(TuxGuitar.getProperty("choose"));
		this.colorButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				UIColorModel colorModel = new UIColorModel();
				colorModel.setRed(TGMarkerEditor.this.marker.getColor().getR());
				colorModel.setGreen(TGMarkerEditor.this.marker.getColor().getG());
				colorModel.setBlue(TGMarkerEditor.this.marker.getColor().getB());

				UIColorChooser colorChooser = uiFactory.createColorChooser(TGMarkerEditor.this.dialog);
				colorChooser.setDefaultModel(colorModel);
				colorChooser.setText(TuxGuitar.getProperty("choose-color"));
				colorChooser.choose(new UIColorChooserHandler() {
					public void onSelectColor(UIColorModel selection) {
						if( selection != null ) {
							TGMarkerEditor.this.marker.getColor().setR(selection.getRed());
							TGMarkerEditor.this.marker.getColor().setG(selection.getGreen());
							TGMarkerEditor.this.marker.getColor().setB(selection.getBlue());
							TGMarkerEditor.this.setButtonColor(uiFactory);
						}
					}
				});
			}
		});
		this.colorButton.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				TGMarkerEditor.this.disposeButtonColor();
			}
		});
		this.setButtonColor(uiFactory);
		groupLayout.set(this.colorButton, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);

		// ------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(this.dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateMarker();
				TGMarkerEditor.this.dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);

		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGMarkerEditor.this.dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);

		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private void setButtonColor(UIFactory factory) {
		this.colorButton.setFgColor(null);
		this.disposeButtonColor();
		this.colorButtonValue = factory.createColor(this.marker.getColor().getR(), this.marker.getColor().getG(), this.marker.getColor().getB());
		this.colorButton.setFgColor(this.colorButtonValue);
	}

	private void disposeButtonColor() {
		if( this.colorButtonValue != null && !this.colorButtonValue.isDisposed()){
			this.colorButton.setFgColor(null);
			this.colorButtonValue.dispose();
			this.colorButtonValue = null;
		}
	}

	private void updateMarker() {
		this.marker.setMeasure(this.measureSpinner.getValue());
		this.marker.setTitle(this.titleText.getText());

		TGSongManager songManager = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGModifyMarkerAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER));
		tgActionProcessor.setAttribute(TGModifyMarkerAction.ATTRIBUTE_MODIFIED_MARKER, this.marker.clone(songManager.getFactory()));
		tgActionProcessor.process();
	}

	private void createEditableMarker() {
		TGSongManager songManager = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGMarker marker = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER);
		if( marker == null ) {
			TGMeasureHeader header = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);

			marker = songManager.getFactory().newMarker();
			marker.setMeasure(header.getNumber());
		}

		this.marker = marker.clone(songManager.getFactory());
	}
}
