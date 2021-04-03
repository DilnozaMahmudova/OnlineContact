package uz.dilnoza.onlinecontact.source.room.dao

import androidx.room.Dao
import androidx.room.Query
import uz.dilnoza.onlinecontact.source.room.entity.ContactData
@Dao
interface ContactDao:BaseDao<ContactData> {
    @Query("SELECT * FROM ContactData")
    fun getAll():List<ContactData>

}