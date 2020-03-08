MAtmos
======

MAtmos is a sound atmosphere generator built for Minecraft 1.12 and [1.7.10](https://github.com/makamys/MAtmos/tree/1_7_10).

Whenever you join a multiplayer server or single-player world, MAtmos will search your surroundings and generates a soundscape of natural noises to match, filling out that perpetual silence of Minecraft. What kind of soundscapes, you ask?

## Featured soundpacks
MAtmos lets you create your own soundscapes using special resource packs called **soundpacks**. Some of the ones that have been created are:


### [MAtmos 2020 "Zen"](https://github.com/makamys/MAtmos-2020-Zen)
[![](http://img.youtube.com/vi/3F85g3e2_MY/0.jpg)](http://www.youtube.com/watch?v=3F85g3e2_MY "")

A default soundpack for MAtmos featuring seamless loops and indoors/outdoors variations for ambiences. It aims to add high quality sounds that enhance the atmosphere without being too oppressive. (MAtmos 34 or higher required.)

*(by makamys)*

### [Minecraft Sound Improvement](https://github.com/makamys/MSI-Conversion)

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/OnUeix34Qc4/0.jpg)](https://www.youtube.com/watch?v=OnUeix34Qc4)

The MSI expansion features a large variety of new sounds for every biome, striving to create a very unique atmosphere for each of them.

*(by [Colonel_Oneill](http://www.minecraftforum.net/members/Colonel_Oneill))*

### [Default soundpack](https://github.com/Sollace/MAtmos/releases) (+introduction)

[![](http://img.youtube.com/vi/Z4Zu4kvyDHU/0.jpg)](http://www.youtube.com/watch?v=Z4Zu4kvyDHU "")

The original soundpack from 2011.

*(by Hurricaaane)*

## Usage
By default, MAtmos does not include any sounds. To get started, move the soundpacks you want to your resource packs folder an enable them.

Pressing F7 opens the MAtmos settings menu which can be used to adjust the volume levels of the individual expansions that make up soundpacks. You can even combine different expansions from different soundpacks! Customize your experience to exactly the way you like it.

<img src="docs/matmos_menu.png" width="600">

## More soundpacks and info

Sollace's old wiki: https://github.com/Sollace/MAtmos/wiki

## Contributing
The project can be built using Gradle.

To run in an IDE, use the following program arguments:

`--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin haddon.mixin.json --mixin haddon_core.mixin.json`

## Licenses

MAtmos itself is under WTFPLv2. Redistribute/Modify at will.

MAtmos source code requires some custom libraries/classes found at https://github.com/makamys/MC-Commons (WTFPLv2)

The (net.sf.) PracticalXML library (Apache License) is also required to compile versions that include the XML expansion converter:
- net.sf.practicalxml.*
