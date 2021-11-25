package autobc.util

import autobc.bot.Bot
import autobc.jna.User32
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinNT.PROCESS_QUERY_INFORMATION
import com.sun.jna.platform.win32.WinNT.PROCESS_VM_READ
import com.sun.jna.ptr.IntByReference


object HwndUtil {

    @JvmStatic
    fun getForegroundWindowProcessName(): String = getWindowProcessName(User32.GetForegroundWindow())

    @JvmStatic
    fun getWindowProcessName(window: Long): String {
        val process: WinNT.HANDLE? =
            Kernel32.INSTANCE.OpenProcess(PROCESS_QUERY_INFORMATION or PROCESS_VM_READ, false, getWindowPID(window))
        val maxSize = 1024
        val buffer = CharArray(maxSize)
        Psapi.GetModuleBaseNameW(process!!.pointer, null, buffer, maxSize)
        return Native.toString(buffer)
    }

    @JvmStatic
    fun getForegroundWindowName(): String = getWindowName(User32.GetForegroundWindow())

    @JvmStatic
    fun getWindowName(hwnd: Long): String {
        val title = CharArray(User32.GetWindowTextLengthW(hwnd) + 1)
        val length: Int = User32.GetWindowTextW(hwnd, title, title.size)
        return Native.toString(title.copyOfRange(0, length))
    }

    @JvmStatic
    fun getForegroundWindowPID(): Int = getWindowPID(User32.GetForegroundWindow())

    @JvmStatic
    fun getWindowPID(window: Long): Int {
        val pid = IntByReference()
        User32.GetWindowThreadProcessId(window, pid)
        return pid.value
    }

    @JvmStatic
    fun isWindowInForeground(targetWindow: Long): Boolean =
        User32.GetForegroundWindow() == targetWindow

    fun getWindowByWindowName(windowName: String): Long {
        User32.EnumWindows(GetWindowCallback())
        return GetWindowCallback.hwnd.hwnd
    }

    fun setForegroundWindow(hwnd: Long) {
        User32.SetForegroundWindow(hwnd)
    }

}

private object Psapi {
    external fun GetModuleBaseNameW(hProcess: Pointer?, hmodule: Pointer?, lpBaseName: CharArray?, size: Int): Int

    init {
        Native.register("psapi")
    }
}

class GetWindowCallback : User32.WndEnumProc {

    object hwnd {
        @JvmStatic
        var hwnd: Long = 0L
    }

    override fun callback(hwnd: Long): Boolean {
        val windowName = HwndUtil.getWindowName(hwnd)
        if (windowName.contains(Bot.BROWSER_PAGE_NAME, true)) {
            GetWindowCallback.hwnd.hwnd = hwnd
            return false
        }
        return true
    }
}