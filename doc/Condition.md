# Conditions
Conditions are simple 'true or false' statements that you can use collectively to describe a certain situation in the game.

There are three components to a condition statement.
These are:
1) **Variable** - the information you would like to lookup and compare.
2) **Operator** - the type of comparison you would like to make.
3) **Value** - the value you would like to compare the Variable to.

---
#### Condition Statement
##### Example:
Let's say we want to check if the player is sneaking:

``player.action.sneaking = true``

The **Variable** is 'player.action.sneaking'
The **Operator** is '='
The **Value** to compare to is 'true'

---
#### Using Multiple Conditions
You will often need to use more than one Condition statement in order to effectively describe a particular situation, action, or environment.

You do so by creating a list of Condition statements that will describe it - when all statements are true then you know the player is in that situation.

##### Example:
Let's say we want to play the sound of birds chirping, but only when:
- the weather is clear
- it is during the morning or evening
- there are trees nearby

We can use the following Condition statements:

    weather.israining = false
    world.time = morning | evening
    scan.block.r28.minecraft:leaves > 500
    scan.block.r28.minecraft:log > 100

Here we are checking that it is **not raining**, it is **morning *or* evening**, and to confirm there are (probably) trees nearby, there are **more than 500 leaf blocks** (so probably tree canopies) and **more than 100 log blocks** (so probably tree trunks).