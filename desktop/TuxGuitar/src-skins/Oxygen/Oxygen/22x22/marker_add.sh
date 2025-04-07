TMPPNG=`mktemp --suffix=.png`
inkscape --export-filename=- --export-type=png --export-background-opacity=0 --export-width=9 --export-height=9 $icon 2>/dev/null | gm convert -border 1x1 -bordercolor transparent - $TMPPNG
inkscape --export-filename=- --export-type=png --export-background-opacity=0 --export-width=$width --export-height=$height `dirname $icon`/marker_list.svgz 2>/dev/null | gm composite -gravity SouthEast $TMPPNG - $out_icon
rm -f $TMPPNG
