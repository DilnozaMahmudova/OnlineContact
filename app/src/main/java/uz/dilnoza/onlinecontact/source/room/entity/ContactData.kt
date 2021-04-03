package uz.dilnoza.onlinecontact.source.room.entity

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactData(
    @PrimaryKey val id: Int = 0,
    var name: String,
    var phoneNumber: String
){
    companion object{
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<ContactData>() {
            override fun areItemsTheSame(oldItem: ContactData, newItem: ContactData) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ContactData, newItem: ContactData) = oldItem.name == newItem.name && oldItem.phoneNumber == newItem.phoneNumber
        }
    }
}