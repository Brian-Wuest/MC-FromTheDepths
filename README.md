# MC-FromTheDepths
This mod is used for spawning bosses for mod packs.

## Recipe Folder
This mod loads recipes from a folder within the config folder in the minecraft folder.
The folder will be created automatically if it doesn't exist.
It will be called "FTD_Summons"

Recipes will follow the standard recipe layout with some caveats.

1. Factory Constants cannot be used.
1. Conditions cannot be used.
    1. It is assumed that if the recipe is there that it should be registered.
    
Below is an example of a recipe to create the item.
**Note**: The "entityInfo" tag is the most important part here. This is what determines what entity to generate when used on an alter. When using a mod-specific entity and the mod is no longer installed the recipe will not be loaded.

    {
      "type": "minecraft:crafting_shaped",
      "group": "Summon",
      "pattern": [
          "aaa",
          "aaa",
          "aaa"
      ],
      "key": {
          "a": {
              "item": "minecraft:cobblestone"
          }
      },
      "result": {
          "item": "from_the_depths:item_totem_of_spawning",
          "data": 0,
          "nbt": {
        "entityInfo": {
          "domain": "minecraft",
          "name": "zombie"
        }
          },
          "count": 1
      }
    }
