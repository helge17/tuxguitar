TMPSVG=`mktemp --suffix=.svg`
TMPPNG=`mktemp --suffix=.png`
GRDSVG="$THIS_DIR/$skin/other/Breeze_Gradient.svg#Gradient1"
sed 's|fill="#232629"|fill="url('$GRDSVG')"|g' $icon > $TMPSVG
inkscape --export-filename=$TMPPNG --export-background-opacity=0 --export-width=$((($width)/2)) --export-height=$((($height)/2)) `dirname $icon`/transport_icon_play.svgz 2>/dev/null
inkscape --export-filename=- --export-type=png --export-background-opacity=0 --export-width=$width --export-height=$height $TMPSVG 2> /dev/null | gm composite -gravity SouthWest $TMPPNG - $out_icon
rm -f $TMPSVG $TMPPNG
