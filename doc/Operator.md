# Operators
Operator symbols represent the type of comparison you wish to perform between a given Variable and a given value.

When writing a Condition Statement, the Operator is placed between the Variable key and the Value to be compared to.

Certain operators can only be applied to certain Variable types (Number, Boolean, String).

----
### Operator Reference:
| Symbol | Description | Type(s) |
|----------|-------------|------|
| = or == | Given Variable '**equal to**' given Value. | Boolean,Number,String |
| != or =! | Given Variable '**not equal to**' given Value. | Boolean,Number,String |
| > | Given Variable '**greater than**' given Value | Number |
| < | Given Variable '**less than**' given Value | Number |
| >= or => | Given Variable '**greater than**' or '**equal to**' given Value | Number |
| <= or =< | Given Variable '**less than**' or '**equal to**' given Value | Number |

#### The 'Or' Operator:
In certain situations you may wish to compare multiple Values to a single Variable.
This can be achieved by placing the 'or' Operator ('|') between Values.

##### Example:
The following returns true if the player is riding a horse **or** a pig:

    player.vehicle = horse | pig