package propagators.types

import propagators.*
import kotlin.math.pow

class ClosedRangeData<T : Comparable<T>> : Data<ClosedRange<T>> {
    override fun ClosedRange<T>.merge(other: ClosedRange<T>): MergeResult<ClosedRange<T>> {
        return if (other.contains(start) && other.contains(endInclusive))
            MergeResult.Redundant
        else if (contains(other.start) && contains(other.endInclusive))
            MergeResult.Replaced(other)
        else if (contains(other.start) || contains(other.endInclusive) || other.contains(start) || other.contains(endInclusive))
            MergeResult.Merged(maxOf(start, other.start).rangeTo(minOf(endInclusive, other.endInclusive)))
        else
            MergeResult.Contradiction("disjoint ranges $this and $other")
    }
}

operator fun ClosedRange<Double>.times(other: ClosedRange<Double>): ClosedRange<Double> =
    (start * other.start).rangeTo(endInclusive * other.endInclusive)

operator fun ClosedRange<Double>.div(other: ClosedRange<Double>): ClosedRange<Double> =
    (start / other.endInclusive).rangeTo(endInclusive / other.start)

fun ClosedRange<Double>.sqrt(): ClosedRange<Double> =
    Math.sqrt(start).rangeTo(Math.sqrt(endInclusive))

fun ClosedRange<Double>.pow(x: Double): ClosedRange<Double> =
    start.pow(x).rangeTo(endInclusive.pow(x))


val doubleClosedRangeProduct = Propagator2(
    abc = ClosedRange<Double>::times,
    cab = ClosedRange<Double>::div,
    cba = ClosedRange<Double>::div
)

val doubleClosedRangeQuadratic = Propagator1<ClosedRange<Double>, ClosedRange<Double>>(
    ab = { x -> (x.start.pow(2)).rangeTo(x.endInclusive.pow(2)) },
    ba = { x -> (Math.sqrt(x.start)).rangeTo(Math.sqrt(x.endInclusive)) }
)
