package ltd.zake.YakumoChatBot.tool

import ltd.zake.YakumoChatBot.YCPluginMain
import ltd.zake.YakumoChatBot.YCPluginMain.YCExploreSet.scene
import ltd.zake.YakumoChatBot.YCPluginMain.YCExploreSet.sceneItem1
import ltd.zake.YakumoChatBot.YCPluginMain.YCExploreSet.sceneItem2
import ltd.zake.YakumoChatBot.YCPluginMain.YCExploreSet.scenesNumber
import ltd.zake.YakumoChatBot.YCPluginMain.YCItems.itemList

//FIXME:未完成，有bug
open class Explore {
    val YCExploreDate = YCPluginMain.YCExploreSet
    fun exploreOver(id: Int, QQid: Long) {
        var id = (1..scenesNumber).random()
        val str = scene[id]
        val item1 = itemList[sceneItem1[id]]
        val item2 = itemList[sceneItem2[id]]

    }
}