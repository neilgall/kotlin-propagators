package propagators

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import propagators.types.IntData
import propagators.types.product
import propagators.types.sum

class PropagationInAnyDirection : StringSpec({

    "sum forward" {
        QueueScheduler().run {
            val a = Cell("a", this, IntData)
            val b = Cell("b", this, IntData)
            val c = Cell("c", this, IntData)
            Int.sum(a, b, c)
            a.addContent(123)
            b.addContent(42)
            run()
            c.content shouldBe Content.Value(165)
        }
    }

    "sum backward lhs" {
        QueueScheduler().run {
            val a = Cell("a", this, IntData)
            val b = Cell("b", this, IntData)
            val c = Cell("c", this, IntData)
            Int.sum(a, b, c)
            b.addContent(42)
            c.addContent(165)
            run()
            a.content shouldBe Content.Value(123)
        }
    }

    "sum backward rhs" {
        QueueScheduler().run {
            val a = Cell("a", this, IntData)
            val b = Cell("b", this, IntData)
            val c = Cell("c", this, IntData)
            Int.sum(a, b, c)
            a.addContent(123)
            c.addContent(165)
            run()
            b.content shouldBe Content.Value(42)
        }
    }

    "product forward" {
        QueueScheduler().run {
            val a = Cell("a", this, IntData)
            val b = Cell("b", this, IntData)
            val c = Cell("c", this, IntData)
            Int.product(a, b, c)
            a.addContent(123)
            b.addContent(42)
            run()
            c.content shouldBe Content.Value(5166)
        }
    }

    "product backward lhs" {
        QueueScheduler().run {
            val a = Cell("a", this, IntData)
            val b = Cell("b", this, IntData)
            val c = Cell("c", this, IntData)
            Int.product(a, b, c)
            b.addContent(42)
            c.addContent(5166)
            run()
            a.content shouldBe Content.Value(123)
        }
    }

    "product backward rhs" {
        QueueScheduler().run {
            val a = Cell("a", this, IntData)
            val b = Cell("b", this, IntData)
            val c = Cell("c", this, IntData)
            Int.product(a, b, c)
            a.addContent(123)
            c.addContent(5166)
            run()
            b.content shouldBe Content.Value(42)
        }
    }

})