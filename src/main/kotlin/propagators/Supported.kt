package propagators

typealias Premise = String

data class Supported<T>(val value: T, val premises: Set<Premise>)

fun <T: Any> T.supported(vararg premises: Premise): Supported<T> =
    Supported(this, premises.toSet())

fun <T> Data<T>.supported(): Data<Supported<T>> = object: Data<Supported<T>> {
    override fun Supported<T>.merge(other: Supported<T>): MergeResult<Supported<T>> =
        when (val result = this.value.merge(other.value)) {
            is MergeResult.Redundant ->
                MergeResult.Redundant
            is MergeResult.Merged<T> ->
                MergeResult.Merged(Supported(result.value, this.premises + other.premises))
            is MergeResult.Replaced<T> ->
                MergeResult.Replaced(Supported(result.value, other.premises))
            is MergeResult.Contradiction ->
                MergeResult.Contradiction("${result.contradiction}: ${this.premises} vs ${other.premises}")
        }
    }

fun <A, B> Propagator1<A, B>.supported(): Propagator1<Supported<A>, Supported<B>> =
    Propagator1(
        ab = { a -> Supported(ab(a.value), a.premises) },
        ba = { b -> Supported(ba(b.value), b.premises) }
    )

fun <A, B, C> Propagator2<A, B, C>.supported(): Propagator2<Supported<A>, Supported<B>, Supported<C>> =
    Propagator2(
        abc = { a, b -> Supported(abc(a.value, b.value), a.premises + b.premises) },
        cab = { c, a -> Supported(cab(c.value, a.value), c.premises + a.premises) },
        cba = { c, b -> Supported(cba(c.value, b.value), c.premises + b.premises) }
    )
