package uz.dilnoza.onlinecontact.ui.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.dilnoza.onlinecontact.R
import uz.dilnoza.onlinecontact.app.App
import uz.dilnoza.onlinecontact.source.AppDatabase
import uz.dilnoza.onlinecontact.source.retrofit.ApiClient
import uz.dilnoza.onlinecontact.source.retrofit.ContactApi
import uz.dilnoza.onlinecontact.source.retrofit.ResponseData
import uz.dilnoza.onlinecontact.source.room.entity.ContactData
import uz.dilnoza.onlinecontact.ui.adapter.ContactAdapter
import uz.dilnoza.onlinecontact.ui.dialog.ContactDialog
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val adapter = ContactAdapter()
    private val api = ApiClient.retrofit.create(ContactApi::class.java)
    private val database = AppDatabase.getDatabase(App.instance)
    private val contactDao = database.contactDao()
    private val executor = Executors.newSingleThreadExecutor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemDeleteListener {
            val contact = ContactData(it.id, it.name, it.phoneNumber)
            api.remove(contact).enqueue(object : Callback<ResponseData<ContactData>> {
                override fun onFailure(call: Call<ResponseData<ContactData>>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "$t", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<ResponseData<ContactData>>,
                    response: Response<ResponseData<ContactData>>
                ) {
                    val data = response.body() ?: return
                    if (data.status == "OK") {
                        val ls = adapter.currentList.toMutableList()
                        ls.remove(data.data)
                        runOnUiThread { adapter.submitList(ls) }
                        executor.execute { contactDao.delete(it) }
                    } else {
                        Toast.makeText(this@MainActivity, data.message, Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
            adapter.setOnItemEditListener {
                val dialog = ContactDialog(this, "Edit")
                dialog.setContactData(it)
                dialog.setOnClickListener {
                    api.update(it).enqueue(object : Callback<ResponseData<ContactData>> {
                        override fun onFailure(
                            call: Call<ResponseData<ContactData>>,
                            t: Throwable
                        ) {
                            Toast.makeText(this@MainActivity, "onFailure", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onResponse(
                            call: Call<ResponseData<ContactData>>,
                            response: Response<ResponseData<ContactData>>
                        ) {
                            val data = response.body() ?: return
                            if (data.status == "OK") {
                                val ls = adapter.currentList.toMutableList()
                                val index = ls.indexOfFirst { it.id == data.data!!.id }
                                ls[index] = data.data
                                 adapter.submitList(ls)
                                adapter.notifyItemChanged(index)
                               executor.execute {  contactDao.update(data.data!!)}
                            } else {
                                Toast.makeText(this@MainActivity, data.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    })
                }
                dialog.show()
            }



    }

    fun loadData() {
        executor.execute {
            val ls = contactDao.getAll()
            runOnUiThread {
                adapter.submitList(ls)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuAdd -> {
                val dialog = ContactDialog(this, "Add")
                dialog.setOnClickListener {
                    api.add(it).enqueue(object : Callback<ResponseData<ContactData>> {
                        override fun onFailure(
                            call: Call<ResponseData<ContactData>>,
                            t: Throwable
                        ) {
                            Toast.makeText(this@MainActivity, "$t", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onResponse(
                            call: Call<ResponseData<ContactData>>,
                            response: Response<ResponseData<ContactData>>
                        ) {
                            val data = response.body() ?: return
                            if (data.status == "OK") {
                                val ls = adapter.currentList.toMutableList()
                                ls.add(data.data)
                                runOnUiThread {
                                    adapter.submitList(ls)
                                    list.smoothScrollToPosition(ls.size - 1)}
                               executor.execute {  contactDao.insert(data.data!!)}
                            } else {
                                Toast.makeText(this@MainActivity, data.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    })
                }
                dialog.show()


            }
            R.id.menuBack -> finish()
            R.id.menuRefresh -> {
                loaderShow()
                api.getAll().enqueue(object : Callback<ResponseData<List<ContactData>>> {
                    override fun onFailure(
                        call: Call<ResponseData<List<ContactData>>>,
                        t: Throwable
                    ) {
                        Toast.makeText(this@MainActivity, "$t", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<ResponseData<List<ContactData>>>,
                        response: Response<ResponseData<List<ContactData>>>
                    ) {
                        val data = response.body() ?: return
                        if (data.status == "OK") {
                            executor.execute {
                            contactDao.deleteAll(contactDao.getAll())
                           runOnUiThread { adapter.submitList(data.data) }
                            contactDao.insertAll(data.data!!)
                            }
                        } else {
                            Toast.makeText(this@MainActivity, data.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                })
                hideLoader()
            }
        }
        return true
    }

    private fun loaderShow() {
        loader.visibility = View.VISIBLE
        view.visibility = View.VISIBLE

    }

    fun hideLoader() {
        loader.visibility = View.INVISIBLE
        view.visibility = View.INVISIBLE
    }


    private val seriesData: MutableList<DataEntry> = ArrayList<DataEntry>()
    private val api = ApiClient.retrofit.create(UserApi::class.java)
    lateinit var balance: List<ValuesData>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        api.getBalance().enqueue(object : Callback<ResponseData<List<ValuesData>>> {
            override fun onResponse(
                call: Call<ResponseData<List<ValuesData>>>,
                response: Response<ResponseData<List<ValuesData>>>
            ) {
                var data = response.body() ?: return
                balance = data.data!!
            }

            override fun onFailure(call: Call<ResponseData<List<ValuesData>>>, t: Throwable) {
            }
        })
        api.getWorkers().enqueue(object : Callback<ResponseData<List<ValuesData>>> {
            override fun onResponse(
                call: Call<ResponseData<List<ValuesData>>>,
                response: Response<ResponseData<List<ValuesData>>>
            ) {
                var data = response.body() ?: return
                setData(data.data!!)
            }

            override fun onFailure(call: Call<ResponseData<List<ValuesData>>>, t: Throwable) {
            }
        })
    }

    private inner class CustomDataEntry internal constructor(
        x: String?,
        value: Number?,
    ) : ValueDataEntry(x, value)

    private fun setData(list: List<ValuesData>) {
        var i = 0
        val data = Calendar.getInstance()
        var currentDate = data.get(Calendar.DATE) - 9
        for (k in balance) {
            if (currentDate <= 0) {
                currentDate += 30
            }
            if (currentDate == 31) {
                currentDate = 1
            }
            seriesData.add(CustomDataEntry("$currentDate", k.value / list[i++].value))
            currentDate++
        }
        loadChart()
    }

}