package crux.api.query.where

import clojure.lang.Keyword
import clojure.lang.Symbol
import crux.api.query.where.JoinType.*
import crux.api.query.where.PredicateType.*
import crux.api.underware.*

class WhereContext private constructor(): BuilderContext<WhereSection> {
    companion object {
        fun build(block: WhereContext.() -> Unit) = WhereContext().also(block).build()
    }

    private val clauses = mutableListOf<WhereClause>()

    private var hangingClause: WhereClause? = null

    data class SymbolAndKey(val symbol: Symbol, val key: Keyword)

    infix fun Symbol.has(key: Keyword) =
        SymbolAndKey(this, key).also {
            lockIn()
            hangingClause = WhereClause.HasKey(this, key)
        }

    infix fun SymbolAndKey.eq(value: Any) {
        hangingClause = WhereClause.HasKeyEqualTo(symbol, key, value)
    }

    private fun join(type: JoinType, block: WhereContext.() -> Unit) {
        lockIn()
        hangingClause = WhereClause.Join(type, build(block))
    }

    fun not(block: WhereContext.() -> Unit) = join(NOT, block)
    fun or(block: WhereContext.() -> Unit) = join(OR, block)

    private fun pred(predicateType: PredicateType, i: Symbol, j: Any) {
        lockIn()
        hangingClause = WhereClause.Predicate(predicateType, i, j)
    }

    infix fun Symbol.gt(other: Any) = pred(GT, this, other)
    infix fun Symbol.lt(other: Any) = pred(LT, this, other)
    infix fun Symbol.gte(other: Any) = pred(GTE, this, other)
    infix fun Symbol.lte(other: Any) = pred(LTE, this, other)
    infix fun Symbol.eq(other: Any) = pred(EQ, this, other)
    infix fun Symbol.neq(other: Any) = pred(NEQ, this, other)

    data class RuleInvocation(val name: Symbol)
    fun rule(name: Symbol) = RuleInvocation(name).also { lockIn() }

    operator fun RuleInvocation.invoke(vararg params: Any) {
        hangingClause = WhereClause.RuleInvocation(name, params.toList())
    }

    private fun lockIn() {
        hangingClause?.run(clauses::add)
        hangingClause = null
    }

    override fun build(): WhereSection {
        lockIn()
        return WhereSection(clauses)
    }
}