package com.android_a865.gebril_app.feature_reports.presentation.overview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.databinding.FragmentReportsMainBinding
import com.android_a865.gebril_app.feature_reports.domain.model.FullReport
import com.android_a865.gebril_app.utils.AUTHORITY
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import com.android_a865.gebril_app.utils.toFormattedString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class ReportsMainFragment : Fragment(R.layout.fragment_reports_main) {

    private val viewModel by viewModels<ReportsViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()

        val binding = FragmentReportsMainBinding.bind(view)

        binding.apply {

            viewModel.report.observe(viewLifecycleOwner) {
                tvInvoicesNumber.text = it?.numberOfInvoices?.toFormattedString()
                tvEstimatesNumber.text = it?.numberOfEstimates?.toFormattedString()
                tvEstimatesTotal.text = it?.estimatesTotal?.toFormattedString()
                tvInvoicesTotal.text = it?.invoicesTotal?.toFormattedString()
            }

            btnMakeReport.setOnClickListener {
                exportReportSample(viewModel.report.value)
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is ReportsViewModel.WindowEvents.NavigateTo -> {
                        findNavController().navigate(event.direction)
                    }
                }.exhaustive
            }
        }

    }

    private fun exportReportSample(report: FullReport?) {
        val wb: Workbook = HSSFWorkbook()

        val clientsSheet = wb.createSheet("Clients")

        var myRow = clientsSheet.createRow(0)
        fillRow(
            myRow,
            listOf(
                "Client ID",
                "Client Name",
                "Organization",
                "Title",
                "Phone 1",
                "Phone 2",
                "Email",
                "Address",
                "Invoices Count",
                "Invoices Total",
                "Estimates Count",
                "Estimate Total"
            )
        )

        report?.clientsReport?.forEachIndexed { index, clientReport ->
            myRow = clientsSheet.createRow(index+1)
            fillRow(
                myRow,
                listOf(
                    clientReport.client.id.toString(),
                    clientReport.client.name,
                    clientReport.client.org.toString(),
                    clientReport.client.title.toString(),
                    clientReport.client.phone1.toString(),
                    clientReport.client.phone2.toString(),
                    clientReport.client.email.toString(),
                    clientReport.client.address.toString(),
                    clientReport.invoices.size.toString(),
                    clientReport.invoices.sumOf { it.total }.toString(),
                    clientReport.estimates.size.toString(),
                    clientReport.estimates.sumOf { it.total }.toString()
                )
            )
        }

        val clientItemsSheet = wb.createSheet("Client items")

        myRow = clientItemsSheet.createRow(0)
        fillRow(
            myRow,
            listOf(
                "Client ID",
                "Item ID",
                "Item Name",
                "Item Qty",
                "Item Total"
            )
        )

        var i = 1
        report?.clientsReport?.forEach { clientReport ->

            clientReport.items.value.forEach { item ->
                myRow = clientItemsSheet.createRow(i)

                fillRow(
                    myRow,
                    listOf(
                        clientReport.client.id.toString(),
                        item.id.toString(),
                        item.name,
                        item.qty.toString(),
                        item.total.toString()
                    )
                )
                i++
            }

        }

        val itemsSheet = wb.createSheet("Items")
        myRow = itemsSheet.createRow(0)
        fillRow(
            myRow,
            listOf(
                "Item ID",
                "Item Name",
                "Sales Count",
                "Sales Total"
            )
        )

        report?.itemsReport?.value?.forEachIndexed { index, item ->
            myRow = itemsSheet.createRow(index+1)
            fillRow(
                myRow,
                listOf(
                    item.id.toString(),
                    item.name,
                    item.qty.toString(),
                    item.total.toString()
                )
            )
        }


        openReportExternally(wb)

    }

    private fun openReportExternally(wb: Workbook) {

        try {
            val fileName = "${System.currentTimeMillis()}.xls"
            val file = File(requireContext().filesDir, fileName)

            val os = FileOutputStream(file)
            wb.write(os)
            os.close()


            val uri = FileProvider.getUriForFile(requireContext(), AUTHORITY, file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.ms-excel")
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.d("ExcelError", e.message.toString())
        }


    }

    private fun fillRow(row: Row, data: List<String>) {
        data.forEachIndexed { index, str ->
            val cell = row.createCell(index)
            cell.setCellValue(str)
        }
    }
}