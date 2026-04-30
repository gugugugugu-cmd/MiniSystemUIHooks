package com.example.minisystemuihooks.hooks

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.example.minisystemuihooks.HookEntry
import com.example.minisystemuihooks.Prefs
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object StatusBarClockSizeHook {

    private var mClockView: TextView? = null
    private var mCenterClockView: TextView? = null
    private var mRightClockView: TextView? = null

    private var mLeftClockSize = 14
    private var mCenterClockSize = 14
    private var mRightClockSize = 14

    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence,:       : Editable) {
            setClockSize()
        }
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookCollapsedStatusBarFragment(lpparam)
        hookPhoneStatusBarViewController(lpparam)
    }

    private fun hookCollapsedStatusBarFragment(lpparam: XC_LoadPackage.LoadPackageParam) {
        val classNames = listOf(
            "com.android.systemui.statusbar.phone.CollapsedStatusBarFragment",
            "com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment"
        )

        classNames.forEach { className ->
            try {
                val clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader)
                    ?: return@forEach

                XposedHelpers.findAndHookMethod(
                    clazz,
                    "onViewCreated",
                    View::class.java,
                    Bundle::class.java,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val rootView = param.args[0] as? View ?: return

                            mClockView = findTextViewByIdName(rootView, "clock")
                            mCenterClockView = findTextViewByIdName(rootView, "center_clock")
                            mRightClockView = findTextViewByIdName(rootView, "right_clock")

                            updateClockTextSize()
                            HookEntry.log("CollapsedStatusBarFragment hooked and clock views updated")
                        }
                    }
                )

                HookEntry.log("Hooked $className#onViewCreated")
            } catch (t: Throwable) {
                HookEntry.log(t)
            }
        }
    }

    private fun hookPhoneStatusBarViewController(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val clazz = XposedHelpers.findClassIfExists(
                "com.android.systemui.statusbar.phone.PhoneStatusBarViewController",
                lpparam.classLoader
            ) ?: return

            XposedHelpers.findAndHookMethod(
                clazz,
                "onViewAttached",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        mClockView = try {
                            XposedHelpers.getObjectField(param.thisObject, "clock") as? TextView
                        } catch (_: Throwable) {
                            null
                        }
                        mCenterClockView = null
                        mRightClockView = null

                        updateClockTextSize()
                        HookEntry.log("PhoneStatusBarViewController.onViewAttached updated clock")
                    }
                }
            )

            XposedHelpers.findAndHookMethod(
                clazz,
                "onViewDetached",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        removeClockTextListener()
                    }
                }
            )

            HookEntry.log("Hooked PhoneStatusBarViewController")
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun updateClockTextSize() {
        mLeftClockSize = mClockView?.textSize?.toInt() ?: 14
        mCenterClockSize = mCenterClockView?.textSize?.toInt() ?: 14
        mRightClockSize = mRightClockView?.textSize?.toInt() ?: 14

        setClockSize()
        addClockTextListener()
    }

    private fun addClockTextListener() {
        mClockView?.removeTextChangedListener(textChangeListener)
        mCenterClockView?.removeTextChangedListener(textChangeListener)
        mRightClockView?.removeTextChangedListener(textChangeListener)

        mClockView?.addTextChangedListener(textChangeListener)
        mCenterClockView?.addTextChangedListener(textChangeListener)
        mRightClockView?.addTextChangedListener(textChangeListener)
    }

    private fun removeClockTextListener() {
        mClockView?.removeTextChangedListener(textChangeListener)
        mCenterClockView?.removeTextChangedListener(textChangeListener)
        mRightClockView?.removeTextChangedListener(textChangeListener)
    }

    private fun setClockSize() {
        val enabled = Prefs.isClockSizeEnabled()
        val customSize = Prefs.getClockSize()

        val leftClockSize = if (enabled) customSize else mLeftClockSize
        val centerClockSize = if (enabled) customSize else mCenterClockSize
        val rightClockSize = if (enabled) customSize else mRightClockSize
        val unit = if (enabled) TypedValue.COMPLEX_UNIT_SP else TypedValue.COMPLEX_UNIT_PX

        mClockView?.let {
            it.setTextSize(unit, leftClockSize.toFloat())
            if (enabled) setClockGravity(it, Gravity.LEFT or Gravity.CENTER_VERTICAL)
        }

        mCenterClockView?.let {
            it.setTextSize(unit, centerClockSize.toFloat())
            if (enabled) setClockGravity(it, Gravity.CENTER)
        }

        mRightClockView?.let {
            it.setTextSize(unit, rightClockSize.toFloat())
            if (enabled) setClockGravity(it, Gravity.RIGHT or Gravity.CENTER_VERTICAL)
        }

        HookEntry.log("setClockSize enabled=$enabled size=$customSize")
    }

    private fun setClockGravity(view: TextView, gravity: Int) {
        try {
            view.gravity = gravity
            view.requestLayout()
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun findTextViewByIdName(rootView: View, name: String): TextView? {
        return try {
            val id = rootView.resources.getIdentifier(
                name,
                "id",
                "com.android.systemui"
            )
            if (id != 0) rootView.findViewById(id) as? TextView else null
        } catch (_: Throwable) {
            null
        }
    }
}