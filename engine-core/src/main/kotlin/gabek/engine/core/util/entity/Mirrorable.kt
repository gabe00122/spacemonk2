package gabek.engine.core.util.entity

/**
 * @author Gabriel Keith
 * @date 4/20/2017
 */

interface Mirrorable<T: Mirrorable<T>> {
    fun set(other: T)
}