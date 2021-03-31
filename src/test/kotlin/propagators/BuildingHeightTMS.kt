package propagators

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import propagators.types.ClosedRangeData
import propagators.types.doubleClosedRangeProduct
import propagators.types.doubleClosedRangeQuadratic

class BuildingHeightTMS: StringSpec({

    class Fixture {
        val scheduler = QueueScheduler()

        val tmsDoubleRangeData = ClosedRangeData<Double>().tms()
        val tmsDoubleClosedRangeProduct = doubleClosedRangeProduct.tms()
        val tmsDoubleClosedRangeQuadratic = doubleClosedRangeQuadratic.tms()

        fun makeCell(name: String) =
            Cell(name, scheduler, tmsDoubleRangeData)

        fun similarTriangles(
            s1: Cell<TMS<ClosedRange<Double>>>,
            h1: Cell<TMS<ClosedRange<Double>>>,
            s2: Cell<TMS<ClosedRange<Double>>>,
            h2: Cell<TMS<ClosedRange<Double>>>
        ) {
            val ratio = makeCell("ratio")
            tmsDoubleClosedRangeProduct.apply(s1, ratio, h1)
            tmsDoubleClosedRangeProduct.apply(s2, ratio, h2)
        }

        val fallTime = makeCell("t")
        val buildingHeight = makeCell("h")

        val g = makeCell("g")
        val half = makeCell("1/2")
        val tSq = makeCell("t^2")
        val gtSq = makeCell("g.t^2")

        val barometerHeight = makeCell("barometerHeight")
        val barometerShadow = makeCell("barometerShadow")
        val buildingShadow = makeCell("buildingShadow")

        init {
            constant(9.789.rangeTo(9.832).tms(), g)
            constant(0.5.rangeTo(0.5).tms(), half)
            tmsDoubleClosedRangeQuadratic.apply(fallTime, tSq)
            tmsDoubleClosedRangeProduct.apply(g, tSq, gtSq)
            tmsDoubleClosedRangeProduct.apply(half, gtSq, buildingHeight)

            similarTriangles(barometerShadow, barometerHeight, buildingShadow, buildingHeight)

            tmsDoubleRangeData.worldView = setOf("shadows", "fall time")
        }

        fun check(
            c: Cell<TMS<ClosedRange<Double>>>,
            expectedRange: ClosedRange<Double>,
            tolerance: Double,
            vararg support: Premise
        ) {
            tmsDoubleRangeData.run {
                val result = c.content.value?.query(support.toSet())
                result?.value?.start shouldBe expectedRange.start.plusOrMinus(tolerance)
                result?.value?.endInclusive shouldBe expectedRange.endInclusive.plusOrMinus(tolerance)
                result?.premises shouldContainExactlyInAnyOrder support.toSet()
            }
        }
    }

    "estimate height using fall time" {
        Fixture().run {
            fallTime.addContent(2.9.rangeTo(3.1).tms("fall time"))
            scheduler.run()
            check(buildingHeight, 41.163.rangeTo(47.243), 0.0005, "fall time")
        }
    }

    "estimate height using shadows" {
        Fixture().run {
            buildingShadow.addContent(54.9.rangeTo(55.1).tms("shadows"))
            barometerHeight.addContent(0.3.rangeTo(0.32).tms("shadows"))
            barometerShadow.addContent(0.36.rangeTo(0.37).tms("shadows"))
            scheduler.run()
            check(buildingHeight, 44.514.rangeTo(48.978), 0.0005, "shadows")
        }
    }

    "estimate height using fall time and shadows" {
        Fixture().run {
            fallTime.addContent(2.9.rangeTo(3.1).tms("fall time"))
            buildingShadow.addContent(54.9.rangeTo(55.1).tms("shadows"))
            barometerHeight.addContent(0.3.rangeTo(0.32).tms("shadows"))
            barometerShadow.addContent(0.36.rangeTo(0.37).tms("shadows"))
            scheduler.run()
            check(buildingHeight, 44.514.rangeTo(47.243), 0.0005, "fall time", "shadows")
        }
    }

    "a lousy fall time does not improve the answer so does not contribute" {
        Fixture().run {
            tmsDoubleRangeData.worldView = setOf("lousy fall time", "shadows")
            fallTime.addContent(2.9.rangeTo(3.3).tms("lousy fall time"))
            buildingShadow.addContent(54.9.rangeTo(55.1).tms("shadows"))
            barometerHeight.addContent(0.3.rangeTo(0.32).tms("shadows"))
            barometerShadow.addContent(0.36.rangeTo(0.37).tms("shadows"))
            scheduler.run()
            check(buildingHeight, 44.514.rangeTo(48.978), 0.0005, "shadows")
        }
    }

    "higher quality information supercedes lower quality" {
        Fixture().run {
            buildingShadow.addContent(54.9.rangeTo(55.1).tms("shadows"))
            barometerHeight.addContent(0.3.rangeTo(0.32).tms("shadows"))
            barometerShadow.addContent(0.36.rangeTo(0.37).tms("shadows"))
            buildingHeight.addContent(45.0.rangeTo(45.0).tms("superintendent"))
            check(buildingHeight, 45.0.rangeTo(45.0), 0.0005, "superintendent")
        }
    }
})
