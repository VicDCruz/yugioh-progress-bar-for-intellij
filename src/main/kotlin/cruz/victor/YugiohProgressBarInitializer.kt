package cruz.victor

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import javax.swing.JFrame
import javax.swing.JProgressBar
import javax.swing.UIManager

class YugiohProgressBarInitializer : ProjectActivity {
    override suspend fun execute(project: Project) {
        updateProgressBarUi()
        invokeLater { showTestWindow() }
    }

    private fun updateProgressBarUi() {
        UIManager.put("ProgressBar.ui", YugiohProgressBarUI::class.java.name)
        UIManager.getDefaults()[YugiohProgressBarUI::class.java.name] = YugiohProgressBarUI::class.java
    }

    private fun showTestWindow() {
        val frame = JFrame("YuGiOh Progress Bar - Test")
        val pb = JProgressBar()
        pb.setUI(YugiohProgressBarUI.createUI(pb))
        pb.isIndeterminate = true
        frame.add(pb)
        frame.setSize(400, 60)
        frame.isVisible = true
    }
}