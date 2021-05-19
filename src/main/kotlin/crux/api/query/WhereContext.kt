package crux.api.query

import clojure.lang.Keyword
import clojure.lang.PersistentVector
import clojure.lang.Symbol
import crux.api.pl
import crux.api.pv
import crux.api.sym

class WhereContext {
    companion object {
        fun build(block: WhereContext.() -> Unit) = WhereContext().also(block).build()
    }

    private val clauses = mutableListOf<Any>()

    private var hangingClause: Any? = null

    data class SymbolAndKey(val symbol: Symbol, val key: Keyword)

    infix fun Symbol.has(key: Keyword) =
        SymbolAndKey(this, key).also {
            lockIn()
            hangingClause = listOf(this, key).pv
        }

    infix fun SymbolAndKey.eq(value: Any) {
        hangingClause = listOf(symbol, key, value).pv
    }

    fun not(block: WhereContext.() -> Unit) {
        lockIn()
        clauses.add(
            (listOf("not".sym) +
                    WhereContext()
                    .also(block)
                    .apply(WhereContext::lockIn)
                    .clauses).pl
        )
    }

    private fun pred(symbol: String, i: Symbol, j: Any) {
        lockIn()
        hangingClause = listOf(
            listOf(symbol.sym, i, j).pl
        ).pv
    }

    infix fun Symbol.gt(other: Any) = pred(">", this, other)
    infix fun Symbol.lt(other: Any) = pred("<", this, other)
    infix fun Symbol.gte(other: Any) = pred(">=", this, other)
    infix fun Symbol.lte(other: Any) = pred("<=", this, other)
    infix fun Symbol.eq(other: Any) = pred("==", this, other)
    infix fun Symbol.neq(other: Any) = pred("!=", this, other)

    private fun lockIn() {
        hangingClause?.run(clauses::add)
        hangingClause = null
    }

    fun build(): PersistentVector {
        lockIn()
        return clauses.pv
    }
}