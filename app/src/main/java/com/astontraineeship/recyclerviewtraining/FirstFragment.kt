package com.astontraineeship.recyclerviewtraining

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.astontraineeship.recyclerviewtraining.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {
    companion object {
        fun newInstance() = FirstFragment()
        const val ARG_CONTACT = "ARG_CONTACT"
    }

    private val binding get() = _binding!!
    private var _binding: FragmentFirstBinding? = null
    private var isDeleting = false
    private var contactsList = arrayListOf<Contact>()
    private var idList = listOf<Int>()
    private lateinit var adapter : RVAdapter
    private lateinit var buttonDelete: Button
    private lateinit var buttonCancel: Button
    private lateinit var buttonAdd: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Удалить?!?!
        setHasOptionsMenu(true)

        contactsList = createListContact()
        idList = contactsList.map { contact : Contact ->  contact.id}

        val recyclerView = binding.recyclerView
        buttonDelete = binding.buttonDelete
        buttonCancel = binding.buttonCancel
        buttonAdd = binding.buttonAdd

        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = RVAdapter(object : OnListItemClickListener {
            override fun onItemClick(contact: Contact) {
                Toast.makeText(requireContext(), "!!!!!!!11", Toast.LENGTH_LONG).show()
                DetailContactFragment
                        //Проверить!!!!!!!!!!!!
                    .addInstance(contact)
                    .show(parentFragmentManager, "DetailContactFragment")
            }


            override fun onItemDrag(oldPosition: Int, newPosition: Int) {
                contactsList.removeAt(oldPosition).apply {
                    contactsList.add(
                        if (newPosition > oldPosition) newPosition - 1 else newPosition,
                        this
                    )
                }
                //Удалить?!?!?!!
                idList = contactsList.map { contact : Contact ->  contact.id}
            }
        })

        recyclerView.adapter = adapter
        ItemTouchHelper(ItemTouchHelperCallback(adapter))
            .attachToRecyclerView(recyclerView)

        adapter.setContacts(contactsList)

        buttonDelete.setOnClickListener {
            contactsList = ArrayList(contactsList)
            val deletingList = adapter.getSelectedItems().toSet()
            contactsList.removeAll(deletingList)
            idList = contactsList.map { contact -> contact.id}
            adapter.clearSelectedItems()
            adapter.setContacts(contactsList)
        }

        buttonAdd.setOnClickListener {
            DetailContactFragment
                .addInstance(null)
                .show(parentFragmentManager, "DetailContactFragment")
        }


        //Объединить в функцию (повтор действия меню)
        buttonCancel.setOnClickListener{
            isDeleting = false
            buttonAdd.visibility = View.VISIBLE
            buttonDelete.visibility = View.GONE
            buttonCancel.visibility = View.GONE
            adapter.changeStatusDeleting()
        }

        parentFragmentManager
            .setFragmentResultListener(
                DetailContactFragment.KEY_RESULT_CONTACT,
                viewLifecycleOwner
            ) { _, result ->
                val contact : Contact? = result.getParcelable(ARG_CONTACT)
                contact?.let {contact ->
//                    if (!idList.contains(contact.id)) {
//                        contactsList = ArrayList(contactsList)
//                        contactsList.add(contact)
//                        adapter.setContacts(contactsList)
//                    } else {
//                        val position = contactsList.indexOfFirst { it.id == contact.id }
//                        contactsList[position] = contact
//                    }
                    val position = contactsList.indexOfFirst { it.id == contact.id }
                    if (position == -1) {
                        contactsList = ArrayList(contactsList)
                        contactsList.add(contact)
                        adapter.setContacts(contactsList)
                    } else {
                        contactsList = ArrayList(contactsList)
                        contactsList[position] = contact
                        adapter.setContacts(contactsList)
                    }
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            if (!isDeleting) {
                isDeleting = true
                buttonAdd.visibility = View.GONE
                buttonDelete.visibility = View.VISIBLE
                buttonCancel.visibility = View.VISIBLE
            } else {
                isDeleting = false
                buttonAdd.visibility = View.VISIBLE
                buttonDelete.visibility = View.GONE
                buttonCancel.visibility = View.GONE
            }
            adapter.changeStatusDeleting()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
