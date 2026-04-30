package com.example.minisystemuihooks

import com.example.minisystemuihooks.hooks.StatusBarClockSizeHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookEntry : IXposedHookLoadPackage, IXposedHookZygoteInit {

    companion object {
        private const val SYSTEMUI = "com.android.systemui"

        fun log(msg: String) {
            XposedBridge.log("MiniSystemUIHooks: $msg")
        }

        fun log(t: Throwable) {
            XposedBridge.log(t)
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        log("initZygote")
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != SYSTEMUI) return

        log("handleLoadPackage: ${lpparam.packageName}")
        StatusBarClockSizeHook.handleLoadPackage(lpparam)
    }
}