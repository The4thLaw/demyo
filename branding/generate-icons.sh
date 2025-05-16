#!/bin/bash

icon=Paw_icon.svg
shadowless=Paw_icon~no_shadow.svg
monochrome=Paw_icon~no_shadow~monochrome.svg

rm -f /tmp/demyo-*.png /tmp/demyo*.ico

# Standard icons
# 48: For .ico
# 64: For .ico
# 144: IE10 metro
# 192: Chrome on Android (new versions)
# 196: Chrome on Android (old versions)
# 270: Windows 8+ medium tile on Start screen
# 558: Windows 8+ largem tile on Start screen
for size in 48 64 144 192 196 270 558; do
	inkscape -o /tmp/demyo-${size}.png -w $size -h $size $icon
done

# Small icons, with less details
for size in 16 24 32; do
	inkscape -o /tmp/demyo-${size}.png -w $size -h $size $shadowless
done

# Low detail for the tray icons
traySize=128
inkscape -o /tmp/demyo-$traySize-shadowless.png -w $traySize -h $traySize $shadowless
inkscape -o /tmp/demyo-$traySize-monochrome.png -w $traySize -h $traySize $monochrome

# White background icons, for precomposed apple
# 76: iPad 1, 2 on iOS 7+
# 120: iPhone with 2x display on iOS 7+
# 152: iPad with 2x display on iOS 7+
# 180: iPhone 6 Plus with 3x display
for size in 76 120 152 180; do
	inkscape -o /tmp/demyo-${size}-whitebg.png -w $size -h $size -b ffffffff $icon
done

# PNGCrush everything
for file in /tmp/demyo-*.png; do
	mv $file ${file}.orig
	pngcrush -brute -l 9 ${file}.orig $file
	rm -f ${file}.orig
done

# Generate the favicon.ico and general purpose Windows ico
convert /tmp/demyo-16.png /tmp/demyo-24.png /tmp/demyo-32.png /tmp/demyo-48.png /tmp/demyo-64.png /tmp/demyo-favicon.ico
convert /tmp/demyo-16.png /tmp/demyo-24.png /tmp/demyo-32.png /tmp/demyo-48.png /tmp/demyo-64.png /tmp/demyo-192.png /tmp/demyo.ico

# Finally, copy to the relevant locations
cp -f /tmp/demyo.ico ../integration/windows/Demyo.ico
cp -f /tmp/demyo-favicon.ico ../source/demyo-web/src/main/webapp/favicon.ico
mkdir -p ../source/demyo-web/src/main/webapp/icons
cp -f /tmp/demyo-*.png ../source/demyo-web/src/main/webapp/icons/
cp -f /tmp/demyo-$traySize-shadowless.png ../source/demyo-common/src/main/resources/org/demyo/common/desktop/app-icon.png
cp -f /tmp/demyo-$traySize-monochrome.png ../source/demyo-common/src/main/resources/org/demyo/common/desktop/app-icon-monochrome.png
