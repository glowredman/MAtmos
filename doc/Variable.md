# Variables
MAtmos provides information about the game via Variables. This information comes in three different types depending on what the Variable represents:
- **Booleans** - 'true' or 'false' values
- **Strings** - text values
- **Numbers** - numeric values

---
#### Variable Keys
To look-up the value of a given Variable, you must provide its name, or 'key'.

Keys are defined in a way that attempts to make them intuitive to use and read.

For example, MAtmos provides variables for player activities such as sneaking, sprinting, jumping etc. The keys for these Variables all begin with:

``player.action.``

So:

    player.action.sneaking
    player.action.sprinting
    player.action.jumping

The easiest way to work with MAtmos' Variable keys is to use the in-built editor, where you can use Tab Completion to help you find the Variables you're looking for - it also displays the current value of that variable.

---
#### Scans
Scanners look around the player's surroundings and count the number of occurrences of a particular 'thing' (currently Entities and Blocks supported).

As with other Variables, you must provide a key to look-up in order to find the current count of the particular 'thing'.

##### Entity Scan Keys
Entity Scan lookups begin with:

    scan.entity.
The Entity Scanner counts Entities within 3 different radius's from the player (8, 16 & 32 blocks). Your look-up must specify which radius to query:

    scan.entity.r8.
    scan.entity.r16.
    scan.entity.r32.

Finally you must specify which entity type to look-up. For example:

    scan.entity.r16.sheep
    scan.entity.r8.creeper

##### Block Scan Keys
MAtmos runs 2 Block Scans:
- **scan.small.** - a radius of 8 blocks that refreshes frequently (~every second)
- **scan.large.** - a radius of 28 blocks horizontally, and +- 14 blocks vertically, that refreshes infrequently (~every 5 seconds)

Block counts can be looked up by appending the block name to the end of the desired scan key, as demonstrated below:

    scan.block.r8.minecraft:leaves
    scan.block.r28.minecraft:stone

**N.B.** vanilla blocks should be referenced with the 'minecraft:' prefix. Block variants/metadatas are not supported.