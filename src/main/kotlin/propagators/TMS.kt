package propagators

import java.lang.IllegalStateException

class TMS<T>(views: Collection<Supported<T>> = emptySet()) {
    var views = views.toSet()
        internal set
}

fun <T : Any> T.tms(vararg premises: Premise): TMS<T> =
    TMS(setOf(this.supported(*premises)))


class TMSData<T>(data: Data<T>) : Data<TMS<T>> {
    private val supportedData = data.supported()

    override fun TMS<T>.merge(other: TMS<T>): MergeResult<TMS<T>> {
        val candidate = assimilate(other)
        val consequence = candidate.strongestConsequence(worldView)
        val better = consequence?.let { candidate.assimilateOne(it) }
        return if (better == null || better === this)
            MergeResult.Redundant
        else
            MergeResult.Replaced(better)
    }

    private fun TMS<T>.assimilate(tms: TMS<T>): TMS<T> =
        tms.views.fold(this) { tms, view -> tms.assimilateOne(view) }

    private fun TMS<T>.assimilateOne(view: Supported<T>): TMS<T> =
        supportedData.run {
            if (views.any { it.subsumes(view) })
                this@assimilateOne
            else
                TMS(views.filterNot { view.subsumes(it) } + view)
        }

    private fun TMS<T>.strongestConsequence(query: Set<Premise>): Supported<T>? =
        supportedData.run {
            val relevant = views.filter { query.containsAll(it.premises) }
            when (relevant.size) {
                0 -> null
                1 -> relevant.first()
                else -> relevant.reduce { v1: Supported<T>?, v2: Supported<T> ->
                    when (val m = v1?.merge(v2)) {
                        is MergeResult.Replaced -> m.value
                        is MergeResult.Merged -> m.value
                        is MergeResult.Redundant -> v1
                        else -> null
                    }
                }
            }
        }

    var worldView: Set<Premise> = emptySet()

    fun TMS<T>.query(vararg view: Premise): Supported<T>? {
        val consequence = strongestConsequence(if (view.isEmpty()) worldView else view.toSet())
        val better = consequence?.let { assimilateOne(it) }
        if (better != null && better !== this) {
            views = better.views
        }
        return consequence
    }
}

fun <T> Data<T>.tms(): TMSData<T> =
    TMSData(this)

fun <A, B> Propagator1<A, B>.tms(): Propagator1<TMS<A>, TMS<B>> =
    supported().run {
        Propagator1(
            ab = { a -> TMS(a.views.mapNotNull(ab)) },
            ba = { b -> TMS(b.views.mapNotNull(ba)) }
        )
    }

fun <A, B, C> Propagator2<A, B, C>.tms(): Propagator2<TMS<A>, TMS<B>, TMS<C>> =
    supported().run {
        Propagator2(
            abc = { a, b ->
                TMS(a.views.flatMap { av ->
                    b.views.mapNotNull { bv -> abc(av, bv) }
                })
            },
            cab = { c, a ->
                TMS(c.views.flatMap { cv ->
                    a.views.mapNotNull { av -> cab(cv, av) }
                })
            },
            cba = { c, b ->
                TMS(c.views.flatMap { cv ->
                    b.views.mapNotNull { bv -> cba(cv, bv) }
                })
            }
        )
    }
