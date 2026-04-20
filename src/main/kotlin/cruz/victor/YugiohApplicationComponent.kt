package cruz.victor

import com.intellij.ide.ui.LafManagerListener
import javax.swing.JFrame
import javax.swing.JProgressBar
import javax.swing.UIManager

class YugiohApplicationComponent : LafManagerListener {
    override fun lookAndFeelChanged(source: com.intellij.ide.ui.LafManager) {
        updateProgressBarUi()
    }

    private fun updateProgressBarUi() {
        UIManager.put("ProgressBar.ui", YugiohProgressBarUI::class.java.name)
        UIManager.getDefaults()[YugiohProgressBarUI::class.java.name] = YugiohProgressBarUI::class.java

        val frame = JFrame("Prueba Yu-Gi-Oh!")
        val pb = JProgressBar()
        pb.isIndeterminate = true // ESTO activa paintIndeterminate
        frame.add(pb)
        frame.pack()
        frame.isVisible = true
    }
}