#!/bin/bash

# Exit on error
set -e
# Print commands
#set -x

COMMAND=`basename $0`

THIS_DIR=`dirname $(realpath "$0")`
TG=TuxGuitar
DT=KDE
DT_URL=https://invent.kde.org/frameworks/breeze-icons/-/raw/master
SKIN=Breeze
DARK=$SKIN-dark
SKIN_DIR=$THIS_DIR/../../share/skins/$SKIN
DARK_DIR=$THIS_DIR/../../share/skins/$DARK

# Breeze/KDE icons are aligned on a 22x22 grid, so we can use this resolution for the PNG images
S_ICON=22x22     # Size of icons in menues and toolbars
S_INFO=64x64     # Size of icons in warnings and settings dialog
S_LOGO=96x96     # Size of TuxGuitar logo

declare -A ICONS=(
  # Icon path/name.svg -convert-> width1xheight1:TuxGuitar_icon1.png width2xheight2+margin2:TuxGuitar_icon2.png
  # Icon path/name.png -convert-> width1xheight1:TuxGuitar_icon1.png width2xheight2:TuxGuitar_icon2.png
  # Icon path/file.png --copy---> $SKIN_DIR/file1.png                $DARK_DIR/file2.png
  ["$TG/1.svg"]="$S_ICON:1.png"
  ["$TG/2.svg"]="$S_ICON:2.png"
  ["$TG/4.svg"]="$S_ICON:4.png"
  ["$TG/8.svg"]="$S_ICON:8.png"
  ["$TG/16.svg"]="$S_ICON:16.png"
  ["$TG/32.svg"]="$S_ICON:32.png"
  ["$TG/64.svg"]="$S_ICON:64.png"
  ["$TG/layout_page.svg"]="$S_ICON:layout_page.png"
  ["$TG/layout_linear.svg"]="$S_ICON:layout_linear.png"
  ["$TG/layout_multitrack.svg"]="$S_ICON:layout_multitrack.png"
  ["$TG/layout_score.svg"]="$S_ICON:layout_score.png"
  ["$TG/layout_tablature.svg"]="$S_ICON:layout_tablature.png"
  ["$TG/layout_compact.svg"]="$S_ICON:layout_compact.png"
  ["$TG/edit_voice_1.svg"]="$S_ICON:edit_voice_1.png"
  ["$TG/edit_voice_2.svg"]="$S_ICON:edit_voice_2.png"
  ["$DT/actions/22/edit-select.svg"]="$S_ICON:edit_mode_selection.png"
  ["$TG/edit_mode_edition_no_natural.svg"]="$S_ICON:edit_mode_edition_no_natural.png"
  ["$TG/timesignature.svg"]="$S_ICON:timesignature.png"
  ["$DT/preferences/22/preferences-system-time.svg"]="$S_ICON:tempoicon.png"
  ["$TG/clef.svg"]="$S_ICON:clef.png"
  ["$TG/keysignature.svg"]="$S_ICON:keysignature.png"
  ["$TG/tripletfeel.svg"]="$S_ICON:tripletfeel.png"
  ["$TG/openrepeat.svg"]="$S_ICON:openrepeat.png"
  ["$TG/closerepeat.svg"]="$S_ICON:closerepeat.png"
  ["$TG/repeat_alternative.svg"]="$S_ICON:repeat_alternative.png"
  ["$TG/dotted.svg"]="$S_ICON:dotted.png"
  ["$TG/doubledotted.svg"]="$S_ICON:doubledotted.png"
  ["$TG/division-type.svg"]="$S_ICON:division-type.png"
  ["$TG/fretboard.svg"]="$S_ICON:fretboard.png"
  ["$TG/firstfret.svg"]="1x50:firstfret.png"
  ["$TG/fret.svg"]="2x45:fret.png"
  ["$TG/chord.svg"]="$S_ICON:chord.png"
  ["$TG/text.svg"]="$S_ICON:text.png"
  ["$TG/tiednote.svg"]="$S_ICON:tiednote.png"
  ["$TG/mixer.svg"]="$S_ICON:mixer.png"
  ["$TG/dynamic_ppp.svg"]="$S_ICON:dynamic_ppp.png"
  ["$TG/dynamic_pp.svg"]="$S_ICON:dynamic_pp.png"
  ["$TG/dynamic_p.svg"]="$S_ICON:dynamic_p.png"
  ["$TG/dynamic_mp.svg"]="$S_ICON:dynamic_mp.png"
  ["$TG/dynamic_mf.svg"]="$S_ICON:dynamic_mf.png"
  ["$TG/dynamic_f.svg"]="$S_ICON:dynamic_f.png"
  ["$TG/dynamic_ff.svg"]="$S_ICON:dynamic_ff.png"
  ["$TG/dynamic_fff.svg"]="$S_ICON:dynamic_fff.png"
  ["$TG/effect_dead.svg"]="$S_ICON:effect_dead.png"
  ["$TG/effect_ghost.svg"]="$S_ICON:effect_ghost.png"
  ["$TG/effect_accentuated.svg"]="$S_ICON:effect_accentuated.png"
  ["$TG/effect_heavy_accentuated.svg"]="$S_ICON:effect_heavy_accentuated.png"
  ["$TG/effect_let_ring.svg"]="$S_ICON:effect_let_ring.png"
  ["$TG/effect_harmonic.svg"]="$S_ICON:effect_harmonic.png"
  ["$TG/effect_grace.svg"]="$S_ICON:effect_grace.png"
  ["$TG/effect_bend.svg"]="$S_ICON:effect_bend.png"
  ["$TG/effect_tremolo_bar.svg"]="$S_ICON:effect_tremolo_bar.png"
  ["$TG/effect_slide.svg"]="$S_ICON:effect_slide.png"
  ["$TG/effect_hammer.svg"]="$S_ICON:effect_hammer.png"
  ["$TG/effect_vibrato.svg"]="$S_ICON:effect_vibrato.png"
  ["$TG/effect_trill.svg"]="$S_ICON:effect_trill.png"
  ["$TG/effect_tremolo_picking.svg"]="$S_ICON:effect_tremolo_picking.png"
  ["$TG/effect_palm_mute.svg"]="$S_ICON:effect_palm_mute.png"
  ["$TG/effect_staccato.svg"]="$S_ICON:effect_staccato.png"
  ["$TG/effect_tapping.svg"]="$S_ICON:effect_tapping.png"
  ["$TG/effect_slapping.svg"]="$S_ICON:effect_slapping.png"
  ["$TG/effect_popping.svg"]="$S_ICON:effect_popping.png"
  ["$TG/effect_fade_in.svg"]="$S_ICON:effect_fade_in.png"
  ["$TG/stroke_up.svg"]="$S_ICON:stroke_up.png"
  ["$TG/stroke_down.svg"]="$S_ICON:stroke_down.png"
  ["$DT/actions/22/configure.svg"]="$S_ICON:settings.png"
  ["$DT/actions/22/document-new.svg"]="$S_ICON:new.png"
  ["$DT/actions/22/document-open.svg"]="$S_ICON:open.png"
  ["$DT/actions/22/document-close.svg"]="$S_ICON:close.png"
  ["$DT/actions/22/document-save.svg"]="$S_ICON:save.png"
  ["$DT/actions/22/document-save-as.svg"]="$S_ICON:save-as.png"
  ["$DT/actions/22/document-import.svg"]="$S_ICON:import.png"
  ["$DT/actions/22/document-export.svg"]="$S_ICON:export.png"
  ["$DT/actions/22/document-print.svg"]="$S_ICON:print.png"
  ["$DT/actions/22/document-preview-archive.svg"]="$S_ICON:print-preview.png"
  ["$DT/actions/22/document-open-recent.svg"]="$S_ICON:history.png"
  ["$DT/actions/22/application-exit.svg"]="$S_ICON:exit.png"
  ["$DT/actions/22/edit-cut.svg"]="$S_ICON:edit_cut.png"
  ["$DT/actions/22/edit-copy.svg"]="$S_ICON:edit_copy.png $S_ICON:measure_copy.png $S_ICON:track_clone.png"
  ["$DT/actions/22/edit-paste.svg"]="$S_ICON:edit_paste.png $S_ICON:measure_paste.png"
  ["$DT/actions/22/edit-clear.svg"]="$S_ICON:measure_clean.png"
  ["$DT/actions/22/special_paste.svg"]="$S_ICON:edit_repeat.png"
  ["$DT/actions/22/edit-undo.svg"]="$S_ICON:edit_undo.png"
  ["$DT/actions/22/edit-redo.svg"]="$S_ICON:edit_redo.png"
  ["$DT/actions/22/document-properties.svg"]="$S_ICON:song_properties.png"
  ["$DT/actions/22/zoom-in.svg"]="$S_ICON:zoom_in.png"
  ["$DT/actions/22/zoom-original.svg"]="$S_ICON:zoom_original.png"
  ["$DT/actions/22/zoom-out.svg"]="$S_ICON:zoom_out.png"
  ["$DT/actions/22/list-add.svg"]="$S_ICON:list_add.png $S_ICON:track_add.png $S_ICON:measure_add.png"
  ["$DT/actions/22/list-remove.svg"]="$S_ICON:list_remove.png $S_ICON:track_remove.png $S_ICON:measure_remove.png 14x14:mute-disabled.png 14x14:mute-disabled-dim.png 14x14:solo-disabled.png 14x14:solo-disabled-dim.png"
  ["$DT/devices/22/audio-headphones.svg"]="$S_ICON:track_solo.png 14x14:solo.png 14x14:solo-dim.png"
  ["$DT/status/22/audio-volume-muted.svg"]="$S_ICON:track_mute.png 14x14:mute.png 14x14:mute-dim.png"
  ["$DT/actions/22/document-edit.svg"]="$S_ICON:list_edit.png $S_ICON:edit_mode_edition.png"
  ["$DT/actions/22/go-down-skip.svg"]="$S_ICON:list_move_up.png"
  ["$DT/actions/22/go-up-skip.svg"]="$S_ICON:list_move_down.png"
  ["$TG/sidebar-expand-left_top.svg"]="$S_ICON:toolbar_main.png"
  ["$DT/actions/22/sidebar-expand-left.svg"]="$S_ICON:toolbar_edit.png"
  ["$TG/sidebar-expand-left_bottom.svg"]="$S_ICON:table_viewer.png"
  ["$DT/devices/22/multimedia-player-ipod-mini-blue.svg"]="$S_ICON:transport.png"
  ["$DT/actions/22/media-playback-start.svg"]="$S_ICON:transport_play_1.png $S_ICON:transport_play_2.png $S_ICON:transport_icon_play_1.png $S_ICON:transport_icon_play_2.png"
  ["$DT/actions/22/media-playback-pause.svg"]="$S_ICON:transport_pause.png $S_ICON:transport_icon_pause.png"
  ["$DT/actions/22/media-playback-stop.svg"]="$S_ICON:transport_stop_1.png $S_ICON:transport_stop_2.png $S_ICON:transport_icon_stop_1.png $S_ICON:transport_icon_stop_2.png"
  ["$DT/actions/22/media-seek-forward.svg"]="$S_ICON:transport_next_1.png $S_ICON:transport_next_2.png $S_ICON:transport_icon_next_1.png $S_ICON:transport_icon_next_2.png"
  ["$DT/actions/22/media-seek-backward.svg"]="$S_ICON:transport_previous_1.png $S_ICON:transport_previous_2.png $S_ICON:transport_icon_previous_1.png $S_ICON:transport_icon_previous_2.png"
  ["$DT/actions/22/media-skip-backward.svg"]="$S_ICON:transport_first_1.png $S_ICON:transport_first_2.png $S_ICON:transport_icon_first_1.png $S_ICON:transport_icon_first_2.png"
  ["$DT/actions/22/media-skip-forward.svg"]="$S_ICON:transport_last_1.png $S_ICON:transport_last_2.png $S_ICON:transport_icon_last_1.png $S_ICON:transport_icon_last_2.png"
  ["$DT/actions/22/media-playlist-repeat.svg"]="$S_ICON:transport_mode.png"
  ["$TG/media-playlist-repeat_start.svg"]="$S_ICON:transport_loop_start.png"
  ["$TG/media-playlist-repeat_end.svg"]="$S_ICON:transport_loop_end.png"
  ["$DT/actions/22/bookmark-new.svg"]="$S_ICON:marker_add.png"
  ["$DT/actions/22/bookmarks.svg"]="$S_ICON:marker_list.png $S_ICON:marker.png"
  ["$DT/actions/22/go-top.svg"]="$S_ICON:track_first.png"
  ["$DT/actions/22/go-bottom.svg"]="$S_ICON:track_last.png"
  ["$DT/actions/22/go-up.svg"]="$S_ICON:track_previous.png $S_ICON:arrow_up.png"
  ["$DT/actions/22/go-down.svg"]="$S_ICON:track_next.png $S_ICON:arrow_down.png"
  ["$DT/actions/22/go-first.svg"]="$S_ICON:marker_first.png $S_ICON:measure_first.png"
  ["$DT/actions/22/go-previous.svg"]="$S_ICON:marker_previous.png $S_ICON:measure_previous.png $S_ICON:browser_back.png $S_ICON:arrow_left.png"
  ["$DT/actions/22/go-next.svg"]="$S_ICON:marker_next.png $S_ICON:measure_next.png $S_ICON:arrow_right.png"
  ["$DT/actions/22/go-last.svg"]="$S_ICON:marker_last.png $S_ICON:measure_last.png"
  ["$DT/preferences/22/preferences-other.svg"]="$S_INFO:option_view.png"
  ["$DT/actions/22/run-build-configure.svg"]="$S_ICON:tools_settings.png"
  ["$DT/actions/22/plugins.svg"]="$S_ICON:tools_plugins.png"
  ["$DT/devices/22/input-keyboard.svg"]="$S_ICON:tools_shortcuts.png"
  ["$DT/preferences/22/preferences-desktop-font.svg"]="$S_INFO:option_style.png"
  ["$DT/preferences/22/preferences-desktop-locale.svg"]="$S_INFO:option_language.png"
  ["$DT/preferences/22/preferences-desktop-theme-applications.svg"]="$S_INFO:option_skin.png"
  ["$DT/status/22/audio-volume-high.svg"]="$S_INFO:option_sound.png"
  ["$DT/actions/22/help-contents.svg"]="$S_ICON:help_doc.png"
  ["$DT/actions/22/help-about.svg"]="$S_INFO:about_description.png $S_ICON:help_about.png"
  ["$DT/actions/22/user-group-new.svg"]="$S_INFO:about_authors.png"
  ["$DT/actions/22/license.svg"]="$S_INFO:about_license.png"
  ["$DT/actions/22/folder-new.svg"]="$S_ICON:browser_new.png"
  ["$DT/actions/22/go-home.svg"]="$S_ICON:browser_root.png"
  ["$DT/actions/22/view-refresh.svg"]="$S_ICON:browser_refresh.png"
  ["$DT/actions/22/view-grid.svg"]="$S_ICON:matrix.png"
  ["$TG/dashboard-show_piano.svg"]="$S_ICON:piano.png"
  ["$DT/mimetypes/22/audio-x-generic.svg"]="$S_ICON:browser_file.png"
  ["$DT/places/22/folder.svg"]="$S_ICON:browser_folder.png"
  ["$DT/actions/22/document-open-remote.svg"]="$S_ICON:browser_folder_remote.png"
  ["$DT/places/22/folder-sound.svg"]="$S_ICON:browser_collection.png"
  ["$DT/status/22/dialog-error.svg"]="$S_INFO:status_error.png"
  ["$DT/status/22/dialog-information.svg"]="$S_INFO:status_info.png"
  ["$DT/status/22/dialog-question.svg"]="$S_INFO:status_question.png"
  ["$DT/status/22/dialog-warning.svg"]="$S_INFO:status_warning.png"
  ["$TG/transport_metronome.svg"]="$S_ICON:transport_metronome.png"
  ["$TG/transport_count_in.svg"]="$S_ICON:transport_count_in.png"
  ["$TG/logo.png"]="$S_LOGO:icon.png"
  ["$TG/splash.png"]="$SKIN_DIR/splash.png $DARK_DIR/splash.png"
  ["$TG/skin-preview.png"]="$SKIN_DIR/skin-preview.png"
  ["$TG/skin-preview-dark.png"]="$DARK_DIR/skin-preview.png"
  ["$TG/skin.info"]="$SKIN_DIR/skin.info"
  ["$TG/skin-dark.info"]="$DARK_DIR/skin.info"
  ["$TG/skin.prop"]="$SKIN_DIR/skin.prop $DARK_DIR/skin.prop"

)

function usage {
  echo
  echo "Usage: $COMMAND [OPTIONS]"
  echo
  echo "Generate the skins $SKIN and $DARK from the SVG/PNG images in the directory"
  echo "  $THIS_DIR"
  echo "  The subfolder \"$DT\" contains the icons that are taken over from the $DT Desktop without modifications."
  echo "  The subfolder \"$TG\" contains icons and other files that are specific to $TG."
  echo "The skins will be written to"
  echo "  $SKIN_DIR"
  echo "  $DARK_DIR"
  echo "These two folders can be safely removed. They will be rebuilt the next time $COMMAND is called."
  echo
  echo "-d"
  echo "  Download/refresh all $DT SVG icons and license files from"
  echo "    $DT_URL"
  echo "  to"
  echo "    $THIS_DIR/$DT"
  echo "  Missing icons are downloaded even if -d is not specified."
  echo
  echo "-h"
  echo "  Display this help message and exit."
  echo
}

# Parse command line options
while getopts "dh" CMDopt
do
  case $CMDopt in
    d) opt_d=1;;
    *) usage
       [ $CMDopt == "h" ] && exit || exit 1
       ;;
  esac
done
# No argument should be left here
shift $((OPTIND-1))
if [ $1 ]; then
  echo "Unknown argument $1."
  usage
  exit 1
fi

mkdir -p $SKIN_DIR $DARK_DIR $THIS_DIR/$DT $THIS_DIR/$TG

# Download $DT license files
for file in COPYING-ICONS COPYING.LIB; do
  if ([ $opt_d ] || [ ! -e $THIS_DIR/$DT/$file ]); then
    echo -n "Downloading $DT $file file from $DT_URL ... "
    curl --silent --output-dir $THIS_DIR/$DT --remote-name $DT_URL/$file
    echo "done."
    echo
  fi
done

# Processing files
for icon in "${!ICONS[@]}"; do

  echo "# Processing file $icon:"

  # Download $DT SVG icon
  if [[ $icon =~ ^$DT/(.+\.svg)$ ]] && ([ $opt_d ] || [ ! -e $THIS_DIR/$icon ]); then
    icon_url=$DT_URL/icons/${BASH_REMATCH[1]}
    echo -n "Downloading $DT icon from $icon_url ... "
    curl --silent --output $THIS_DIR/$icon --create-dirs $icon_url
    echo "done."
  fi

  # Convert icon to PNG (normal and dark)
  for out_icon in ${ICONS[$icon]}; do
    if [[ $out_icon =~ ([0-9]+)"x"([0-9]+)":"(.+) ]]; then
      width=${BASH_REMATCH[1]}
      height=${BASH_REMATCH[2]}
      png_icon=${BASH_REMATCH[3]}
      if [[ $icon =~ ^.+\.svg$ ]]; then
        echo -n "Converting to $SKIN_DIR/$png_icon ... "
        rsvg-convert --width=$width --height=$height $THIS_DIR/$icon > $SKIN_DIR/$png_icon
        echo "done."
        echo -n "Converting to $DARK_DIR/$png_icon ... "
        sed -e 's/#232629/#eff0f1/g' $THIS_DIR/$icon | rsvg-convert --width=$width --height=$height > $DARK_DIR/$png_icon
        echo "done."
      elif [[ $icon =~ ^.+\.png$ ]]; then
        echo -n "Converting to $SKIN_DIR/$png_icon ... "
        gm convert -scale $width"x"$height! $THIS_DIR/$icon $SKIN_DIR/$png_icon
        echo "done."
        echo -n "Converting to $DARK_DIR/$png_icon ... "
        cp -p $SKIN_DIR/$png_icon $DARK_DIR
        echo "done."
      fi
    elif [[ $out_icon =~ ([0-9]+)"x"([0-9]+)"+"([0-9]+)":"(.+) ]]; then
      width=${BASH_REMATCH[1]}
      height=${BASH_REMATCH[2]}
      margin=${BASH_REMATCH[3]}
      png_icon=${BASH_REMATCH[4]}
      page_width=$(( $width + 2 * margin ))
      page_height=$(( $height + 2 * margin ))
      if [[ $icon =~ ^.+\.svg$ ]]; then
        echo -n "Converting to $SKIN_DIR/$png_icon ... "
        rsvg-convert --width=$width --height=$height --page-width=$page_width --page-height=$page_height --left=$margin --top=$margin $THIS_DIR/$icon > $SKIN_DIR/$png_icon
        echo "done."
        echo -n "Converting to $DARK_DIR/$png_icon ... "
        sed -e 's/#232629/#eff0f1/g' $THIS_DIR/$icon | rsvg-convert --width=$width --height=$height --page-width=$page_width --page-height=$page_height --left=$margin --top=$margin > $DARK_DIR/$png_icon
        echo "done."
      elif [[ $icon =~ ^.+\.png$ ]]; then
        echo "Margin not supported for PNGs."
        exit 1
      fi
    else
      echo -n "Copying to $out_icon ... "
      cp -p $THIS_DIR/$icon $out_icon
      echo "done."
    fi
  done

  echo "# $icon done."
  echo

done
