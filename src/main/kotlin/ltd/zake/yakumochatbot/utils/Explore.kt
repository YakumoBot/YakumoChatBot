package ltd.zake.yakumochatbot.utils

import ltd.zake.yakumochatbot.YCPluginMain
import ltd.zake.yakumochatbot.YCPluginMain.YCExploreSet.scene
import ltd.zake.yakumochatbot.YCPluginMain.YCExploreSet.sceneItem1
import ltd.zake.yakumochatbot.YCPluginMain.YCExploreSet.sceneItem2
import ltd.zake.yakumochatbot.YCPluginMain.YCExploreSet.scenesNumber
import ltd.zake.yakumochatbot.YCPluginMain.YCItems.itemList

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