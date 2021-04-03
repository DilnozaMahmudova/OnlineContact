package uz.dilnoza.onlinecontact.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_contact.view.*
import uz.dilnoza.onlinecontact.R
import uz.dilnoza.onlinecontact.source.room.entity.ContactData
import uz.dilnoza.onlinecontact.utils.SingleBlock
import uz.dilnoza.onlinecontact.utils.extentions.bindItem
import uz.dilnoza.onlinecontact.utils.extentions.inflate

class ContactAdapter:androidx.recyclerview.widget.ListAdapter<ContactData, ContactAdapter.ViewHolder>(ContactData.ITEM_CALLBACK){
    private var listenerEdit: SingleBlock<ContactData>? = null
    private var listenerDelete: SingleBlock<ContactData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=ViewHolder(parent.inflate(R.layout.item_contact))

    override fun onBindViewHolder(holder: ViewHolder, position: Int)=holder.bind()
    fun setOnItemEditListener(block: SingleBlock<ContactData>) {
        listenerEdit = block
    }

    fun setOnItemDeleteListener(block: SingleBlock<ContactData>) {
        listenerDelete = block
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init{
            itemView.apply {
                buttonMore.setOnClickListener {
                    val menu = PopupMenu(context,it)
                    menu.inflate(R.menu.menu_more)
                    menu.setOnMenuItemClickListener {
                        when(it.itemId){
                            R.id.menuDelete -> listenerDelete?.invoke(getItem(adapterPosition))
                            R.id.menuEdit -> listenerEdit?.invoke(getItem(adapterPosition))
                        }
                        true
                    }
                    menu.show()
                }
            }
        }
        fun bind() = bindItem {
            val d = getItem(adapterPosition)
            Name.text =d.name
            Number.text = d.phoneNumber
        }
    }
}
