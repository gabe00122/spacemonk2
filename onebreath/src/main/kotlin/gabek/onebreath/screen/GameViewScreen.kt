package gabek.onebreath.screen

import com.artemis.World
import com.artemis.managers.TagManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.utils.JsonReader
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import gabek.engine.core.graphics.DirectDisplay
import gabek.engine.core.graphics.Display
import gabek.engine.core.graphics.PixelRatio
import gabek.engine.core.input.InputManager
import gabek.engine.core.screen.Screen
import gabek.onebreath.system.OneBreathLevelLoader
import gabek.engine.core.systems.InputSystem
import gabek.engine.core.systems.common.PrefabManager
import gabek.engine.core.systems.common.UpdateManager
import gabek.engine.core.systems.graphics.CameraDirectControlSystem
import gabek.engine.core.systems.graphics.CameraSystem
import gabek.engine.core.systems.graphics.CameraTrackingSystem
import gabek.engine.core.util.getSystem
import gabek.engine.core.systems.graphics.RenderManager


/**
 * @another Gabriel Keith
 * @date 5/16/2017.
 */

class GameViewScreen(kodein: Kodein): Screen() {
    val display: Display = DirectDisplay()
    val world: World = kodein.instance()

    val updateManager: UpdateManager = world.getSystem()
    val renderManager: RenderManager = world.getSystem()

    val input = kodein.instance<InputManager>().getPlayerInput(0)

    val pixelRatio: PixelRatio = kodein.instance()

    override fun update(delta: Float) {
        super.update(delta)

        updateManager.update(delta)
    }

    override fun render(batch: Batch) {
        super.render(batch)

        renderManager.render(display, batch, updateManager.progress)
    }

    override fun show() {
        super.show()

        val prefabManager: PrefabManager = world.getSystem()
        val tagManager: TagManager = world.getSystem()

        val cameraSystem: CameraSystem = world.getSystem()
        val cameraUtil: CameraDirectControlSystem = world.getSystem()
        val cameraTracking: CameraTrackingSystem = world.getSystem()

        val inputSystem: InputSystem = world.getSystem()

        val loader: OneBreathLevelLoader = world.getSystem()

        val mapJson = JsonReader().parse(Gdx.files.internal("assets/maps/test.json"))
        loader.loadLevel(mapJson)

        val player = tagManager.getEntityId("player")
        inputSystem.setInput(player, input)

        val camera = prefabManager.create("camera")
        tagManager.register("camera", camera)

        cameraSystem.setView(camera, 0f, 0f , 5f, 5f)
        cameraTracking.setTarget(camera, player)

        renderManager.clearColor.set(91/255f, 110/255f, 225/255f, 1f)

        val container = Container(display)
        container.fill()
        container.setFillParent(true)

        root.addActor(container)
    }

    override fun dispose() {

        super.dispose()
        display.dispose()
    }
}