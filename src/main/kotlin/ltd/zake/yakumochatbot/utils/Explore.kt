package ltd.zake.yakumochatbot.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ltd.zake.yakumochatbot.YCPluginMain
import ltd.zake.yakumochatbot.YCPluginMain.YCListData.deadList
import java.sql.ResultSet
import java.sql.Statement

class Explore(val statement: Statement) {
    val Sql = SqlDao()
    fun power(base: Int, powerRaised: Int): Int {
        if (powerRaised != 0)
            return base * power(base, powerRaised - 1)
        else
            return 1
    }

    fun maxExp(Id: Long): Int {
        val level = Sql.readSqlData(statement, "Level", "playerData", "Id = ${Id}").getInt(1)
        val maxExp: Int = (level * 16) + power(level, 2) + 33
        return maxExp
    }

    fun levelUp(add: Int, Id: Long): String {
        val Level = Sql.readSqlData(statement, "Level", "playerData", "Id = ${Id}").getInt(1)
        val nowExp = Sql.readSqlData(statement, "Exp", "playerData", "Id = ${Id}").getInt(1) + add
        val nowHealth = Sql.readSqlData(statement, "Health", "playerData", "Id = ${Id}").getInt(1)
        val nowAttack = Sql.readSqlData(statement, "Attack", "playerData", "Id = ${Id}").getInt(1)
        val maxExp = maxExp(Id)
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

    fun makeSpoils(statement: Statement, id: Long): List<Int> {
        val Money = 20 + (-2..8).random()
        val addExp = ((maxExp(id) + 5) / 8) + (4..10).random()
        val returnS = listOf(Money, addExp)
        return returnS
    }

    fun pvpAttack(sender: ResultSet, target: ResultSet): String {
        val senderInfo: Map<String, Int> = mapOf(
            "attack" to sender.getInt(7),
            "armor" to sender.getInt(6),
            "health" to sender.getInt(4),
            "maxHealth" to sender.getInt(5)
        )
        val targetInfo: Map<String, Int> = mapOf(
            "attack" to target.getInt(7),
            "armor" to target.getInt(6),
            "health" to target.getInt(4),
            "maxHealth" to target.getInt(5)
        )
        val targetNowHP: Int
        val senderNowHP: Int
        val str1: String
        val str2: String
        val sb = StringBuilder("pvp:\n")
        val type = (1..3).random()
        if (type == 1) {
            targetNowHP = targetInfo["health"]!! - (senderInfo["attack"]!! * 0.5).toInt()
            str1 =
                "${sender.getString(2)}对${target.getString(2)}造成了0.5倍伤害\n${target.getString(2)}的生命:${targetNowHP}(-${senderInfo["attack"]!! * 0.5})\n"
            sb.append(str1)
            Sql.updateDate(statement, "playerData", "Health", "${targetNowHP}", "Id = ${target.getLong(1)}")
        } else if (type == 2) {
            targetNowHP = (targetInfo["health"]!! - (senderInfo["attack"]!!))
            str1 =
                "${sender.getString(2)}对${target.getString(2)}造成了1倍伤害\n${target.getString(2)}的生命:${targetNowHP}(-${senderInfo["attack"]!!})\n"
            sb.append(str1)
            Sql.updateDate(statement, "playerData", "Health", "${targetNowHP}", "Id = ${target.getLong(1)}")
        } else if (type == 3) {
            targetNowHP = targetInfo["health"]!! - (senderInfo["attack"]!! * 1.5).toInt()
            str1 =
                "${sender.getString(2)}对${target.getString(2)}造成了1.5倍伤害\n${target.getString(2)}的生命:${targetNowHP}(-${senderInfo["attack"]!! * 1.5})\n"
            sb.append(str1)
            Sql.updateDate(statement, "playerData", "Health", "${targetNowHP}", "Id = ${target.getLong(1)}")
        }

        val type2 = (1..3).random()
        if (type2 == 1) {
            senderNowHP = senderInfo["health"]!! - (targetInfo["attack"]!! * 0.5).toInt()
            str2 =
                "${target.getString(2)}对${sender.getString(2)}造成了0.5倍伤害\n${sender.getString(2)}的生命:${senderNowHP}(-${targetInfo["attack"]!! * 0.5})\n"
            sb.append(str2)
            Sql.updateDate(statement, "playerData", "Health", "${senderNowHP}", "Id = ${sender.getLong(1)}")
        } else if (type2 == 2) {
            senderNowHP = (senderInfo["health"]!! - (targetInfo["attack"]!!))
            str2 =
                "${target.getString(2)}对${sender.getString(2)}造成了1倍伤害\n${sender.getString(2)}的生命:${senderNowHP}(-${targetInfo["attack"]!!})\n"
            sb.append(str2)
            Sql.updateDate(statement, "playerData", "Health", "${senderNowHP}", "Id = ${sender.getLong(1)}")
        } else if (type2 == 3) {
            senderNowHP = senderInfo["health"]!! - (targetInfo["attack"]!! * 1.5).toInt()
            str2 =
                "${target.getString(2)}对${sender.getString(2)}造成了1.5倍伤害\n${sender.getString(2)}的生命:${senderNowHP}(-${targetInfo["attack"]!! * 1.5})\n"
            sb.append(str2)
            Sql.updateDate(statement, "playerData", "Health", "${senderNowHP}", "Id = ${sender.getLong(1)}")
        }
        return sb.toString()
    }
}