@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE", "UseJBColor")

package cruz.victor.cruz.victor

import com.intellij.util.ui.JBUI
import java.awt.Color
import javax.swing.JComponent
import javax.swing.plaf.basic.BasicProgressBarUI

private val ONE_OVER_SEVEN = 1f / 7
private val VIOLET = Color(222, 205, 106)

class YugiohProgressBarUI : BasicProgressBarUI() {
    companion object {
        @JvmStatic
        fun createUI(c: JComponent?): YugiohProgressBarUI {
            c?.border = JBUI.Borders.empty().asUIResource()
            return YugiohProgressBarUI()
        }
    }
}
