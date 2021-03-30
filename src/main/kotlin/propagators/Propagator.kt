package propagators

class Propagator(private val name: String, private val f: () -> Unit) {
    operator fun invoke() { f() }
    override fun toString(): String = name
}

fun <A, Output> propagator(name: String, a: Cell<A>, output: Cell<Output>, f: (A) -> Output) {
    a.addNeighbour(Propagator("$name($a, $output)") {
        val ac = a.content
        if (ac is Content.Value<A>) {
            output.addContent(f(ac.value))
        }
    })
}

fun <A, B, Output> propagator(name: String, a: Cell<A>, b: Cell<B>, output: Cell<Output>, f: (A, B) -> Output) {
    val p = Propagator("$name($a, $b, $output)") {
        val ac = a.content
        val bc = b.content
        if (ac is Content.Value<A> && bc is Content.Value<B>) {
            output.addContent(f(ac.value, bc.value))
        }
    }
    a.addNeighbour(p)
    b.addNeighbour(p)
}

fun <A, B, C, Output> propagator(name: String, a: Cell<A>, b: Cell<B>, c: Cell<C>, output: Cell<Output>, f: (A, B, C) -> Output) {
    val p = Propagator("$name($a, $b, $c, $output)") {
        val ac = a.content
        val bc = b.content
        val cc = c.content
        if (ac is Content.Value<A> && bc is Content.Value<B> && cc is Content.Value<C>) {
            output.addContent(f(ac.value, bc.value, cc.value))
        }
    }
    a.addNeighbour(p)
    b.addNeighbour(p)
    c.addNeighbour(p)
}

fun <A: Any> constant(value: A, output: Cell<A>) {
    output.addNeighbour(Propagator("constant $value") {
        output.addContent(value)
    })
}
