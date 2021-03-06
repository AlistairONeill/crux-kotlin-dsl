package crux.api.query.conversion

import crux.api.query.domain.RuleDefinition
import crux.api.query.domain.WhereClause
import crux.api.underware.pl
import crux.api.underware.prefix
import crux.api.underware.pv

fun RuleDefinition.toEdn() = body.clauses.map(WhereClause::toEdn).prefix(
    parameters.prefix(name).pl
).pv