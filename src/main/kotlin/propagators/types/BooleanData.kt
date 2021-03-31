package propagators.types

import propagators.Data
import propagators.MergeResult
import propagators.Propagator2

object BooleanData : Data<Boolean> {
    override fun Boolean.merge(other: Boolean): MergeResult<Boolean> =
        if (this == other)
            MergeResult.Redundant
        else
            MergeResult.Contradiction("$other != $this")
}

// F F F
// F T F
// T F F
// T T T
val conjunction = Propagator2(
    abc = Boolean::and,
    cab = { c, a -> if (c) true else if (a) false else null },
    cba = { c, b -> if (c) true else if (b) false else null }
)

// F F F
// F T T
// T F T
// T T T
val disjunction = Propagator2(
    abc = Boolean::or,
    cab = { c, a -> if (!c) false else if (!a) true else null },
    cba = { c, b -> if (!c) false else if (!b) true else null }
)
