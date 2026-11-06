//If you need more information you can check the wiki
//https://wikis.degrassi.es/docs/modular-machinery-reborn

/*
Using the machine from basic_structure.js, we can change it
to make it useful

For example, we would need to add hatches and buses for items
and fluids
*/
MMREvents.machines(event => {
    event.create("mmr:lcr3")
    .structure(
        MMRStructureBuilder.create()
        .pattern([
            ["baa","aaa","aac"],
            ["ama","a a","aaa"],
            ["aaa","aaa","aaa"]
        ])
        .keys({
            "a":["modular_machinery_reborn:casing_plain"],
            //it can only be a tiny input bus and a tiny output bus
            "b":["modular_machinery_reborn:inputbus_tiny"],
            "c":["modular_machinery_reborn:outputbus_tiny"]
        })
    )
})

/*
What if we dont want to hardcode a particular hatch or bus?
You can use tags!
*/
MMREvents.machines(event => {
    event.create("mmr:lcr4")
    .structure(
        MMRStructureBuilder.create()
        .pattern([
            ["baa","aaa","aac"],
            ["ama","a a","aaa"],
            ["aaa","aaa","aaa"]
        ])
        .keys({
            "a":["modular_machinery_reborn:casing_plain"],
            "b":["#modular_machinery_reborn:inputbus"], //allows any input bus
            "c":["#modular_machinery_reborn:outputbus"] //allows any output bus
        })
    )
})

/*
Okay, but what if we want to allow the user to choose
where to put them?

Well, keys (a, b, c, m) are a list of blocks, so you can 
just add more blocks to the list
*/
MMREvents.machines(event => {
    event.create("mmr:lcr5")
    .structure(
        MMRStructureBuilder.create()
        .pattern([
            ["baa","aaa","aab"],
            ["ama","a a","aaa"],
            ["aaa","aaa","aaa"]
        ])
        .keys({
            "a":["modular_machinery_reborn:casing_plain"],
            "b":[
                "#modular_machinery_reborn:inputbus",
                "#modular_machinery_reborn:outputbus"
            ], //allows any input bus and output bus on b
        })
    )
})

/*
Imagine that you want to add a slab or some block
that can change position, like a stair

Using the Structure Creator, you can do that
*/
MMREvents.machines(event => {
    event.create("mmr:lcr6")
    .structure(
        MMRStructureBuilder.create()
        .pattern([
            ["baa","aaa","aab"],
            ["ama","a a","aaa"],
            ["aaa","aaa","aaa"]
        ])
        .keys({
            "a":[
                //allows the user to choose from the casing
                //or a slab that is on the bottom side of a block
                "modular_machinery_reborn:casing_plain",
                "minecraft:deepslate_brick_slab[type=bottom]"
            ],
            "b":[
                "#modular_machinery_reborn:inputbus",
                "#modular_machinery_reborn:outputbus"
            ], //allows any input bus and output bus
        })
    )
})

/*
Another important things are the color and name
Color is a bit tricky since it follows this pattern #aarrggbb,
where a is alpha or how bright and a rgb color, all in hexadecimal
Normally, choose the color you want and add ff (full brightness)
at the beginning

For name, it's a lot more simplier, just put the name that
you want the user to see.
You can also use a language file, but you will need to add it
with datapacks or kubejs
*/
MMREvents.machines(event => {
    event.create("mmr:lcr7")
    .color("#ff0080ff") //Color with format #ffrrggbb in hexadecimal
    .name("Large Chemical Reactor") //or .color("namespace.machine.name") to use the lang file
    .structure(
        MMRStructureBuilder.create()
        .pattern([
            ["aaa","aaa","aaa"],
            ["ama","a a","aaa"],
            ["aaa","aaa","aaa"]
        ])
        .keys({
            "a":["modular_machinery_reborn:casing_plain"]
        })
    )
})

/*
You can also say "I want at max 2 hatches but I want at least 50 casing"
Well, you can do that too!

You can use a block or a tag:
min_range, max_range and amount are numbers, from 1 to higher

//Range of allowed blocks
.addMinMaxBlock("<block or tag>", min_range, max_range)

//Minimun amount of blocks
.addMinBlock("<block or tag>", min_range)

//Maximum amount of blocks
.addMaxBlock("<block or tag>", max_range)

//An exact number of block 
.addExactBlock("<block or tag>", amount)

*/

MMREvents.machines(event => {
    event.create("mmr:lcr8")
    .color("#ff0080ff") //Color with format #ffrrggbb in hexadecimal
    .name("Large Chemical Reactor") //or .color("namespace.machine.name") to use the lang file
    .structure(
        MMRStructureBuilder.create()
        .pattern([
            ["aaa","aaa","aaa"],
            ["ama","a a","aaa"],
            ["aaa","aaa","aaa"]
        ])
        .keys({
            "a":["modular_machinery_reborn:casing_plain"]
        })
        .addMinBlock("minecraft:stone", 1)
        //Those requirements goes on the structure builder
    )
})

/*
Or maybe you want to have a custom model for the controller?

modelLocation can be ControllerModel.of("modid:blockid")
status can be the following values: "MISSING_STRUCTURE", "IDLE", "RUNNING", "ERRORED", "PAUSED"
Can be in lowercase

.controllerModel(modelLocation)

.controllerModel(status, modelLocation)
*/
MMREvents.machines(event => {
    event.create("mmr:lcr9")
    .color("#ff0080ff") //Color with format #ffrrggbb in hexadecimal
    .name("Large Chemical Reactor") //or .color("namespace.machine.name") to use the lang file
    .structure(
        MMRStructureBuilder.create()
        .pattern([
            ["aaa","aaa","aaa"],
            ["ama","a a","aaa"],
            ["aaa","aaa","aaa"]
        ])
        .keys({
            "a":["modular_machinery_reborn:casing_plain"]
        })
    )
    .controllerModel(ControllerModel.of("immersiveengineering:refinery"))
})
