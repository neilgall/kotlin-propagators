package propagators

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import propagators.types.DoubleData
import propagators.types.product
import propagators.types.sum

class TemperatureExample: StringSpec({

    fun Scheduler.fahrenheitCelcius(): Pair<Cell<Double>, Cell<Double>> {
        fun makeCell(name: String): Cell<Double> = Cell(name, this, DoubleData)
        val f = makeCell("f")
        val c = makeCell("c")
        val thirtyTwo = makeCell("thirtyTwo")
        val fMinus32 = makeCell("f-32")
        val cTimes9 = makeCell("c*9")
        val five = makeCell("five")
        val nine = makeCell("nine")
        constant(32.0, thirtyTwo)
        constant(5.0, five)
        constant(9.0, nine)
        Double.sum(thirtyTwo, fMinus32, f)
        Double.product(fMinus32, five, cTimes9)
        Double.product(c, nine, cTimes9)
        return Pair(f, c)
    }

    "Fahrenheit to Celsius" {
        QueueScheduler().run {
            val (f, c) = fahrenheitCelcius()
            f.addContent(88.0)
            run()
            c.content should beInstanceOf<Content.Value<Double>>()
            (c.content as Content.Value<Double>).value shouldBe 31.1111.plusOrMinus(0.0001)
        }
    }

    "Celsius to Fahrenheit" {
        QueueScheduler().run {
            val (f, c) = fahrenheitCelcius()
            c.addContent(25.0)
            run()
            f.content should beInstanceOf<Content.Value<Double>>()
            (f.content as Content.Value<Double>).value shouldBe 77.0.plusOrMinus(0.0001)
        }
    }
})
