package propagators

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import propagators.types.*

class PropagationInAnyDirection : StringSpec({

    fun Scheduler.makeIntCell(name: String) = Cell(name, this, IntData)

    "sum forward" {
        QueueScheduler().run {
            val a = makeIntCell("a")
            val b = makeIntCell("b")
            val c = makeIntCell("c")
            intSum(a, b, c)
            a.addContent(123)
            b.addContent(42)
            run()
            c.content shouldBe Content.Value(165)
        }
    }

    "sum backward lhs" {
        QueueScheduler().run {
            val a = makeIntCell("a")
            val b = makeIntCell("b")
            val c = makeIntCell("c")
            intSum(a, b, c)
            b.addContent(42)
            c.addContent(165)
            run()
            a.content shouldBe Content.Value(123)
        }
    }

    "sum backward rhs" {
        QueueScheduler().run {
            val a = makeIntCell("a")
            val b = makeIntCell("b")
            val c = makeIntCell("c")
            intSum(a, b, c)
            a.addContent(123)
            c.addContent(165)
            run()
            b.content shouldBe Content.Value(42)
        }
    }

    "product forward" {
        QueueScheduler().run {
            val a = makeIntCell("a")
            val b = makeIntCell("b")
            val c = makeIntCell("c")
            intProduct(a, b, c)
            a.addContent(123)
            b.addContent(42)
            run()
            c.content shouldBe Content.Value(5166)
        }
    }

    "product backward lhs" {
        QueueScheduler().run {
            val a = makeIntCell("a")
            val b = makeIntCell("b")
            val c = makeIntCell("c")
            intProduct(a, b, c)
            b.addContent(42)
            c.addContent(5166)
            run()
            a.content shouldBe Content.Value(123)
        }
    }

    "product backward rhs" {
        QueueScheduler().run {
            val a = makeIntCell("a")
            val b = makeIntCell("b")
            val c = makeIntCell("c")
            intProduct(a, b, c)
            a.addContent(123)
            c.addContent(5166)
            run()
            b.content shouldBe Content.Value(42)
        }
    }

})