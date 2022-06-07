/*
 * Nextcloud Talk application
 *
 * @author Álvaro Brey
 * Copyright (C) 2022 Álvaro Brey
 * Copyright (C) 2022 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.nextcloud.talk.polls.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import autodagger.AutoInjector
import com.nextcloud.talk.application.NextcloudTalkApplication
import com.nextcloud.talk.databinding.DialogPollVoteBinding
import com.nextcloud.talk.polls.viewmodels.PollViewModel
import com.nextcloud.talk.polls.viewmodels.PollVoteViewModel
import javax.inject.Inject

@AutoInjector(NextcloudTalkApplication::class)
class PollVoteFragment(private val parentViewModel: PollViewModel) : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: PollVoteViewModel

    var _binding: DialogPollVoteBinding? = null
    val binding: DialogPollVoteBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NextcloudTalkApplication.sharedApplication!!.componentApplication.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[PollVoteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPollVoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentViewModel.viewState.observe(viewLifecycleOwner) { state ->
            if (state is PollViewModel.PollOpenState) {
                val poll = state.poll
                binding.radioGroup.removeAllViews()
                poll.options?.map { option ->
                    RadioButton(context)
                        .apply { text = option }
                        .also {
                            it.setOnClickListener {
                                // todo
                            }
                        }
                }?.forEach {
                    binding.radioGroup.addView(it)
                }
            }
        }
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            // todo set selected in viewmodel
        }
        // todo observe viewmodel checked, set view checked with it
        // todo listen to button click, submit
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
