@file:Suppress("unused")

package ltd.zake.YakumoChatBot

import com.google.auto.service.AutoService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ltd.zake.YakumoChatBot.MyPluginMain.YCDate.signList
import ltd.zake.YakumoChatBot.tool.SqlStorage
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.PluginDataExtensions.mapKeys
import net.mamoe.mirai.console.data.PluginDataExtensions.withEmptyDefault
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
import java.text.SimpleDateFormat
import kotlin.collections.set


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
object MyPluginMain : KotlinPlugin(
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

    val bot = Bot(YCSetting.qqID, YCSetting.password)

    fun regMonSignUser(id: Long, days: Int?) {
        var signMap = YCDate.monSignDays
        if (!signMap.containsKey(id)) {
            signMap[id] = 1
        } else if (signMap.containsKey(id)) {
            if (days != null) {
                signMap[id] = days + 1
            }
        }
    }


    fun regAllSignUser(id: Long, days: Int?) {
        var signMap = YCDate.allSignDays
        if (!signMap.containsKey(id)) {
            signMap[id] = 1
        } else if (signMap.containsKey(id)) {
            if (days != null) {
                signMap[id] = days + 1
            }
        }
    }

    fun cleanMonSign() {
        val nowTime = SimpleDateFormat("ddHHmm").format(System.currentTimeMillis())
        if (nowTime == "010005") {
            YCDate.monSignDays.clear()
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
        YCDate.reload()
        YCExploreDate.reload()
        YCCommand.reload()
//=====================================================================================================================
        Class.forName(DriveName)//加载驱动,连接sqlite的jdbc
        val historyConn: Connection =
            DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Public\\Documents\\botDate.db3")
//======================================================================================================================
        logger.info { "Hi: ${YCSetting.name}" }
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
        MyPluginMain.launch {
            while (true) {
                cleanMonSign()
                coldDown()
            }
        }

        //YCMainCommand.register();

        YCSetting.count++ // 对 Setting 的改动会自动在合适的时间保存

        subscribeGroupMessages {
            startsWith(YCCommand.help, removePrefix = true) {
                reply(
                    ("这里是${YCSetting.botname}bot哦! \n功能列表:" +
                            "\n1.对我说“<时间>分钟/小时后提醒我做<要做的事>”，可以创建提醒" +
                            "\n2. .r<骰子数：不超过100>d<面数：不超过1000>").trimMargin()
                )
            }
            Regex("\\d+分钟后提醒我.*?") matching {
                try {
                    val time = it.split("分钟后提醒我")[0].toInt()
                    val things = it.split("分钟后提醒我")[1]
                    reply(At(sender as Member) + "提醒创建成功了")
                    MyPluginMain.launch {
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
                    MyPluginMain.launch {
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
                        reply("唔，${YCSetting.botname}要被骰子淹没啦！")
                    } else if (fas > 1000) {
                        reply("${YCSetting.botname}数不清有多少面啦")
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
                val user = sender.id
                if (!(user in signList)) {
                    regAllSignUser(user, YCDate.allSignDays[user])
                    regMonSignUser(user, YCDate.monSignDays[user])
                    val monNowDays = YCDate.monSignDays[user]
                    val allNowDays = YCDate.allSignDays[user]
                    signList.add(user)
                    reply("@${sender.nameCard}\n签到成功！\n累计签到:${allNowDays}\n本月签到:${monNowDays}")
                } else {
                    reply("@${sender.nameCard}你今天已经签到过了！")
                }
            }
//===================================================================================================================
// 文游部分
            startsWith(YCCommand.logon, removePrefix = true) {
                val statement = historyConn.createStatement()
                val rSet = Sql.readSqlDate(statement, "Id", "playerDate")
                while (rSet.next()) {
                    YCDate.playerCanLogon.add(rSet.getLong(1))
                }
                if (sender.id in YCDate.playerCanLogon) {
                    reply(" 你已经注册过了！")
                    statement.close()
                } else {
                    if (it.length > 20) {
                        reply("你的昵称太长啦")
                    } else {
                        //statement.executeUpdate("INSERT  INTO playerDate VALUES (${sender.id}, 20, 0, 1, 0)")
                        Sql.writeSqlDate(statement, "playerDate", "${sender.id}, 20, 0, 1, 0")
                        logger.info("register player ${sender.id},niki${it}")
                        statement.close()
                    }
                }

            }
            startsWith(YCCommand.explore, removePrefix = true) {
                val time = YCSetting.coldDown
                if (!(sender.id in MyPluginMain.YCDate.canExplore)) {
                    reply("开始探索了！预计需要${time}分钟！")
                    MyPluginMain.launch {
                        MyPluginMain.YCDate.canExplore.add(sender.id)
                        delay((60 * time * 1000).toLong())
                    }
                } else {
                    reply("现在还在探索哦")
                }
            }
            startsWith(YCCommand.exploreOver, removePrefix = true) {
                val size = MyPluginMain.YCExploreDate.scene.size
                if (!(sender.id in MyPluginMain.YCDate.canExplore)) {
                    var ran = (1..size).random()
                }
            }
//===================================================================================================================

        }
    }


    // 定义插件数据
// 插件
    object YCDate : AutoSavePluginData("YCDate") {
        var list: MutableList<String> by value(mutableListOf("a", "b")) // mutableListOf("a", "b") 是初始值, 可以省略
        var long: Long by value(0L) // 允许 var
        var int by value(0) // 可以使用类型推断, 但更推荐使用 `var long: Long by value(0)` 这种定义方式.
        val Map: MutableMap<Int, String> by value()
        var signList: MutableList<Long> by value()
        var canExplore: MutableList<Long> by value()


        // 带默认值的非空 map.
        // notnullMap[1] 的返回值总是非 null 的 MutableMap<Int, String>
        var notnullMap
                by value<MutableMap<Int, MutableMap<Int, String>>>().withEmptyDefault()

        // 可将 MutableMap<Long, Long> 映射到 MutableMap<Bot, Long>.
        val botToLongMap: MutableMap<Bot, Long> by value<MutableMap<Long, Long>>().mapKeys(Bot::getInstance, Bot::id)
        val monSignDays: MutableMap<Long, Int?> by value()
        val allSignDays: MutableMap<Long, Int?> by value()
        val playerCanLogon: MutableList<Long> by value()
    }

    object YCExploreDate : AutoSavePluginData("YCExploreDate") {
        val scene: MutableList<String> by value()
    }


    // 定义一个配置. 所有属性都会被追踪修改, 并自动保存.
// 配置是插件与用户交互的接口, 但不能用来保存插件的数据.
    object YCSetting : AutoSavePluginConfig("YCSetting") {
        val name by value("test")
        val botname by value("莉莉白")
        var count by value(0)
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

    }

    /*object YCMainCommand : SimpleCommand(
        MyPluginMain, "YC",
        description = "YC主指令"
    ) {
        fun CommandSender.cleanSignColdDown(){
            signList = mutableListOf()
        }
    }*/
}
