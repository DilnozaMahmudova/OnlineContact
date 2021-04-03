package uz.dilnoza.onlinecontact.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.dilnoza.onlinecontact.source.room.dao.ContactDao
import uz.dilnoza.onlinecontact.source.room.entity.ContactData

@Database(entities = [ContactData::class],version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun contactDao():ContactDao
    companion object{
        @Volatile
        private var INSTANS:AppDatabase?=null
        fun getDatabase(context: Context):AppDatabase{
            val tempInstance= INSTANS
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance=Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"app_database").build()
                INSTANS=instance
                return instance
            }
        }
    }
}