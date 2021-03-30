package propagators.types

import propagators.*

object IntData: Data<Int> {
    override fun Int.merge(other: Int): MergeResult<Int> =
        if (this == other)
            MergeResult.Redundant
        else
            MergeResult.Contradiction("$other != $this")
}

fun Scheduler.makeIntCell(name: String): Cell<Int> =
    Cell(name, this, IntData)

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
