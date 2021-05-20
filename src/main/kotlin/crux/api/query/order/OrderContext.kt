package crux.api.query.order

import clojure.lang.Symbol
import crux.api.query.order.OrderDirection.*
import crux.api.underware.BuilderContext

class OrderContext private constructor(): BuilderContext<OrderSection> {
    companion object {
        fun build(block: OrderContext.() -> Unit) = OrderContext().also(block).build()
    }

    private val data = mutableListOf<OrderClause>()

    private fun add(symbol: Symbol, direction: OrderDirection) {
        data.add(OrderClause(symbol, direction))
    }

    operator fun Symbol.unaryPlus() = add(this, ASC)
    operator fun Symbol.unaryMinus() = add(this, DESC)

    override fun build() = OrderSection(data)
}