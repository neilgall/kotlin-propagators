package propagators

typealias ContradictionInfo = String

sealed class Content<out T> {
    object Empty : Content<Nothing>()
    data class Value<T>(val value_: T) : Content<T>()
    data class Contradiction(val contradiction: ContradictionInfo) : Content<Nothing>()

    val value: T?
        get() = when (this) {
            is Empty, is Contradiction -> null
            is Value -> value_
        }
}

sealed class MergeResult<out T> {
    object Redundant : MergeResult<Nothing>()
    data class Merged<T>(val value: T) : MergeResult<T>()
    data class Replaced<T>(val value: T) : MergeResult<T>()
    data class Contradiction(val contradiction: ContradictionInfo) : MergeResult<Nothing>()
}

interface Data<T> {
    fun T.merge(other: T): MergeResult<T>

    fun T.subsumes(other: T): Boolean =
        merge(other) is MergeResult.Redundant
}
