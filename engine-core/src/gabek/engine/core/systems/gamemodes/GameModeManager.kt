package gabek.engine.core.systems.gamemodes

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.utils.IntBag
import gabek.engine.core.components.InputCom

/**
 * @author Gabriel Keith
 * @date 3/28/2017
 */
class GameModeManager: BaseEntitySystem(Aspect.all(InputCom::class.java)) {

    private var onGameOver: (() -> Unit)? = null

    override fun processSystem() {}


    val playerList get() = entityIds!!

    override fun removed(entities: IntBag) {
        super.removed(entities)
        if (playerList.size() == 0) {
            onGameOver?.invoke()
        }
    }

    fun onGameOver(onGameOver: () -> Unit) {
        this.onGameOver = onGameOver
    }
}