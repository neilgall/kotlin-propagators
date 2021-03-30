package propagators

typealias Propagator = () -> Unit

fun <A, B> propagator(a: Cell<A>, b: Cell<B>, f: (A) -> B) {
    a.addNeighbour {
        val ac = a.content
        if (ac is Content.Value<A>) {
            b.addContent(f(ac.value))
        }
    }
}

fun <A, B, C> propagator(a: Cell<A>, b: Cell<B>, c: Cell<C>, f: (A, B) -> C) {
    val p = {
        val ac = a.content
        val bc = b.content
        if (ac is Content.Value<A> && bc is Content.Value<B>) {
            c.addContent(f(ac.value, bc.value))
        }
    }
    a.addNeighbour(p)
    b.addNeighbour(p)
}

fun <A, B, C, D> propagator(a: Cell<A>, b: Cell<B>, c: Cell<C>, d: Cell<D>, f: (A, B, C) -> D) {
    val p = {
        val ac = a.content
        val bc = b.content
        val cc = c.content
        if (ac is Content.Value<A> && bc is Content.Value<B> && cc is Content.Value<C>) {
            d.addContent(f(ac.value, bc.value, cc.value))
        }
    }
    a.addNeighbour(p)
    b.addNeighbour(p)
    c.addNeighbour(p)
}

fun <A: Any> constant(value: A, output: Cell<A>) {
    output.addNeighbour {
        output.addContent(value)
    }
}

fun <A, B> propagator(a: Cell<A>, b: Cell<B>, ab: (A) -> B, ba: (B) -> A) {
    propagator(a, b, ab)
    propagator(b, a, ba)
}

fun <A, B, C> propagator(a: Cell<A>, b: Cell<B>, c: Cell<C>, abc: (A, B) -> C, cab: (C, A) -> B, cba: (C, B) -> A) {
    propagator(a, b, c, abc)
    propagator(c, a, b, cab)
    propagator(c, b, a, cba)
}
