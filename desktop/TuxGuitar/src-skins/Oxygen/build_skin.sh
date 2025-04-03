#!/bin/bash

# Exit on error
set -e
# Print commands
#set -x

COMMAND=`basename $0`

THIS_DIR=`dirname $(realpath "$0")`
TG=TuxGuitar
DT=KDE
DT_URL=https://invent.kde.org/frameworks/oxygen-icons/-/raw/master
SKIN=Oxygen
DARK=$SKIN-dark
SKIN_DIR=`realpath $THIS_DIR/../../share/skins/$SKIN`
DARK_DIR=`realpath $THIS_DIR/../../share/skins/$DARK`

declare -A ICONS=(
  # Icon path/name.png --copy---> icon.png
  ["$DT/22x22/actions/chronometer.png"]="tempoicon.png"
  ["$DT/22x22/actions/view-media-equalizer.png"]="mixer.png"
  ["$DT/22x22/actions/configure.png"]="settings.png tools_settings.png list_edit.png"
  ["$DT/22x22/actions/document-new.png"]="new.png"
  ["$DT/22x22/actions/document-open.png"]="open.png"
  ["$DT/22x22/actions/document-close.png"]="close.png"
  ["$DT/22x22/actions/document-save.png"]="save.png"
  ["$DT/22x22/actions/document-save-as.png"]="save-as.png"
  ["$DT/22x22/actions/document-import.png"]="import.png"
  ["$DT/22x22/actions/document-export.png"]="export.png"
  ["$DT/22x22/actions/document-print.png"]="print.png"
  ["$DT/22x22/actions/document-print-preview.png"]="print-preview.png"
  ["$DT/22x22/actions/document-open-recent.png"]="history.png"
  ["$DT/22x22/actions/application-exit.png"]="exit.png"
  ["$DT/22x22/actions/edit-cut.png"]="edit_cut.png"
  ["$DT/22x22/actions/edit-copy.png"]="edit_copy.png measure_copy.png"
  ["$DT/22x22/actions/edit-paste.png"]="edit_paste.png measure_paste.png"
  ["$DT/22x22/actions/edit-clear.png"]="measure_clean.png"
  ["$DT/22x22/actions/edit-undo.png"]="edit_undo.png"
  ["$DT/22x22/actions/edit-redo.png"]="edit_redo.png"
  ["$DT/22x22/actions/document-properties.png"]="song_properties.png"
  # ["$DT/22x22/actions/zoom-in.png"]="zoom_in.png"                           # Dark variant differs from the normal icon
  # ["$DT/22x22/actions/zoom-original.png"]="zoom_original.png"               # Dark variant differs from the normal icon
  # ["$DT/22x22/actions/zoom-out.png"]="zoom_out.png"                         # Dark variant differs from the normal icon
  ["$DT/22x22/actions/list-add.png"]="list_add.png measure_add.png"
  ["$DT/22x22/actions/list-remove.png"]="list_remove.png measure_remove.png"
  # ["$DT/22x22/devices/audio-headphones.png"]="track_solo.png"               # Dark variant differs from the normal icon
  # ["$DT/22x22/status/audio-volume-muted.png"]="track_mute.png"              # Dark variant differs from the normal icon
  ["$DT/22x22/actions/player-time.png"]="transport.png"
  ["$DT/22x22/actions/media-playback-start.png"]="transport_icon_play.png"
  ["$DT/22x22/actions/media-playback-pause.png"]="transport_icon_pause.png"
  ["$DT/22x22/actions/media-playback-stop.png"]="transport_icon_stop.png"
  ["$DT/22x22/actions/media-seek-forward.png"]="transport_icon_next.png"
  ["$DT/22x22/actions/media-seek-backward.png"]="transport_icon_previous.png"
  ["$DT/22x22/actions/media-skip-backward.png"]="transport_icon_first.png"
  ["$DT/22x22/actions/media-skip-forward.png"]="transport_icon_last.png"
  ["$DT/22x22/actions/media-playlist-repeat.png"]="transport_mode.png"
  ["$DT/22x22/actions/go-top.png"]="track_first.png"
  ["$DT/22x22/actions/go-bottom.png"]="track_last.png"
  ["$DT/22x22/actions/go-up.png"]="track_previous.png"
  ["$DT/22x22/actions/go-down.png"]="track_next.png"
  ["$DT/22x22/actions/arrow-up.png"]="arrow_up.png"
  ["$DT/22x22/actions/arrow-down.png"]="arrow_down.png"
  ["$DT/22x22/actions/go-first.png"]="measure_first.png"
  ["$DT/22x22/actions/go-previous.png"]="measure_previous.png browser_back.png"
  ["$DT/22x22/actions/arrow-left.png"]="arrow_left.png"
  ["$DT/22x22/actions/go-next.png"]="measure_next.png"
  ["$DT/22x22/actions/arrow-right.png"]="arrow_right.png"
  ["$DT/22x22/actions/go-last.png"]="measure_last.png"
  ["$DT/22x22/actions/dialog-ok-apply.png"]="measure_status_ok.png"
  ["$DT/22x22/actions/edit-delete.png"]="measure_status_error.png"
  ["$DT/22x22/apps/preferences-plugin.png"]="tools_plugins.png"
  ["$DT/22x22/actions/configure-shortcuts.png"]="tools_shortcuts.png"
  ["$DT/22x22/apps/help-browser.png"]="help_doc.png"
  ["$DT/22x22/actions/help-about.png"]="help_about.png"
  ["$DT/22x22/actions/folder-new.png"]="browser_new.png"
  ["$DT/22x22/actions/go-home.png"]="browser_root.png"
  ["$DT/22x22/actions/view-refresh.png"]="browser_refresh.png"
  ["$DT/22x22/actions/view-grid.png"]="matrix.png"
  ["$DT/22x22/mimetypes/audio-ac3.png"]="browser_file.png"
  ["$DT/22x22/mimetypes/inode-directory.png"]="browser_folder.png"
  ["$DT/22x22/actions/document-open-remote.png"]="browser_folder_remote.png"
  ["$DT/22x22/places/folder-sound.png"]="browser_collection.png"

)

function usage {
  echo
  echo "Usage: $COMMAND [OPTIONS]"
  echo
  echo "PARTIALLY generate the skins $SKIN and $DARK from the PNG images in the directory"
  echo "  $THIS_DIR"
  echo "  The subfolder \"$DT\" contains the icons that are taken over from the $DT Desktop without modifications."
  echo "  The subfolder \"$TG\" contains icons and other files that are specific to $TG."
  echo "The icons will be written to"
  echo "  $SKIN_DIR"
  echo "  $DARK_DIR"
  echo
  echo "-d"
  echo "  Download/refresh all $DT icons from"
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

# Processing input files
for icon in "${!ICONS[@]}"; do

  echo "# Processing file $icon:"

  # Download $DT PNG icon
  if [[ $icon =~ ^$DT/(.+\.png)$ ]] && ([ $opt_d ] || [ ! -e $THIS_DIR/$icon ]); then
    icon_url=$DT_URL/${BASH_REMATCH[1]}
    echo -n "Downloading $DT icon from $icon_url ... "
    curl --silent --output $THIS_DIR/$icon --create-dirs $icon_url
    echo "done."
  fi

  # Generate output files
  for out_icon in ${ICONS[$icon]}; do
    if [[ $icon =~ ^.+\.png$ ]]; then
      echo -n "Copying as $out_icon to skins $SKIN and $DARK ... "
      cp -p $THIS_DIR/$icon $SKIN_DIR/$out_icon
      cp -p $THIS_DIR/$icon $DARK_DIR/$out_icon
      echo "done."
    else
      echo -e "\nError: $out_icon unknown."
      echo -e "\nAborting.\n"
      exit 1
    fi
  done

  echo "# $icon done."
  echo

done
