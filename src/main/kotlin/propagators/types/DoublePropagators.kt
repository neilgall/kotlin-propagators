package propagators.types

import propagators.Cell
import propagators.Content
import propagators.Data
import propagators.propagator

object DoubleData : Data<Double> {
    override fun Double.isRedundant(other: Double): Boolean =
        this == other

    override fun Double.merge(other: Double): Content<Double> =
        if (this == other)
            Content.Value(this)
        else
            Content.Contradiction("$other !+ $this")
}

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
