package com.android_a865.gebril_app.features.item_details

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.databinding.ActivityDetailsBinding
import com.android_a865.gebril_app.features.shopping.ItemsChooseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<ItemsChooseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailsBinding.inflate(layoutInflater)

        val item = intent.getParcelableExtra<InvoiceItem>("item")

        if (item == null) finish()

        item?.let {

            binding.apply {
                setContentView(root)

                setSupportActionBar(toolbar)
                toolbarLayout.title = item.fullName

                fab.setOnClickListener { view ->
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show()
                }
            }

        }


    }
}