package propagators.types

import propagators.*

object DoubleData : Data<Double> {
    override fun Double.merge(other: Double): MergeResult<Double> =
        if (this == other)
            MergeResult.Redundant
        else
            MergeResult.Contradiction("$other != $this")
}

val doubleSum = Propagator2<Double, Double, Double>(
    abc = Double::plus,
    cab = Double::minus,
    cba = Double::minus
)

val doubleProduct = Propagator2<Double, Double, Double>(
    abc = Double::times,
    cab = Double::div,
    cba = Double::div
)

val doubleQuadratic = Propagator1<Double, Double>(
    ab = { x -> x * x },
    ba = Math::sqrt
)
