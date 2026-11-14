package app.tuxguitar.audio.ui;

import java.util.List;
import java.util.Optional;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.audio.TGLiveAudio;
import app.tuxguitar.audio.TGLivePlugins;
import app.tuxguitar.audio.TGSoundChainRows;
import app.tuxguitar.audio.action.TGAddVst3EffectAction;
import app.tuxguitar.audio.action.TGChooseGeneralMidiAction;
import app.tuxguitar.audio.action.TGChooseVst3InstrumentAction;
import app.tuxguitar.audio.action.TGEffectAction;
import app.tuxguitar.audio.action.TGMoveEffectUpAction;
import app.tuxguitar.audio.action.TGOpenPluginEditorAction;
import app.tuxguitar.audio.action.TGRemoveEffectAction;
import app.tuxguitar.audio.action.TGSetEffectEnabledAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UIListBoxSelect;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;
import app.tuxguitar.vst3.TGVst3Hosting;
import app.tuxguitar.vst3.TGVst3Plugin;

/**
 * A track's sound chain (its channel's): the source and the effects in processing order (TGSoundChainRows), and the
 * changes of it, each one undo step (the sound chain actions): add a VST3 effect, make a VST3 instrument or the General
 * MIDI program the source, move the picked effect up, remove it, enable or disable it, open the picked plugin's editor
 * (its own, else TuxGuitar's parameter editor, TGPluginParametersDialog). The plugins offered are those the last scan of
 * the VST3 folders found; Scan again scans them off the UI thread, then offers what it found. What can be done with the
 * row picked is decided there; the rows are read again after each change.
 */
public class TGSoundChainDialog {

	private final TGContext context;
	private final TGTrack track;
	private final UIFactory factory;
	private UIWindow dialog;
	private UIListBoxSelect<Integer> rows;
	private UIDropDownSelect<TGVst3Plugin> effects;
	private UIDropDownSelect<TGVst3Plugin> instruments;
	private UIButton addEffect;
	private UIButton chooseInstrument;
	private UIButton generalMidi;
	private UIButton up;
	private UIButton remove;
	private UIButton enable;
	private UIButton edit;
	private UIButton scan;

	public TGSoundChainDialog(TGContext context, TGTrack track) {
		this.context = context;
		this.track = track;
		this.factory = TGApplication.getInstance(context).getFactory();
	}

	public void open() {
		this.dialog = this.factory.createWindow(TGWindow.getInstance(this.context).getWindow(), true, false);
		this.dialog.setLayout(new UITableLayout());
		this.dialog.setText(TuxGuitar.getProperty("audio.sound-chain.title") + ": " + this.track.getName());
		this.rows = this.factory.createListBoxSelect(this.dialog);
		this.rows.addSelectionListener(event -> this.updateButtons());
		this.layout().set(this.rows, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 4, 360f, 160f, null);
		this.effects = this.plugins(2, "audio.sound-chain.effect");
		this.addEffect = this.button(2, 4, "audio.sound-chain.add-effect", this::addEffect);
		this.instruments = this.plugins(3, "audio.sound-chain.instrument");
		this.chooseInstrument = this.button(3, 4, "audio.sound-chain.choose-instrument", this::chooseInstrument);
		this.generalMidi = this.button(4, 4, "audio.sound-chain.general-midi", () -> this.change(new TGActionProcessor(this.context, TGChooseGeneralMidiAction.NAME)));
		this.up = this.button(5, 1, "audio.sound-chain.up", () -> this.changeEffect(TGMoveEffectUpAction.NAME));
		this.remove = this.button(5, 2, "audio.sound-chain.remove", () -> this.changeEffect(TGRemoveEffectAction.NAME));
		this.enable = this.button(5, 3, "audio.sound-chain.disable", this::toggleEnabled);
		this.edit = this.button(5, 4, "audio.sound-chain.edit", this::openEditor);
		this.scan = this.button(6, 1, "audio.sound-chain.scan", this::scanAgain);
		this.button(6, 4, "close", this.dialog::dispose);
		this.showPlugins();
		this.refresh();
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private UITableLayout layout() {
		return (UITableLayout) this.dialog.getLayout();
	}

	// a drop-down of plugins, with its label, on the row
	private UIDropDownSelect<TGVst3Plugin> plugins(int row, String key) {
		TGDialogParts.label(this.factory, this.dialog, row, key);
		UIDropDownSelect<TGVst3Plugin> select = this.factory.createDropDownSelect(this.dialog);
		select.addSelectionListener(event -> this.updateButtons());
		this.layout().set(select, row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 2, 200f, null, null);
		return select;
	}

	// the drop-downs offer the plugins the last scan found, each its first picked
	private void showPlugins() {
		List<TGVst3Plugin> plugins = TGVst3Hosting.getInstance(this.context).getPlugins();
		offer(this.effects, TGSoundChainRows.effects(plugins));
		offer(this.instruments, TGSoundChainRows.instruments(plugins));
	}

	private static void offer(UIDropDownSelect<TGVst3Plugin> select, List<TGVst3Plugin> plugins) {
		select.removeItems();
		plugins.forEach(plugin -> select.addItem(new UISelectItem<TGVst3Plugin>(plugin.getName(), plugin)));
		plugins.stream().findFirst().ifPresent(select::setSelectedValue);
	}

	// the VST3 folders scanned again off the UI thread (a plugin installed while TuxGuitar runs), then offered here
	private void scanAgain() {
		this.scan.setEnabled(false);
		Thread scanning = new Thread(() -> {
			System.err.println(TGLiveAudio.getInstance(this.context).scanPlugins());
			TGSynchronizer.getInstance(this.context).executeLater(this::scanned);
		}, "TuxGuitar VST3 scan");
		scanning.setDaemon(true);
		scanning.start();
	}

	private void scanned() {
		if (!this.dialog.isDisposed()) {
			this.showPlugins();
			this.scan.setEnabled(true);
			this.updateButtons();
		}
	}

	private UIButton button(int row, int column, String key, Runnable action) {
		UIButton button = this.factory.createButton(this.dialog);
		button.setText(TuxGuitar.getProperty(key));
		button.addSelectionListener(event -> action.run());
		this.layout().set(button, row, column, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, 100f, 25f, null);
		return button;
	}

	// the track's channel as the song has it now (an undo may have replaced it)
	private TGChannel channel() {
		TGDocumentManager document = TGDocumentManager.getInstance(this.context);
		return document.getSongManager().getChannel(document.getSong(), this.track.getChannelId());
	}

	// the rows read again, the row picked kept
	private void refresh() {
		Integer picked = this.rows.getSelectedValue();
		this.rows.removeItems();
		List<String> read = TGSoundChainRows.of(this.channel()).getRows(TuxGuitar.getProperty("audio.sound-chain.source"));
		for (int row = 0; row < read.size(); row++) {
			this.rows.addItem(new UISelectItem<Integer>(read.get(row), row));
		}
		this.rows.setSelectedValue(picked);
		this.updateButtons();
	}

	private void updateButtons() {
		TGSoundChainRows chain = TGSoundChainRows.of(this.channel());
		int row = TGSoundChainRows.row(this.rows.getSelectedValue());
		this.addEffect.setEnabled(chain.canAdd(this.effects.getSelectedValue()));
		this.chooseInstrument.setEnabled(chain.canChoose(this.instruments.getSelectedValue()));
		this.generalMidi.setEnabled(chain.canChooseSource());
		this.up.setEnabled(chain.canMoveUp(row));
		this.remove.setEnabled(chain.isEffect(row));
		this.enable.setEnabled(chain.isEffect(row));
		this.enable.setText(TuxGuitar.getProperty(chain.toggleKey(row)));
		this.edit.setEnabled(chain.canEdit(row));
	}

	// the change runs on the action's thread; the rows are read again on the UI thread once it is done
	private void change(TGActionProcessor processor) {
		this.change(processor, this::refresh);
	}

	private void change(TGActionProcessor processor, Runnable done) {
		processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.channel());
		processor.setOnFinish(() -> TGSynchronizer.getInstance(this.context).executeLater(done));
		processor.process();
	}

	private void addEffect() {
		this.usePlugin(TGAddVst3EffectAction.NAME, this.effects);
	}

	private void chooseInstrument() {
		this.usePlugin(TGChooseVst3InstrumentAction.NAME, this.instruments);
	}

	// the action with the plugin picked in the drop-down
	private void usePlugin(String action, UIDropDownSelect<TGVst3Plugin> picked) {
		TGActionProcessor processor = new TGActionProcessor(this.context, action);
		processor.setAttribute(TGAddVst3EffectAction.ATTRIBUTE_PLUGIN, picked.getSelectedValue());
		this.change(processor);
	}

	// an action on the picked row's effect (the source: none)
	private TGActionProcessor onPicked(String action) {
		TGActionProcessor processor = new TGActionProcessor(this.context, action);
		processor.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, TGSoundChainRows.effect(TGSoundChainRows.row(this.rows.getSelectedValue())));
		return processor;
	}

	private void changeEffect(String action) {
		this.change(this.onPicked(action));
	}

	private void toggleEnabled() {
		TGActionProcessor processor = this.onPicked(TGSetEffectEnabledAction.NAME);
		processor.setAttribute(TGSetEffectEnabledAction.ATTRIBUTE_ENABLED, !TGSoundChainRows.of(this.channel()).isEnabled(TGSoundChainRows.row(this.rows.getSelectedValue())));
		this.change(processor);
	}

	// the picked plugin's own editor opens (a window of its host process), or else TuxGuitar's parameter editor here
	private void openEditor() {
		Integer effect = TGSoundChainRows.effect(TGSoundChainRows.row(this.rows.getSelectedValue()));
		this.change(this.onPicked(TGOpenPluginEditorAction.NAME), () -> this.showParameters(effect));
	}

	private void showParameters(Integer effect) {
		this.refresh();
		Optional.ofNullable(TGLivePlugins.getInstance(this.context).parameterEditor(this.channel(), effect))
			.ifPresent(editor -> new TGPluginParametersDialog(this.context, this.track, effect, editor).open(this.dialog));
	}
}
