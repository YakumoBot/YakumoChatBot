@file:Suppress("unused")

package ltd.zake.yakumochatbot

import com.google.auto.service.AutoService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ltd.zake.yakumochatbot.YCPluginMain.YCExploreSet.botMoney
import ltd.zake.yakumochatbot.YCPluginMain.YCListData.deadList
import ltd.zake.yakumochatbot.YCPluginMain.YCListData.signList
import ltd.zake.yakumochatbot.YCPluginMain.YCSetting.botname
import ltd.zake.yakumochatbot.YCPluginMain.YCSetting.name
import ltd.zake.yakumochatbot.business.PvpAttack
import ltd.zake.yakumochatbot.utils.ExploreUtils
import ltd.zake.yakumochatbot.utils.SqlDao
import ltd.zake.yakumochatbot.utils.WeatherAPI
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.any
import net.mamoe.mirai.message.data.buildXmlMessage
import net.mamoe.mirai.message.nextMessage
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
val Sql = SqlDao()

@AutoService(JvmPlugin::class)
object YCPluginMain : KotlinPlugin(
    JvmPluginDescription(
        "ltd.zake.YakumoChatBot",
        "1.0.1-20Dec31"
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
        // 从数据库自动读取配置实例
        YCSetting.reload()
        YCListData.reload()
        YCExploreSet.reload()
        YCCommand.reload()
        YCInfos.reload()
//=====================================================================================================================
        Class.forName("org.sqlite.JDBC")//加载驱动,连接sqlite的jdbc
        val historyConn: Connection =
            DriverManager.getConnection("jdbc:sqlite:data/botData.db3")
        YCPluginMain.launch {
            val statement = historyConn.createStatement()
            val rSet = Sql.readSqlData(statement, "Id", "playerData")
            while (rSet.next()) {
                if (rSet.getLong(1) in YCListData.playerCanLogon) {
                    continue
                } else {
                    YCListData.playerCanLogon.add(rSet.getLong(1))
                }
            }
        }

//======================================================================================================================
        logger.info { "Hi: $name" }
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
                    reply(At(sender) + "提醒创建成功了")
                    YCPluginMain.launch {
                        delay((60 * time * 1000 * 3600).toLong())
                        reply(At(sender) + "[提醒]\n现在该\"${things}\"了哦")
                    }
                } catch (e: Exception) {
                    reply("错误")
                }
            }
            Regex("\\d+小时后提醒我.*?") matching {
                try {
                    val time = it.split("小时后提醒我")[0].toInt()
                    val things = it.split("小时后提醒我")[1]
                    reply(At(sender) + "提醒创建成功了")
                    YCPluginMain.launch {
                        delay((60 * time * 1000).toLong())
                        reply(At(sender) + "[提醒]\n现在该\"${things}\"了哦")
                    }
                } catch (e: Exception) {
                    reply("错误")
                }
            }
            startsWith(YCCommand.dice, removePrefix = true) {
                val pattern = "\\d+d\\d+".toRegex()
                if (pattern.matches(it)) {
                    val num = it.split("d")[0].toInt()
                    val fas = it.split("d")[1].toInt()
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
            startsWith(YCCommand.weather, removePrefix = true) {
                YCPluginMain.launch {
                    try {
                        val weather = WeatherAPI()
                        val weatherList = weather.getWeather(weather.getCityId(it))
                        val reply = """
                            ${weatherList.city}今日天气:
                            天气${weatherList.weather}
                            气温:${weatherList.temperature}
                            ${weatherList.winddirection}风
                            风力${weatherList.windpower}级
                            空气湿度${weatherList.humidity}
                            [更新时间 ${weatherList.reporttime}]
                        """.trimIndent()
                        reply(reply)
                    } catch (e: Exception) {
                        reply("[错误]${e}")
                    }


                }
            }
            startsWith(YCCommand.sign, removePrefix = true) {
                YCPluginMain.launch {
                    val user = sender.id
                    val statement0 = historyConn.createStatement()
                    val statement1 = historyConn.createStatement()
                    val explore = ExploreUtils()
                    val CAN_SIGN = Sql.readSqlData(statement0, "count(*)", "signMap", "Id=${user}").getInt(1)
                    if (user !in signList && user in YCListData.playerCanLogon) {
                        signList.add(user)
//======================================================================================================================
                        if (CAN_SIGN == 0) {
                            Sql.writeSqlData(statement0, "signMap", user, 1, 1)
                        } else if (CAN_SIGN == 1) {
                            val rSet = Sql.readSqlData(statement0, "signMap", "Id=${user}", true)
                            val regAllDays = rSet.getInt(3)
                            val regMonDays = rSet.getInt(2)
                            val alldays = regAllDays + 1
                            Sql.updateDate(statement0, "signMap", "AllSign", "$alldays", "Id=${user}")
                            val mondays = regMonDays + 1
                            Sql.updateDate(statement0, "signMap", "MonSign", "$mondays", "Id=${user}")
                        }
//======================================================================================================================
                        val allNowDays = Sql.readSqlData(statement0, "AllSign", "signMap", "Id=${user}").getInt(1)
                        val monNowDays = Sql.readSqlData(statement0, "MonSign", "signMap", "Id=${user}").getInt(1)
                        //makeSpoils
                        val (money, addExp) = explore.makeSpoils(statement1, sender.id)
                        reply(At(sender) + "\n签到成功！\n累计签到:${allNowDays}\n本月签到:${monNowDays}\n${botMoney}+${money}\n经验+${addExp}")
                        try {
                            val trueMoney =
                                Sql.readSqlData(statement1, "Money", "playerData", "Id=${sender.id}").getInt(1) + money
                            Sql.updateDate(statement1, "playerData", "Money", "$trueMoney", "Id=${sender.id}")
                            explore.levelUp(statement1, addExp, sender.id)
                            statement0.close()
                            statement1.close()
                        } catch (e: Exception) {
                            reply("唔，你貌似还没有注册哦，今天的奖励${botname}没办法给你了哦")
                        }

                    } else if (sender.id !in YCListData.playerCanLogon) {
                        reply("你还没有注册哦!\n注册使用:${YCCommand.logon} <冒险者的名称>\n冒险者的名字不要超过20个字哟")
                    } else {
                        reply(At(sender) + "你今天已经签到过了！")
                    }
                }
            }
//===================================================================================================================
// 文游部分
            startsWith(YCCommand.type, removePrefix = true) {
                when (sender.id !in deadList) {
                    true -> reply("[正常]")
                    false -> reply("[死亡]")
                }
            }
            startsWith(YCCommand.logon, removePrefix = true) {
                val statement = historyConn.createStatement()
                if (sender.id in YCListData.playerCanLogon) {
                    reply(" 你已经注册过了！")
                    statement.close()
                } else if (it == "") {
                    reply("你还没有输入冒险者的名字哦\n指令格式\n${YCCommand.logon} <冒险者的名字>")
                } else {
                    if (it.length > 20) {
                        reply("你的昵称太长啦")
                    } else {
                        YCListData.playerCanLogon.add(sender.id)
                        Sql.writeSqlData(statement, "playerData", sender.id, "\'$it\'", 0, 20, 20, 0.0, 10, 1, 0)
                        reply(At(sender) + "已注册\n冒险者的名字:${it}")
                        logger.verbose("[${sender.id}]${sender.nameCard}已注册\n冒险者的名字:${it}")
                        statement.close()
                    }
                }
            }
            startsWith(YCCommand.explore, removePrefix = true) {
                val time = YCSetting.coldDown
                if (sender.id !in YCListData.canExplore) {
                    reply("开始探索了！预计需要${time}分钟！")
                    YCPluginMain.launch {
                        YCListData.canExplore.add(sender.id)
                        delay((60 * time * 1000).toLong())
                    }
                } else {
                    reply("现在还在探索哦")
                }
            }
            startsWith(YCCommand.exploreOver, removePrefix = true) {
                val size = YCExploreSet.scene.size
                if (sender.id !in YCListData.canExplore) {
                    var ran = (1..size).random()
                }
            }
            startsWith(YCCommand.info, removePrefix = true) {
                val statement0 = historyConn.createStatement()
                val statement1 = historyConn.createStatement()
                val explore = ExploreUtils()
                val rSet = Sql.readSqlData(statement0, "playerData", "Id = ${sender.id}", true)
                if (sender.id !in YCListData.playerCanLogon) {
                    reply("你还没有注册哦!\n注册使用:${YCCommand.logon} <冒险者的名称>\n冒险者的名字不要超过20个字哟")
                } else {
                    YCPluginMain.launch {
                        while (rSet.next()) {
                            reply(At(sender) +
                                    """
                                的信息
                                玩家信息:
                                游戏昵称:${rSet.getString(2)}
                                等级:Lv${rSet.getInt(8)}
                                经验:${rSet.getInt(3)}/${explore.maxExp(statement1, sender.id)}
                                生命:${rSet.getInt(4)}/${rSet.getInt(5)}
                                攻击力:${rSet.getInt(7)}
                                护甲:${rSet.getInt(6)}
                                ${botMoney}:${rSet.getInt(9)}
                                """.trimIndent()
                            )
                        }
                        statement0.close()
                        statement1.close()
                    }
                }
            }

//======================================================================================================================
            //Regex(".pvp.*?") matching {
            startsWith(YCCommand.pvp, removePrefix = true) {
                YCPluginMain.launch {
                    if (sender.id !in YCListData.playerCanLogon) {
                        reply("你还没有注册哦!\n注册使用:${YCCommand.logon} <冒险者的名称>\n冒险者的名字不要超过20个字哟")
                    } else {

                        val statement0 = historyConn.createStatement()
                        val senderNick =
                            Sql.readSqlData(statement0, "Nick", "playerData", "Id=${sender.id}").getString(1)
                        if (sender.id in deadList) {
                            /*使用命令"#info"查看自己的复活时间吧~*/reply("${senderNick}还没有复活哦...\n可能进行不了pvp啦...")
                        } else {
                            reply("${senderNick}是否要进行友尽(x)pvp模式呢?[y/n]")
                            if (nextMessage().contentToString() == "y") {
                                reply("${senderNick}请艾特出要pvp的目标吧~")
                                val target = nextMessage { message.any(At) }[At]!!.asMember()
                                val targetNick =
                                    Sql.readSqlData(statement0, "Nick", "playerData", "Id=${target.id}").getString(1)
                                if (target.id in deadList) {
                                    reply("不能和${targetNick}pvp啦~\n他好像还没复活...")
                                } else if (target.id !in YCListData.playerCanLogon) {
                                    reply("不能和${target.nameCard}pvp啦~\n他好像还没注册哦")
                                } else if (sender.id == target.id) {
                                    reply("${senderNick}不可以伤害自己啦...")
                                } else {
                                    val pvpAttack = PvpAttack(historyConn, sender.id, target.id)
                                    val replyStr = pvpAttack.pvpAttack()
                                    reply(replyStr)
                                }
                            } else {
                                reply("${senderNick}的pvp请求已取消")
                                statement0.close()
                            }
                        }
                    }
                }
            }
//===================================================================================================================
        }
    }

    // 定义插件数据
// 插件
    object YCListData : AutoSavePluginData("YCData") {
        var signList: MutableList<Long> by value()
        var canExplore: MutableList<Long> by value()
        var deadList: MutableList<Long> by value()

        /* 可将 MutableMap<Long, Long> 映射到 MutableMap<Bot, Long>.
        val botToLongMap: MutableMap<Bot, Long> by value<MutableMap<Long, Long>>().mapKeys(Bot::getInstance,
            Bot::id)*/
        val playerCanLogon: MutableList<Long> by value()
    }

    // 定义一个配置. 所有属性都会被追踪修改, 并自动保存.
// 配置是插件与用户交互的接口, 但不能用来保存插件的数据.
    object YCSetting : AutoSavePluginConfig("YCSetting") {
        val name by value("test")
        val botname by value("莉莉白")
        val coldDown: Int by value(10)
        val weatherAppKey: String by value("")
    }

    object YCCommand : AutoSavePluginConfig("YCCommand") {
        //日常功能:
        val weather by value(".天气")

        val dice by value(".r")
        val sign by value(".签到")
        val help by value(".help")

        val explore by value(".探索")
        val exploreOver by value(".整理探索结果")
        val logon by value(".开始冒险")
        val info by value(".info")
        val type by value(".状态")
        val pvp by value(".pvp")

    }

    object YCInfos : AutoSavePluginConfig("YCInfo") {
        val newDays by value("新的一天开始啦")
    }

    object YCItems : AutoSavePluginConfig("YCItems") {
        val itemList: MutableMap<String, String> by value()
        val itemSet: MutableMap<String, String> by value()
    }

    object YCExploreSet : AutoSavePluginConfig("YCExploreSet") {
        val scenesNumber: Int by value(10)
        val scene: MutableMap<Int, String> by value()
        val sceneItem1: MutableMap<Int, String> by value()
        val sceneItem2: MutableMap<Int, String> by value()

        val botMoney: String by value("春点")
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
