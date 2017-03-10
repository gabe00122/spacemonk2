package gabek.sm2.systems

import com.artemis.BaseSystem
import com.badlogic.gdx.utils.JsonValue
import gabek.sm2.leveltemplete.TemplateOperation
import gabek.sm2.physics.RBody
import gabek.sm2.tilemap.ArrayGrid
import gabek.sm2.tilemap.TileReference
import gabek.sm2.world.WorldConfig
import gabek.sm2.world.clear
import gabek.sm2.world.getSystem

/**
 * @author Gabriel Keith
 */
class LevelTemplateLoader : BaseSystem() {
    private lateinit var factoryManager: FactoryManager
    private lateinit var tileSystem: TileMapSystem
    private lateinit var box2dSystem: Box2dSystem
    private var operation = HashMap<String, TemplateOperation>()

    override fun initialize() {
        super.initialize()

        operation.put("pos", object: TemplateOperation{
            val translationSystem = world.getSystem<TranslationSystem>()

            override fun preform(entity: Int, json: JsonValue) {
                translationSystem.teleport(entity, json.getFloat("x"), json.getFloat("y"), 0f)
            }
        })
    }


    override fun processSystem() {}

    fun loadLevel(jsonValue: JsonValue, worldConfig: WorldConfig) {
        world.clear()

        for(actor in jsonValue.get("actors")){
            val entity = factoryManager.create(actor.getString("type"))
            for(fields in actor.JsonIterator()){
                operation[fields.name]?.preform(entity, fields)
            }
        }

        val definitions = tileSystem.definitions
        
        val symbolToId = mutableMapOf<String, Array<Int>>()

        val tileMapJson = jsonValue.get("tilemap")
        for(def in tileMapJson.get("def").JsonIterator()){
            symbolToId.put(def.name, arrayOf(
                    definitions.getIdByName(def.get(0).asString()),
                    definitions.getIdByName(def.get(1).asString())
                    ))
        }

        val tiles = tileMapJson.get("tiles").asStringArray()
        val width = tiles[0].length
        val height = tiles.size

        tileSystem.store()
        tileSystem.body = RBody()

        tileSystem.backgroundTiles = ArrayGrid(width, height){ x, y ->
            TileReference(symbolToId[tiles[height-y - 1].elementAt(x).toString()]!![0])
        }
        tileSystem.foregroundTiles = ArrayGrid(width, height){ x, y ->
            TileReference(symbolToId[tiles[height-y - 1].elementAt(x).toString()]!![1])
        }
        tileSystem.initPhysics()
    }
}