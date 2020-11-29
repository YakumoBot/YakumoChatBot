@file:Suppress("unused")

package ltd.zake.YakumoChatBot

import com.google.auto.service.AutoService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ltd.zake.YakumoChatBot.YCPluginMain.YCData.signList
import ltd.zake.YakumoChatBot.YCPluginMain.YCSetting.botname
import ltd.zake.YakumoChatBot.YCPluginMain.YCSetting.name
import ltd.zake.YakumoChatBot.tool.SqlStorage
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.utils.info
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.text.SimpleDateFormat


/*
*
* 插件基于Su1kaYCP前辈的
* ”parseeBot“进行开发(https://github.com/Su1kaYCP/parseeBot)


██╗   ██╗ █████╗ ██╗  ██╗██╗   ██╗███╗   ███╗ ██████╗  ██████╗██╗  ██╗ █████╗ ████████╗██████╗  ██████╗ ████████╗
╚██╗ ██╔╝██╔══██╗██║ ██╔╝██║   ██║████╗ ████║██╔═══██╗██╔════╝██║  ██║██╔══██╗╚══██╔══╝██╔══██╗██╔═══██╗╚══██╔══╝
 ╚████╔╝ ███████║█████╔╝ ██║   ██║██╔████╔██║██║   ██║██║     ███████║███████║   ██║   ██████╔╝██║   ██║   ██║
  ╚██╔╝  ██╔══██║██╔═██╗ ██║   ██║██║╚██╔╝██║██║   ██║██║     ██╔══██║██╔══██║   ██║   ██╔══██╗██║   ██║   ██║
   ██║   ██║  ██║██║  ██╗╚██████╔╝██║ ╚═╝ ██║╚██████╔╝╚██████╗██║  ██║██║  ██║   ██║   ██████╔╝╚██████╔╝   ██║
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═════╝  ╚═════╝    ╚═╝


* 请遵循AGPL-3.0协议
* 你可以在:https://github.com/Zake-arias/YakumoChatBot/blob/main/LICENSE 找到该协议
* 本插件仅供学习交流之用
* 请勿将本插件用于一切商业性/非法用途
*/

val DriveName: String = "org.sqlite.JDBC"
val Sql = SqlStorage()

@AutoService(JvmPlugin::class)
object YCPluginMain : KotlinPlugin(
    JvmPluginDescription(
        "ltd.zake.YakumoChatBot",
        "1.0.1-Alphal"
    )
) {
    val PERMISSION_EXECUTE_1 by lazy {
        PermissionService.INSTANCE.register(
            permissionId("yc canUse"),
            "注册八云管理权限"
        )
    }

    fun cleanMonSign(historyConn: Connection) {
        val nowTime = SimpleDateFormat("ddHHmm").format(System.currentTimeMillis())
        if (nowTime == "010005") {
            val statement: Statement = historyConn.createStatement()
            Sql.updateDate(statement, "signMap", "MonSign", "0")
        }
    }

    fun coldDown() {
        val nowTime = SimpleDateFormat("HHmm").format(System.currentTimeMillis())
        if (nowTime == "0000") {
            signList = mutableListOf()
        }
    }

    override fun onEnable() {
        YCSetting.reload() // 从数据库自动读取配置实例
        YCData.reload()
        YCExploreData.reload()
        YCCommand.reload()
        YCInfos.reload()
//=====================================================================================================================
        Class.forName("org.sqlite.JDBC")//加载驱动,连接sqlite的jdbc
        val historyConn: Connection =
            DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Public\\Documents\\botData.db3")
        YCPluginMain.launch {
            val statement = historyConn.createStatement()
            val rSet = Sql.readSqlData(statement, "Id", "playerData")
            while (rSet.next()) {
                YCData.playerCanLogon.add(rSet.getLong(1))
            }
        }

//======================================================================================================================
        logger.info { "Hi: ${name}" }
        logger.info {
            """
██╗   ██╗ █████╗ ██╗  ██╗██╗   ██╗███╗   ███╗ ██████╗  ██████╗██╗  ██╗ █████╗ ████████╗██████╗  ██████╗ ████████╗
╚██╗ ██╔╝██╔══██╗██║ ██╔╝██║   ██║████╗ ████║██╔═══██╗██╔════╝██║  ██║██╔══██╗╚══██╔══╝██╔══██╗██╔═══██╗╚══██╔══╝
 ╚████╔╝ ███████║█████╔╝ ██║   ██║██╔████╔██║██║   ██║██║     ███████║███████║   ██║   ██████╔╝██║   ██║   ██║   
  ╚██╔╝  ██╔══██║██╔═██╗ ██║   ██║██║╚██╔╝██║██║   ██║██║     ██╔══██║██╔══██║   ██║   ██╔══██╗██║   ██║   ██║   
   ██║   ██║  ██║██║  ██╗╚██████╔╝██║ ╚═╝ ██║╚██████╔╝╚██████╗██║  ██║██║  ██║   ██║   ██████╔╝╚██████╔╝   ██║   
   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═════╝  ╚═════╝    ╚═╝   
                                                                                                                 
"""
        }
        logger.warning(
            "\n\n* 本插件仅供学习交流之用\n* 请勿将本插件用于一切商业性/非法用途\n\n"
        )// 输出一条日志.
        YCPluginMain.launch {
            while (true) {
                cleanMonSign(historyConn)
                coldDown()
            }
        }

        //YCMainCommand.register();

        subscribeGroupMessages {
            startsWith(YCCommand.help, removePrefix = true) {
                reply(
                    ("这里是${botname}bot哦! \n功能列表:" +
                            "\n1.对我说“<时间>分钟/小时后提醒我做<要做的事>”，可以创建提醒" +
                            "\n2. .r<骰子数：不超过100>d<面数：不超过1000>").trimMargin()
                )
            }
            Regex("\\d+分钟后提醒我.*?") matching {
                try {
                    val time = it.split("分钟后提醒我")[0].toInt()
                    val things = it.split("分钟后提醒我")[1]
                    reply(At(sender as Member) + "提醒创建成功了")
                    YCPluginMain.launch {
                        delay((60 * time * 1000 * 3600).toLong())
                        reply("[提醒]\n@${sender.nameCard}\n现在该\"${things}\"了哦")
                    }
                } catch (e: Exception) {
                    reply("错误")
                }
            }
            Regex("\\d+小时后提醒我.*?") matching {
                try {
                    val time = it.split("小时后提醒我")[0].toInt()
                    val things = it.split("小时后提醒我")[1]
                    reply(At(sender as Member) + "提醒创建成功了")
                    YCPluginMain.launch {
                        delay((60 * time * 1000).toLong())
                        reply("[提醒]\n@${sender.nameCard}\n现在该\"${things}\"了哦")
                    }
                } catch (e: Exception) {
                    reply("错误")
                }
            }
            startsWith(YCCommand.dice, removePrefix = true) {
                val pattern = "\\d+d\\d+".toRegex()
                if (pattern.matches(it)) {
                    var num = it.split("d")[0].toInt()
                    var fas = it.split("d")[1].toInt()
                    if (num > 100) {
                        reply("唔，${botname}要被骰子淹没啦！")
                    } else if (fas > 1000) {
                        reply("${botname}数不清有多少面啦")
                    } else if (num < 0 || fas < 0) {
                        reply("参数错误")
                    } else {
                        var result = (1..fas).random()
                        val sb = StringBuilder("${senderName}掷骰:\n${num}D${fas}=${result}")
                        if (num != 1) {
                            for (i in 1..num - 1) {
                                val randoms = (1..fas).random()
                                result += randoms
                                sb.append("+${randoms}")
                            }
                            sb.append("=${result}")
                        }
                        reply(sb.toString())
                    }
                }
            }
            startsWith(YCCommand.sign, removePrefix = true) {
                YCPluginMain.launch {
                    val user = sender.id
                    val statement0 = historyConn.createStatement()
                    val statement1 = historyConn.createStatement()
                    val CAN_SIGN = Sql.readSqlData(statement0, "count(*)", "signMap", "Id=${user}").getInt(1)
                    if (!(user in signList)) {
                        signList.add(user)
//======================================================================================================================
                        if (CAN_SIGN == 0) {
                            val value = "${user},1,1"
                            Sql.writeSqlData(statement0, "signMap", value)
                        } else if (CAN_SIGN == 1) {
                            val regAllDays = Sql.readSqlData(statement0, "signMap", "Id=${user}", true).getInt(3)
                            val regMonDays = Sql.readSqlData(statement0, "signMap", "Id=${user}", true).getInt(2)
                            val alldays = regAllDays + 1
                            Sql.updateDate(statement0, "signMap", "AllSign", "$alldays", "Id=${user}")
                            val mondays = regMonDays + 1
                            Sql.updateDate(statement0, "signMap", "MonSign", "$mondays", "Id=${user}")
                        }
//======================================================================================================================
                        val allNowDays = Sql.readSqlData(statement0, "AllSign", "signMap", "Id=${user}").getInt(1)
                        val monNowDays = Sql.readSqlData(statement0, "MonSign", "signMap", "Id=${user}").getInt(1)
                        //Money
                        val rMoney = (-2..8).random()
                        val money = 20 + rMoney
                        reply("@${sender.nameCard}\n签到成功！\n累计签到:${allNowDays}\n本月签到:${monNowDays}\n${YCExploreData.money}+${money}")
                        try {
                            val trueMoney =
                                Sql.readSqlData(statement1, "playerData", "Id=${sender.id}", true).getInt(8) + money
                            Sql.updateDate(statement1, "playerData", "Money", "${trueMoney}", "Id=${sender.id}")
                            statement0.close()
                            statement1.close()
                        } catch (e: Exception) {
                            reply("唔，你貌似还没有注册哦，今天的奖励${botname}没办法给你了哦")
                        }

                    } else {
                        reply("@${sender.nameCard}你今天已经签到过了！")
                    }
                }
            }
//===================================================================================================================
// 文游部分
            startsWith(YCCommand.logon, removePrefix = true) {
                val statement = historyConn.createStatement()
                if (sender.id in YCData.playerCanLogon) {
                    reply(" 你已经注册过了！")
                    statement.close()
                } else if (it == "") {
                    reply("你还没有输入冒险者的名字哦\n指令格式\n${YCCommand.info} <冒险者的名字>")
                } else {
                    if (it.length > 20) {
                        reply("你的昵称太长啦")
                    } else {
                        YCData.playerCanLogon.add(sender.id)
                        val value = "${sender.id}, '${it}', 20, 20, 0.0, 10, 1, 0"
                        Sql.writeSqlData(statement, "playerData", value)
                        reply("${sender.nameCard}已注册\n冒险者的名字:${it}")
                        logger.verbose("[${sender.id}]${sender.nameCard}已注册\n冒险者的名字:${it}")
                        statement.close()
                    }
                }
            }
            startsWith(YCCommand.explore, removePrefix = true) {
                val time = YCSetting.coldDown
                if (!(sender.id in YCData.canExplore)) {
                    reply("开始探索了！预计需要${time}分钟！")
                    YCPluginMain.launch {
                        YCData.canExplore.add(sender.id)
                        delay((60 * time * 1000).toLong())
                    }
                } else {
                    reply("现在还在探索哦")
                }
            }
            startsWith(YCCommand.exploreOver, removePrefix = true) {
                val size = YCExploreData.scene.size
                if (!(sender.id in YCData.canExplore)) {
                    var ran = (1..size).random()
                }
            }
            startsWith(YCCommand.info, removePrefix = true) {
                val statement = historyConn.createStatement()
                val rSet = Sql.readSqlData(statement, "playerData", "Id=${sender.id}", true)
                if (!(sender.id in YCData.playerCanLogon)) {
                    reply("你还没有注册哦!\n注册使用:${YCCommand.info} <冒险者的名称>\n冒险者的名字不要超过20个字哟")
                } else {
                    YCPluginMain.launch {
                        while (rSet.next()) {
                            reply(
                                """
                                ${sender.nameCard}的信息
                                玩家信息:
                                游戏昵称:${rSet.getString(2)}
                                等级:Lv${rSet.getInt(7)}
                                生命:${rSet.getInt(3)}/${rSet.getInt(4)}
                                攻击力:${rSet.getInt(6)}
                                护甲:${rSet.getInt(5)}
                                ${YCExploreData.money}:${rSet.getInt(8)}
                                """.trimIndent()
                            )
                        }
                        statement.close()
                    }
                }
            }
//===================================================================================================================

        }
    }

    // 定义插件数据
// 插件
    object YCData : AutoSavePluginData("YCData") {
        var list: MutableList<String> by value(mutableListOf("a", "b")) // mutableListOf("a", "b") 是初始值, 可以省略
        var long: Long by value(0L) // 允许 var
        var int by value(0) // 可以使用类型推断, 但更推荐使用 `var long: Long by value(0)` 这种定义方式.
        val Map: MutableMap<Int, String> by value()
        var signList: MutableList<Long> by value()
        var canExplore: MutableList<Long> by value()

        /* 可将 MutableMap<Long, Long> 映射到 MutableMap<Bot, Long>.
        val botToLongMap: MutableMap<Bot, Long> by value<MutableMap<Long, Long>>().mapKeys(Bot::getInstance,
            Bot::id)*/
        val playerCanLogon: MutableList<Long> by value()
    }

    object YCExploreData : AutoSavePluginData("YCExploreData") {
        val scene: MutableMap<Int, String> by value()
        val money: String by value("春点")
    }


    // 定义一个配置. 所有属性都会被追踪修改, 并自动保存.
// 配置是插件与用户交互的接口, 但不能用来保存插件的数据.
    object YCSetting : AutoSavePluginConfig("YCSetting") {
        val name by value("test")
        val botname by value("莉莉白")
        val qqID: Long by value(2351468409L)
        val password by value("pyw20040429")
        val coldDown: Int by value(10)
    }

    object YCCommand : AutoSavePluginConfig("YCCommand") {
        val dice by value(".r")
        val sign by value(".签到")
        val help by value(".help")

        val explore by value(".探索")
        val exploreOver by value(".整理探索结果")
        val logon by value(".开始冒险")
        val info by value(".info")

    }

    object YCInfos : AutoSavePluginConfig("YCInfo") {
        val newDays by value("新的一天开始啦")
    }

    /*object YCMainCommand : SimpleCommand(
        MyPluginMain, "YC",
        description = "YC主指令"
    ) {
        fun CommandSender.cleanSignColdDown(){
            signList = mutableListOf()
        }
    }*/


//TODO:添加管理指令
}
