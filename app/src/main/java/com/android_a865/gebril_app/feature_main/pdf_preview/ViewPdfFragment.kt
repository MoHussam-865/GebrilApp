package com.android_a865.gebril_app.feature_main.pdf_preview

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import com.github.barteksc.pdfviewer.PDFView
import dagger.hilt.android.AndroidEntryPoint
import gebril_app.R
import gebril_app.databinding.FragmentViewPdfBinding
import kotlinx.coroutines.flow.collect
import java.io.*

@AndroidEntryPoint
class ViewPdfFragment : Fragment(R.layout.fragment_view_pdf) {

    private val viewModule by viewModels<PdfPreviewViewModule>()
    private lateinit var myPdf: PDFView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentViewPdfBinding.bind(view)

        binding.apply {
            myPdf = pdfView

            save.setOnClickListener {
                viewModule.onSaveClicked()
            }

            send.setOnClickListener {
                viewModule.onSendPdfClicked()
            }

        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModule.windowEvents.collect { event ->
                when (event) {
                    PdfPreviewViewModule.WindowEvents.SendContext -> {
                        viewModule.onStart(requireContext())
                        true
                    }
                    is PdfPreviewViewModule.WindowEvents.OpenPdf -> {
                        openPdf(event.fileName)
                        true
                    }
                    is PdfPreviewViewModule.WindowEvents.Finish -> {
                        findNavController().navigate(event.direction)
                        true
                    }
                    is PdfPreviewViewModule.WindowEvents.ShowMessage -> {
                        Toast.makeText(requireContext(),event.msg, Toast.LENGTH_LONG).show()
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
    }


    private fun openPdf(fileName: String) {
        try {
            val pdfFile: InputStream = requireContext().openFileInput(fileName)
            myPdf.recycle()
            myPdf.fromStream(pdfFile)
                .password(null)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true) // double tap to zoom
                .onTap { true }
                .onRender { _, _, _ -> myPdf.fitToWidth() }
                .enableAnnotationRendering(true)
                .invalidPageColor(Color.GRAY)
                .load()

        } catch (e: FileNotFoundException) {
            Log.d("Error Opening Pdf" , "$fileName ${e.message}")
        }
    } // open in internal pdfViewer


}