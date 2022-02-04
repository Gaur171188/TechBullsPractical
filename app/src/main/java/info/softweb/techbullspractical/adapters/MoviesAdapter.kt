package info.softweb.techbullspractical.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import info.softweb.techbullspractical.databinding.RowMovieBinding
import info.softweb.techbullspractical.models.Search
import info.softweb.techbullspractical.utils.MoviesDiffUtil

class MoviesAdapter : RecyclerView.Adapter<MoviesAdapter.MyViewHolder>() {

    private var movies = emptyList<Search>()

    class MyViewHolder(private val binding: RowMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Search){
            binding.movie = movie
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowMovieBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = movies[position]
        holder.bind(currentRecipe)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun setData(newData: List<Search>){
        val moviesDiffUtil = MoviesDiffUtil(movies, newData)
        val diffUtilResult = DiffUtil.calculateDiff(moviesDiffUtil)
        movies = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}