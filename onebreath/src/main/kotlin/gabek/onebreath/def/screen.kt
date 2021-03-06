package gabek.onebreath.def

import com.github.salomonbrys.kodein.Kodein
import gabek.engine.core.screen.ScreenManager
import gabek.onebreath.screen.GameViewScreen

/**
 * @another Gabriel Keith
 * @date 5/16/2017.
 */

fun buildScreenManager(kodein: Kodein): ScreenManager {
    val screenManager = ScreenManager(kodein)

    screenManager.bind("GameViewScreen", { GameViewScreen(kodein) })
    screenManager.show("GameViewScreen")

    return screenManager
}