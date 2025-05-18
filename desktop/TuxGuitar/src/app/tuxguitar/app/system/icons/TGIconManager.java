package app.tuxguitar.app.system.icons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.app.util.TGFileUtils;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGIconManager {

	private TGContext context;

	private TGIconTheme theme;
	private Map<String, TGIconTheme> themeCache;

	// 2 different ways to access a specific icon:
	// - legacy: access through a named method (one dedicated method for each icon)
	// - new (04/2025: access through a file name
	//   a lot easier to pass as a parameter
	//   BUT with a risk of duplicated definition of file names
	//   so, please make sure to only use file names defined as static final String here
	public static final String FILE_NEW = "new.png";
	public static final String FILE_OPEN = "open.png";
	public static final String FILE_SAVE = "save.png";
	public static final String FILE_SAVE_AS = "save-as.png";
	public static final String FILE_CLOSE = "close.png";
	public static final String PRINT_PREVIEW = "print-preview.png";
	public static final String EDIT_CUT = "edit_cut.png";
	public static final String EDIT_COPY = "edit_copy.png";
	public static final String EDIT_PASTE = "edit_paste.png";
	public static final String EDIT_REPEAT = "edit_repeat.png";
	public static final String EDIT_MODE_SELECTION = "edit_mode_selection.png";
	public static final String EDIT_MODE_EDITION = "edit_mode_edition.png";
	public static final String EDIT_MODE_EDITION_NO_NATURAL = "edit_mode_edition_no_natural.png";
	public static final String EDIT_MODE_FREE = "edit_mode_free.png";
	public static final String EDIT_MEASURE_STATUS_CHECK = "measure_status_check.png";
	public static final String EDIT_VOICE_1 = "edit_voice_1.png";
	public static final String EDIT_VOICE_2 = "edit_voice_2.png";
	public static final String PRINT = "print.png";
	public static final String UNDO = "edit_undo.png";
	public static final String REDO = "edit_redo.png";
	public static final String TOOLBAR_EDIT = "toolbar_edit.png";
	public static final String TABLE_VIEWER = "table_viewer.png";
	public static final String FRETBOARD = "fretboard.png";
	public static final String INSTRUMENTS = "mixer.png";
	public static final String MATRIX = "matrix.png";
	public static final String PIANO = "piano.png";
	public static final String ZOOM_OUT = "zoom_out.png";
	public static final String ZOOM_RESET = "zoom_original.png";
	public static final String ZOOM_IN = "zoom_in.png";
	public static final String LAYOUT_PAGE = "layout_page.png";
	public static final String LAYOUT_LINEAR = "layout_linear.png";
	public static final String LAYOUT_MULTITRACK = "layout_multitrack.png";
	public static final String LAYOUT_SCORE = "layout_score.png";
	public static final String LAYOUT_TABLATURE = "layout_tablature.png";
	public static final String LAYOUT_COMPACT = "layout_compact.png";
	public static final String TIME_SIGNATURE = "timesignature.png";
	public static final String TEMPO = "tempoicon.png";
	public static final String CLEF ="clef.png";
	public static final String KEY_SIGNATURE = "keysignature.png";
	public static final String TRIPLET_FEEL = "tripletfeel.png";
	public static final String REPEAT_OPEN ="openrepeat.png";
	public static final String REPEAT_CLOSE ="closerepeat.png";
	public static final String REPEAT_ALTERNATIVE ="repeat_alternative.png";
	public static final String SONG_PROPERTIES = "song_properties.png";
	public static final String TRACK_FIRST = "track_first.png";
	public static final String TRACK_LAST = "track_last.png";
	public static final String TRACK_PREVIOUS = "track_previous.png";
	public static final String TRACK_NEXT = "track_next.png";
	public static final String TRACK_ADD = "track_add.png";
	public static final String TRACK_CLONE = "track_clone.png";
	public static final String TRACK_REMOVE = "track_remove.png";
	public static final String TRACK_SOLO = "track_solo.png";
	public static final String TRACK_MUTE = "track_mute.png";
	public static final String MEASURE_FIRST = "measure_first.png";
	public static final String MEASURE_LAST ="measure_last.png";
	public static final String MEASURE_PREVIOUS = "measure_previous.png";
	public static final String MEASURE_NEXT = "measure_next.png";
	public static final String MEASURE_ADD ="measure_add.png";
	public static final String MEASURE_CLEAN = "measure_clean.png";
	public static final String MEASURE_REMOVE = "measure_remove.png";
	public static final String MEASURE_COPY = "measure_copy.png";
	public static final String MEASURE_PASTE ="measure_paste.png";
	public static final String MARKER_ADD = "marker_add.png";
	public static final String MARKER_LIST = "marker_list.png";
	public static final String MARKER_FIRST = "marker_first.png";
	public static final String MARKER_PREVIOUS = "marker_previous.png";
	public static final String MARKER_NEXT = "marker_next.png";
	public static final String MARKER_LAST = "marker_last.png";
	public static final String TRANSPORT = "transport.png";
	public static final String TRANSPORT_ICON_FIRST = "transport_icon_first.png";
	public static final String TRANSPORT_ICON_LAST = "transport_icon_last.png";
	public static final String TRANSPORT_ICON_PREVIOUS = "transport_icon_previous.png";
	public static final String TRANSPORT_ICON_NEXT = "transport_icon_next.png";
	public static final String TRANSPORT_ICON_STOP = "transport_icon_stop.png";
	public static final String TRANSPORT_ICON_PLAY = "transport_icon_play.png";
	public static final String TRANSPORT_ICON_PAUSE = "transport_icon_pause.png";
	public static final String TRANSPORT_METRONOME = "transport_metronome.png";
	public static final String TRANSPORT_COUNT_IN = "transport_count_in.png";
	public static final String TRANSPORT_MODE = "transport_mode.png";
	public static final String TRANSPORT_LOOP_START = "transport_loop_start.png";
	public static final String TRANSPORT_LOOP_END = "transport_loop_end.png";
	public static final String TRANSPORT_HIGHLIGHT_PLAYED_BEAT = "highlight_played_beat.png";
	public static final String SETTINGS = "settings.png";
	public static final String LIST_REMOVE = "list_remove.png";
	public static final String ARROW_UP = "arrow_up.png";
	public static final String ARROW_DOWN = "arrow_down.png";
	public static final String ARROW_LEFT = "arrow_left.png";
	public static final String ARROW_RIGHT = "arrow_right.png";
	public static final String WHOLE = "1.png";
	public static final String HALF = "2.png";
	public static final String QUARTER = "4.png";
	public static final String EIGHTH = "8.png";
	public static final String SIXTEENTH = "16.png";
	public static final String THIRTYSECOND = "32.png";
	public static final String SIXTYFOURTH = "64.png";
	public static final String DOTTED = "dotted.png";
	public static final String DOUBLE_DOTTED = "doubledotted.png";
	public static final String TIED_NOTE = "tiednote.png";
	public static final String DYNAMIC_PPP = "dynamic_ppp.png";
	public static final String DYNAMIC_PP = "dynamic_pp.png";
	public static final String DYNAMIC_P = "dynamic_p.png";
	public static final String DYNAMIC_MP = "dynamic_mp.png";
	public static final String DYNAMIC_MF = "dynamic_mf.png";
	public static final String DYNAMIC_F = "dynamic_f.png";
	public static final String DYNAMIC_FF = "dynamic_ff.png";
	public static final String DYNAMIC_FFF = "dynamic_fff.png";
	public static final String EFFECT_VIBRATO = "effect_vibrato.png";
	public static final String EFFECT_BEND = "effect_bend.png";
	public static final String EFFECT_TREMOLO_BAR = "effect_tremolo_bar.png";
	public static final String EFFECT_DEAD = "effect_dead.png";
	public static final String EFFECT_SLIDE = "effect_slide.png";
	public static final String EFFECT_HAMMER = "effect_hammer.png";
	public static final String EFFECT_GHOST = "effect_ghost.png";
	public static final String EFFECT_ACCENTUATED = "effect_accentuated.png";
	public static final String EFFECT_HEAVY_ACCENTUATED = "effect_heavy_accentuated.png";
	public static final String EFFECT_LET_RING = "effect_let_ring.png";
	public static final String EFFECT_HARMONIC = "effect_harmonic.png";
	public static final String EFFECT_GRACE = "effect_grace.png";
	public static final String EFFECT_TRILL = "effect_trill.png";
	public static final String EFFECT_TREMOLO_PICKING = "effect_tremolo_picking.png";
	public static final String EFFECT_PALM_MUTE = "effect_palm_mute.png";
	public static final String EFFECT_STACCATO = "effect_staccato.png";
	public static final String EFFECT_TAPPING = "effect_tapping.png";
	public static final String EFFECT_SLAPPING = "effect_slapping.png";
	public static final String EFFECT_POPPING = "effect_popping.png";
	public static final String EFFECT_FADE_IN = "effect_fade_in.png";
	public static final String CHORD = "chord.png";
	public static final String TEXT = "text.png";
	public static final String STROKE_UP = "stroke_up.png";
	public static final String STROKE_DOWN = "stroke_down.png";
	public static final String PICK_STROKE_UP = "pick_stroke_up.png";
	public static final String PICK_STROKE_DOWN = "pick_stroke_down.png";
	public static final String ALTERNATIVE_ENHARMONIC = "alt_enharmonic.png";
	public static final String TOOLS_PLUGINS = "tools_plugins.png";
	public static final String TOOLS_SHORTCUTS = "tools_shortcuts.png";
	public static final String TOOLS_SETTINGS = "tools_settings.png";
	public static final String HELP_DOC = "help_doc.png";
	public static final String HELP_ABOUT = "help_about.png";
	public static final String GO_HOME = "browser_root.png";


	private UIImage[] durations;
	private UIImage[] durationsDotted;
	private UIImage transportFirst;
	private UIImage transportLast;
	private UIImage transportPrevious;
	private UIImage transportNext;
	private UIImage transportStop;
	private UIImage transportPlay;
	private UIImage transportPause;
	private UIImage marker;
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
	private UIImage fretboardFirstFret;
	private UIImage fretboardFret;
	private UIImage fretboardSmaller;
	private UIImage fretboardBigger;
	private Map<Integer,UIImage> divisionTypes;
	private UIImage fileImport;
	private UIImage fileExport;
	private UIImage fileHistory;
	private UIImage fileExit;
	private UIImage browserNew;
	private UIImage browserFile;
	private UIImage browserFolder;
	private UIImage browserFolderRemote;
	private UIImage browserCollection;
	private UIImage browserRoot;
	private UIImage browserBack;
	private UIImage browserRefresh;
	private UIImage statusQuestion;
	private UIImage statusError;
	private UIImage statusWarning;
	private UIImage statusInfo;
	private UIImage toolbarMain;
	private UIImage listAdd;
	private UIImage listEdit;
	private UIImage solo;
	private UIImage soloDisabled;
	private UIImage soloDim;
	private UIImage soloDisabledDim;
	private UIImage mute;
	private UIImage muteDisabled;
	private UIImage muteDim;
	private UIImage muteDisabledDim;
	private UIImage OK;
	private UIImage KO;
	
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
			loadIcon(WHOLE),
			loadIcon(HALF),
			loadIcon(QUARTER),
			loadIcon(EIGHTH),
			loadIcon(SIXTEENTH),
			loadIcon(THIRTYSECOND),
			loadIcon(SIXTYFOURTH)
		};
		this.durationsDotted = new UIImage[]{
				loadIcon("2dotted.png"),
				loadIcon("4dotted.png"),
				loadIcon("8dotted.png"),
			};
		loadIcon(LAYOUT_PAGE);
		loadIcon(LAYOUT_LINEAR);
		loadIcon(LAYOUT_MULTITRACK);
		loadIcon(LAYOUT_SCORE);
		loadIcon(LAYOUT_TABLATURE);
		loadIcon(LAYOUT_COMPACT);
		loadIcon(TRANSPORT_HIGHLIGHT_PLAYED_BEAT);
		loadIcon(FILE_NEW);
		loadIcon(FILE_OPEN);
		loadIcon(FILE_CLOSE);
		loadIcon(FILE_SAVE);
		loadIcon(FILE_SAVE_AS);
		this.fileImport = loadIcon("import.png");
		this.fileExport = loadIcon("export.png");
		loadIcon(PRINT);
		loadIcon(PRINT_PREVIEW);
		this.fileHistory = loadIcon("history.png");
		this.fileExit = loadIcon("exit.png");
		loadIcon(EDIT_CUT);
		loadIcon(EDIT_COPY);
		loadIcon(EDIT_PASTE);
		loadIcon(EDIT_REPEAT);
		loadIcon(UNDO);
		loadIcon(REDO);
		loadIcon(EDIT_VOICE_1);
		loadIcon(EDIT_VOICE_2);
		loadIcon(EDIT_MODE_SELECTION);
		loadIcon(EDIT_MODE_EDITION);
		loadIcon(EDIT_MODE_EDITION_NO_NATURAL);
		loadIcon(EDIT_MODE_FREE);
		loadIcon(EDIT_MEASURE_STATUS_CHECK);
		loadIcon(HELP_DOC);
		loadIcon(HELP_ABOUT);
		loadIcon(GO_HOME);
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
		loadIcon(TIME_SIGNATURE);
		loadIcon(TEMPO);
		loadIcon(CLEF);
		loadIcon(KEY_SIGNATURE);
		loadIcon(TRIPLET_FEEL);
		loadIcon(REPEAT_OPEN);
		loadIcon(REPEAT_CLOSE);
		loadIcon(REPEAT_ALTERNATIVE);
		loadIcon(SONG_PROPERTIES);
		loadIcon(TRACK_FIRST);
		loadIcon(TRACK_LAST);
		loadIcon(TRACK_PREVIOUS);
		loadIcon(TRACK_NEXT);
		loadIcon(TRACK_ADD);
		loadIcon(TRACK_CLONE);
		loadIcon(TRACK_REMOVE);
		loadIcon(TRACK_SOLO);
		loadIcon(TRACK_MUTE);
		loadIcon(DOTTED);
		loadIcon(DOUBLE_DOTTED);
		this.divisionTypes = new HashMap<Integer, UIImage>();
		for (int i = 0; i < TGDivisionType.DIVISION_TYPES.length; i++) {
			Integer enters = TGDivisionType.DIVISION_TYPES[i].getEnters();
			this.divisionTypes.put(enters, loadIcon(getDivisionTypeIconFileName(enters)));
		}
		loadIcon(FRETBOARD);
		this.fretboardFirstFret = loadIcon("firstfret.png");
		this.fretboardFret = loadIcon("fret.png");
		this.fretboardSmaller = loadIcon("fretboard_smaller.png");
		this.fretboardBigger = loadIcon("fretboard_bigger.png");
		loadIcon(CHORD);
		loadIcon(TEXT);
		loadIcon(TIED_NOTE);
		loadIcon(TRANSPORT);
		this.transportFirst = loadIcon("transport_first.png");
		this.transportLast = loadIcon("transport_last.png");
		this.transportPrevious = loadIcon("transport_previous.png");
		this.transportNext = loadIcon("transport_next.png");
		this.transportStop = loadIcon("transport_stop.png");
		this.transportPlay = loadIcon("transport_play.png");
		this.transportPause = loadIcon("transport_pause.png");
		loadIcon(TRANSPORT_ICON_FIRST);
		loadIcon(TRANSPORT_ICON_LAST);
		loadIcon(TRANSPORT_ICON_PREVIOUS);
		loadIcon(TRANSPORT_ICON_NEXT);
		loadIcon(TRANSPORT_ICON_STOP);
		loadIcon(TRANSPORT_ICON_PLAY);
		loadIcon(TRANSPORT_ICON_PAUSE);
		loadIcon(TRANSPORT_METRONOME);
		loadIcon(TRANSPORT_COUNT_IN);
		loadIcon(TRANSPORT_MODE);
		loadIcon(TRANSPORT_LOOP_START);
		loadIcon(TRANSPORT_LOOP_END);
		loadIcon(MARKER_LIST);
		this.marker = loadIcon("marker.png");
		loadIcon(MARKER_ADD);
		loadIcon(MARKER_FIRST);
		loadIcon(MARKER_LAST);
		loadIcon(MARKER_PREVIOUS);
		loadIcon(MARKER_NEXT);
		loadIcon(MEASURE_FIRST);
		loadIcon(MEASURE_LAST);
		loadIcon(MEASURE_PREVIOUS);
		loadIcon(MEASURE_NEXT);
		loadIcon(MEASURE_ADD);
		loadIcon(MEASURE_CLEAN);
		loadIcon(MEASURE_REMOVE);
		loadIcon(MEASURE_COPY);
		loadIcon(MEASURE_PASTE);
		loadIcon(INSTRUMENTS);
		loadIcon(MATRIX);
		loadIcon(PIANO);
		loadIcon(DYNAMIC_PPP);
		loadIcon(DYNAMIC_PP);
		loadIcon(DYNAMIC_P);
		loadIcon(DYNAMIC_MP);
		loadIcon(DYNAMIC_MF);
		loadIcon(DYNAMIC_F);
		loadIcon(DYNAMIC_FF);
		loadIcon(DYNAMIC_FFF);
		loadIcon(EFFECT_VIBRATO);
		loadIcon(EFFECT_BEND );
		loadIcon(EFFECT_TREMOLO_BAR );
		loadIcon(EFFECT_DEAD );
		loadIcon(EFFECT_SLIDE );
		loadIcon(EFFECT_HAMMER );
		loadIcon(EFFECT_GHOST );
		loadIcon(EFFECT_ACCENTUATED );
		loadIcon(EFFECT_HEAVY_ACCENTUATED );
		loadIcon(EFFECT_LET_RING );
		loadIcon(EFFECT_HARMONIC );
		loadIcon(EFFECT_GRACE );
		loadIcon(EFFECT_TRILL );
		loadIcon(EFFECT_TREMOLO_PICKING );
		loadIcon(EFFECT_PALM_MUTE );
		loadIcon(EFFECT_STACCATO );
		loadIcon(EFFECT_TAPPING );
		loadIcon(EFFECT_SLAPPING );
		loadIcon(EFFECT_POPPING );
		loadIcon(EFFECT_FADE_IN);
		this.browserNew = loadIcon("browser_new.png");
		this.browserFile = loadIcon("browser_file.png");
		this.browserFolder = loadIcon("browser_folder.png");
		this.browserFolderRemote = loadIcon("browser_folder_remote.png");
		this.browserCollection = loadIcon("browser_collection.png");
		this.browserRoot = loadIcon("browser_root.png");
		this.browserBack = loadIcon("browser_back.png");
		this.browserRefresh = loadIcon("browser_refresh.png");
		loadIcon(ARROW_UP);
		loadIcon(ARROW_DOWN);
		loadIcon(ARROW_LEFT);
		loadIcon(ARROW_RIGHT);
		this.statusQuestion = loadIcon("status_question.png");
		this.statusError = loadIcon("status_error.png");
		this.statusWarning = loadIcon("status_warning.png");
		this.statusInfo = loadIcon("status_info.png");
		loadIcon(STROKE_UP);
		loadIcon(STROKE_DOWN);
		loadIcon(PICK_STROKE_UP);
		loadIcon(PICK_STROKE_DOWN);
		loadIcon(ALTERNATIVE_ENHARMONIC);
		loadIcon(SETTINGS);
		this.toolbarMain = loadIcon("toolbar_main.png");
		loadIcon(TOOLBAR_EDIT);
		loadIcon(TABLE_VIEWER);
		this.listAdd = loadIcon("list_add.png");
		this.listEdit = loadIcon("list_edit.png");
		loadIcon(LIST_REMOVE);
		this.solo = loadIcon("solo.png");
		this.soloDisabled = loadIcon("solo-disabled.png");
		this.soloDim = loadIcon("solo-dim.png");
		this.soloDisabledDim = loadIcon("solo-disabled-dim.png");
		loadIcon(TOOLS_PLUGINS);
		loadIcon(TOOLS_SHORTCUTS);
		loadIcon(TOOLS_SETTINGS);
		this.mute = loadIcon("mute.png");
		this.muteDisabled = loadIcon("mute-disabled.png");
		this.muteDim = loadIcon("mute-dim.png");
		this.muteDisabledDim = loadIcon("mute-disabled-dim.png");
		loadIcon(ZOOM_OUT);
		loadIcon(ZOOM_RESET);
		loadIcon(ZOOM_IN);
		this.OK = loadIcon("measure_status_ok.png");
		this.KO = loadIcon("measure_status_error.png");
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

	public UIImage getDuration(int value, boolean dotted){
		return dotted ? getDurationDotted(value) : getDuration(value);
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

	public UIImage getDurationDotted(int value){
		switch(value){
		case TGDuration.HALF:
			return this.durationsDotted[0];
		case TGDuration.QUARTER:
			return this.durationsDotted[1];
		case TGDuration.EIGHTH:
			return this.durationsDotted[2];
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

	public UIImage getDivisionType(int divisionTypeEnters) {
		return this.divisionTypes.get(divisionTypeEnters);
	}

	public static String getDivisionTypeIconFileName(int divisionTypeEnters) {
		return "division-type-" + String.valueOf(divisionTypeEnters) + ".png";	
	}

	public UIImage getFileHistory() {
		return this.fileHistory;
	}

	public UIImage getFileExit() {
		return this.fileExit;
	}

	public UIImage getFileImport() {
		return this.fileImport;
	}

	public UIImage getFileExport() {
		return this.fileExport;
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

	public UIImage getMarker() {
		return this.marker;
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

	public UIImage getTransportFirst() {
		return this.transportFirst;
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

	public UIImage getToolbarMain() {
		return this.toolbarMain;
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

	public UIImage getOK() {
		return this.OK;
	}

	public UIImage getKO() {
		return this.KO;
	}

	// Make sure to only use file names defined as static final String in this class
	// when calling this method. Don't duplicate file names definitions
	public UIImage getImageByName(String name) {
		return this.theme.getResource(name);
	}
	

	public static TGIconManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGIconManager.class.getName(), new TGSingletonFactory<TGIconManager>() {
			public TGIconManager createInstance(TGContext context) {
				return new TGIconManager(context);
			}
		});
	}
}
