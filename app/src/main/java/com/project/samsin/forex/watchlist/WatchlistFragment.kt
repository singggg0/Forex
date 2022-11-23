package com.project.samsin.forex.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.samsin.forex.databinding.FragmentWatchlistBinding
import com.project.samsin.forex.extension.toFormatDecimal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchlistFragment : Fragment() {
    private var binding: FragmentWatchlistBinding? = null
    private val viewModel: WatchlistViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observeVm()
        viewModel.onCreate()
    }

    private fun initUi() {
        binding?.apply {
            rvWatchlist.let {
                it.itemAnimator = null
                it.adapter = WatchlistAdapter()
                it.layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun observeVm() {
        viewModel.quotationResults.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding?.tvEmpty?.visibility = View.VISIBLE
                binding?.rvWatchlist?.visibility = View.GONE
            } else {
                (binding?.rvWatchlist?.adapter as WatchlistAdapter?)?.submitList(it.toList())
                binding?.tvEmpty?.visibility = View.GONE
                binding?.rvWatchlist?.visibility = View.VISIBLE
            }
        }
        viewModel.used.observe(viewLifecycleOwner) {
            binding?.tvUsed?.text = "$${it.toFormatDecimal(2)}"
        }
        viewModel.margin.observe(viewLifecycleOwner) {
            binding?.tvMargin?.text = "$${it.toFormatDecimal(2)}"
        }
        viewModel.equity.observe(viewLifecycleOwner) {
            binding?.tvEquity?.text = "$${it.toFormatDecimal(2)}"
        }
        viewModel.balance.observe(viewLifecycleOwner) {
            binding?.tvBalance?.text = "$${it.toFormatDecimal(2)}"
        }
        viewModel.showLoadingDialog.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        viewModel.showError.observe(viewLifecycleOwner) {
            showError(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.rvWatchlist?.adapter = null
        binding = null
    }

    private fun showLoading(show: Boolean) {
        binding?.apply {
            if (show) {
                rvWatchlist.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                rvWatchlist.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}