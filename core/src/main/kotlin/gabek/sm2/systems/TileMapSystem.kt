package gabek.sm2.systems

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.World
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import gabek.sm2.assets.Assets
import gabek.sm2.physics.RBody
import gabek.sm2.physics.REdge
import gabek.sm2.physics.RFixture
import gabek.sm2.tilemap.*
import gabek.sm2.world.WALL
import gabek.sm2.world.filter

/**
 * @author Gabriel Keith
 */
class TileMapSystem(kodein: Kodein) : BaseSystem() {
    private lateinit var box2dSystem: Box2dSystem
    override fun processSystem() {}

    val assets: Assets = kodein.instance()

    val tileSize = 0.75f
    val definitions = TileDefinitions(kodein)
    var backgroundTiles: Grid<TileReference> = ArrayGrid(0, 0, { _, _ -> TileReference(0) })
    var foregroundTiles: Grid<TileReference> = ArrayGrid(0, 0, { _, _ -> TileReference(0) })

    var body = RBody()

    fun render(batch: SpriteBatch, culling: Rectangle) {
        val x1 = MathUtils.clamp(MathUtils.floor(culling.x / tileSize), 0, backgroundTiles.w)
        val x2 = MathUtils.clamp(MathUtils.ceil((culling.x + culling.width) / tileSize), 0, backgroundTiles.w)

        val y1 = MathUtils.clamp(MathUtils.floor(culling.y / tileSize), 0, backgroundTiles.h)
        val y2 = MathUtils.clamp(MathUtils.ceil((culling.y + culling.height) / tileSize), 0, backgroundTiles.h)

        for (y in y1 until y2) {
            for (x in x1 until x2) {
                drawTile(batch, backgroundTiles, x, y)
                drawTile(batch, foregroundTiles, x, y)
            }
        }
    }

    private fun drawTile(batch: SpriteBatch, grid: Grid<TileReference>, x: Int, y: Int){
        val tile = grid.get(x, y)
        val ref = definitions[tile.typeId].texture
        if(ref != null) {
            batch.draw(assets.retrieveRegion(ref), x * tileSize, y * tileSize, tileSize, tileSize)
        }
    }

    fun resize(w: Int, h: Int){

    }

    fun initPhysics() {
        for (y in 0 until backgroundTiles.h) {
            for (x in 0 until backgroundTiles.w) {
                if (definitions[backgroundTiles.get(x, y)].solid) {
                    initSolid(x, y)
                }
            }
        }

        body.initialise(box2dSystem.box2dWorld)
    }

    fun store() {
        body.store(box2dSystem.box2dWorld)
        for (y in 0 until backgroundTiles.h) {
            for (x in 0 until backgroundTiles.w) {
                backgroundTiles.get(x, y).fixtures = null
            }
        }
    }

    private fun checkSolid(x: Int, y: Int) = backgroundTiles.has(x, y) && definitions[backgroundTiles.get(x, y)].solid

    private fun RFixture.defaultSettings(x: Int, y: Int): RFixture {
        friction = 1f
        restitution = 0f
        categoryBits = filter(WALL)
        return this
    }

    private fun initSolid(x: Int, y: Int) {
        val fixtures = Array<RFixture?>(4, { null })

        val n = checkSolid(x, y + 1)
        val s = checkSolid(x, y - 1)
        val e = checkSolid(x + 1, y)
        val w = checkSolid(x - 1, y)

        if (!n || !s || !e || !w) {
            val x1 = tileSize * x
            val y1 = tileSize * y
            val x2 = x1 + tileSize
            val y2 = y1 + tileSize

            if (!n) {
                val edge = REdge()
                edge.set(x1, y2, x2, y2)
                if (w) {
                    edge.setVertex0(x1 - tileSize, y2)
                }
                if (e) {
                    edge.setVertex3(x2 + tileSize, y2)
                }
                fixtures[0] = RFixture(edge).defaultSettings(x, y)
            }

            if (!s) {
                val edge = REdge()
                edge.set(x2, y1, x1, y1)
                if (e) {
                    edge.setVertex0(x2 + tileSize, y1)
                }
                if (w) {
                    edge.setVertex3(x1 - tileSize, y1)
                }
                fixtures[1] = RFixture(edge).defaultSettings(x, y)
            }

            if (!e) {
                val edge = REdge()
                edge.set(x2, y2, x2, y1)
                if (n) {
                    edge.setVertex0(x2, y2 + tileSize)
                }
                if (s) {
                    edge.setVertex3(x2, y1 - tileSize)
                }
                fixtures[2] = RFixture(edge).defaultSettings(x, y)
            }

            if (!w) {
                val edge = REdge()
                edge.set(x1, y1, x1, y2)
                if (s) {
                    edge.setVertex0(x1, y1 - tileSize)
                }
                if (n) {
                    edge.setVertex3(x1, y2 + tileSize)
                }
                fixtures[3] = RFixture(edge).defaultSettings(x, y)
            }

            for (fixture in fixtures) {
                if (fixture != null) {
                    body.addFixture(fixture)
                }
            }
            backgroundTiles.get(x, y).fixtures = fixtures
        }
    }
}