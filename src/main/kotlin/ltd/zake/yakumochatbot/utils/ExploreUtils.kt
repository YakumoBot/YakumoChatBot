package ltd.zake.yakumochatbot.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ltd.zake.yakumochatbot.YCPluginMain
import ltd.zake.yakumochatbot.YCPluginMain.YCListData.deadList
import java.sql.Statement

class ExploreUtils {
    val Sql = SqlDao()

    /**幂运算util
     * @param base 底数
     * @param powerRaised 指数*/
    fun power(base: Int, powerRaised: Int): Int {
        if (powerRaised != 0)
            return base * power(base, powerRaised - 1)
        else
            return 1
    }

    /**玩家最大经验值
     * 计算玩家当前等级可拥有的最大经验值
     * @param statement java.sql.Statement
     * @param id 玩家QQID
     * @return 当前最大经验*/
    fun maxExp(statement: Statement, Id: Long): Int {
        val level = Sql.readSqlData(statement, "Level", "playerData", "Id = ${Id}").getInt(1)
        val maxExp: Int = (level * 16) + power(level, 2) + 33
        return maxExp
    }

    /**实现玩家升级
     * @param statement java.sql.Statement
     * @param add 获得的经验
     * @param id 玩家QQID
     * @return 回复的文本*/
    fun levelUp(statement: Statement, add: Int, Id: Long): String {
        val Level = Sql.readSqlData(statement, "Level", "playerData", "Id = ${Id}").getInt(1)
        val nowExp = Sql.readSqlData(statement, "Exp", "playerData", "Id = ${Id}").getInt(1) + add
        val nowHealth = Sql.readSqlData(statement, "Health", "playerData", "Id = ${Id}").getInt(1)
        val nowAttack = Sql.readSqlData(statement, "Attack", "playerData", "Id = ${Id}").getInt(1)
        val maxExp = maxExp(statement, Id)
        var replay: String = ""
        while (true) {
            if (Level == 100) {
                replay = "你已经满级了哦"
                break
            } else if (nowExp >= maxExp) {
                val nowLevel = Level + 1
                val newExp = nowExp - maxExp
                Sql.updateDate(statement, "playerData", "Exp", "${newExp}", "Id = ${Id}")
                Sql.updateDate(statement, "playerData", "Level", "${nowLevel}", "Id = ${Id}")
                Sql.updateDate(statement, "playerData", "Health", "${nowHealth + 15}", "Id = ${Id}")
                Sql.updateDate(statement, "playerData", "MaxHealth", "${nowHealth + 15}", "Id = ${Id}")
                Sql.updateDate(statement, "playerData", "Attack", "${nowAttack + 10}", "Id = ${Id}")
                replay = "Level Up!"
            } else if (nowExp < maxExp) {
                Sql.updateDate(statement, "playerData", "Exp", "${nowExp}", "Id = ${Id}")
                break
            }
        }
        return replay
    }
    /**设置玩家为死亡状态
     * @param statement java.sql.Statement
     * @param id 玩家QQID*/
    fun setDead(statement: Statement, id: Long) {
        deadList.add(id)
        Sql.updateDate(statement, "playerData", "Health", "0", "Id=${id}")
        val playerList = Sql.readSqlData(statement, "playerData", "Id=${id}", true)
        while (playerList.next()) {
            if (playerList.getInt(4) == 0) {
                YCPluginMain.launch {
                    delay((60 * 5 * 1000 * 3600).toLong())
                    deadList.remove(id)
                    Sql.updateDate(statement, "playerData", "Health", "${playerList.getInt(5)}", "Id=${id}")
                }
            }
        }
    }

    fun hasDead(id: Long): Boolean {
        return id in deadList
    }

    /**战利品生成
     * @param statement java.sql.Statement
     * @param id 玩家QQID
     * @return 战利品数值数组*/
    fun makeSpoils(statement: Statement, id: Long): Pair<Int, Int> {
        val Money = 20 + (-2..8).random()
        val addExp = ((maxExp(statement, id) + 5) / 8) + (4..10).random()
        return Pair(Money, addExp)
    }

}