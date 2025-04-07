TMPPNG=`mktemp --suffix=.png`
inkscape --export-filename=$TMPPNG --export-background-opacity=0 --export-width=$width --export-height=$height `dirname $icon`/fretboard.svg 2>/dev/null
inkscape --export-filename=- --export-type=png --export-background-opacity=0 --export-width=9 --export-height=9 $icon 2>/dev/null | gm composite - $TMPPNG $out_icon
rm -f $TMPPNG
