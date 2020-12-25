package ltd.zake.yakumochatbot.business

import ltd.zake.yakumochatbot.utils.ExploreUtils
import ltd.zake.yakumochatbot.utils.SqlDao
import java.sql.Statement

class PvpAttack(val statement: Statement, val senderId: Long, val targetId: Long) {
    val Sql = SqlDao()
    val explore = ExploreUtils()
    val senderSet = Sql.readSqlData(statement, "Nick", "playerData", "Id=${senderId}")
    val targetSet = Sql.readSqlData(statement, "Nick", "playerData", "Id=${targetId}")
    val senderName = senderSet.getString(2)
    val targetName = senderSet.getString(2)
    var senderHP = senderSet.getInt(4)
    var targetHP = targetSet.getInt(4)

    fun isDead(HP: Int): Boolean {
        return HP <= 0
    }

    /**
     * pvp伤害的计算与处理
     */
    fun pvpAttack(): String {
        val senderATK = senderSet.getInt(7)
        val targetATK = targetSet.getInt(7)
        val _replay = StringBuilder("[战斗结果]\n")
        fun dice(): Int {
            return (1..6).random()
        }

        /**不同骰子点数下的伤害
         * @param num 骰子点数
         * @param atk 基础ATK
         * @return 点数1~2:小伤害\点数2~3:中等伤害\点数5~6:大伤害*/
        fun ATK(num: Int, atk: Int): Int? {
            if (num in 1..2) {
                return atk / (2..5).random()
            } else if (num in 3..4) {
                return atk + (0..5).random()
            } else if (num in 5..6) {
                return atk + (atk / (2..5).random())
            }
            return null
        }

        /**对sender的伤害计算和字符串生成
         * @param dice 骰子点数
         * @return Pair(消息文本,最终HP)*/
        fun sender(dice: Int): Pair<String, Int> {
            val ATK = ATK(dice, targetATK)
            val atkType = when (dice) {
                in 1..2 -> "轻微"
                in 3..4 -> "中等"
                in 5..6 -> "大"
                else -> null
            }
            val HP = senderHP
            val overHP = HP - ATK!!
            val str = "r 1d6=${dice}\n${targetName}对${senderName}造成${atkType}伤害！\n${HP}-${ATK}=${overHP}"
            return Pair(str, overHP)
        }

        /**对target的伤害计算和字符串生成
         * @param dice 骰子点数
         * @return Pair(消息文本,最终HP)*/
        fun target(dice: Int): Pair<String, Int> {
            val ATK = ATK(dice, senderATK)
            val atkType = when (dice) {
                in 1..2 -> "轻微"
                in 3..4 -> "中等"
                in 5..6 -> "大"
                else -> null
            }
            val HP = targetHP
            val overHP = HP - ATK!!
            val str = "r 1d6=${dice}\n${senderName}对${targetName}造成${atkType}伤害！\n${HP}-${ATK}=${overHP}"
            return Pair(str, overHP)
        }

        fun overBuilder(round: Int, isFinalRound: Boolean): Pair<String, Boolean> {
            val sb = StringBuilder("==第${round}回合==\n")
            val (senderStr, senderOverHP) = sender(dice())
            if (senderOverHP >= 0) {
                sb.append(senderStr)
                sb.append("\n===========")
                Sql.updateDate(statement, "playerData", "Health", "${senderOverHP}", "Id=${senderId}")
                if (senderOverHP <= 0) {
                    explore.setDead(statement, senderId)
                    sb.append("${senderName}失败了,胜利者是${targetName}")
                    //TODO:战利品配发
                    return Pair(sb.toString(), false)
                } else {
                    val (targetStr, targetOverHP) = target(dice())
                    if (targetOverHP <= 0) {
                        explore.setDead(statement, targetId)
                        sb.append("${targetName}失败了,胜利者是${senderName}")
                        //TODO:战利品配发
                        return Pair(sb.toString(), false)
                    } else {
                        sb.append(targetStr)
                        Sql.updateDate(statement, "playerData", "Health", "${targetOverHP}", "Id=${targetId}")
                    }
                }
            } else if (senderOverHP <= 0) {
                explore.setDead(statement, senderId)
                sb.append("${senderName}失败了,胜利者是${targetName}")
                return Pair(sb.toString(), false)
            } else if (isFinalRound || senderHP != 0 || targetHP != 0) {
                when (senderHP > targetHP) {
                    true -> {
                        sb.append("${targetName}失败了,胜利者是${senderName}")
                        return Pair(sb.toString(), false)
                    }
                    false -> {
                        sb.append("${senderName}失败了,胜利者是${targetName}")
                        return Pair(sb.toString(), false)
                    }
                }
            }
            return Pair(sb.toString(), true)
        }
        for (i in 1..8) {
            if (i in 1..7) {
                val (replay, canBreak) = overBuilder(i, false)
                _replay.append(replay)
                if (!canBreak) {
                    break
                }
            } else if (i == 8) {
                val (replay, canBreak) = overBuilder(i, true)
                _replay.append(replay)
                if (!canBreak) {
                    break
                }
            }
            //delay(500)

        }
        return _replay.toString()
    }
}