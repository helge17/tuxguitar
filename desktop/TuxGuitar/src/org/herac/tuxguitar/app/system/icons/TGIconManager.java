package org.herac.tuxguitar.app.system.icons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGIconManager {

	private TGContext context;

	private TGIconTheme theme;
	private Map<String, TGIconTheme> themeCache;

	private UIImage[] durations;
	private UIImage editCut;
	private UIImage editCopy;
	private UIImage editPaste;
	private UIImage editRepeat;
	private UIImage editUndo;
	private UIImage editRedo;
	private UIImage editVoice1;
	private UIImage editVoice2;
	private UIImage editModeSelection;
	private UIImage editModeEdition;
	private UIImage editModeEditionNotNatural;
	private UIImage helpDoc;
	private UIImage helpAbout;
	private UIImage goHome;
	private UIImage layoutPage;
	private UIImage layoutLinear;
	private UIImage layoutMultitrack;
	private UIImage layoutScore;
	private UIImage layoutTablature;
	private UIImage layoutCompact;
	private UIImage layoutHighlightPlayedBeat;
	private UIImage transport;
	private UIImage transportFirst;
	private UIImage transportLast;
	private UIImage transportPrevious;
	private UIImage transportNext;
	private UIImage transportStop;
	private UIImage transportPlay;
	private UIImage transportPause;
	private UIImage transportIconFirst;
	private UIImage transportIconLast;
	private UIImage transportIconPrevious;
	private UIImage transportIconNext;
	private UIImage transportIconStop;
	private UIImage transportIconPlay;
	private UIImage transportIconPause;
	private UIImage transportMetronome;
	private UIImage transportCountIn;
	private UIImage transportMode;
	private UIImage transportLoopStart;
	private UIImage transportLoopEnd;
	private UIImage markerList;
	private UIImage marker;
	private UIImage markerAdd;
	private UIImage markerFirst;
	private UIImage markerLast;
	private UIImage markerPrevious;
	private UIImage markerNext;
	private UIImage measureFirst;
	private UIImage measureLast;
	private UIImage measurePrevious;
	private UIImage measureNext;
	private UIImage measureAdd;
	private UIImage measureClean;
	private UIImage measureRemove;
	private UIImage measureCopy;
	private UIImage measurePaste;
	private UIImage aboutDescription;
	private UIImage aboutLicense;
	private UIImage aboutAuthors;
	private UIImage appIcon;
	private UIImage appSplash;
	private UIImage optionMain;
	private UIImage optionStyle;
	private UIImage optionSound;
	private UIImage optionLanguage;
	private UIImage optionSkin;
	private UIImage trackFirst;
	private UIImage trackLast;
	private UIImage trackPrevious;
	private UIImage trackNext;
	private UIImage trackAdd;
	private UIImage trackClone;
	private UIImage trackRemove;
	private UIImage trackSolo;
	private UIImage trackMute;
	private UIImage fretboard;
	private UIImage fretboardFirstFret;
	private UIImage fretboardFret;
	private UIImage fretboardSmaller;
	private UIImage fretboardBigger;
	private UIImage compositionTimeSignature;
	private UIImage compositionTempo;
	private UIImage compositionClef;
	private UIImage compositionKeySignature;
	private UIImage compositionTripletFeel;
	private UIImage compositionRepeatOpen;
	private UIImage compositionRepeatClose;
	private UIImage compositionRepeatAlternative;
	private UIImage songProperties;
	private UIImage durationDotted;
	private UIImage durationDoubleDotted;
	private Map<Integer,UIImage> divisionTypes;
	private UIImage fileNew;
	private UIImage fileOpen;
	private UIImage fileClose;
	private UIImage fileSave;
	private UIImage fileSaveAs;
	private UIImage fileImport;
	private UIImage fileExport;
	private UIImage filePrint;
	private UIImage filePrintPreview;
	private UIImage fileHistory;
	private UIImage fileExit;
	private UIImage chord;
	private UIImage text;
	private UIImage noteTied;
	private UIImage instruments;
	private UIImage matrix;
	private UIImage piano;
	private UIImage dynamicPPP;
	private UIImage dynamicPP;
	private UIImage dynamicP;
	private UIImage dynamicMP;
	private UIImage dynamicMF;
	private UIImage dynamicF;
	private UIImage dynamicFF;
	private UIImage dynamicFFF;
	private UIImage effectDead;
	private UIImage effectGhost;
	private UIImage effectAccentuated;
	private UIImage effectHeavyAccentuated;
	private UIImage effectLetRing;
	private UIImage effectHarmonic;
	private UIImage effectGrace;
	private UIImage effectBend;
	private UIImage effectTremoloBar;
	private UIImage effectSlide;
	private UIImage effectHammer;
	private UIImage effectVibrato;
	private UIImage effectTrill;
	private UIImage effectTremoloPicking;
	private UIImage effectPalmMute;
	private UIImage effectStaccato;
	private UIImage effectTapping;
	private UIImage effectSlapping;
	private UIImage effectPopping;
	private UIImage effectFadeIn;
	private UIImage browserNew;
	private UIImage browserFile;
	private UIImage browserFolder;
	private UIImage browserFolderRemote;
	private UIImage browserCollection;
	private UIImage browserRoot;
	private UIImage browserBack;
	private UIImage browserRefresh;
	private UIImage arrowUp;
	private UIImage arrowDown;
	private UIImage arrowLeft;
	private UIImage arrowRight;
	private UIImage statusQuestion;
	private UIImage statusError;
	private UIImage statusWarning;
	private UIImage statusInfo;
	private UIImage strokeUp;
	private UIImage strokeDown;
	private UIImage settings;
	private UIImage toolbarMain;
	private UIImage toolbarEdit;
	private UIImage tableViewer;
	private UIImage listAdd;
	private UIImage listEdit;
	private UIImage listRemove;
	private UIImage solo;
	private UIImage soloDisabled;
	private UIImage soloDim;
	private UIImage soloDisabledDim;
	private UIImage toolsPlugins;
	private UIImage toolsShortcuts;
	private UIImage toolsSettings;
	private UIImage mute;
	private UIImage muteDisabled;
	private UIImage muteDim;
	private UIImage muteDisabledDim;
	private UIImage listMoveUp;
	private UIImage listMoveDown;
	private UIImage zoomOut;
	private UIImage zoomReset;
	private UIImage zoomIn;

	private TGIconManager(TGContext context){
		this.context = context;
		this.themeCache = new HashMap<String, TGIconTheme>();
		this.loadIcons();
	}

	public TGIconTheme findIconTheme(String theme) {
		if( this.themeCache.containsKey(theme) ) {
			return this.themeCache.get(theme);
		}

		this.themeCache.put(theme, new TGIconTheme(theme));

		return this.findIconTheme(theme);
	}

	public String findConfiguredThemeName() {
		return TGSkinManager.getInstance(this.context).getCurrentSkin();
	}

	public void loadIcons(){
		this.theme = this.findIconTheme(this.findConfiguredThemeName());
		this.durations = new UIImage[]{
			loadIcon("1.png"),
			loadIcon("2.png"),
			loadIcon("4.png"),
			loadIcon("8.png"),
			loadIcon("16.png"),
			loadIcon("32.png"),
			loadIcon("64.png")
		};
		this.layoutPage = loadIcon("layout_page.png");
		this.layoutLinear = loadIcon("layout_linear.png");
		this.layoutMultitrack = loadIcon("layout_multitrack.png");
		this.layoutScore = loadIcon("layout_score.png");
		this.layoutTablature = loadIcon("layout_tablature.png");
		this.layoutCompact = loadIcon("layout_compact.png");
		this.layoutHighlightPlayedBeat = loadIcon("highlight_played_beat.png");
		this.fileNew = loadIcon("new.png");
		this.fileOpen = loadIcon("open.png");
		this.fileClose = loadIcon("close.png");
		this.fileSave = loadIcon("save.png");
		this.fileSaveAs = loadIcon("save-as.png");
		this.fileImport = loadIcon("import.png");
		this.fileExport = loadIcon("export.png");
		this.filePrint = loadIcon("print.png");
		this.filePrintPreview = loadIcon("print-preview.png");
		this.fileHistory = loadIcon("history.png");
		this.fileExit = loadIcon("exit.png");
		this.editCut = loadIcon("edit_cut.png");
		this.editCopy = loadIcon("edit_copy.png");
		this.editPaste = loadIcon("edit_paste.png");
		this.editRepeat = loadIcon("edit_repeat.png");
		this.editUndo = loadIcon("edit_undo.png");
		this.editRedo = loadIcon("edit_redo.png");
		this.editVoice1 = loadIcon("edit_voice_1.png");
		this.editVoice2 = loadIcon("edit_voice_2.png");
		this.editModeSelection = loadIcon("edit_mode_selection.png");
		this.editModeEdition = loadIcon("edit_mode_edition.png");
		this.editModeEditionNotNatural = loadIcon("edit_mode_edition_no_natural.png");
		this.helpDoc = loadIcon("help_doc.png");
		this.helpAbout = loadIcon("help_about.png");
		this.goHome = loadIcon("browser_root.png");
		this.appIcon = loadIcon("icon.png");
		this.appSplash = loadIcon("splash.png");
		this.aboutDescription = loadIcon("about_description.png");
		this.aboutLicense = loadIcon("about_license.png");
		this.aboutAuthors = loadIcon("about_authors.png");
		this.optionMain = loadIcon("option_view.png");
		this.optionStyle = loadIcon("option_style.png");
		this.optionSound = loadIcon("option_sound.png");
		this.optionSkin = loadIcon("option_skin.png");
		this.optionLanguage= loadIcon("option_language.png");
		this.compositionTimeSignature = loadIcon("timesignature.png");
		this.compositionTempo = loadIcon("tempoicon.png");
		this.compositionClef = loadIcon("clef.png");
		this.compositionKeySignature = loadIcon("keysignature.png");
		this.compositionTripletFeel = loadIcon("tripletfeel.png");
		this.compositionRepeatOpen = loadIcon("openrepeat.png");
		this.compositionRepeatClose = loadIcon("closerepeat.png");
		this.compositionRepeatAlternative = loadIcon("repeat_alternative.png");
		this.songProperties = loadIcon("song_properties.png");
		this.trackFirst = loadIcon("track_first.png");
		this.trackLast = loadIcon("track_last.png");
		this.trackPrevious = loadIcon("track_previous.png");
		this.trackNext = loadIcon("track_next.png");
		this.trackAdd = loadIcon("track_add.png");
		this.trackClone = loadIcon("track_clone.png");
		this.trackRemove = loadIcon("track_remove.png");
		this.trackSolo = loadIcon("track_solo.png");
		this.trackMute = loadIcon("track_mute.png");
		this.durationDotted = loadIcon("dotted.png");
		this.durationDoubleDotted = loadIcon("doubledotted.png");
		this.divisionTypes = new HashMap<Integer, UIImage>();
		for (int i = 0; i < TGDivisionType.DIVISION_TYPES.length; i++) {
			Integer enters = TGDivisionType.DIVISION_TYPES[i].getEnters();
			this.divisionTypes.put(enters, loadIcon("division-type-" + String.valueOf(enters) + ".png"));
		}
		this.fretboard = loadIcon("fretboard.png");
		this.fretboardFirstFret = loadIcon("firstfret.png");
		this.fretboardFret = loadIcon("fret.png");
		this.fretboardSmaller = loadIcon("fretboard_smaller.png");
		this.fretboardBigger = loadIcon("fretboard_bigger.png");
		this.chord = loadIcon("chord.png");
		this.text = loadIcon("text.png");
		this.noteTied = loadIcon("tiednote.png");
		this.transport = loadIcon("transport.png");
		this.transportFirst = loadIcon("transport_first.png");
		this.transportLast = loadIcon("transport_last.png");
		this.transportPrevious = loadIcon("transport_previous.png");
		this.transportNext = loadIcon("transport_next.png");
		this.transportStop = loadIcon("transport_stop.png");
		this.transportPlay = loadIcon("transport_play.png");
		this.transportPause = loadIcon("transport_pause.png");
		this.transportIconFirst = loadIcon("transport_icon_first.png");
		this.transportIconLast = loadIcon("transport_icon_last.png");
		this.transportIconPrevious = loadIcon("transport_icon_previous.png");
		this.transportIconNext = loadIcon("transport_icon_next.png");
		this.transportIconStop = loadIcon("transport_icon_stop.png");
		this.transportIconPlay = loadIcon("transport_icon_play.png");
		this.transportIconPause = loadIcon("transport_icon_pause.png");
		this.transportMetronome = loadIcon("transport_metronome.png");
		this.transportCountIn = loadIcon("transport_count_in.png");
		this.transportMode = loadIcon("transport_mode.png");
		this.transportLoopStart = loadIcon("transport_loop_start.png");
		this.transportLoopEnd = loadIcon("transport_loop_end.png");
		this.markerList = loadIcon("marker_list.png");
		this.marker = loadIcon("marker.png");
		this.markerAdd = loadIcon("marker_add.png");
		this.markerFirst = loadIcon("marker_first.png");
		this.markerLast = loadIcon("marker_last.png");
		this.markerPrevious = loadIcon("marker_previous.png");
		this.markerNext = loadIcon("marker_next.png");
		this.measureFirst = loadIcon("measure_first.png");
		this.measureLast = loadIcon("measure_last.png");
		this.measurePrevious = loadIcon("measure_previous.png");
		this.measureNext = loadIcon("measure_next.png");
		this.measureAdd = loadIcon("measure_add.png");
		this.measureClean = loadIcon("measure_clean.png");
		this.measureRemove = loadIcon("measure_remove.png");
		this.measureCopy = loadIcon("measure_copy.png");
		this.measurePaste = loadIcon("measure_paste.png");
		this.instruments = loadIcon("mixer.png");
		this.matrix = loadIcon("matrix.png");
		this.piano = loadIcon("piano.png");
		this.dynamicPPP = loadIcon("dynamic_ppp.png");
		this.dynamicPP = loadIcon("dynamic_pp.png");
		this.dynamicP = loadIcon("dynamic_p.png");
		this.dynamicMP =loadIcon("dynamic_mp.png");
		this.dynamicMF = loadIcon("dynamic_mf.png");
		this.dynamicF = loadIcon("dynamic_f.png");
		this.dynamicFF = loadIcon("dynamic_ff.png");
		this.dynamicFFF = loadIcon("dynamic_fff.png");
		this.effectDead = loadIcon("effect_dead.png");
		this.effectGhost = loadIcon("effect_ghost.png");
		this.effectAccentuated = loadIcon("effect_accentuated.png");
		this.effectHeavyAccentuated = loadIcon("effect_heavy_accentuated.png");
		this.effectLetRing = loadIcon("effect_let_ring.png");
		this.effectHarmonic = loadIcon("effect_harmonic.png");
		this.effectGrace = loadIcon("effect_grace.png");
		this.effectBend = loadIcon("effect_bend.png");
		this.effectTremoloBar = loadIcon("effect_tremolo_bar.png");
		this.effectSlide = loadIcon("effect_slide.png");
		this.effectHammer = loadIcon("effect_hammer.png");
		this.effectVibrato = loadIcon("effect_vibrato.png");
		this.effectTrill= loadIcon("effect_trill.png");
		this.effectTremoloPicking = loadIcon("effect_tremolo_picking.png");
		this.effectPalmMute= loadIcon("effect_palm_mute.png");
		this.effectStaccato = loadIcon("effect_staccato.png");
		this.effectTapping = loadIcon("effect_tapping.png");
		this.effectSlapping = loadIcon("effect_slapping.png");
		this.effectPopping = loadIcon("effect_popping.png");
		this.effectFadeIn = loadIcon("effect_fade_in.png");
		this.browserNew = loadIcon("browser_new.png");
		this.browserFile = loadIcon("browser_file.png");
		this.browserFolder = loadIcon("browser_folder.png");
		this.browserFolderRemote = loadIcon("browser_folder_remote.png");
		this.browserCollection = loadIcon("browser_collection.png");
		this.browserRoot = loadIcon("browser_root.png");
		this.browserBack = loadIcon("browser_back.png");
		this.browserRefresh = loadIcon("browser_refresh.png");
		this.arrowUp = loadIcon("arrow_up.png");
		this.arrowDown = loadIcon("arrow_down.png");
		this.arrowLeft = loadIcon("arrow_left.png");
		this.arrowRight = loadIcon("arrow_right.png");
		this.statusQuestion = loadIcon("status_question.png");
		this.statusError = loadIcon("status_error.png");
		this.statusWarning = loadIcon("status_warning.png");
		this.statusInfo = loadIcon("status_info.png");
		this.strokeUp = loadIcon("stroke_up.png");
		this.strokeDown = loadIcon("stroke_down.png");
		this.settings = loadIcon("settings.png");
		this.toolbarMain = loadIcon("toolbar_main.png");
		this.toolbarEdit = loadIcon("toolbar_edit.png");
		this.tableViewer = loadIcon("table_viewer.png");
		this.listAdd = loadIcon("list_add.png");
		this.listEdit = loadIcon("list_edit.png");
		this.listRemove = loadIcon("list_remove.png");
		this.solo = loadIcon("solo.png");
		this.soloDisabled = loadIcon("solo-disabled.png");
		this.soloDim = loadIcon("solo-dim.png");
		this.soloDisabledDim = loadIcon("solo-disabled-dim.png");
		this.toolsPlugins = loadIcon("tools_plugins.png");
		this.toolsShortcuts = loadIcon("tools_shortcuts.png");
		this.toolsSettings = loadIcon("tools_settings.png");
		this.mute = loadIcon("mute.png");
		this.muteDisabled = loadIcon("mute-disabled.png");
		this.muteDim = loadIcon("mute-dim.png");
		this.muteDisabledDim = loadIcon("mute-disabled-dim.png");
		this.listMoveUp = loadIcon("list_move_up.png");
		this.listMoveDown = loadIcon("list_move_down.png");
		this.zoomOut = loadIcon("zoom_out.png");
		this.zoomReset = loadIcon("zoom_original.png");
		this.zoomIn = loadIcon("zoom_in.png");
	}

	private UIImage loadIcon(String name) {
		UIImage image = this.theme.getResource(name);
		if( image == null ) {
			image = TGFileUtils.loadImage(this.context, this.theme.getName(), name);

			this.theme.setResource(name, image);
		}
		return image;
	}

	public void disposeThemes() {
		List<String> themes = new ArrayList<String>(this.themeCache.keySet());
		for(String theme : themes) {
			this.disposeTheme(this.themeCache.remove(theme));
		}
	}

	public void disposeTheme(TGIconTheme theme) {
		List<UIImage> uiImages = new ArrayList<UIImage>(theme.getResources().values());
		for(UIImage uiImage : uiImages) {
			uiImage.dispose();
		}
	}

	public void onSkinDisposed(){
		this.disposeThemes();
	}

	public void onSkinChange() {
		this.loadIcons();
	}

	public UIImage getDuration(int value){
		switch(value){
		case TGDuration.WHOLE:
			return this.durations[0];
		case TGDuration.HALF:
			return this.durations[1];
		case TGDuration.QUARTER:
			return this.durations[2];
		case TGDuration.EIGHTH:
			return this.durations[3];
		case TGDuration.SIXTEENTH:
			return this.durations[4];
		case TGDuration.THIRTY_SECOND:
			return this.durations[5];
		case TGDuration.SIXTY_FOURTH:
			return this.durations[6];
		}
		return null;
	}

	public UIImage getAboutAuthors() {
		return this.aboutAuthors;
	}

	public UIImage getAboutDescription() {
		return this.aboutDescription;
	}

	public UIImage getAboutLicense() {
		return this.aboutLicense;
	}

	public UIImage getAppIcon() {
		return this.appIcon;
	}

	public UIImage getAppSplash() {
		return this.appSplash;
	}

	public UIImage getChord() {
		return this.chord;
	}

	public UIImage getText() {
		return this.text;
	}

	public UIImage getCompositionRepeatClose() {
		return this.compositionRepeatClose;
	}

	public UIImage getCompositionRepeatAlternative() {
		return this.compositionRepeatAlternative;
	}

	public UIImage getCompositionRepeatOpen() {
		return this.compositionRepeatOpen;
	}

	public UIImage getCompositionTempo() {
		return this.compositionTempo;
	}

	public UIImage getCompositionClef() {
		return this.compositionClef;
	}

	public UIImage getCompositionKeySignature() {
		return this.compositionKeySignature;
	}

	public UIImage getCompositionTripletFeel() {
		return this.compositionTripletFeel;
	}

	public UIImage getCompositionTimeSignature() {
		return this.compositionTimeSignature;
	}

	public UIImage getDurationDotted() {
		return this.durationDotted;
	}

	public UIImage getDurationDoubleDotted() {
		return this.durationDoubleDotted;
	}

	public UIImage getDivisionType(int divisionTypeEnters) {
		return this.divisionTypes.get(divisionTypeEnters);
	}

	public UIImage getDynamicF() {
		return this.dynamicF;
	}

	public UIImage getDynamicFF() {
		return this.dynamicFF;
	}

	public UIImage getDynamicFFF() {
		return this.dynamicFFF;
	}

	public UIImage getDynamicMF() {
		return this.dynamicMF;
	}

	public UIImage getDynamicMP() {
		return this.dynamicMP;
	}

	public UIImage getDynamicP() {
		return this.dynamicP;
	}

	public UIImage getDynamicPP() {
		return this.dynamicPP;
	}

	public UIImage getDynamicPPP() {
		return this.dynamicPPP;
	}

	public UIImage getEditModeEdition() {
		return this.editModeEdition;
	}

	public UIImage getEditModeEditionNotNatural() {
		return this.editModeEditionNotNatural;
	}

	public UIImage getEditModeSelection() {
		return this.editModeSelection;
	}

	public UIImage getEditCut() {
		return this.editCut;
	}

	public UIImage getEditCopy() {
		return this.editCopy;
	}

	public UIImage getEditPaste() {
		return this.editPaste;
	}

	public UIImage getEditRepeat() {
		return this.editRepeat;
	}

	public UIImage getEditRedo() {
		return this.editRedo;
	}

	public UIImage getEditUndo() {
		return this.editUndo;
	}

	public UIImage getEditVoice1() {
		return this.editVoice1;
	}

	public UIImage getEditVoice2() {
		return this.editVoice2;
	}

	public UIImage getHelpDoc() {
		return this.helpDoc;
	}

	public UIImage getHelpAbout() {
		return this.helpAbout;
	}
	
	public UIImage getGoHome() {
		return this.goHome;
	}

	public UIImage getEffectAccentuated() {
		return this.effectAccentuated;
	}

	public UIImage getEffectBend() {
		return this.effectBend;
	}

	public UIImage getEffectDead() {
		return this.effectDead;
	}

	public UIImage getEffectFadeIn() {
		return this.effectFadeIn;
	}

	public UIImage getEffectGhost() {
		return this.effectGhost;
	}

	public UIImage getEffectGrace() {
		return this.effectGrace;
	}

	public UIImage getEffectHammer() {
		return this.effectHammer;
	}

	public UIImage getEffectHarmonic() {
		return this.effectHarmonic;
	}

	public UIImage getEffectHeavyAccentuated() {
		return this.effectHeavyAccentuated;
	}

	public UIImage getEffectLetRing() {
		return this.effectLetRing;
	}

	public UIImage getEffectPalmMute() {
		return this.effectPalmMute;
	}

	public UIImage getEffectPopping() {
		return this.effectPopping;
	}

	public UIImage getEffectSlapping() {
		return this.effectSlapping;
	}

	public UIImage getEffectSlide() {
		return this.effectSlide;
	}

	public UIImage getEffectStaccato() {
		return this.effectStaccato;
	}

	public UIImage getEffectTapping() {
		return this.effectTapping;
	}

	public UIImage getEffectTremoloBar() {
		return this.effectTremoloBar;
	}

	public UIImage getEffectTremoloPicking() {
		return this.effectTremoloPicking;
	}

	public UIImage getEffectTrill() {
		return this.effectTrill;
	}

	public UIImage getEffectVibrato() {
		return this.effectVibrato;
	}

	public UIImage getFileNew() {
		return this.fileNew;
	}

	public UIImage getFileOpen() {
		return this.fileOpen;
	}

	public UIImage getFileClose() {
		return this.fileClose;
	}

	public UIImage getFilePrint() {
		return this.filePrint;
	}

	public UIImage getFilePrintPreview() {
		return this.filePrintPreview;
	}

	public UIImage getFileHistory() {
		return this.fileHistory;
	}

	public UIImage getFileExit() {
		return this.fileExit;
	}

	public UIImage getFileSave() {
		return this.fileSave;
	}

	public UIImage getFileSaveAs() {
		return this.fileSaveAs;
	}

	public UIImage getFileImport() {
		return this.fileImport;
	}

	public UIImage getFileExport() {
		return this.fileExport;
	}

	public UIImage getFretboard() {
		return this.fretboard;
	}

	public UIImage getFretboardFirstFret() {
		return this.fretboardFirstFret;
	}

	public UIImage getFretboardFret() {
		return this.fretboardFret;
	}

	public UIImage getFretboardSmaller() {
		return this.fretboardSmaller;
	}

	public UIImage getFretboardBigger() {
		return this.fretboardBigger;
	}

	public UIImage getLayoutLinear() {
		return this.layoutLinear;
	}

	public UIImage getLayoutMultitrack() {
		return this.layoutMultitrack;
	}

	public UIImage getLayoutPage() {
		return this.layoutPage;
	}

	public UIImage getLayoutScore() {
		return this.layoutScore;
	}

	public UIImage getLayoutTablature() {
		return this.layoutTablature;
	}

	public UIImage getLayoutCompact() {
		return this.layoutCompact;
	}

	public UIImage getLayoutHighlightPlayedBeat() {
		return this.layoutHighlightPlayedBeat;
	}

	public UIImage getMarkerAdd() {
		return this.markerAdd;
	}

	public UIImage getMarkerFirst() {
		return this.markerFirst;
	}

	public UIImage getMarkerLast() {
		return this.markerLast;
	}

	public UIImage getMarkerList() {
		return this.markerList;
	}

	public UIImage getMarker() {
		return this.marker;
	}

	public UIImage getMarkerPrevious() {
		return this.markerPrevious;
	}

	public UIImage getMarkerNext() {
		return this.markerNext;
	}

	public UIImage getMeasureFirst() {
		return this.measureFirst;
	}

	public UIImage getMeasureLast() {
		return this.measureLast;
	}

	public UIImage getMeasurePrevious() {
		return this.measurePrevious;
	}

	public UIImage getMeasureNext() {
		return this.measureNext;
	}

	public UIImage getMeasureAdd() {
		return this.measureAdd;
	}

	public UIImage getMeasureClean() {
		return this.measureClean;
	}

	public UIImage getMeasureRemove() {
		return this.measureRemove;
	}

	public UIImage getMeasureCopy() {
		return this.measureCopy;
	}

	public UIImage getMeasurePaste() {
		return this.measurePaste;
	}

	public UIImage getInstruments() {
		return this.instruments;
	}

	public UIImage getMatrix() {
		return this.matrix;
	}

	public UIImage getPiano() {
		return this.piano;
	}

	public UIImage getNoteTied() {
		return this.noteTied;
	}

	public UIImage getOptionLanguage() {
		return this.optionLanguage;
	}

	public UIImage getOptionMain() {
		return this.optionMain;
	}

	public UIImage getOptionSound() {
		return this.optionSound;
	}

	public UIImage getOptionStyle() {
		return this.optionStyle;
	}

	public UIImage getOptionSkin() {
		return this.optionSkin;
	}

	public UIImage getSongProperties() {
		return this.songProperties;
	}

	public UIImage getTrackFirst() {
		return this.trackFirst;
	}

	public UIImage getTrackLast() {
		return this.trackLast;
	}

	public UIImage getTrackPrevious() {
		return this.trackPrevious;
	}

	public UIImage getTrackNext() {
		return this.trackNext;
	}

	public UIImage getTrackAdd() {
		return this.trackAdd;
	}

	public UIImage getTrackClone() {
		return this.trackClone;
	}

	public UIImage getTrackRemove() {
		return this.trackRemove;
	}

	public UIImage getTrackSolo() {
		return this.trackSolo;
	}

	public UIImage getTrackMute() {
		return this.trackMute;
	}

	public UIImage getTransport() {
		return this.transport;
	}

	public UIImage getTransportFirst() {
		return this.transportFirst;
	}

	public UIImage getTransportIconFirst() {
		return this.transportIconFirst;
	}

	public UIImage getTransportIconLast() {
		return this.transportIconLast;
	}

	public UIImage getTransportIconNext() {
		return this.transportIconNext;
	}

	public UIImage getTransportIconPause() {
		return this.transportIconPause;
	}

	public UIImage getTransportIconPlay() {
		return this.transportIconPlay;
	}

	public UIImage getTransportIconPrevious() {
		return this.transportIconPrevious;
	}

	public UIImage getTransportIconStop() {
		return this.transportIconStop;
	}

	public UIImage getTransportLast() {
		return this.transportLast;
	}

	public UIImage getTransportNext() {
		return this.transportNext;
	}

	public UIImage getTransportPause() {
		return this.transportPause;
	}

	public UIImage getTransportPlay() {
		return this.transportPlay;
	}

	public UIImage getTransportPrevious() {
		return this.transportPrevious;
	}

	public UIImage getTransportStop() {
		return this.transportStop;
	}

	public UIImage getTransportMetronome() {
		return this.transportMetronome;
	}

	public UIImage getTransportCountIn() {
		return this.transportCountIn;
	}

	public UIImage getTransportMode() {
		return this.transportMode;
	}

	public UIImage getTransportLoopStart() {
		return this.transportLoopStart;
	}

	public UIImage getTransportLoopEnd() {
		return this.transportLoopEnd;
	}

	public UIImage getBrowserBack() {
		return this.browserBack;
	}

	public UIImage getBrowserFile() {
		return this.browserFile;
	}

	public UIImage getBrowserFolder() {
		return this.browserFolder;
	}

	public UIImage getBrowserFolderRemote() {
		return this.browserFolderRemote;
	}

	public UIImage getBrowserCollection() {
		return this.browserCollection;
	}

	public UIImage getBrowserRefresh() {
		return this.browserRefresh;
	}

	public UIImage getBrowserRoot() {
		return this.browserRoot;
	}

	public UIImage getBrowserNew() {
		return this.browserNew;
	}

	public UIImage getStrokeUp() {
		return this.strokeUp;
	}

	public UIImage getStrokeDown() {
		return this.strokeDown;
	}

	public UIImage getSettings() {
		return this.settings;
	}

	public UIImage getToolbarMain() {
		return this.toolbarMain;
	}

	public UIImage getToolbarEdit() {
		return this.toolbarEdit;
	}

	public UIImage getTableViewer() {
		return this.tableViewer;
	}

	public UIImage getArrowUp() {
		return this.arrowUp;
	}

	public UIImage getArrowDown() {
		return this.arrowDown;
	}

	public UIImage getArrowLeft() {
		return this.arrowLeft;
	}

	public UIImage getArrowRight() {
		return this.arrowRight;
	}

	public UIImage getStatusQuestion() {
		return this.statusQuestion;
	}

	public UIImage getStatusError() {
		return this.statusError;
	}

	public UIImage getStatusWarning() {
		return this.statusWarning;
	}

	public UIImage getStatusInfo() {
		return this.statusInfo;
	}

	public UIImage getListAdd() {
		return this.listAdd;
	}

	public UIImage getListEdit() {
		return this.listEdit;
	}

	public UIImage getListRemove() {
		return this.listRemove;
	}

	public UIImage getSolo() {
		return solo;
	}

	public UIImage getSoloDisabled() {
		return soloDisabled;
	}

	public UIImage getSoloDim() {
		return soloDim;
	}

	public UIImage getSoloDisabledDim() {
		return soloDisabledDim;
	}

	public UIImage getToolsPlugins() {
		return toolsPlugins;
	}

	public UIImage getToolsShortcuts() {
		return toolsShortcuts;
	}

	public UIImage getToolsSettings() {
		return toolsSettings;
	}

	public UIImage getMute() {
		return mute;
	}

	public UIImage getMuteDisabled() {
		return muteDisabled;
	}

	public UIImage getMuteDim() {
		return muteDim;
	}

	public UIImage getMuteDisabledDim() {
		return muteDisabledDim;
	}

	public UIImage getListMoveUp() {
		return this.listMoveUp;
	}

	public UIImage getListMoveDown() {
		return this.listMoveDown;
	}

	public UIImage getZoomOut() {
		return this.zoomOut;
	}

	public UIImage getZoomReset() {
		return this.zoomReset;
	}

	public UIImage getZoomIn() {
		return this.zoomIn;
	}

	public static TGIconManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGIconManager.class.getName(), new TGSingletonFactory<TGIconManager>() {
			public TGIconManager createInstance(TGContext context) {
				return new TGIconManager(context);
			}
		});
	}
}
