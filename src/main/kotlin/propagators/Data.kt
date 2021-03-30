package propagators

typealias ContradictionInfo = String

sealed class Content<out T> {
    object Empty : Content<Nothing>()
    data class Value<T>(val value: T) : Content<T>()
    data class Contradiction(val contradiction: ContradictionInfo) : Content<Nothing>()

    final override fun toString(): String = when (this) {
        is Empty -> "{}"
        is Value<T> -> "{$value}"
        is Contradiction -> "contradiction($contradiction)"
    }
}

sealed class MergeResult<out T> {
    object Redundant: MergeResult<Nothing>()
    data class Value<T>(val value: T): MergeResult<T>()
    data class Contradiction(val contradiction: ContradictionInfo): MergeResult<Nothing>()

    fun <U> biMap(vf: (T) -> U, cf: (ContradictionInfo) -> ContradictionInfo): MergeResult<U> = when(this) {
        is Redundant -> Redundant
        is Value<T> -> Value(vf(value))
        is Contradiction -> Contradiction(cf(contradiction))
    }
}

interface Data<T> {
    fun T.merge(other: T): MergeResult<T>
}
