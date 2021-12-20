package com.melgosadev.examenkotlin.ui.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.melgosadev.examenkotlin.databinding.FragmentMoviesBinding
import com.melgosadev.examenkotlin.models.NowPlayingMovies
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFragment : Fragment() {

    private lateinit var moviesViewModel: MoviesViewModel
    private var _binding: FragmentMoviesBinding? = null
    private var moviesAdapter: MoviesAdapter? = null
    private val binding get() = _binding!!

    companion object{
        const val PADDING_LEFT = 100
        const val PADDING_TOP = 0
        const val PADDING_RIGHT = 100
        const val PADDING_BOTTOM = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        moviesViewModel =
            ViewModelProvider(this).get(MoviesViewModel::class.java)

        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Se consume servicio web par obtner el listado de películas que estan actualmente en cine
        moviesViewModel.onCreate(requireContext())

        //Se observa la variable de interés (nowPlayingMovies) y cuando cambia se ajusta al adaptador
        moviesViewModel.nowPlayingMovies.observe(viewLifecycleOwner, { nowPlayingMovies ->
            setMoviesAdapter(nowPlayingMovies)
        })

        return root
    }

    /**
     * Se ajusta el adaptador par mostar las películas (una imagen, título y descripción)
     */
    private fun setMoviesAdapter(nowPlayingMovies: NowPlayingMovies){
        moviesAdapter = MoviesAdapter(requireActivity(), nowPlayingMovies)
        _binding?.viewPager?.adapter = moviesAdapter
        _binding?.viewPager?.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}