package gabek.sm2.leveltemplete

import com.badlogic.gdx.utils.JsonValue

/**
 * @author Gabriel Keith
 */
interface TemplateOperation{
    fun preform(entity: Int, json: JsonValue)
}