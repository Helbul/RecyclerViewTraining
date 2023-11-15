package com.astontraineeship.recyclerviewtraining

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox

class RVAdapter(
    private var onListItemClickListener: OnListItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    private var contacts: ArrayList<Contact> = arrayListOf <Contact>()
    private var isDeleting: Boolean = false
    private var selectedItemList: ArrayList<Contact> = arrayListOf <Contact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ContactViewHolder(inflater.inflate(R.layout.item_view, parent, false) as View)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ContactViewHolder
        holder.bind(contacts[position])
    }

    fun changeStatusDeleting() {
        isDeleting = !isDeleting
        if(!isDeleting) clearStatusDeletingForEachContact()
        notifyDataSetChanged()
    }

    private fun clearStatusDeletingForEachContact() {
        contacts.forEach{contact: Contact -> contact.isSelected = false }
    }

    fun getSelectedItems() = selectedItemList

    fun clearSelectedItems()  {selectedItemList.clear()}

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

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view), ItemTouchHelperViewHolder {
        fun bind(contact: Contact) {
            with(itemView) {
                findViewById<TextView>(R.id.rv_id).text = contact.id.toString()
                val fullName = "${contact.firstName} ${contact.lastName}"
                findViewById<TextView>(R.id.rv_name).text = fullName
                findViewById<TextView>(R.id.rv_number).text = contact.number

                if (isDeleting) {
                    val position = this@ContactViewHolder.layoutPosition
                    with(findViewById<MaterialCheckBox>(R.id.rv_checkbox)){
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
                    findViewById<MaterialCheckBox>(R.id.rv_checkbox).visibility = View.GONE
                }
                setOnClickListener { onListItemClickListener.onItemClick(contact) }
            }
        }

        override fun onItemSelected() {
            itemView.findViewById<ConstraintLayout>(R.id.item_layout).setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.findViewById<ConstraintLayout>(R.id.item_layout).setBackgroundColor(0)
        }
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