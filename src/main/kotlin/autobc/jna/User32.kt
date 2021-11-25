package autobc.jna

import com.sun.jna.Native
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary

object User32 {

    init {
        Native.register("user32")
    }

    interface WndEnumProc : StdCallLibrary.StdCallCallback {
        fun callback(hwnd: Long): Boolean
    }

    external fun SetWindowDisplayAffinity(hwnd: Long, dwAffinity: Long): Boolean
    external fun SetActiveWindow(hwnd: Long): Long
    external fun FindWindowA(s: String?, s1: String?): Long
    external fun GetWindowLongA(hwnd: Long, i: Int): Int
    external fun SetWindowLongA(hwnd: Long, i: Int, i1: Int): Int
    external fun SetWindowPos(hwnd: Long, hwnd1: Long, i: Int, i1: Int, i2: Int, i3: Int, i4: Int): Boolean
    external fun SetForegroundWindow(hwnd: Long): Boolean
    external fun SetFocus(hwnd: Long): Long
    external fun IsWindowVisible(hwnd: Long): Boolean
    external fun ShowWindow(hwnd: Long, i: Int): Boolean
    external fun GetWindowThreadProcessId(hwnd: Long, intByReference: IntByReference?): Int
    external fun AttachThreadInput(dword: Long, dword1: Long, b: Boolean): Boolean
    external fun EnumWindows(enumProc: WndEnumProc): Boolean
    external fun GetWindowTextW(hwnd: Long, title: CharArray, length: Int): Int
    external fun GetWindowTextLengthW(hwnd: Long): Int
    external fun GetForegroundWindow(): Long

}