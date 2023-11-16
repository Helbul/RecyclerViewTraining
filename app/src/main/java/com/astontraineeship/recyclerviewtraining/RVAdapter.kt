package com.astontraineeship.recyclerviewtraining

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.astontraineeship.recyclerviewtraining.databinding.ItemViewBinding

class RVAdapter(
    private var onListItemClickListener: OnListItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    var selectedItemList: ArrayList<Contact> = arrayListOf <Contact>()
    private var contacts: ArrayList<Contact> = arrayListOf <Contact>()
    private var isDeleting: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ContactViewHolder
        holder.bind(contacts[position])
    }

    fun setContacts(newContacts: ArrayList<Contact>) {
        val diffCallback = ContactsDiffCallback(contacts, newContacts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        contacts = newContacts
        diffResult.dispatchUpdatesTo(this)
    }

    private class ContactsDiffCallback(private val oldList: List<Contact>, private val newList: List<Contact>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }

    inner class ContactViewHolder(private val itemBinding: ItemViewBinding) : RecyclerView.ViewHolder(itemBinding.root), ItemTouchHelperViewHolder {
        fun bind(contact: Contact) {
            with(itemBinding) {
                rvId.text = contact.id.toString()
                val fullName = "${contact.firstName} ${contact.lastName}"
                rvName.text = fullName
                rvNumber.text = contact.number

                if (isDeleting) {
                    val position = this@ContactViewHolder.layoutPosition
                    with(rvCheckbox){
                        visibility = View.VISIBLE
                        isChecked = contact.isSelected

                        setOnClickListener {
                            if(isChecked) {
                                if(!selectedItemList.contains(contact))selectedItemList.add(contact)
                            } else if(selectedItemList.contains(contact))selectedItemList.remove(contact)
                            contacts[position].isSelected = isChecked
                        }
                    }
                } else {
                    rvCheckbox.visibility = View.GONE
                }
                    root.setOnClickListener { onListItemClickListener.onItemClick(contact) }
            }
        }
        override fun onItemSelected() {
            itemBinding.itemLayout.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemBinding.itemLayout.setBackgroundColor(0)
        }

    }

    fun changeStatusDeleting() {
        isDeleting = !isDeleting
        if(!isDeleting) clearStatusDeletingForEachContact()
        notifyDataSetChanged()
    }

    private fun clearStatusDeletingForEachContact() {
        contacts.forEach{contact: Contact -> contact.isSelected = false }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val newContacts = ArrayList(contacts)
        newContacts.removeAt(fromPosition).apply {
            newContacts.add(if (toPosition > fromPosition) toPosition - 1 else toPosition,
                this)
        }
        setContacts(newContacts)
        contacts = newContacts

        onListItemClickListener.onItemDrag(fromPosition, toPosition)
    }

}