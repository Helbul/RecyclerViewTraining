package com.astontraineeship.recyclerviewtraining

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.astontraineeship.recyclerviewtraining.databinding.FragmentDetailContactBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailContactFragment : BottomSheetDialogFragment(){
    companion object {
        const val KEY_RESULT_CONTACT = "KEY_RESULT_CONTACT"
        const val ARG_CONTACT = "ARG_CONTACT"

        fun addInstance(contact: Contact?): DetailContactFragment {
            return contact?.let {
                val args = Bundle()
                args.putParcelable(ARG_CONTACT, contact)

                val fragment = DetailContactFragment()
                fragment.arguments = args
                fragment
            } ?: run {
                DetailContactFragment()
            }
        }
    }

    private val binding get() = _binding!!
    private var _binding: FragmentDetailContactBinding? = null
    private var contact: Contact? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDetailContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments

        if (arguments != null) {
            contact = arguments.getParcelable(ARG_CONTACT)
            contact?.let { showContact(it) }
        }

        with(binding) {
            buttonSave.setOnClickListener {
                val firstName = inputFirstname.text.toString()
                val lastName = inputLastname.text.toString()
                val number = inputNumber.text.toString()

                if (number != "") {
                    contact?.let {
                        val id = contact!!.id
                        contact = Contact(id, firstName, lastName, number)
                    }?:run {
                        contact = Contact(CreateId.getNext(), firstName, lastName, number)
                    }
                    addContact(contact!!)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addContact(contact: Contact) {
        val bundle = Bundle()
        bundle.putParcelable(ARG_CONTACT, contact)

        parentFragmentManager.setFragmentResult(KEY_RESULT_CONTACT, bundle)
        dismiss()
    }

    private fun showContact(contact: Contact) {
        with(binding) {
            contactId.text = contact.id.toString()
            inputFirstname.setText(contact.firstName)
            inputLastname.setText(contact.lastName)
            inputNumber.setText(contact.number)
        }
    }
}