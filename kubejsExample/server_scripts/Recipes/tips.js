//If you need more information you can check the wiki
//https://wikis.degrassi.es/docs/modular-machinery-reborn

/*
Here is a list of general tips, from KubeJS stuff to other related things.
This is not an extensive KubeJS tutorial, just some tips
You would still need some basic knowlegde.
ProbeJS is recommended not to guess what you are working with, since JS don't have a type system
like in Java
*/

//Items with NBT

/*
Some items like armors or tools can have Data Components (also known as NBT)
This can be tricky to deal with, because it can make some recipes impossible if you don't know

For example, let's say that I have a machine that uses diamonds swords, but I only want swords
that has 1 to 10 durabiliy left. 
Well, you can say that! There are 2 ways, using Item.of or a String("1x minecraft:diamond_sword[...]")

For the lastest you can use KubeJS commands (/kjs hand or /kubejs hand) and it will give you the correct one
Here I will show the other way
*/

ServerEvents.recipes(event => {
    const time = 20 //in ticks (20 ticks = 1 second)
    const machine_id = "mmr:lcr6"

    //Here we say that we want as input 1 stone that has data components (a map)
    //A map is an array of pairs, a key and value, you can access a value with a key
    const ingredient_wiht_nbt = Item.of("minecraft:stone", 1, {
        stone: "adios"
    })

    event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
    .requireItem(ingredient_wiht_nbt, 10, 10)
    .emptyItem(10, 20)
    .produceItem("1x minecraft:oak_log", 0.1, 40, 10)
})

/*
Lets say that we have a machine, that we have a lot of keys on the structure, as much as enough.
But we want to add parallel recipes, lets say instead of doing 256 recipes at the same time, we do 
1 recipe but takes 256x the input and gives 256x output.

We can do that too! And its pretty much easy to do, if we use Item.of or `${<value here>}x minecraft:stone`
*/

ServerEvents.recipes(event => {
    const time = 20 //in ticks (20 ticks = 1 second)
    const machine_id = "mmr:lcr6"

    const ingredient_wiht_nbt = Item.of("minecraft:stone", 1, {
        stone: "adios"
    })
    let original_count = ingredient_wiht_nbt.count()

    //On require we use the Item.of and on produce the ``
    [1,2,3,4,5,6,7,8,9,10].forEach(number => {
        let copy = ingredient_wiht_nbt.copy() //Copy is not necessary, but makes sure you don't modify the original item

        //Using this method has an advantage over the other, being able to do more than 99 items per recipe
        copy.setCount(original_count * number)

        event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
            .requireItem(copy, 10, 10)
            .emptyItem(10, 20)
            .produceItem(`${number}x minecraft:oak_log`, 0.1, 40, 10) //Up to 99 items
        //For higher than 1, I would recommend to use .hide(), since it can add to the recipe view 
        //pretty fast
    });

})

/*
Let's say that we want to make a better furnace, or a Mekanism infuser, but we don't want to code 
the recipes, since we can add a new mod that add more recipes and we would have to go and add them.

Well, there is a solution to that!
*/

ServerEvents.recipes(event => {
    const time = 20 //in ticks (20 ticks = 1 second)
    const machine_id = "mmr:lcr6"


    event.forEachRecipe({ type: 'minecraft:smelting' }, recipe => {
        //Here depends on what recipe you want to filter and such
        //recipe is a object from KubeRecipe (https://github.com/KubeJS-Mods/KubeJS/blob/main/src/main/java/dev/latvian/mods/kubejs/recipe/KubeRecipe.java)
        //All public variables can be called like a function
    })
})

/*
Now, each time we load our world, the patterns from AE2 or RS2 might be invalid, well...

We also have another solution for that too!
Its simple, adding a new function on the recipe builder

Or let the user use processing patterns, which it's not great
*/
ServerEvents.recipes(event => {
    const time = 20 //in ticks (20 ticks = 1 second)
    const machine_id = "mmr:lcr6"

    const ingredient_wiht_nbt = Item.of("minecraft:stone", 1, {
        stone: "adios"
    })

    event.recipes.modular_machinery_reborn.machine_recipe(machine_id, time)
    .requireItem(ingredient_wiht_nbt, 10, 10)
    .emptyItem(10, 20)
    .produceItem("1x minecraft:oak_log", 0.1, 40, 10)
    .id("mmr:recipes/patos") //Everytime this recipe fires, it will have a ResourceLocation of mmr:recipes/patos
})