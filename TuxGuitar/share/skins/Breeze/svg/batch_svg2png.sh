#!/bin/sh

# Use inkscape (ver 1.0+) to convert SVG to PNG.
if [ -f /usr/bin/inkscape ]; then
  for folder in 22 60 64 
  do
    for file in $folder/*.svg
    do
      outfile="$(basename $file)"
      /usr/bin/inkscape $file -w $folder -h $folder -o ../${outfile%svg}png; 
    done 
  done 
else
  echo "Please install inkscape (ver 1.0+)."
fi
