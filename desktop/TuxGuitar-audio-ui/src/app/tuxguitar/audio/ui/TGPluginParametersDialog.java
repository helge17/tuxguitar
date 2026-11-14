package app.tuxguitar.audio.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.util.TGContinousControlSelectionListener;
import app.tuxguitar.app.util.TGContinuousControl;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.audio.TGPluginEditor;
import app.tuxguitar.audio.TGPluginParameterRow;
import app.tuxguitar.audio.action.TGEffectAction;
import app.tuxguitar.audio.action.TGSetPluginParameterAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIScale;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

/**
 * TuxGuitar's parameter editor of a plugin without an editor of its own (TGPluginEditor's rows), for an effect of a
 * track's sound chain (by its number; none: the VST3 instrument that is the source): a row per parameter, its title, a
 * slider over its range and its plain value. A slider moved, once it rests, or a value typed and set, sets the parameter
 * by one undoable change of the song (TGSetPluginParameterAction). Each time the plugin may hold other values (an undo,
 * a redo) the rows show what it holds; once it no longer runs, the window closes. Glue only: the rows decide positions
 * and values, the editor when it follows its plugin.
 */
public class TGPluginParametersDialog {

	// how long a slider rests before its value is set (one undo step per rest, not per move)
	private static final int REST_MILLISECONDS = 250;

	private final TGContext context;
	private final TGTrack track;
	private final Integer effect;
	private final TGPluginEditor editor;
	private final List<Row> rows = new ArrayList<Row>();
	private UIWindow dialog;

	public TGPluginParametersDialog(TGContext context, TGTrack track, Integer effect, TGPluginEditor editor) {
		this.context = context;
		this.track = track;
		this.effect = effect;
		this.editor = editor;
	}

	public void open(UIWindow parent) {
		UIFactory factory = TGApplication.getInstance(this.context).getFactory();
		this.dialog = factory.createWindow(parent, true, false);
		this.dialog.setLayout(new UITableLayout());
		this.dialog.setText(TuxGuitar.getProperty("audio.sound-chain.parameters") + ": " + this.track.getName());
		List<TGPluginParameterRow> rows = this.editor.getRows();
		for (int i = 0; i < rows.size(); i++) {
			this.rows.add(new Row(factory, i + 1, rows.get(i)));
		}
		TGDialogParts.button(factory, this.dialog, rows.size() + 1, 4, "close", this.dialog::dispose);
		this.editor.watch(() -> TGSynchronizer.getInstance(this.context).executeLater(this::follow));
		this.dialog.addDisposeListener(event -> this.editor.watch(() -> { }));
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	// on the UI thread, once the plugin may hold other values (an undo, a redo) or no longer runs
	private void follow() {
		if (!this.dialog.isDisposed()) {
			this.followEditor();
		}
	}

	// the rows show the values the plugin holds now; a plugin that no longer runs closes the window
	private void followEditor() {
		if (this.editor.isOpen()) {
			this.editor.refresh();
			this.show(this.editor.getRows());
		} else {
			this.dialog.dispose();
		}
	}

	private void show(List<TGPluginParameterRow> rows) {
		for (int i = 0; i < this.rows.size(); i++) {
			this.rows.get(i).show(rows.get(i));
		}
	}

	private UITableLayout layout() {
		return (UITableLayout) this.dialog.getLayout();
	}

	// one undoable change of the song: the parameter of the track's channel's plugin set to the plain value
	private void set(TGPluginParameterRow row, double plain) {
		TGDocumentManager document = TGDocumentManager.getInstance(this.context);
		TGActionProcessor processor = new TGActionProcessor(this.context, TGSetPluginParameterAction.NAME);
		processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, document.getSongManager().getChannel(document.getSong(), this.track.getChannelId()));
		processor.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, this.effect);
		processor.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_PARAMETER, row.getId());
		processor.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_VALUE, plain);
		processor.process();
	}

	private final class Row implements TGContinuousControl {

		private TGPluginParameterRow row;
		private final UIScale slider;
		private final UITextField value;

		Row(UIFactory factory, int number, TGPluginParameterRow row) {
			UILabel title = factory.createLabel(dialog);
			title.setText(row.getTitle() + ":");
			layout().set(title, number, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
			this.slider = factory.createHorizontalScale(dialog);
			this.slider.setMinimum(0);
			this.slider.setMaximum(row.positions());
			this.slider.setIncrement(1);
			this.slider.addSelectionListener(new TGContinousControlSelectionListener(this, REST_MILLISECONDS));
			layout().set(this.slider, number, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 240f, null, null);
			this.value = factory.createTextField(dialog);
			layout().set(this.value, number, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 1, 80f, null, null);
			TGDialogParts.button(factory, dialog, number, 4, "audio.sound-chain.set", this::typed);
			this.show(row);
		}

		// the row's value, on the slider and typed
		void show(TGPluginParameterRow row) {
			this.row = row;
			this.slider.setValue(row.position(row.getPlain()));
			this.value.setText(String.valueOf(row.getPlain()));
		}

		// the slider rests: its value is set
		public void doActionWhenStable() {
			double plain = this.row.plain(this.slider.getValue());
			this.value.setText(String.valueOf(plain));
			set(this.row, plain);
		}

		public TGContext getContext() {
			return context;
		}

		// the value typed is set when it is inside the range; otherwise the field shows the slider's value again
		private void typed() {
			Optional<Double> plain = Optional.ofNullable(this.row.parse(this.value.getText()));
			plain.ifPresent(typed -> this.slider.setValue(this.row.position(typed)));
			plain.ifPresent(typed -> set(this.row, typed));
			this.value.setText(String.valueOf(plain.orElse(this.row.plain(this.slider.getValue()))));
		}
	}
}
