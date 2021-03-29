package propagators.types

import propagators.*
import kotlin.math.pow

object DoubleRangeData : Data<ClosedRange<Double>> {
    override fun ClosedRange<Double>.isRedundant(other: ClosedRange<Double>): Boolean =
        contains(other.start) && contains(other.endInclusive)

    override fun ClosedRange<Double>.merge(other: ClosedRange<Double>): Content<ClosedRange<Double>> =
        if (contains(other.start) || contains(other.endInclusive))
            Content.Value(maxOf(start, other.start).rangeTo(minOf(endInclusive, other.endInclusive)))
        else
            Content.Contradiction("disjoint ranges $this and $other")
}

fun Scheduler.makeDoubleRangeCell(name: String) =
    Cell(name, this, DoubleRangeData)

fun DoubleRangeData.scalarMultiplier(a: Cell<ClosedRange<Double>>, b: Cell<Double>, c: Cell<ClosedRange<Double>>) =
    propagator("*", a, b, c) { r: ClosedRange<Double>, m: Double ->
        (r.start * m).rangeTo(r.endInclusive * m)
    }

fun DoubleRangeData.scalarDivider(a: Cell<ClosedRange<Double>>, b: Cell<Double>, c: Cell<ClosedRange<Double>>) =
    propagator("/", a, b, c) { r: ClosedRange<Double>, d: Double ->
        (r.start / d).rangeTo(r.endInclusive / d)
    }

fun DoubleRangeData.rangeMultiplier(a: Cell<ClosedRange<Double>>, b: Cell<ClosedRange<Double>>, c: Cell<ClosedRange<Double>>) =
    propagator("*", a, b, c) { r1: ClosedRange<Double>, r2: ClosedRange<Double> ->
        (r1.start * r2.start).rangeTo(r1.endInclusive * r2.endInclusive)
    }

fun DoubleRangeData.rangeDivider(a: Cell<ClosedRange<Double>>, b: Cell<ClosedRange<Double>>, c: Cell<ClosedRange<Double>>) =
    propagator("/", a, b, c) { r1: ClosedRange<Double>, r2: ClosedRange<Double> ->
        (r1.start / r2.endInclusive).rangeTo(r1.endInclusive / r2.start)
    }

fun DoubleRangeData.product(a: Cell<ClosedRange<Double>>, b: Cell<ClosedRange<Double>>, c: Cell<ClosedRange<Double>>) {
    rangeMultiplier(a, b, c)
    rangeDivider(c, a, b)
    rangeDivider(c, b, a)
}

fun DoubleRangeData.quadratic(a: Cell<ClosedRange<Double>>, b: Cell<ClosedRange<Double>>) {
    propagator("square", a, b) { x: ClosedRange<Double> ->
        (x.start.pow(2)).rangeTo(x.endInclusive.pow(2))
    }
    propagator("sqrt", b, a) { x: ClosedRange<Double> ->
        (Math.sqrt(x.start)).rangeTo(Math.sqrt(x.endInclusive))
    }
}
