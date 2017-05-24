package gabek.spacemonk.prefab

import com.github.salomonbrys.kodein.instance
import gabek.engine.core.physics.shape.RPolygon

/**
 * @author Gabriel Keith
 */


class PlayerPrefab : gabek.engine.core.prefab.Prefab() {
    override fun define() {
        super.define()

        val assets: gabek.engine.core.assets.Assets = kodein.instance()

        val width = 0.5f
        val height = 1f
        val bodyHeight = height - width / 2f

        val runningAnim = assets.findAnimation("fred:running")
        val stillAnim = assets.findAnimation("fred:still")
        val jumpingAnim = assets.findAnimation("fred:jumping")

        //val legFactory = prefab { kodein, world ->
        //    com<ParentOfCom> { diesWithParent = true }
        //    com<TranslationCom>()
        //    com<BodyCom> {
        //        body.addFixture(RCircle(width / 2f), density = 0.5f, restitution = 0f, friction = 1f, categoryBits = filter(CHARACTER))
        //        body.bodyType = BodyDef.BodyType.DynamicBody
        //        body.setPosition(0f, -bodyHeight / 2)
        //    }
        //}.build(kodein, world)

        add<gabek.engine.core.components.common.TranslationCom>()
        add<gabek.engine.core.components.BodyCom> {
            //val bodyShape = RPolygon()
            //bodyShape.setAsBox(width, bodyHeight, 0f, 0f)
            //body.addFixture(bodyShape, density = 1f, categoryBits = filter(CHARACTER))
            val platformShape = RPolygon().withClippedCorners(width, height, 0f, 0f, width / 4, 0.25f)
            body.addFixture(platformShape, density = 1f, restitution = 0f, friction = 1f, categoryBits = gabek.engine.core.world.filter(gabek.engine.core.world.CHARACTER))

            body.bodyType = com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
            body.isFixedRotation = true
        }

        add<gabek.engine.core.components.graphics.SpriteCom> {
            setSize(width + 0.025f, height + 0.025f)
            //offsetY = -width / 4f
            layer = 2
        }

        add<gabek.engine.core.components.graphics.AnimationCom>()
        add<gabek.engine.core.components.InputCom>()
        add<gabek.engine.core.components.character.CharacterControllerCom>()
        add<gabek.engine.core.components.character.BiDirectionCom>()
        add<gabek.engine.core.components.character.CharacterMovementStateCom>()
        add<gabek.engine.core.components.character.MovementGroundContactCom> {
            platformIndex = 0
        }
        //com<MovementPhysicsWheelCom> { entity ->
        //    wheelRef = legFactory.create()
        //    parentMapper[wheelRef].parent = entity

        //    motor.isMoterEnabled = true
        //    motor.maxTorque = 5f
        //    motor.anchorAY = -bodyHeight / 2

        //    motor.bodyA = bodyMapper[entity].body
        //    motor.bodyB = bodyMapper[wheelRef].body
        //}

        add<gabek.engine.core.components.character.MovementDefinitionCom> {
            airSpeed = 3f
            groundSpeed = 3f
            airDamping = 0.1f

            jumpCooldown = 0.1f
            jumpForce = 2.5f

            this.width = width
            this.height = height
            pad = 0.01f
        }

        add<gabek.engine.core.components.character.CharacterAnimatorCom> {
            runningAnimationDef = runningAnim
            jumpingAnimationDef = jumpingAnim
            stillAnimationDef = stillAnim
        }

        //com<AbilityIndexCom>()
        add<gabek.engine.core.components.character.HealthCom> {
            value = 10f
            maximum = 10f
        }
        add<gabek.engine.core.components.graphics.HealthDisplayCom> {
            offsetY = height * 0.75f
        }
    }
}