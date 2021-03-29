package propagators.types

import propagators.Cell
import propagators.Content
import propagators.Data
import propagators.propagator

object IntData: Data<Int> {
    override fun Int.isRedundant(other: Int): Boolean =
        this == other

    override fun Int.merge(other: Int): Content<Int> =
        if (this == other)
            Content.Value(this)
            else
                Content.Contradiction("$other !+ $this")
}

fun Int.Companion.adder(a: Cell<Int>, b: Cell<Int>, out: Cell<Int>) =
        propagator("+", a, b, out) { x, y -> x + y }

fun Int.Companion.subtractor(a: Cell<Int>, b: Cell<Int>, out: Cell<Int>) =
        propagator("-", a, b, out) { x, y -> x - y }

fun Int.Companion.multiplier(a: Cell<Int>, b: Cell<Int>, out: Cell<Int>) =
    propagator("*", a, b, out) { x, y -> x * y }

fun Int.Companion.divider(a: Cell<Int>, b: Cell<Int>, out: Cell<Int>) =
    propagator("/", a, b, out) { x, y -> x / y }

fun Int.Companion.sum(a: Cell<Int>, b: Cell<Int>, out: Cell<Int>) {
    adder(a, b, out)
    subtractor(out, a, b)
    subtractor(out, b, a)
}

fun Int.Companion.product(a: Cell<Int>, b:Cell<Int>, out: Cell<Int>) {
    multiplier(a, b, out)
    divider(out, a, b)
    divider(out, b, a)
}
