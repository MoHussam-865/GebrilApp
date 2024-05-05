package com.android_a865.gebril_app.feature_reports.domain.use_cases

import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_main.data.mapper.toInvoice
import com.android_a865.gebril_app.feature_main.domain.model.InvoiceItem
import com.android_a865.gebril_app.feature_main.domain.model.InvoiceTypes
import com.android_a865.gebril_app.feature_reports.domain.model.ClientReport
import com.android_a865.gebril_app.feature_reports.domain.model.FullReport
import com.android_a865.gebril_app.feature_reports.domain.model.SmartList
import com.android_a865.gebril_app.feature_reports.domain.repository.ReportRepository
import com.android_a865.gebril_app.utils.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FullReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {

    suspend operator fun invoke(): FullReport {
        val bills = repository.getInvoices().map { it.toInvoice() }
        val allInvoices = bills.filter { it.type == InvoiceTypes.Invoice }
        val allEstimates = bills.filter { it.type == InvoiceTypes.Estimate }

        val numberOfInvoices = allInvoices.size
        val numberOfEstimates = allEstimates.size

        val invoicesTotal = allInvoices.sumOf { it.total }
        val estimatesTotal = allEstimates.sumOf { it.total }


        /** Client based Report */
        val clients = mutableListOf<Client>()
        // gets The Unique Clients
        bills.forEach { invoice ->

            invoice.client?.let { client ->
                clients.addUnique(client) { listClient ->
                    listClient == client
                }
            }

        }

        // getClient Invoices & Items
        val clientsReport = clients.map { client ->
            val clientInvoices = allInvoices.filter { invoice ->
                invoice.client == client
            }
            val clientEstimates = allEstimates.filter { estimate ->
                estimate.client == client
            }



            val clientItems = SmartList<InvoiceItem>(emptyList())
            clientInvoices.forEach { invoice ->
                invoice.items.forEach { item ->
                    clientItems.update0 {
                        it.addOf(item)
                    }
                }
            }

            ClientReport(
                client = client,
                invoices = clientInvoices,
                estimates = clientEstimates,
                items = clientItems
            )
        }

        /** Items based Report */
        val allItems = SmartList<InvoiceItem>(emptyList())
        allInvoices.forEach { invoice ->
            invoice.items.forEach { item ->
                allItems.update0 {
                    it.addOf(item)
                }
            }
        }

        /** Days based Report */
        val uniqueDays = mutableListOf<String>()
        allInvoices.forEach { invoice ->
            val date = invoice.date.date("d/M/yyyy")


            uniqueDays.addUnique(
                data = date,
                equals = { myDate ->
                    myDate == date
                }
            )

        }

        return FullReport(
            numberOfInvoices = numberOfInvoices,
            numberOfEstimates = numberOfEstimates,
            invoicesTotal = invoicesTotal,
            estimatesTotal = estimatesTotal,
            clientsReport = clientsReport,
            itemsReport = allItems
        )

    }

}