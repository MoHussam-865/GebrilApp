package com.android_a865.gebril_app.feature_main.presentation.pdf_preview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.databinding.FragmentViewPdfBinding
import com.android_a865.gebril_app.utils.AUTHORITY
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import com.github.barteksc.pdfviewer.PDFView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.*

@AndroidEntryPoint
class ViewPdfFragment : Fragment(R.layout.fragment_view_pdf) {

    private val viewModule by viewModels<PdfPreviewViewModule>()
    private lateinit var pdfView: PDFView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentViewPdfBinding.bind(view)
        pdfView = binding.pdfView


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModule.windowEvents.collect { event ->
                when (event) {
                    is PdfPreviewViewModule.WindowEvents.OpenPdfExternally -> {
                        openPdfExternal(event.fileName)
                        true
                    }
                    is PdfPreviewViewModule.WindowEvents.SendPdf -> {
                        sendPdf(event.fileName)
                        true
                    }
                    PdfPreviewViewModule.WindowEvents.SendContext -> {
                        viewModule.onStart(requireContext())
                        true
                    }
                    is PdfPreviewViewModule.WindowEvents.OpenPdf -> {
                        openPdf(event.fileName)
                        true
                    }
                }.exhaustive

            }
        }

        viewModule.fileName.let {
            if (it != null) {
                openPdf(it)
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pdf_preview_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_pdf_external -> {
                viewModule.onOpenPdfClicked()
                true
            }

            R.id.send_pdf -> {
                viewModule.onSendPdfClicked()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun openPdf(fileName: String) {
        try {
            val pdfFile: InputStream = requireContext().openFileInput(fileName)
            pdfView.recycle()
            pdfView.fromStream(pdfFile)
                .password(null)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true) // double tap to zoom
                .onTap { true }
                .onRender { _, _, _ -> pdfView.fitToWidth() }
                .enableAnnotationRendering(true)
                .invalidPageColor(Color.GRAY)
                .load()

        } catch (e: FileNotFoundException) {
            Log.d("Error Opening Pdf" , "$fileName ${e.message}")
        }
    } // open in internal pdfViewer

    private fun openPdfExternal(fileName: String) {
        try {
            context?.let {
                val file = getFile(it, fileName)

                val uri = FileProvider.getUriForFile(it, AUTHORITY, file)

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(intent)
            }

        } catch (e: Exception) {
            Log.d("pdf_error", e.message.toString())
        }

    } // open in external pdfViewer

    @Throws(IOException::class)
    private fun getFile(context: Context, fileName: String): File {

        Log.d("pdf_error", fileName)
        val tempFile = File.createTempFile(".tempInvoice", fileName)
        tempFile.deleteOnExit()
        val os = FileOutputStream(tempFile)
        val inputStream: InputStream = context.openFileInput(fileName)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            FileUtils.copy(inputStream, os)
        }
        return tempFile
    }

    private fun sendPdf(fileName: String) {
        try {
            context?.let {
                val file = getFile(it, fileName)

                val uri = FileProvider.getUriForFile(it, AUTHORITY, file)

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "application/pdf"
                intent.putExtra(Intent.EXTRA_SUBJECT, file.name)
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(intent)
            }

        } catch (e: Exception) {
            Log.d("NewEstimateFragment pdf", e.message.toString())
        }
    }
}