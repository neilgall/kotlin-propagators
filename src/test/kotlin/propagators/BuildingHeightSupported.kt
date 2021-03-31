package propagators

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import propagators.types.ClosedRangeData
import propagators.types.doubleClosedRangeProduct
import propagators.types.doubleClosedRangeQuadratic

class BuildingHeightSupported: StringSpec({

    val supportedDoubleRangeData = ClosedRangeData<Double>().supported()
    val supportedDoubleClosedRangeProduct = doubleClosedRangeProduct.supported()
    val supportedDoubleClosedRangeQuadratic = doubleClosedRangeQuadratic.supported()

    fun Scheduler.makeCell(name: String) =
        Cell(name, this, supportedDoubleRangeData)

    fun Scheduler.similarTriangles(
        s1: Cell<Supported<ClosedRange<Double>>>,
        h1: Cell<Supported<ClosedRange<Double>>>,
        s2: Cell<Supported<ClosedRange<Double>>>,
        h2: Cell<Supported<ClosedRange<Double>>>) {
        val ratio = makeCell("ratio")
        supportedDoubleClosedRangeProduct.apply(s1, ratio, h1)
        supportedDoubleClosedRangeProduct.apply(s2, ratio, h2)
    }

    class Fixture {
        val scheduler = QueueScheduler()

        val fallTime = scheduler.makeCell("t")
        val buildingHeight = scheduler.makeCell("h")

        val g = scheduler.makeCell("g")
        val half = scheduler.makeCell("1/2")
        val tSq = scheduler.makeCell("t^2")
        val gtSq = scheduler.makeCell("g.t^2")

        val barometerHeight = scheduler.makeCell("barometerHeight")
        val barometerShadow = scheduler.makeCell("barometerShadow")
        val buildingShadow = scheduler.makeCell("buildingShadow")

        init {
            constant(9.789.rangeTo(9.832).supported(), g)
            constant(0.5.rangeTo(0.5).supported(), half)
            supportedDoubleClosedRangeQuadratic.apply(fallTime, tSq)
            supportedDoubleClosedRangeProduct.apply(g, tSq, gtSq)
            supportedDoubleClosedRangeProduct.apply(half, gtSq, buildingHeight)

            scheduler.similarTriangles(barometerShadow, barometerHeight, buildingShadow, buildingHeight)
        }
    }

    fun check(c: Cell<Supported<ClosedRange<Double>>>, expectedRange: ClosedRange<Double>, tolerance: Double, vararg support: Premise) {
        with (c.content) {
            value?.value?.start shouldBe expectedRange.start.plusOrMinus(tolerance)
            value?.value?.endInclusive shouldBe expectedRange.endInclusive.plusOrMinus(tolerance)
            value?.premises shouldContainExactlyInAnyOrder support.toSet()
        }
    }

    "estimate height using fall time" {
        Fixture().run {
            fallTime.addContent(2.9.rangeTo(3.1).supported("fall time"))
            scheduler.run()
            check(buildingHeight, 41.163.rangeTo(47.243), 0.0005, "fall time")
        }
    }

    "estimate height using shadows" {
        Fixture().run {
            buildingShadow.addContent(54.9.rangeTo(55.1).supported("shadows"))
            barometerHeight.addContent(0.3.rangeTo(0.32).supported("shadows"))
            barometerShadow.addContent(0.36.rangeTo(0.37).supported("shadows"))
            scheduler.run()
            check(buildingHeight, 44.514.rangeTo(48.978), 0.0005, "shadows")
        }
    }

    "estimate height using fall time and shadows" {
        Fixture().run {
            fallTime.addContent(2.9.rangeTo(3.1).supported("fall time"))
            buildingShadow.addContent(54.9.rangeTo(55.1).supported("shadows"))
            barometerHeight.addContent(0.3.rangeTo(0.32).supported("shadows"))
            barometerShadow.addContent(0.36.rangeTo(0.37).supported("shadows"))
            scheduler.run()
            check(buildingHeight, 44.514.rangeTo(47.243), 0.0005, "fall time", "shadows")
        }
    }

    "a lousy fall time does not improve the answer so does not contribute" {
        Fixture().run {
            fallTime.addContent(2.9.rangeTo(3.3).supported("lousy fall time"))
            buildingShadow.addContent(54.9.rangeTo(55.1).supported("shadows"))
            barometerHeight.addContent(0.3.rangeTo(0.32).supported("shadows"))
            barometerShadow.addContent(0.36.rangeTo(0.37).supported("shadows"))
            scheduler.run()
            check(buildingHeight, 44.514.rangeTo(48.978), 0.0005, "shadows")
        }
    }

    "higher quality information supercedes lower quality" {
        Fixture().run {
            buildingShadow.addContent(54.9.rangeTo(55.1).supported("shadows"))
            barometerHeight.addContent(0.3.rangeTo(0.32).supported("shadows"))
            barometerShadow.addContent(0.36.rangeTo(0.37).supported("shadows"))
            buildingHeight.addContent(45.0.rangeTo(45.0).supported("superintendent"))
            check(buildingHeight, 45.0.rangeTo(45.0), 0.0005, "superintendent")
        }
    }
})
