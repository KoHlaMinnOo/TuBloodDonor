package com.example.roomdatabase.Screen

import android.app.Application
import android.content.res.Resources
import android.os.Bundle
import android.text.Spanned
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Transformations
import com.example.roomdatabase.database.Test
import com.example.roomdatabase.database.TestDao
import com.example.roomdatabase.database.TestDatabase
import com.example.roomdatabase.databinding.FragmentLoginBinding
import kotlinx.coroutines.*
import java.lang.StringBuilder


class LoginFragment : Fragment() {

   private lateinit var dataSource:TestDao
    val job= Job()
    val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var binding=FragmentLoginBinding.inflate(inflater)
        val name=binding.editTextName.text.toString()
        val password=binding.editTextPassword.text.toString()
        var application = requireNotNull(this.activity).application
        dataSource=TestDatabase.getInstance(application).testDao
        binding.btnLogin.setOnClickListener {
            coroutineScope.launch {
                insert(Test(null,name,password))
            }
        }
        binding.btnGet.setOnClickListener {
            coroutineScope.launch {
                getData()
            }
        }
        return binding.root
    }

    private suspend fun insert(test: Test){
        withContext(Dispatchers.IO){
            dataSource?.inset(test)
        }
    }
    private suspend fun getData(){
        withContext(Dispatchers.IO){
            val data=dataSource?.getTest()
            val dateString=Transformations.map(data){
                formatNight(it,resources)
            }
           Log.e("Data",dateString.toString())
        }

    }
    fun formatNight(data:List<Test>,resources: Resources): StringBuilder {
        val stringBuilder=StringBuilder()
        stringBuilder.apply {
            data.forEach {


                val name=stringBuilder.append(it.name)
                Log.e("name",name.toString())
                stringBuilder.append(it.name)
                stringBuilder.append(it.password)
            }
        }
        return stringBuilder
    }
}