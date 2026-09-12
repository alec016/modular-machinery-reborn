/*
Tooltips are a way to show your players the intended way to use your multiblock
These can range from simple examples, like "You need to add a new pickaxe for every recipe!"
to more complex one, like those "Press Ctrl+Z to show more information"

There are 4 ways of adding these texts, static and dynamic, on the controller's gui or in the item
*/

//To start you need to get a working controller first! We will be using the mmr:lcr from basic_structure.js
//More info of methods can be obtained here:
//https://github.com/alec016/modular-machinery-reborn/blob/1.21-NeoForge/src/main/java/es/degrassi/mmreborn/common/integration/kubejs/builder/ExtraTooltipsBuilderJS.java
MMREvents.extraTooltips(event => {
    event.create("mmr:lcr", 'item')
    .add("An item tooltip") //This shows on the controller itself

    event.create("mmr:lcr", 'gui')
    .add("An gui tooltip") //This shows hovering over the ? tab on the controller gui
})

//But can I only add 1 text? Nope, you can add multiple of them!
MMREvents.extraTooltips(event => {
    event.create("mmr:lcr", 'item')
    .add("An item tooltip")
    .add("And even have them with different types!")

    event.create("mmr:lcr", 'gui')
    .add("An gui tooltip")

    let builder = event.create("mmr:lcr", 'gui')

    builder.add("On a builder")

    //You can do multiple multiblocks here, not just one
})

//Some notes here:
MMREvents.extraTooltips(event => {
    event.create("mmr:lcr", 'item')
    .add("An item tooltip") //Important, if we add the same text again, it will not let you do so!
    //So if you have multiple controllers that uses the same text, consider using lang files or change the text
    .add("And even have them with different types!")

    event.create("mmr:lcr", 'gui')
    .add("An gui tooltip")
})

//But it gets better!
//You can add translatable tooltips or even colors!
MMREvents.extraTooltips(event => {
    //Needs a lang file, you can add one on kubejs/assets/<mod_id>/lang/<lang>.json
    //<mod_id> can be any mod id, for example, kubejs, modular_machinery_reborn or even a custom name folder!
    //<lang> must be a valid name like es_es.json or en_us.json
    event.create("mmr:lcr", 'item')
    .add(Component.translatable("mmr.controller.lcr.item")) //example of lang key, you can put anything as long as it's valid

    event.create("mmr:lcr", 'gui')
    .add(Component.translatable("mmr.controller.lcr.item"))
    .add(Text.of("Common API usage!")) //or use Text.of() https://github.com/KubeJS-Mods/KubeJS/blob/2101/src/main/java/dev/latvian/mods/kubejs/plugin/builtin/wrapper/TextWrapper.java
})

//And what if we need something else? Like dynamic tooltips? Well ,we can!
MMREvents.extraTooltips(event => {
    event.create("mmr:lcr", 'item')
    .add("An item tooltip")
    .addDynamic("color_coding") //The text inside can be any name, but needs to have the following next MMREvents

    event.create("mmr:lcr", 'gui')
    .add("An gui tooltip")
})

MMREvents.dynamicTooltip("color_coding", event => {
    //This event works a bit different, we need to return something with event.success!
    //Imagine that we have a list of all multiblocks, and we need to check if they have the nbt "custom_color": "Gray"
    //And then apply a tooltip saying "This multiblock needs the color Gray", we can do that!
    //Disclaimer, these kind of operations can lead to massive lag if not done proper, like not using cache or doing the operations every tick

    event.success("This multiblock needs the color " + "GRAY") //You can even use Componet or Text.of()!
})