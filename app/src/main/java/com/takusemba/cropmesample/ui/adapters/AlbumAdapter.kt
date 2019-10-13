package com.takusemba.cropmesample.ui.adapters

import android.content.Context
import android.graphics.Point
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView

import com.takusemba.cropmesample.R
import com.takusemba.cropmesample.models.Album
import com.takusemba.cropmesample.ui.OnPhotoClickListener

/**
 * Adapter to show [albums]
 */
class AlbumAdapter(
        private val context: Context,
        private val albums: MutableList<Album>,
        private val listener: OnPhotoClickListener
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    private val length: Int

    init {
        val point = Point()
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.defaultDisplay.getSize(point)
        length = point.x / COLUMNS
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_album, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albums[position]
        holder.title.text = album.name

        val adapter = PhotoAdapter(album.photos, listener, length)
        val layoutManager = GridLayoutManager(context, COLUMNS)
        holder.recyclerView.layoutManager = layoutManager
        holder.recyclerView.adapter = adapter
        holder.recyclerView.setHasFixedSize(true)
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
    }

    fun addItem(album: Album) {
        albums.add(album)
        notifyDataSetChanged()
    }

    fun clear() {
        albums.clear()
        notifyDataSetChanged()
    }

    companion object {

        private const val COLUMNS = 4
    }
}