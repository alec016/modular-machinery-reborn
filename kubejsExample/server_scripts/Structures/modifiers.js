/*
If you need more information you can check the wiki
https://wikis.degrassi.es/docs/modular-machinery-reborn
*/

/*
Now, you want to customize even more your machine?
Well, lets start with the controller
*/
MMREvents.machines(event => {
    event.create("mmr:lcr8")
    .color("#ff000000")
    .name("Large Chemical Reactor")
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
            ],
        })
    )
    /*
    In this case, we want the controller to have the Chemical Infuser
    for Mekanism

    Can be on assets/mod/model (example: minecraft:block/grass) or a blockid (modid:blockid)
    ControllerModel is accesible without adding a Java.loadClass

    There are more functions from .controllerModel on more_complex_structure.js
    */
    .controllerModel(ControllerModel.of('minecraft:stone'))
})

/*
What if we want to change the hatches and buses textures?
Well, we can!
*/
MMREvents.machines(event => {
    event.create("mmr:lcr9")
    .color("#ff000000")
    .name("Large Chemical Reactor")
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
            ],
        })
    )
    /*
    In this case, we want a tiny input bus to have a custom texture

    Can be on assets/mod/model (example: minecraft:block/grass) or a blockid (modid:blockid)
    More info for the bus and hatches here: https://wikis.degrassi.es/docs/modular-machinery-reborn/section/misc/article/texture-types
    Or you can check on the more.js on the same folder than this file

    If you have FTB quests and you can edit quests, right click on a chapter
    add image and you can check the textures or you can go to the mod repository
    to check manually
    */
    .texture("modular_machinery_reborn:item_input_bus_tiny", //this is not the same as the item
            true, //Should the texture be colored? true or false
            //If true, your texture might not show
            "mekanism:block/cardboard_box_side", //the Machine Casing part
            null //Null means "Do nothing"
    )
    .texture("modular_machinery_reborn:item_output_bus_tiny",
            false,
            null,
            "mekanism:block/cardboard_box_side" //The circle above the Machine casing
    )
})

/*
What if we want to add a sound to the machine while working?
or if there was an error?

You can also do that!
*/
MMREvents.machines(event => {
    event.create("mmr:lcr10")
    .color("#ff000000")
    .name("Large Chemical Reactor")
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
            ],
        })
    )
    /*
    There are 5 posibilities:
    "errored", "idle", "missing_structure", "paused", "running"
    */
    .sound("idle", {
        ambient: { //You can use with "" or without, since JS will convert automatically to json
            sound: "actuallyadditions:coffee_machine",
            volume: 1.0, //From 0 to MAX_VALUE of a Java float (https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Float.html)
            pitch: 1.0, // From 0 to MAX_VALUE of a Java float
            source: "BLOCKS", //Where it is played. Possible values: MASTER, MUSIC, RECORDS, WEATHER, BLOCKS, HOSTILE, NEUTRAL, PLAYERS, AMBIENT, VOICE
            loop: true, //Should the sound be on repeat?
            attenuation: true, // Should the sound be attenuated?
            delay: 0, // Should the sound have a delay? From 0 to MAX_VALUE of a Java Integer (https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Integer.html)
            relative: false // Should be relative?
        },
        "interaction": { //all are optionals, but allows for more customization
            "volume": 100, //a whole number from 0 to 100
            "pitch": 0,//a number from 0.0 to 2.0
            "break": "actuallyadditions:coffee_machine",//if you want a custom sound for breaking the machine
            "step": "actuallyadditions:coffee_machine",//if you want a custom sound when you walk on the machine
            "place": "actuallyadditions:coffee_machine",//if you want a custom sound for placing the machine
            "hit": "actuallyadditions:coffee_machine",//if you want a custom sound when you hit the machine
            "fall": "actuallyadditions:coffee_machine"//if you want a custom sound when you fall on the machine
        }
    })

    .sound("errored", {
        "ambient": "actuallyadditions:coffee_machine",
        "interaction": { //all are optionals, but allows for more customization
            "volume": 100, //a whole number from 0 to 100
            "pitch": 0,//a number from 0.0 to 2.0
            "break": "actuallyadditions:coffee_machine",//if you want a custom sound for breaking the machine
            "step": "actuallyadditions:coffee_machine",//if you want a custom sound when you walk on the machine
            "place": "actuallyadditions:coffee_machine",//if you want a custom sound for placing the machine
            "hit": "actuallyadditions:coffee_machine",//if you want a custom sound when you hit the machine
            "fall": "actuallyadditions:coffee_machine"//if you want a custom sound when you fall on the machine
        }
    })
})

/*
And the most complex stuff, Modifiers a.k.a upgrades
There blocks are optional to make the structure, but allows 
you customize them even more

For example, placing a diamond block on the center reduce speed
but a netherite one speeds the machine
These are relative to the controller
*/
MMREvents.machines(event => {
    event.create("mmr:lcr11")
    .color("#ff000000")
    .name("Large Chemical Reactor")
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
            ],
        })
    )
    .addModifier(
        MMRModifierReplacement.create()
        .ingredient("minecraft:diamond_block")
        /*
        0,0,0 being the controller
        first argument is the X (or left and right from the controller)
        second argument is Y (or top and bottom of the controller)
        third argument is Z (or how deep)

        This is more of a perspective view than anything

        In this case, we want a diamond block on positive side (axis X)
        next to the controller, that is on the same height (axis Y) and same deep (axis Z)
        */
        .position(1, 0, 0)
        .addModifier( //here we do what we want to do
            MMRRecipeModifier.create()
            //More info for target can be seen here https://wikis.degrassi.es/docs/modular-machinery-reborn/section/misc/article/recipe-modifier-target
            .target("modular_machinery_reborn:speed") //required
            .addition() //addition is by default, you can change it to .multiply()
            .modifier(0.1) //required
            //input is by default, you can change it .output() (output is required by loot_table target)
            //input is required by speed
            .input()
        )
        //You can also make it so modifiers grants more items or something
        //Play yourself with those 
    )
    .addModifier(
        MMRModifierReplacement.create()
        .ingredient("minecraft:netherite_block")
        .position(1, 0, 0)
        .addModifier(
            MMRRecipeModifier.create()
            .target("modular_machinery_reborn:speed")
            .multiply()
            .modifier(1.2)
            .min(0.1) //lowest possible number, by default has no limit
            .max(1.2) //maximum possible number, by default has no limit
            //.chance(0.5) //for other things, you can add a chance base (0.0 up to 1.0)
            //in this example, you have a 50% chance to get more item if the target was item
        )   
    )
})