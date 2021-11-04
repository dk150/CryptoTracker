package rs.dk150.cryptotracker.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import rs.dk150.cryptotracker.R
import rs.dk150.cryptotracker.data.CryptoField
import rs.dk150.cryptotracker.databinding.GeneralImageViewHolderBinding
import rs.dk150.cryptotracker.databinding.GeneralTextViewHolderBinding


class GeneralListAdapter(
    private val list: List<CryptoField>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TEXT -> GeneralTextViewHolder(
                GeneralTextViewHolderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> GeneralImageViewHolder(
                GeneralImageViewHolderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        list?.let {
            val pos = viewHolder.bindingAdapterPosition
            val element = list[pos]
            when (viewHolder.itemViewType) {
                TEXT -> {
                    val holder = viewHolder as GeneralTextViewHolder
                    val valueLabel = holder.valueLabel
                    valueLabel.text =
                        String.format(
                            valueLabel.context.getString(R.string.value_label),
                            element.label
                        )
                    holder.value.text = formatValue(element.value)
                }
                else -> {
                    val holder = viewHolder as GeneralImageViewHolder
                    val imageView = holder.value
                    val progressBar = holder.progress
                    var requestBuilder =
                        Glide.with(imageView.context)
                            .load("https://cryptocompare.com" + element.value)
                    requestBuilder = requestBuilder.listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.INVISIBLE
                            imageView.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    imageView.context,
                                    R.drawable.placeholder_img
                                )
                            )
                            imageView.visibility = View.VISIBLE
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.INVISIBLE
                            imageView.visibility = View.VISIBLE
                            return false
                        }

                    })
                    requestBuilder.centerInside().into(imageView)
                }
            }
        }
    }

    private fun formatValue(value: String): CharSequence {
        val v = value.trim()
        return if (v[0] == '/') {
            "http://www.cryptocompare.com${v}"
        } else {
            return v
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return if (list?.get(position)?.label == "imageUrl") {
            IMAGE
        } else {
            TEXT
        }
    }

    companion object {
        private const val TEXT = 0
        private const val IMAGE = 1
    }

    class GeneralTextViewHolder(binding: GeneralTextViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val valueLabel: TextView = binding.valueLabel
        val value: TextView = binding.value

    }

    class GeneralImageViewHolder(binding: GeneralImageViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val value: ImageView = binding.image
        val progress: View = binding.progress
    }

}