package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.RecyclerViewListItemBinding

class AsteroidRecyclerListAdapter(val onClickListener: OnClickListener) : ListAdapter<Asteroid, AsteroidRecyclerListAdapter.AstroidsViewHolder>(DiffCallBack) {

    class AstroidsViewHolder(private val binding: RecyclerViewListItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            binding.asteroidProperty = asteroid
            binding.executePendingBindings()
        }

    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AstroidsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerViewListItemBinding.inflate(inflater, parent, false)
        return AstroidsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AsteroidRecyclerListAdapter.AstroidsViewHolder, position: Int) {
        val asteroidProperty = getItem(position)
        holder.bind(asteroidProperty)

        holder.itemView.setOnClickListener{
            onClickListener.onClick(asteroidProperty)
        }
    }

    class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }

}