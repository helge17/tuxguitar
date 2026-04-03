TMPPNG=`mktemp --suffix=.png`
inkscape --export-filename=$TMPPNG --export-background-opacity=0 --export-width=$width --export-height=$height `dirname $icon`/../other/view-close.svgz 2>/dev/null
inkscape --export-filename=- --export-type=png --export-background-opacity=0 --export-width=$width --export-height=$height $icon 2>/dev/null | gm composite - $TMPPNG $out_icon
rm -f $TMPPNG
