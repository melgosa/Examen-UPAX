package com.melgosadev.examenkotlin.ui.movies

import android.content.Context
import android.media.Image
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.melgosadev.examenkotlin.R
import com.melgosadev.examenkotlin.models.NowPlayingMovies
import com.squareup.picasso.Picasso

const val URL_BASE = "https://image.tmdb.org/t/p/w500"

class MoviesAdapter(private val context: Context, private val nowPlayingMovies: NowPlayingMovies) : PagerAdapter() {
    override fun getCount(): Int {
        return nowPlayingMovies.results.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    /**
     * Se ajusta el item con la información obtenida de cada película (Imagen, título y descripción)
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.card_item, container, false)
        val model = nowPlayingMovies.results[position]
        val title = model.title
        val description = model.overview
        val urlImage = URL_BASE + model.poster_path

        val image: ImageView = view.findViewById(R.id.imgBanner)
        val tvTitle: TextView = view.findViewById(R.id.tvMovieTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvMovieDescription)


        if(internetAvailable(context))
            Picasso.get().load(urlImage).into(image)
        else
            image.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.common_full_open_on_phone
                )
            )
        tvTitle.text = title
        tvDescription.text = description

        container.addView(view, position)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }

    /**
     * Verifica si hay conexión a internet o no
     */
    private fun internetAvailable(context: Context): Boolean {
        val cmg = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cmg.getNetworkCapabilities(cmg.activeNetwork)?.let { networkCapabilities ->
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
        } else {
            return cmg.activeNetworkInfo?.isConnectedOrConnecting == true
        }

        return false
    }

}