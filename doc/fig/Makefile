# Make PNG from SVG (require inkscape)
# as Github does not support linking to SVG from Markdown

SVG=$(wildcard *.svg)
PNG=$(SVG:.svg=.png)

# Specific path for Inkscape under Mac OS
export PATH := /Applications/Inkscape.app/Contents/MacOS/:$(PATH)

%.png : %.svg
	inkscape --file="$<" --export-png="$@" -d 75

ALL: $(PNG)
