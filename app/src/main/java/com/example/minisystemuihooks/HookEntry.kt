package com.example.minisystemuihooks

import com.example.minisystemuihooks.hooks.HideLockscreenStatusbarHook
import com.example.minisystemuihooks.hooks.HideQsCarrierHook
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookEntry : IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {

    companion object {
        fun log(t: Throwable) {
            XposedBridge.log(t)
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) = Unit

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        HideQsCarrierHook.handleLoadPackage(lpparam)
    }

    override fun handleInitPackageResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam
    ) {
        if (resparam.packageName != "com.android.systemui") return
        HideLockscreenStatusbarHook.handleInitPackageResources(resparam)
    }
}