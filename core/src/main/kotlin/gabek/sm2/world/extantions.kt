package gabek.sm2.world

import com.artemis.*
import com.artemis.managers.GroupManager
import kotlin.reflect.KClass

/**
 * @author Gabriel Keith
 */

inline fun <reified T : Component> World.getMapper(): ComponentMapper<T> {
    return getMapper(T::class.java)
}

inline fun <reified T : BaseSystem> World.getSystem(): T {
    return getSystem(T::class.java)
}

fun <T : Component> Aspect.Builder.all(vararg clazz: KClass<T>) {
    all(clazz.map { it.java })
}

fun World.clear() {
    val bag = aspectSubscriptionManager.get(Aspect.all()).entities
    for (i in 0 until bag.size()) {
        delete(bag[i])
    }
    process() //remove entities
    entityManager.reset() //reset entity ids
    process() //clean up rest
}