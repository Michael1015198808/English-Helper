package xyz.salieri.english.type

import net.mamoe.mirai.console.command.ConsoleCommandOwner.permissionId
import net.mamoe.mirai.console.permission.PermissionService

object Comps {
    val comps: MutableList<Comp> = mutableListOf()              // 组织所有群的comp
    fun getComp(groupnum: Long): Int{                           // 判断群groupnum是否有comp
        return this.comps.indexOfFirst{it.groupnum == groupnum}
    }

    fun getOrCreatComp(groupnum: Long): Comp{
        val compIndex = getComp(groupnum)
        if(compIndex == -1){
            //create a comp
            val comp = Comp(groupnum)
            this.comps += comp
            return comp
        } else {
            return this.comps[compIndex]
        }
    }

    val BasePermission by lazy { // Lazy: Lazy 是必须的, console 不允许提前访问权限系统
        PermissionService.INSTANCE.register(permissionId("Base"), "启动单词比赛之权限")
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun mainlogic(groupnum: Long, sender: Long, msginput: String){
        var msg = msginput.trim().uppercase()
        var comp = this.getOrCreatComp(groupnum)
        if( sender == 80000000L ){
            return;
        }

        if(msg.startsWith("背单词")){
            // 背单词 <book> <times>
            if(comp.state == STATE_RUNNING)
                return;
            else if(msg.split(" ").size == 1){
                comp.intro()
            }
            else {
                comp.set(msg, groupnum)
                comp.sendMsg()
            }
        } else {
            when (msg) {
                "开始" -> {
                    if (comp.state == STATE_SLEEP) {
                        comp.msg += "还未完成设置，请通过\"背单词 <book> <times>\"命令完成设置"
                        comp.sendMsg()
                    } else if (comp.state == STATE_RUNNING) {
                        return;
                    } else {
                        comp.run()
                    }
                }
                "结束背单词" -> {
                    comp.state = STATE_STOP
                }
            }
        }


        // 输出信息


    }




}
