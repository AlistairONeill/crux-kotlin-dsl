package xtdb.api.query.context

import clojure.lang.Keyword
import xtdb.api.query.domain.PullSpec.Item.Field.Attribute
import xtdb.api.query.domain.PullSpec.Item.Field.Attribute.*
import xtdb.api.query.domain.PullSpec.Item.Field.Attributes
import xtdb.api.underware.BuilderContext
import xtdb.api.underware.SimpleBuilderContext
import javax.naming.OperationNotSupportedException

class PullFieldAttributesContext: SimpleBuilderContext<Attribute, Attributes>(::Attributes) {
    companion object : BuilderContext.Companion<Attributes, PullFieldAttributesContext>(::PullFieldAttributesContext)

    var name: Keyword
        get() = throw OperationNotSupportedException()
        set(value) = add(Name(value))

    var limit: Int
        get() = throw OperationNotSupportedException()
        set(value) = add(Limit(value))

    var default: Any
        get() = throw OperationNotSupportedException()
        set(value) = add(Default(value))
}