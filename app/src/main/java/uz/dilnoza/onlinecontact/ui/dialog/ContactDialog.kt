package uz.dilnoza.onlinecontact.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_cont.view.*
import uz.dilnoza.onlinecontact.R
import uz.dilnoza.onlinecontact.source.room.entity.ContactData
import uz.dilnoza.onlinecontact.utils.SingleBlock


class ContactDialog(context: Context, actionName:String) : AlertDialog(context) {
    private val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_cont, null, false)
    private var listener: SingleBlock<ContactData>? = null
    private var contactDatas: ContactData? = null

    init {
        setView(contentView)
        setButton(BUTTON_POSITIVE, actionName) { _, _ ->
            val data = contactDatas ?: ContactData(0,"","")
            data.name = contentView.inputName.text.toString()
            data.phoneNumber=contentView.inputNumber.text.toString()
            listener?.invoke(data)
        }
        setButton(BUTTON_NEGATIVE, "Cancel") { _, _ -> }
    }

    fun setContactData(contactDatas: ContactData) = with(contentView) {
        this@ContactDialog.contactDatas = contactDatas
        inputName.setText(contactDatas.name)
        inputNumber.setText(contactDatas.phoneNumber)
    }


    fun setOnClickListener(block: SingleBlock<ContactData>) {
        listener = block
    }
}