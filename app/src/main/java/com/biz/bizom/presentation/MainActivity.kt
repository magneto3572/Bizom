package com.biz.bizom.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.biz.bizom.databinding.ActivityMainBinding
import com.biz.bizom.domain.utils.NetworkUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkUtil: NetworkUtil
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(NetworkUtil.Variables.checkvalue.toString() == "true"){
            binding?.apply {
                internetError.root.visibility = View.GONE
                fragmentContainer.visibility = View.VISIBLE
            }
        }else{
            binding?.apply {
                internetError.root.visibility = View.VISIBLE
                fragmentContainer.visibility = View.GONE
            }
        }

        networkUtil.observe(this) {
            if (it == true) {
                binding?.apply {
                    internetError.root.visibility = View.GONE
                    fragmentContainer.visibility = View.VISIBLE
                }
            } else {
                binding?.apply {
                    internetError.root.visibility = View.VISIBLE
                    fragmentContainer.visibility = View.GONE
                }
            }
        }
    }
}