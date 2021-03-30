package propagators.types

import propagators.*

object IntData: Data<Int> {
    override fun Int.merge(other: Int): MergeResult<Int> =
        if (this == other)
            MergeResult.Redundant
        else
            MergeResult.Contradiction("$other != $this")
}

fun intSum(a: Cell<Int>, b: Cell<Int>, c: Cell<Int>) =
    propagator(a, b, c,
        abc = Int::plus,
        cab = Int::minus,
        cba = Int::minus
    )


fun intProduct(a: Cell<Int>, b:Cell<Int>, c: Cell<Int>) =
    propagator(a, b, c,
        abc = Int::times,
        cab = Int::div,
        cba = Int::div
    )
