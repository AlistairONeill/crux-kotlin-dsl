package utils

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import crux.api.CruxDocument
import crux.api.ICruxAPI
import crux.api.ICruxDatasource
import crux.api.TransactionInstant
import crux.api.tx.Transaction
import crux.api.tx.Transaction.buildTx
import org.junit.jupiter.api.Assertions.assertNull
import java.time.Duration
import java.util.*

fun aDocument(): CruxDocument = CruxDocument.build(
    UUID.randomUUID().toString()
) {
    it.put("Name", UUID.randomUUID().toString())
}

private fun ICruxAPI.performAndWait(transaction: Transaction): TransactionInstant =
    awaitTx(
        submitTx(transaction),
        Duration.ofSeconds(10)
    )

fun ICruxAPI.putAndWait(document: CruxDocument) =
    performAndWait(
        buildTx {
            it.put(document)
        }
    )

fun ICruxAPI.putAndWait(document: CruxDocument, validTime: Date) =
    performAndWait(
        buildTx {
            it.put(document, validTime)
        }
    )

fun MutableCollection<MutableList<*>>.singleResults() = map { it[0] }.toSet()
fun MutableCollection<MutableList<*>>.simplify() = map { it.toList() }.toSet()