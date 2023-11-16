package com.astontraineeship.recyclerviewtraining

import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.fakerConfig

fun createListContact() : ArrayList<Contact> {
    val contactsList = arrayListOf<Contact>()
    val config = fakerConfig { locale = "ru" }
    val faker = Faker(config)
    for (i in 1..100) {
        val contact: Contact
            with(faker) {
                contact = Contact(
                    CreateId.getNext(),
                    name.firstName(),
                    name.lastName(),
                    phoneNumber.phoneNumber()
                )
            }
        contactsList.add(contact)
    }
    return contactsList
}