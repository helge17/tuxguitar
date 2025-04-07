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

DT_ICONS="
  scalable/actions/chronometer.svgz
  scalable/actions/small/22x22/view-media-equalizer.svgz
  scalable/actions/small/22x22/configure.svgz
  scalable/actions/small/22x22/document-new.svgz
  scalable/actions/small/22x22/document-open.svgz
  scalable/actions/document-close.svgz
  scalable/actions/small/22x22/document-save.svgz
  scalable/actions/small/22x22/document-save-as.svgz
  scalable/actions/small/22x22/document-import.svgz
  scalable/actions/small/22x22/document-export.svgz
  scalable/actions/small/22x22/document-print.svgz
  scalable/actions/small/22x22/document-print-preview.svgz
  scalable/actions/document-open-recent.svgz
  scalable/actions/small/22x22/application-exit.svgz
  scalable/actions/small/22x22/edit-cut.svgz
  scalable/actions/small/22x22/edit-copy.svgz
  scalable/actions/small/22x22/edit-paste.svgz
  scalable/actions/edit-clear.svgz
  scalable/actions/small/22x22/edit-undo.svgz
  scalable/actions/small/22x22/edit-redo.svgz
  scalable/actions/small/22x22/document-properties.svgz
  scalable/actions/small/22x22/zoom-in.svgz
  scalable/actions/small/22x22/zoom-original.svgz
  scalable/actions/small/22x22/zoom-out.svgz
  scalable/actions/small/22x22/list-add.svgz
  scalable/actions/small/22x22/list-remove.svgz
  scalable/devices/audio-headphones.svgz
  scalable/status/audio-volume-muted.svgz
  scalable/actions/player-time.svgz
  scalable/actions/media-playback-start.svgz
  scalable/actions/media-playback-pause.svgz
  scalable/actions/media-playback-stop.svgz
  scalable/actions/media-seek-forward.svgz
  scalable/actions/media-seek-backward.svgz
  scalable/actions/media-skip-backward.svgz
  scalable/actions/media-skip-forward.svgz
  scalable/actions/go-top.svgz
  scalable/actions/go-bottom.svgz
  scalable/actions/small/22x22/go-up.svgz
  scalable/actions/small/22x22/go-down.svgz
  scalable/actions/arrow-up.svgz
  scalable/actions/arrow-down.svgz
  scalable/actions/go-first.svgz
  scalable/actions/small/22x22/go-previous.svgz
  scalable/actions/arrow-left.svgz
  scalable/actions/small/22x22/go-next.svgz
  scalable/actions/arrow-right.svgz
  scalable/actions/go-last.svgz
  scalable/actions/small/22x22/dialog-ok-apply.svgz
  scalable/actions/small/22x22/edit-delete.svgz
  scalable/apps/preferences-plugin.svgz
  scalable/actions/configure-shortcuts.svgz
  scalable/apps/help-browser.svgz
  scalable/actions/small/22x22/help-about.svgz
  scalable/actions/small/22x22/folder-new.svgz
  scalable/actions/small/22x22/go-home.svgz
  scalable/actions/small/22x22/view-refresh.svgz
  scalable/actions/small/22x22/view-grid.svgz
  scalable/mimetypes/small/22x22/audio-ac3.svgz
  scalable/mimetypes/small/22x22/inode-directory.svgz
  scalable/actions/small/22x22/document-open-remote.svgz
  scalable/places/small/22x22/folder-sound.svgz
  scalable/apps/system-users.svgz
  scalable/status/dialog-information.svgz
  scalable/status/dialog-error.svgz
  scalable/status/dialog-warning.svgz
  scalable/categories/system-help.svgz
  scalable/actions/small/22x22/mixer-midi.svgz
  scalable/apps/preferences-desktop-locale.svgz
  scalable/apps/preferences-desktop-theme.svgz
  scalable/apps/preferences-desktop-sound.svgz
  scalable/apps/preferences-desktop-font.svgz
  scalable/actions/draw-text.svgz
  scalable/actions/flag-blue.svgz
  scalable/actions/small/16x16/go-first-view.svgz
  scalable/actions/small/16x16/go-previous-view.svgz
  scalable/actions/small/16x16/go-next-view.svgz
  scalable/actions/small/16x16/go-last-view.svgz
  scalable/actions/draw-freehand.svgz
  scalable/actions/small/22x22/draw-freehand.svgz
  scalable/actions/small/22x22/edit-select.svgz
  scalable/actions/irc-voice.svgz
  scalable/actions/small/22x22/page-simple.svgz
  scalable/emblems/small/16x16/emblem-unmounted.svgz
  scalable/actions/small/22x22/view-close.svgz
"

function usage {
  echo
  echo "Usage: $COMMAND [OPTIONS]"
  echo
  echo "Generate the skins $SKIN and $DARK from the SVG/PNG images in the directory"
  echo "  $THIS_DIR"
  echo "  The subfolder \"$DT\" contains the icons that are taken over from the $DT Desktop without modifications."
  echo "  The subfolders \"$SKIN\" and \"$DARK\" contain links to the $DT icons, $TG specific icons and other files."
  echo "The skins will be written to"
  echo "  $SKIN_DIR"
  echo "  $DARK_DIR"
  echo "These two folders can be safely removed. They will be rebuilt the next time $COMMAND is called."
  echo
  echo "-d"
  echo "  Download/refresh all $DT SVG icons from"
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

mkdir -p $SKIN_DIR $DARK_DIR $THIS_DIR/$DT $THIS_DIR/$SKIN $THIS_DIR/$DARK

echo
echo "# Download $DT icons to $THIS_DIR/$DT:"
for dt_icon in $DT_ICONS; do
  if ([ $opt_d ] || [ ! -e $THIS_DIR/$DT/$dt_icon ]); then
    echo -n "$DT_URL/$dt_icon ... "
    curl --silent --output $THIS_DIR/$DT/$dt_icon --create-dirs $DT_URL/$dt_icon
    echo "done."
  fi
done
echo "# Done."

function make_icon {
  if [[ $icon =~ ^$THIS_DIR/$skin/([0-9]+)"x"([0-9]+)/(.+)\.(svgz?)$ ]]; then
    # Icon path/WWxHH/name.svg  -inkscape-export-WWxHHpx-> name.png
    # Icon path/WWxHH/name.svgz -inkscape-export-WWxHHpx-> name.png
    width=${BASH_REMATCH[1]}
    height=${BASH_REMATCH[2]}
    name=${BASH_REMATCH[3]}
    svg=${BASH_REMATCH[4]}
    out_icon=$skin_dir/$name.png
    conv_sh=$THIS_DIR/$skin/$width"x"$height/$name.sh
    echo -n "Generating icon $name.png ("$width"x"$height"px) from $name.$svg ... "
    if [ -e $conv_sh ]; then
      # SVG -> PNG custom conversion in name.sh
      . $conv_sh
    else
      # SVG -> PNG default conversion
      inkscape --export-filename=$out_icon --export-background-opacity=0 --export-width=$width --export-height=$height $icon > /dev/null 2>&1
    fi
    echo "done."
  elif [[ $icon =~ ^$THIS_DIR/$skin/([0-9]+)"x"([0-9]+)/(.+)\.png$ ]]; then
    # Icon path/WWxHH/name.png -gm-convert-scale-WWxHHpx-> name.png
    width=${BASH_REMATCH[1]}
    height=${BASH_REMATCH[2]}
    name=${BASH_REMATCH[3]}
    out_icon=$skin_dir/$name.png
    echo -n "Generating icon $name.png ("$width"x"$height"px) from $name.png ... "
    gm convert -scale $width"x"$height! $icon $out_icon
    echo "done."
  elif [[ $icon =~ ^$THIS_DIR/$skin/copy/.+$ ]]; then
    # File path/copy/name.xyz -copy-> name.xyz
    echo -n "Copying `basename $icon` ... "
    cp -p $icon $skin_dir/
    echo "done."
  fi
}

echo
echo "# Generate $SKIN icons:"
skin=$SKIN
skin_dir=$SKIN_DIR
for icon in $THIS_DIR/$skin/*/*; do
 make_icon
done
echo "# Done."
echo
echo "# Generate $DARK icons:"
skin=$DARK
skin_dir=$DARK_DIR
for icon in $THIS_DIR/$skin/*/*; do
 make_icon
done
echo "# Done."
echo
