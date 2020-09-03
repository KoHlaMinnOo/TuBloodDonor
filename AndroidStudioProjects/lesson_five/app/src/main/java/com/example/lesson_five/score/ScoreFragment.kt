package com.example.lesson_five.score

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.lesson_five.R
import com.example.lesson_five.databinding.FragmentScoreBinding


class ScoreFragment : Fragment() {

    private lateinit var viewModel: ScoreViewModel
    private lateinit var viewModelFactory: ScoreViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentScoreBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_score, container, false
        )
        val args: ScoreFragmentArgs by navArgs()
        val score = args.score

        viewModelFactory = ScoreViewModelFactory(args.score)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(
            ScoreViewModel::class.java
        )

        binding.textViewScore.text = score.toString()
        binding.btnPlayAgain.setOnClickListener {
            Navigation.findNavController(it).navigate(
                R.id.action_scoreFragment_to_gameFragment
            )
        }

        return binding.root
    }


}