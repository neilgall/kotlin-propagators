package propagators.types

import propagators.*

object DoubleData : Data<Double> {
    override fun Double.merge(other: Double): MergeResult<Double> =
        if (this == other)
            MergeResult.Redundant
        else
            MergeResult.Contradiction("$other != $this")
}

fun doubleSum(a: Cell<Double>, b: Cell<Double>, sum: Cell<Double>) =
    propagator(a, b, sum,
        abc = Double::plus,
        cab = Double::minus,
        cba = Double::minus
    )

fun doubleProduct(a: Cell<Double>, b: Cell<Double>, product: Cell<Double>) =
    propagator(a, b, product,
        abc = Double::times,
        cab = Double::div,
        cba = Double::div
    )

fun doubleQuadratic(a: Cell<Double>, square: Cell<Double>) =
    propagator(a, square,
        ab = { x -> x * x },
        ba = Math::sqrt
    )
