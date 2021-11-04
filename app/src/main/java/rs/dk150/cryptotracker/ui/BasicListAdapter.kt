package rs.dk150.cryptotracker.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import rs.dk150.cryptotracker.R
import rs.dk150.cryptotracker.data.CryptoCurrency
import rs.dk150.cryptotracker.databinding.BasicViewHolderBinding


class BasicListAdapter(
    private val list: List<CryptoCurrency>?,
    private val listener: CryptoListListener
) : RecyclerView.Adapter<BasicListAdapter.BasicViewHolder>(),
    RecyclerTouchListener.ClickListener {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                recyclerView.context,
                this
            )
        )
    }

    private var selectedView: View? = null

    interface CryptoListListener {
        fun listItemClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicViewHolder {
        return BasicViewHolder(
            BasicViewHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BasicViewHolder, position: Int) {
        list?.let { list ->
            val pos = holder.bindingAdapterPosition
            val element = list.elementAt(pos)
            holder.nameTextView.text = element.fullName
            holder.symbolTextView.text = element.symbol
            val imageView = holder.imageView
            imageView.visibility = View.INVISIBLE
            val progressBar = holder.progressBar
            progressBar.visibility = View.VISIBLE

            var requestBuilder =
                Glide.with(imageView.context).load("https://cryptocompare.com" + element.imageUrl)
            requestBuilder = requestBuilder.listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.INVISIBLE
                    imageView.setImageDrawable(
                        getDrawable(
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

    override fun getItemCount(): Int = list?.size ?: 0

    override fun onDown(view: View?) {
        selectedView = view
        selectedView?.isSelected = true
    }

    override fun onUp() {
        selectedView?.isSelected = false
        selectedView = null
    }

    override fun onShortLongClick(view: View?, position: Int) {
        view?.playSoundEffect(SoundEffectConstants.CLICK)
        listener.listItemClicked(position)
    }

    override fun onViewRecycled(holder: BasicViewHolder) {
        super.onViewRecycled(holder)
        val imageView = holder.imageView
        Glide.with(imageView.context).clear(imageView)
    }

    class BasicViewHolder(binding: BasicViewHolderBinding) : RecyclerView.ViewHolder(binding.root) {
        var progressBar: ProgressBar = binding.progress
        var imageView: ImageView = binding.image
        var nameTextView: TextView = binding.name
        var symbolTextView: TextView = binding.symbol
    }
}