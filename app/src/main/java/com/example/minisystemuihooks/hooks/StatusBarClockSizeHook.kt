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

    private const val FIXED_CLOCK_SIZE_SP = 21

    private var mClockView: TextView? = null
    private var mCenterClockView: TextView? = null
    private var mRightClockView: TextView? = null

    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
        }

        override fun afterTextChanged(s: Editable?) {
            setClockSizeIfEnabled()
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

                            addClockTextListener()
                            setClockSizeIfEnabled()

                            HookEntry.log("CollapsedStatusBarFragment applied fixed clock size")
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

                        addClockTextListener()
                        setClockSizeIfEnabled()

                        HookEntry.log("PhoneStatusBarViewController applied fixed clock size")
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

    private fun setClockSizeIfEnabled() {
        val enabled = Prefs.isFixedClockSizeEnabled()
        HookEntry.log("Fixed clock size enabled=$enabled")

        if (!enabled) return

        mClockView?.let { clock ->
            clock.setTextSize(TypedValue.COMPLEX_UNIT_SP, FIXED_CLOCK_SIZE_SP.toFloat())
            setClockGravity(clock, Gravity.LEFT or Gravity.CENTER_VERTICAL)
        }

        mCenterClockView?.let { clock ->
            clock.setTextSize(TypedValue.COMPLEX_UNIT_SP, FIXED_CLOCK_SIZE_SP.toFloat())
            setClockGravity(clock, Gravity.CENTER)
        }

        mRightClockView?.let { clock ->
            clock.setTextSize(TypedValue.COMPLEX_UNIT_SP, FIXED_CLOCK_SIZE_SP.toFloat())
            setClockGravity(clock, Gravity.RIGHT or Gravity.CENTER_VERTICAL)
        }

        HookEntry.log("setClockSize fixed=$FIXED_CLOCK_SIZE_SP")
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