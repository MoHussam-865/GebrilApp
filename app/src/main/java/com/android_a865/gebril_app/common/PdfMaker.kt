package com.android_a865.gebril_app.common

import android.content.Context
import android.util.Log
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.data.domain.Invoice
import com.android_a865.gebril_app.data.domain.InvoiceHolder
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.utils.date
import com.android_a865.gebril_app.utils.toFormattedString
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.OutputStream
import javax.inject.Singleton

@Singleton
class PdfMaker {


    init {
        try {
            val base = BaseFont.createFont(
                "assets/fonts/arial.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED
            )
            font1_ = Font(base, 48f, Font.BOLD, BaseColor.BLUE)
            font2 = Font(base, 18f, Font.NORMAL, BaseColor.BLACK)
            font3_ = Font(base, 18f, Font.BOLD, BaseColor.BLUE)
            font4 = Font(base, 24f, Font.NORMAL, BaseColor.BLACK)
            font5 = Font(base, 14f, Font.NORMAL, BaseColor.BLACK)
        } catch (e: Exception) {
            Log.d( "Font Error",e.message!!)
        }
        font1_.setColor(3, 11, 40)
        font3_.setColor(3, 11, 40)
    }

    fun make(
        context: Context,
        invoice: Invoice,
        appSettings: AppSettings
    ): String? {
        val document = Document(PageSize.A4)
        val fileName = "${invoice.date}.pdf"
        try {
            val os: OutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            PdfWriter.getInstance(document, os)
            document.open()

            //
            document.setMargins(2.5f, 2.5f, 2.5f, 2.5f)
            var c1: PdfPCell
            val table0 = PdfPTable(1)
            table0.widthPercentage = 100f

            // company Name
            val invoiceType = Paragraph()
            val myName = context.getString(R.string.my_name)
            invoiceType.add(Paragraph(myName, font1_))
            invoiceType.alignment = Element.ALIGN_JUSTIFIED
            c1 = PdfPCell(invoiceType)
            c1.border = Rectangle.NO_BORDER
            c1.runDirection = PdfWriter.RUN_DIRECTION_RTL
            c1.horizontalAlignment = Element.ALIGN_RIGHT
            table0.addCell(c1)
            addEmptyRaw(table0)


            val table1 = PdfPTable(2)
            table1.widthPercentage = 100f

            // To:  (Client Info)
            val clientInfo = Paragraph()
            clientInfo.add(Paragraph(appSettings.clientInfo, font2))
            clientInfo.alignment = Element.ALIGN_JUSTIFIED
            c1 = PdfPCell(clientInfo)
            c1.runDirection = PdfWriter.RUN_DIRECTION_RTL
            c1.horizontalAlignment = Element.ALIGN_RIGHT
            c1.verticalAlignment = Element.ALIGN_CENTER
            c1.border = Rectangle.NO_BORDER
            table1.addCell(c1)


            // date
            val dateInfo = Paragraph().apply {
                add(Paragraph(invoice.date.date(appSettings.dateFormat), font4))
            }

            c1 = PdfPCell(dateInfo).apply {
                horizontalAlignment = Element.ALIGN_RIGHT
                border = Rectangle.NO_BORDER
            }

            table1.addCell(c1)
            addEmptyRaw(table1)
            document.add(table1)


            // items
            val table2 = PdfPTable(4)
            table2.widthPercentage = 100f
            arrayOf("Name", "Unit Price", "Qty", "Amount").forEach {
                c1 = PdfPCell(Paragraph(it, font3_))
                c1.disableBorderSide(Rectangle.RIGHT)
                c1.disableBorderSide(Rectangle.LEFT)
                c1.borderWidthBottom = 1f
                c1.borderWidthTop = 1f
                c1.borderColor = BaseColor.RED
                c1.runDirection = PdfWriter.RUN_DIRECTION_RTL
                c1.horizontalAlignment = Element.ALIGN_CENTER
                c1.verticalAlignment = Element.ALIGN_CENTER
                c1.setPadding(10f)
                table2.addCell(c1)
            }

            table2.headerRows = 1
            invoice.items.forEach { item ->
                c1 = PdfPCell(Paragraph(item.fullName, font2)).apply {
                    border = Rectangle.NO_BORDER
                    runDirection = PdfWriter.RUN_DIRECTION_LTR
                    horizontalAlignment = Element.ALIGN_LEFT
                    paddingTop = 10f
                }
                table2.addCell(c1)

                c1 = PdfPCell(Paragraph(item.finalPrice.toFormattedString(), font2))
                c1.border = Rectangle.NO_BORDER
                c1.horizontalAlignment = Element.ALIGN_CENTER
                c1.isNoWrap = false
                table2.addCell(c1)

                c1 = PdfPCell(Paragraph(item.qty.toFormattedString(), font2))
                c1.border = Rectangle.NO_BORDER
                c1.horizontalAlignment = Element.ALIGN_CENTER
                table2.addCell(c1)

                c1 = PdfPCell(Paragraph(item.total.toFormattedString(), font2))
                c1.border = Rectangle.NO_BORDER
                c1.horizontalAlignment = Element.ALIGN_RIGHT
                table2.addCell(c1)
            }
            addEmptyRaw(table2)

            // Total
            table2.addCell(emptyCell())
            table2.addCell(emptyCell())
            c1 = PdfPCell(Paragraph("Total", font3_))
            c1.border = Rectangle.NO_BORDER
            c1.horizontalAlignment = Element.ALIGN_LEFT
            table2.addCell(c1)
            c1 = PdfPCell(Paragraph(invoice.total.toFormattedString(), font3_))
            c1.border = Rectangle.NO_BORDER
            c1.horizontalAlignment = Element.ALIGN_RIGHT
            table2.addCell(c1)
            document.add(table2)

            document.close()
            return fileName
        } catch (e: Exception) {
            Log.d("pdf_error:", e.message.toString())
        }
        return null
    }


    private fun addEmptyRaw(table: PdfPTable) {
        for (i in 0 until table.numberOfColumns) {
            table.addCell(
                PdfPCell(Phrase(" \n")).apply {
                    border = Rectangle.NO_BORDER
                }
            )
        }
    }

    private fun emptyCell(): PdfPCell = PdfPCell(Phrase("")).apply {
        border = Rectangle.NO_BORDER
    }

    companion object {
        private var font1_: Font = Font(Font.FontFamily.TIMES_ROMAN, 24f, Font.BOLD, BaseColor.BLUE)
        private var font2: Font =
            Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.NORMAL, BaseColor.BLACK)
        private var font3_: Font = Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.BOLD, BaseColor.BLUE)
        private var font4: Font =
            Font(Font.FontFamily.TIMES_ROMAN, 24f, Font.NORMAL, BaseColor.BLACK)
        private var font5: Font =
            Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.NORMAL, BaseColor.BLACK)
    }

}
