package propagators

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beInstanceOf
import propagators.types.ClosedRangeData
import propagators.types.doubleClosedRangeProduct
import propagators.types.doubleClosedRangeQuadratic

class BuildingHeightExample: StringSpec({

    fun Scheduler.makeDoubleRangeCell(name: String) =
        Cell(name, this, ClosedRangeData<Double>())

    fun Scheduler.similarTriangles(
        s1: Cell<ClosedRange<Double>>,
        h1: Cell<ClosedRange<Double>>,
        s2: Cell<ClosedRange<Double>>,
        h2: Cell<ClosedRange<Double>>) {
        val ratio = makeDoubleRangeCell("ratio")
        doubleClosedRangeProduct.apply(s1, ratio, h1)
        doubleClosedRangeProduct.apply(s2, ratio, h2)
    }

    class Fixture {
        val scheduler = QueueScheduler()

        val fallTime = scheduler.makeDoubleRangeCell("t")
        val buildingHeight = scheduler.makeDoubleRangeCell("h")

        val g = scheduler.makeDoubleRangeCell("g")
        val half = scheduler.makeDoubleRangeCell("1/2")
        val tSq = scheduler.makeDoubleRangeCell("t^2")
        val gtSq = scheduler.makeDoubleRangeCell("g.t^2")

        val barometerHeight = scheduler.makeDoubleRangeCell("barometerHeight")
        val barometerShadow = scheduler.makeDoubleRangeCell("barometerShadow")
        val buildingShadow = scheduler.makeDoubleRangeCell("buildingShadow")

        init {
            constant(9.789.rangeTo(9.832), g)
            constant(0.5.rangeTo(0.5), half)
            doubleClosedRangeQuadratic.apply(fallTime, tSq)
            doubleClosedRangeProduct.apply(g, tSq, gtSq)
            doubleClosedRangeProduct.apply(half, gtSq, buildingHeight)

            scheduler.similarTriangles(barometerShadow, barometerHeight, buildingShadow, buildingHeight)
        }
    }

    fun check(c: Cell<ClosedRange<Double>>, expectedRange: ClosedRange<Double>, tolerance: Double) {
        c.content should beInstanceOf<Content.Value<Double>>()
        with(c.content) {
            value?.start shouldBe expectedRange.start.plusOrMinus(tolerance)
            value?.endInclusive shouldBe expectedRange.endInclusive.plusOrMinus(tolerance)
        }
    }

    "estimate height using fall time" {
        Fixture().run {
            fallTime.addContent(2.9.rangeTo(3.1))
            scheduler.run()
            check(buildingHeight, 41.163.rangeTo(47.243), 0.0005)
        }
    }

    "estimate height using shadows" {
        Fixture().run {
            buildingShadow.addContent(54.9.rangeTo(55.1))
            barometerHeight.addContent(0.3.rangeTo(0.32))
            barometerShadow.addContent(0.36.rangeTo(0.37))
            scheduler.run()
            check(buildingHeight, 44.514.rangeTo(48.978), 0.0005)
        }
    }

    "estimate height using fall time and shadows" {
        Fixture().run {
            fallTime.addContent(2.9.rangeTo(3.1))
            buildingShadow.addContent(54.9.rangeTo(55.1))
            barometerHeight.addContent(0.3.rangeTo(0.32))
            barometerShadow.addContent(0.36.rangeTo(0.37))
            scheduler.run()
            check(buildingHeight, 44.514.rangeTo(47.243), 0.0005)
        }
    }
})
