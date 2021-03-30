package propagators.types

import propagators.*
import kotlin.math.pow

class ClosedRangeData<T: Comparable<T>> : Data<ClosedRange<T>> {
    override fun ClosedRange<T>.merge(other: ClosedRange<T>): MergeResult<ClosedRange<T>> {
        val intersectLow = contains(other.start)
        val intersectHigh = contains(other.endInclusive)
        return if (intersectLow && intersectHigh)
            MergeResult.Redundant
        else if (intersectLow || intersectHigh)
            MergeResult.Value(maxOf(start, other.start).rangeTo(minOf(endInclusive, other.endInclusive)))
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


fun doubleClosedRangeProduct(a: Cell<ClosedRange<Double>>, b: Cell<ClosedRange<Double>>, c: Cell<ClosedRange<Double>>) =
    propagator(a, b, c,
        abc = ClosedRange<Double>::times,
        cab = ClosedRange<Double>::div,
        cba = ClosedRange<Double>::div
    )

fun doubleClosedRangeQuadratic(a: Cell<ClosedRange<Double>>, b: Cell<ClosedRange<Double>>) =
    propagator(a, b,
        ab = { x -> (x.start.pow(2)).rangeTo(x.endInclusive.pow(2)) },
        ba = { x -> (Math.sqrt(x.start)).rangeTo(Math.sqrt(x.endInclusive)) }
    )
