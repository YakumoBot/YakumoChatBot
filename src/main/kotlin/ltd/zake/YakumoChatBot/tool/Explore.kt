package ltd.zake.YakumoChatBot.tool

import ltd.zake.YakumoChatBot.YCPluginMain

//FIXME:未完成，有bug
open class Explore {
    val YCExploreDate = YCPluginMain.YCExploreData
    fun exploreOver(id: Int, QQid: Long) {
        var output: String
        when (id) {
            1 -> output = YCExploreDate.scene[id].toString()
            2 -> output = YCExploreDate.scene[id].toString()
            3 -> output = YCExploreDate.scene[id].toString()
            4 -> output = YCExploreDate.scene[id].toString()
            5 -> output = YCExploreDate.scene[id].toString()
            6 -> output = YCExploreDate.scene[id].toString()
        }
        //return output
    }
}