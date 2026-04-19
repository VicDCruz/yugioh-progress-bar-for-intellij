package cruz.victor

import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import cruz.victor.cruz.victor.YugiohProgressBarUI
import javax.swing.UIManager

class YugiohApplicationComponent : Disposable {
    init {
        ApplicationManager.getApplication().messageBus.connect(this)
            .subscribe(LafManagerListener.TOPIC, LafManagerListener { updateProgressBarUi() })
        updateProgressBarUi()
    }

    private fun updateProgressBarUi() {
        UIManager.put("ProgressBar.ui", YugiohProgressBarUI::class.java.name)
        UIManager.getDefaults()[YugiohProgressBarUI::class.java.name] = YugiohProgressBarUI::class.java
    }

    override fun dispose() = Unit
}