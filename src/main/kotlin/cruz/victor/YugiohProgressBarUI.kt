@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE", "UseJBColor")

package cruz.victor

import com.intellij.openapi.util.ScalableIcon
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.util.ui.GraphicsUtil
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import sun.swing.SwingUtilities2
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.LinearGradientPaint
import javax.swing.Timer
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.SwingConstants
import javax.swing.plaf.basic.BasicProgressBarUI

private val ONE_OVER_FOUR = 1f / 4
private val YELLOW = Color(242, 184, 7)
private val ORANGE = Color(242, 159, 5)
private val BROWN = Color(115, 32, 2)

class YugiohProgressBarUI : BasicProgressBarUI() {
    companion object {
        @JvmStatic
        fun createUI(c: JComponent?): YugiohProgressBarUI {
            c?.border = JBUI.Borders.empty().asUIResource()
            return YugiohProgressBarUI()
        }
    }

    override fun getPreferredSize(c: JComponent?): Dimension? {
        return Dimension(super.getPreferredSize(c).width, JBUI.scale(20))
    }

    override fun installListeners() {
        super.installListeners()
        repaintTimer = Timer(16) { progressBar.repaint() }.also { it.start() }
    }

    override fun uninstallListeners() {
        super.uninstallListeners()
        repaintTimer?.stop()
        repaintTimer = null
    }

    private var repaintTimer: Timer? = null

    @Volatile
    private var offset = 0

    @Volatile
    private var offset2 = 0

    @Volatile
    private var velocity = 1
    override fun paintIndeterminate(g2d: Graphics?, c: JComponent) {
        if (g2d !is Graphics2D) return

        val g = g2d
        val b = progressBar.insets // area for Border
        val barRectWidth = progressBar.width - (b.right + b.left)
        val barRectHeight = progressBar.height - (b.top + b.bottom)
        if (barRectWidth <= 0 || barRectHeight <= 0) return

        // boxRect = getBox(boxRect)
        g.color = JBColor(Gray._240.withAlpha(50), Gray._128.withAlpha(50))
        val w = c.width
        var h = c.preferredSize.height
        if (!isEven(c.height - h)) h++
        val baseRainbowPaint = LinearGradientPaint(
            0f,
            JBUI.scale(2).toFloat(),
            0f,
            (h - JBUI.scale(6)).toFloat(),
            floatArrayOf(
                ONE_OVER_FOUR * 1,
                ONE_OVER_FOUR * 2,
                ONE_OVER_FOUR * 3,
                ONE_OVER_FOUR * 4,
            ),
            arrayOf(
                BROWN, YELLOW, YELLOW, Color.RED
            )
        )

        g.paint = baseRainbowPaint
        if (c.isOpaque) {
            g.fillRect(0, (c.height - h) / 2, w, h)
        }

        g.color = JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50))
        val config = GraphicsUtil.setupAAPainting(g)
        g.translate(0, (c.height - h) / 2)
        val x = -offset

        val old = g.paint
        g.paint = baseRainbowPaint
        val R = JBUI.scale(8).toFloat()
        val R2 = JBUI.scale(9).toFloat()
        val containRoundRect = Area(RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, R, R))
        g.fill(containRoundRect)

        g.paint = old
        offset = (offset + 1) % getPeriodLength()
        offset2 += velocity
        if (offset2 <= 2) {
            offset2 = 2
            velocity = 1
        } else if (offset2 >= w - JBUI.scale(15)) {
            offset2 = w - JBUI.scale(15)
            velocity = -1
        }

        val area = Area(Rectangle2D.Float(0f, 0f, w.toFloat(), h.toFloat()))
        area.subtract(Area(RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, R, R)))
        g.paint = Gray._128

        if (c.isOpaque) {
            g.fill(area)
        }
        area.subtract(Area(RoundRectangle2D.Float(0f, 0f, w.toFloat(), h.toFloat(), R2, R2)))
        val parent = c.parent
        val background = if (parent != null) parent.background else UIUtil.getPanelBackground()
        g.paint = background
        //        g.setPaint(baseRainbowPaint);
        if (c.isOpaque) {
            g.fill(area)
        }

        val scaledIcon: Icon = if (velocity > 0) MillenniumPuzzleIcons.PUZZLE_ICON else MillenniumPuzzleIcons.RPUZZLE_ICON
        if (scaledIcon is ScalableIcon) {
            val icon = scaledIcon.scale(JBUI.scale(1).toFloat())
            icon.paintIcon(progressBar, g, offset2 - JBUI.scale(10), -JBUI.scale(6))
        } else {
            scaledIcon.paintIcon(progressBar, g, offset2 - JBUI.scale(10), -JBUI.scale(6))
        }
        g.draw(RoundRectangle2D.Float(1f, 1f, w - 2f - 1f, h - 2f - 2f, R, R))
        g.translate(0, -(c.height - h) / 2)

        // Deal with possible text painting
        if (progressBar.isStringPainted) {
            if (progressBar.orientation == SwingConstants.HORIZONTAL) {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width)
            } else {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height)
            }
        }
        config.restore()
    }

    private fun paintString(g: Graphics, x: Int, y: Int, w: Int, h: Int, fillStart: Int, amountFull: Int) {
        if (g !is Graphics2D) {
            return
        }
        val g2 = g
        val progressString = progressBar.string
        g2.font = progressBar.font
        var renderLocation = getStringPlacement(
            g2, progressString,
            x, y, w, h
        )
        val oldClip = g2.clipBounds
        if (progressBar.orientation == SwingConstants.HORIZONTAL) {
            g2.color = selectionBackground
            SwingUtilities2.drawString(
                progressBar, g2, progressString,
                renderLocation.x, renderLocation.y
            )
            g2.color = selectionForeground
            g2.clipRect(fillStart, y, amountFull, h)
            SwingUtilities2.drawString(
                progressBar, g2, progressString,
                renderLocation.x, renderLocation.y
            )
        } else { // VERTICAL
            g2.color = selectionBackground
            val rotate = AffineTransform.getRotateInstance(Math.PI / 2)
            g2.font = progressBar.font.deriveFont(rotate)
            renderLocation = getStringPlacement(
                g2, progressString,
                x, y, w, h
            )
            SwingUtilities2.drawString(
                progressBar, g2, progressString,
                renderLocation.x, renderLocation.y
            )
            g2.color = selectionForeground
            g2.clipRect(x, fillStart, w, amountFull)
            SwingUtilities2.drawString(
                progressBar, g2, progressString,
                renderLocation.x, renderLocation.y
            )
        }
        g2.clip = oldClip
    }

    private fun isEven(value: Int): Boolean {
        return value % 2 == 0
    }

    private fun getPeriodLength(): Int {
        return JBUI.scale(16)
    }
}
