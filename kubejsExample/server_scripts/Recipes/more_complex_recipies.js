//If you need more information you can check the wiki
//https://wikis.degrassi.es/docs/modular-machinery-reborn

/*
You can specify more requirements to your recipes
On older versions, you would need to specify a X and Y after to put it on the JEI/EMI

For the other blocks, you have the following (needs the block):
- .chunkload -> Allows to chunkload X number of chunks

- .biomes -> Allows to specify if you can run a recipe in certain biomes or not
            Needs a list (["biome_here", "biome_here"]) of biomes
            If passed after the list a true, then, those biome becomes a blacklist

- .dimensions -> Similar to biomes, but for dimensions

- .weather -> Allows to run a recipe when on a specific weather ("rain", "clear", "snow", "thunder")

- .time -> Allows you to specify a range where recipe can be done (it's relative, from 0 to 24000), more info here: https://wikis.degrassi.es/docs/modular-machinery-reborn/section/misc/article/range

- .requieredHeight -> Similar to time, but for height (range is from -64 to 320)

- .lootTable -> Allows you to specify a lootTable (like minecraft:chests/ancient_city)
                If passed an argument after the loottable, then you have luck (similar to looting, but for all lootTables)

- .damageItem / .repairItem -> Allows to change durability of certain item
                               If the item is easy in nbt like a sword, a function can be better to deal with those

- .damageItemPerTick / .repairItemPerTick -> Allows to change durability of certain item each tick, like a fan being used
                                             If the item is easy in nbt like a sword, a function can be better to deal with those

Specials require and produce (needs their special hatches):

Similar to a Furnace burning fuel:
.requireFuel(amount) -> Using the fuel hatch to do a recipe (Uses default position)
.requireFuel(amount, data) -> data is similar to ProgressData, except using FuelData and direction is inverted (instead of left to right, from right to left for example)
Here is an example: .requireFuel(1000, FuelData.create().x(10).y(20)) //In this example, it will go from right to left and its on the position (10,20) on JEI/EMI

"Portable" splash potion:
.giveEffect(effectId, time, level, entities[]) -> effectId can be the one that you use on /effect command.
.giveEffect(effectId, time, entitites[]) -> If no entities is provided, it will affect all entities.
.giveEffect(effectId, time, entity1, entity2, ...) -> You dont need entities[], you can pass them all at the same time
                                                      Entities means the ResourceLocation of that mob type

Entities related:
Radius is a number from 1 to higher. From the special hatch
If not specified, whitelist is by default (true), if you need a blacklist, set to false
Amount its from 1 to higher
ResourceLocation... means an array like .giveEffect(effectId, time, entity1, entity2, etc)

.healEntitiesInRadius(radius, amount, ResourceLocation... filter)
.healEntities(amount, ResourceLocation...filter)

.hurtEntitiesInRadius(radius, amount, ResourceLocation... filter)
.hurtEntities(amount, ResourceLocation...filter)

.checkEntitiesAmountInRadius(radius, amount, whitelist, ResourceLocation...filter)
.checkEntitiesAmountInRadius(radius, amount, ResourceLocation...filter)

.checkEntitiesAmount(amount, whitelist, ResourceLocation... filter)

.checkEntitiesHealthInRadius(radius, amount, whitelist, ResourceLocation...filter)
.checkEntitiesHealthInRadius(radius, amount, ResourceLocation...filter)

.checkEntitiesHealth(amount, whitelist, ResourceLocation...filter)
.checkEntitiesHealth(amount, ResourceLocation...filter)

.killEntitiesInRadius(radius, amount, ResourceLocation...filter)

.killEntities(amount, ResourceLocation...filter) 
.killEntity(ResourceLocation...filter) //Same as the one above, but with amount = 1

.spawnEntitiesInRadius(radius, amount, ResourceLocation type) //On a random position inside that radius
.spawnEntities(amount, ResourceLocation type)
.spawnEntity(ResourceLocation type) //Same as the one above, but with amount = 1

Redstone related:
Amount from 0 to 15, like Minecraft redstone
.requireRedstone(amount)
.emitRedstone(amount)

Commands related:
command is a String, it must starts with / (for example, /kill @e), similar on how you use it on the chat
permissionLevel uses from 0 to 4 (both inclusive, default is 2) (More info here: https://minecraft.wiki/w/Permission_level)
log is a true or false, telling you if it should log the command (false by default)

.runCommandOnStart(command)
.runCommandOnStart(command, permissionLevel)
.runCommandOnStart(command, log)
.runCommandOnStart(command, permissionLevel, log)
.runCommandEachTick(command)
.runCommandEachTick(command, permissionLevel)
.runCommandEachTick(command, log)
.runCommandEachTick(command, permissionLevel, log)
.runCommandOnEnd(command)
.runCommandOnEnd(command, permissionLevel)
.runCommandOnEnd(command, log)
.runCommandOnEnd(command, permissionLevel, log)

*/
ServerEvents.recipes(event => {
    const time = 20 //in ticks (20 ticks = 1 second)
    const machine_id = "mmr:lcr6"

    event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
    .requireItem("minecraft:dark_oak_boat")
    .chunkload(3)
})


/*
Lets say that you have a bunch of repetive recipes but something
changed, like the boat produces a log and a boat and an anvil
produces a log

You can specify which recipe is considered first
*/
ServerEvents.recipes(event => {
    const time = 20 //in ticks (20 ticks = 1 second)
    const machine_id = "mmr:lcr6"

    event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
    .requireItem("minecraft:spruce_boat", 10, 10)
    .produceItem("minecraft:oak_log", 0.1, 40, 10)

    event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
    .requireItem("minecraft:spruce_boat", 10, 10)
    .requireItem("minecraft:anvil", 10, 20)
    .produceItem("minecraft:oak_log", 0.1, 40, 10)
    .priority(2) //this recipe is consider first, higher number, higher priority
})

/*
Now lets say that you want to hide the second recipe to be hidden
*/
ServerEvents.recipes(event => {
    const time = 20 //in ticks (20 ticks = 1 second)
    const machine_id = "mmr:lcr6"

    event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
    .requireItem("minecraft:spruce_boat", 10, 10)
    .produceItem("minecraft:oak_log", 0.1, 40, 10)

    event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
    .requireItem("minecraft:spruce_boat", 10, 10)
    .requireItem("minecraft:anvil", 10, 20)
    .produceItem("minecraft:oak_log", 0.1, 40, 10)
    .hide()
    .priority(2) //this recipe is consider first, higher number, higher priority
})

/*
Now lets say that we have an standard machine that always have 5 slots
but you don't want to put 5 items, well, you can do that!

- emptyItem -> If passed 2 arguments, acts as X and Y on the recipe Viewer
- emptyFluid -> If passed 2 arguments, acts as X and Y on the recipe Viewer
- emptyEnergy -> If passed 2 arguments, acts as X and Y on the recipe Viewer

If not passed anything, like emptyItem(), the default is 0,0
*/
ServerEvents.recipes(event => {
    const time = 20 //in ticks (20 ticks = 1 second)
    const machine_id = "mmr:lcr6"

    event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
    .requireItem("minecraft:spruce_boat", 10, 10)
    .emptyItem(10, 20)
    .produceItem("minecraft:oak_log", 0.1, 40, 10)
})

/*
Now, lets say that you want to customize even more your recipes

Well, you can do that
*/
ServerEvents.recipes(event => {
    const time = 20 //in ticks (20 ticks = 1 second)
    const machine_id = "mmr:lcr6"

    event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
    .requireItem("minecraft:spruce_boat", 10, 10)
    .produceItem("minecraft:oak_log", 0.1, 40, 10)
    .jei() //if nothing is after jei(), it will use the real recipe
    //if no jei() is present, it will use the real recipe

    event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
    .requireItem("minecraft:spruce_boat", 10, 10)
    .requireItem("minecraft:anvil", 10, 20)
    .produceItem("minecraft:oak_log", 0.1, 40, 10)
    .jei()
    //Recipe viewer will show oak boat, but real item is a spruce_boat
    //You can customize here even more
    .requireItem("minecraft:oak_boat", 10, 10)
    .produceItem("minecraft:oak_log", 0.1, 40, 10)
})