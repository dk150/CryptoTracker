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


class CryptoListAdapter(
    private val list: Collection<CryptoCurrency>?,
    private val listener: CryptoListListener
) : RecyclerView.Adapter<CryptoListAdapter.CryptoViewHolder>(),
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

    private var selectedItem: Int = RecyclerView.NO_POSITION

    interface CryptoListListener {
        fun listItemClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.crypto_basic_view_holder, parent, false)
        return CryptoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        list?.let { list ->
            val pos = holder.adapterPosition
            holder.rootView.isSelected = pos == selectedItem
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

    class CryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootView: View = itemView.rootView
        var progressBar: ProgressBar = itemView.findViewById(R.id.progress)
        var imageView: ImageView = itemView.findViewById(R.id.image)
        var nameTextView: TextView = itemView.findViewById(R.id.name)
        var symbolTextView: TextView = itemView.findViewById(R.id.symbol)
    }

    override fun onDown(view: View?, position: Int) {
        val temp = selectedItem
        selectedItem = position
        view?.isSelected = true
        notifyItemChanged(temp)
        notifyItemChanged(position)
    }

    override fun onUp(view: View?, position: Int) {
        val temp = selectedItem
        selectedItem = RecyclerView.NO_POSITION
        notifyItemChanged(temp)
    }

    override fun onShortLongClick(view: View?, position: Int) {
        view?.playSoundEffect(SoundEffectConstants.CLICK)
        listener.listItemClicked(position)
    }
}