<!--
Author: @Davoleo
-->

# MC-FromTheDepths RoTN Edition
Welcome to this fork of MC-FromTheDepths, 
here you can read about feature-wise changes to the mod and their correct usage.<br>
This doc describes all the new features in the mod, 
you can refer to the original wiki located [here](https://www.curseforge.com/minecraft/mc-mods/from-the-depths/pages/welcome) 
for more information on original content

## Boss Restrictions
A new system that allows the pack developer 
to block or allow boss summoning depending on different conditions<br>
Boss Restrictions are specified in a top-level JSON Object under the key `restrictions` 
which contains object related to each restriction type that needs to be checked before summoning the boss.<br>
_Note: The different restriction types are linked with **AND** logic by default_

### Available Restrictions
- **Dimension**: Players will only be able to spawn the boss in the specified dimensions
  - Example:
    ```json
    {
      "dimensions": [
        -1,
        0
      ]
    } 
    ```
- **Time of Day**: Players will only be able to spawn the boss in a certain time-of-day range
  - Example:
    ```json
    {
      "timeOfDay": {
        "data": 12000,
        "operator": "LESS"
      }
    }
    ```
  - **data** is the time of a minecraftian day in ticks (a whole day+night lasts `24000` ticks)
  - **operator** is used to compare the timestamp deserialized from json and in-game time value, for more
  information see the paragraph on [operators](#operators)
- **Weather**: Players will only be able to spawn the boss with a certain weather
  - Example:
    ```json
    {
      "weather": "RAIN"
    }
    ```
  - weather can be 3 different types of value:
    - **CLEAN**: Only summon with clear weather _Aliases: [Clean, clean]_
    - **RAIN**: Only summon with raining or storm weather _Aliases: [rain, Rain, RAINING, raining]_
    - **STORM**: Only summon during storms _Aliases: [STORM, Storm, thundering, THUNDERING]_
- **Biome**: Players will only be able to spawn the boss in certain biomes
  - Example:
    ```json
    {
      "biomes": [
        "minecraft:plains"
      ]
    }
    ```
    - **biomes** is a biomes ids array in which the boss can be spawned (supports mod biomes)
- **Y Level**
  - Example:
    ```json
    {
      "yLevel": {
        "data": 40,
        "operator": "MORE"
      }
    }
    ```
  - **data** The Y Level against which the mod should compare the spawning altar level
  - **operator** is used to compare the Y deserialized from json and in-game altar Y level value, for more
    information see the paragraph on [Operators](#operators)
- **Ground Radius**
  - Example:
    ```json
    {
      "groundRadius": 12
    }
    ```
    - **groundRadius** is the required radius of flat land around the altar for the boss to be spawned
- **Serene Seasons ([Mod Compat](https://www.curseforge.com/minecraft/mc-mods/serene-seasons))**
  - Example:
    ```json
    {
      "sereneSeasons": {
        "season": "summer",
        "subSeason": "mid"
      }
    }
    ```
  - Note: All the season values are case-insensitive 
  - **season**: the season id
    - Possible Values: `spring`,`summer`,`autumn`,`winter`
  - **subSeason**: id of a sub-section in the season (related to the `season` value) [optional]
    - Possible Values: `early`,`mid`,`late`
    - Note: When you don't specify all subSeasons will be accepted when summoning the boss

### Operators
Operators are used in restrictions to compare json data with in-game data<br>
Possible Operator Values:
- **EQUALS** is verified if in-game value equals exactly json value
  - Aliases: `equals`, `Equals`, `=`
- **LESS** is verified if in-game value is less than json value
  - Aliases: `less`, `Less`, `<`
- **MORE** is verified if in-game value is more than the json value
  - Aliases: `more`, `More`, `>`, `greater`, `Greater`, `GREATER`
  
## Concurrent Bosses
In the original mod, as long as all the entities were already spawned,
you could summon another boss while the old one was still alive.
In this fork there's a new General config option available called `Allow Spawning of multiple bosses`
which can be set to false in case you don't want the players to be able to spawn multiple bosses contemporarily

## Better Error Messages
This Fork features better error messages when the boss can't be spawned.

## Boss Warning and Spawned Message
You can specify 2 optional custom messages for each custom boss.<br>
These 2 messages will be sent to players in the boss fight respectively when the boss is about to be spawned
and when it's been already spawned.<br>
Messages are specified via these 2 keys in the `bossInfo` object:
- Boss Warning message key: `warningMessage`
- Boss Spawned message key: `spawnedMessage`

## Boss De-Spawn after idling for X seconds
You can add an integer property called `idleTimeBeforeDespawning` to the `bossInfo` object.<br>
This value is the amount of time in seconds after which a boss should de-spawn if it doesn't have a valid target.

## Totem Item Changes

### Registry Names
Totem Items Registry Names have been changed as such all the totems in previous worlds created with the original mod
will disappear as soon as you log in with this fork installed
- This change has the purpose of making the names easier and shorter
- Example (boss key: hyper_evil_rat): 
  - Old Name: `from_the_depths:item_totem_of_spawning_hyper_evil_rat`
  - New Name: `from_the_depths:totem_hyper_evil_rat`

### Custom Totem Textures
In the original mod all totems had the same default texture; 
in this fork you can optionally add a custom texture that will be loaded and used as a texture for 
the totem item.<br>
You can add textures in `config/FTD_Summons/textures` folder use the boss key as file name 
and make sure the image is a PNG<br>
_**Note**: if you don't specify a texture for on of the bosses, the texture will fallback to the default one_<br>
_**Note**: you might see another folder under `config/FTD_Summons` which is called `models`, that
directory contains generated item model files you don't need to worry about them as you only need to add textures_
