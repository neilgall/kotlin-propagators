package propagators.types

import propagators.*

object IntData : Data<Int> {
    override fun Int.merge(other: Int): MergeResult<Int> =
        if (this == other)
            MergeResult.Redundant
        else
            MergeResult.Contradiction("$other != $this")
}

val intSum = Propagator2<Int, Int, Int>(
    abc = Int::plus,
    cab = Int::minus,
    cba = Int::minus
)


val intProduct = Propagator2<Int, Int, Int>(
    abc = Int::times,
    cab = Int::div,
    cba = Int::div
)
