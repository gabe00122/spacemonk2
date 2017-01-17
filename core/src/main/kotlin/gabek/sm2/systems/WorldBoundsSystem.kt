package gabek.sm2.systems

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import gabek.sm2.components.TranslationCom

/**
 * @author Gabriel Keith
 */
class WorldBoundsSystem: BaseEntitySystem(Aspect.all(TranslationCom::class.java)){
    val minX = -5f
    val minY = -5

    val maxX = 20f
    val maxY = 20f

    private lateinit var transMapper: ComponentMapper<TranslationCom>

    override fun processSystem() {
        val entities = entityIds

        for(i in 0 until entities.size()){
            val entity = entities[i]

            val trans = transMapper[entity]
            if(trans.x < minX || trans.y < minY || trans.x > maxX || trans.y > maxY){
                world.delete(entity)
            }
        }
    }
}