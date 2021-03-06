package gabek.engine.core.physics.shape

import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape

class RPolygon : RShape {
    var vertices: FloatArray = FloatArray(0)

    private val peel = 0.03f

    constructor() : super()

    constructor(w: Float, h: Float, x: Float = 0f, y: Float = 0f) : super() {
        setAsBox(w, h, x, y)
    }

    override fun generate(): Shape {
        val polyShape = PolygonShape()
        polyShape.set(vertices)
        //polyShape.radius = 0f

        return polyShape
    }

    override fun clone(): RShape {
        val out = RPolygon()
        out.vertices = vertices.clone()
        return out
    }

    fun setAsBox(w: Float, h: Float, x: Float, y: Float) {
        val hw = (w - peel) / 2
        val hh = (h - peel) / 2

        vertices = floatArrayOf(
                -hw + x, hh + y,
                -hw + x, -hh + y,
                hw + x, -hh + y,
                hw + x, hh + y
        )

        //deSkin(x, y)
    }

    fun withClippedCorners(w: Float, h: Float, x: Float, y: Float, clipW: Float, clipH: Float): RPolygon {
        val hw = (w - peel) / 2
        val hh = (h - peel) / 2

        val clipWScaled = clipW * (w - peel)
        val clipHScaled = clipH * (h - peel)

        vertices = floatArrayOf(
                -hw + x, hh + y,
                -hw + x, -hh + y + clipHScaled,
                -hw + x + clipWScaled, -hh + y,
                hw + x - clipWScaled, -hh + y,
                hw + x, -hh + y + clipHScaled,
                hw + x, hh + y
        )

        //deSkin(x, y)
        return this
    }
}