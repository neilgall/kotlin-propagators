package propagators.types

import propagators.*

object DoubleData : Data<Double> {
    override fun Double.isRedundant(other: Double): Boolean =
        this == other

    override fun Double.merge(other: Double): Content<Double> =
        if (this == other)
            Content.Value(this)
        else
            Content.Contradiction("$other !+ $this")
}

fun Scheduler.makeDoubleCell(name: String): Cell<Double> =
    Cell(name, this, DoubleData)

fun Double.Companion.adder(a: Cell<Double>, b: Cell<Double>, out: Cell<Double>) =
    propagator("+", a, b, out) { x, y -> x + y }

fun Double.Companion.subtractor(a: Cell<Double>, b: Cell<Double>, out: Cell<Double>) =
    propagator("-", a, b, out) { x, y -> x - y }

fun Double.Companion.multiplier(a: Cell<Double>, b: Cell<Double>, out: Cell<Double>) =
    propagator("*", a, b, out) { x, y -> x * y }

fun Double.Companion.divider(a: Cell<Double>, b: Cell<Double>, out: Cell<Double>) =
    propagator("/", a, b, out) { x, y -> x / y }

fun Double.Companion.sum(a: Cell<Double>, b: Cell<Double>, sum: Cell<Double>) {
    adder(a, b, sum)
    subtractor(sum, a, b)
    subtractor(sum, b, a)
}

fun Double.Companion.product(a: Cell<Double>, b: Cell<Double>, product: Cell<Double>) {
    multiplier(a, b, product)
    divider(product, a, b)
    divider(product, b, a)
}

fun Double.Companion.quadratic(a: Cell<Double>, square: Cell<Double>) {
    propagator("square", a, square) { x -> x * x }
    propagator("sqrt", square, a) { x -> Math.sqrt(x) }
}
