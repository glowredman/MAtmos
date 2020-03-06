MAtmos
======

MAtmos is a sound atmosphere generator for Minecraft.

## MAtmos 2020 "Zen" demo video
[![](http://img.youtube.com/vi/3F85g3e2_MY/0.jpg)](http://www.youtube.com/watch?v=3F85g3e2_MY "")

## Contributing
The project can be built using Gradle.

To run in an IDE, use the following program arguments:

`--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin haddon.mixin.json --mixin haddon_core.mixin.json`

## Licenses

MAtmos itself is under WTFPLv2. Redistribute/Modify at will.

MAtmos source code requires some custom libraries/classes found at https://github.com/makamys/MC-Commons (WTFPLv2)

The (net.sf.) PracticalXML library (Apache License) is also required to compile versions that include the XML expansion converter:
- net.sf.practicalxml.*
