package com.astontraineeship.recyclerviewtraining

interface OnListItemClickListener {
    fun onItemClick(contact: Contact)

    fun onItemDrag(oldPosition: Int, newPosition: Int)
}