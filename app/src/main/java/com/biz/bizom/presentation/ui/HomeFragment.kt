package com.biz.bizom.presentation.ui

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biz.bizom.R
import com.biz.bizom.data.sources.Resource
import com.biz.bizom.databinding.FragmentHomeBinding
import com.biz.bizom.domain.Claim
import com.biz.bizom.domain.Claimfieldoption
import com.biz.bizom.domain.Claimtypedetail
import com.biz.bizom.domain.utils.CShowProgress
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    lateinit var progress: CShowProgress
    private val viewModel: HomeViewModel by viewModels()
    private var binding : FragmentHomeBinding? = null
    private val hashMap = Collections.synchronizedMap(HashMap<Any, ArrayList<Claimtypedetail>>())
    private val clamiFieldOptionHashMap = Collections.synchronizedMap(HashMap<Any, Claimfieldoption>())
    private val keyHashmap : ArrayList<String> = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        fetchdata()
        setupObservable()
    }

    private fun setupObservable() {
        viewModel.apply {
            myCustomUi.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        progress.hideProgress()
                        try {
                            if (it.value.Result){
                                val claims = it.value.Claims
                                setupdata(claims)
                            }
                            else {
                                Toast.makeText(requireContext(), it.value.Reason, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: NullPointerException) {
                            Log.d("LogTag", e.toString())
                        }
                    }
                    is Resource.Failure -> {
                        progress.hideProgress()
                        Toast.makeText(requireActivity(), "Failed.", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        if (progress.mDialog?.isShowing == true) {
                            progress.hideProgress()
                        } else {
                            progress.showProgress(requireContext())
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setupdata(claims: ArrayList<Claim>) {
        binding?.apply {
            parentLayout.orientation = LinearLayout.VERTICAL
            parentLayout.setPadding(dpToPx(16), dpToPx(10), dpToPx(16), dpToPx(10))

            val button = Button(requireContext())
            val nestedscrollview = NestedScrollView(requireContext())
            nestedscrollview.isSmoothScrollingEnabled = true


            val parentContainer =  LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = setparam()
            }

            val subcontainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = setparam()
            }

            //Spinner
            val spinnerArray = ArrayList<String>()
            for (i in 0 until claims.size) {
                spinnerArray.add(claims[i].Claimtype.name)
                hashMap[claims[i].Claimtype.name] = claims[i].Claimtypedetail
            }

            val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray
            )

            val spinBack = LinearLayout(requireContext()).apply {
                layoutParams = setparam()
                setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))
                background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_steps_backgrond_rectangle
                )
            }

            val spinner = Spinner(requireContext())
            spinner.adapter = spinnerArrayAdapter
            spinner.layoutParams = setparam()
            spinBack.addView(spinner)
            parentLayout.addView(spinBack)

            //Date
            val dateback = LinearLayout(requireContext()).apply {
                layoutParams = setparam()
                setPadding(dpToPx(5), dpToPx(10), dpToPx(5), dpToPx(10))
            }
            dateback.orientation = LinearLayout.HORIZONTAL

            val textview = TextView(requireContext())
            textview.text = "Date: "

            val sdf = SimpleDateFormat("dd/M/yyyy")
            val currentDate = sdf.format(Date())

            val textview2 = TextView(requireContext()).apply {
                text = currentDate
                setTypeface(null, Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
            }

            dateback.addView(textview)
            dateback.addView(textview2)
            parentLayout.addView(dateback)

            //generating based on spinner selection

            var selectedItem = spinner.selectedItem

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    selectedItem = parent.getItemAtPosition(position).toString()
                    triggerSelection(selectedItem.toString(), parentLayout, nestedscrollview, button, subcontainer, currentDate, parentContainer)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
    }

    private fun triggerSelection(
        selectedItem: String,
        parentLayout: LinearLayout,
        nestedscrollview: NestedScrollView,
        button: Button,
        subcontainer: LinearLayout,
        currentDate: String,
        parentContainer: LinearLayout
    ) {
        val claimDetails = hashMap[selectedItem]
        setupDataOnselection(claimDetails, parentLayout, nestedscrollview, button, subcontainer, currentDate, selectedItem, parentContainer)
        Log.d("LogTag", "triggerSelection: $claimDetails")
    }

    @SuppressLint("SetTextI18n")
    private fun setupDataOnselection(
        claimtypedetail: ArrayList<Claimtypedetail>?,
        parentLayout: LinearLayout,
        nestedscrollview: NestedScrollView,
        button: Button,
        subcontainer: LinearLayout,
        currentDate: String,
        selectedItem: String,
        parentContainer: LinearLayout
    ) {
        try {
            subcontainer.removeAllViews()
            keyHashmap.clear()
            binding.apply {
                for (i in 0 until claimtypedetail!!.size) {
                    when (claimtypedetail[i].Claimfield.type) {
                        "DropDown" -> {
                            val spinnerParentBack = LinearLayout(requireContext())
                            spinnerParentBack.orientation = LinearLayout.VERTICAL

                            val spinBack = LinearLayout(requireContext()).apply {
                                layoutParams = setparam()
                                setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))
                                background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_steps_backgrond_rectangle
                                )
                            }

                            val label = TextView(requireContext()).apply {
                                setTypeface(null, Typeface.BOLD)
                                setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
                            }
                            label.setPadding(dpToPx(0), dpToPx(10), dpToPx(0), dpToPx(10))

                            if (claimtypedetail[i].Claimfield.required == "1" ){
                                label.text = claimtypedetail[i].Claimfield.label + "*"
                            }else{
                                label.text = claimtypedetail[i].Claimfield.label
                            }

                            val spinner = Spinner(requireContext())
                            val list : ArrayList<Claimfieldoption> = ArrayList()
                            list.addAll(claimtypedetail[i].Claimfield.Claimfieldoption)

                            val subSpinner = ArrayList<String>()

                            for (i in 0 until list.size) {
                                subSpinner.add(list[i].name)
                                clamiFieldOptionHashMap[list[i].name] = list[i]
                            }

                            val subSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                subSpinner
                            )

                            spinner.adapter = subSpinnerAdapter
                            spinner.layoutParams = setparam()
                            spinBack.addView(spinner)
                            spinnerParentBack.addView(label)
                            spinnerParentBack.addView(spinBack)
                            subcontainer.addView(spinnerParentBack)
                        }
                        "SingleLineTextAllCaps" -> {
                            val editTextLyout = LinearLayout(requireContext()).apply {
                                layoutParams = setparam()
                            }
                            val labelText = EditText(requireContext()).apply {
                                hint = claimtypedetail[i].Claimfield.label
                                textSize = 16f
                                tag = claimtypedetail[i].Claimfield.id
                                keyHashmap.add(claimtypedetail[i].Claimfield.id)
                                isSingleLine = true
                                filters += InputFilter.AllCaps()
                                inputType = InputType.TYPE_CLASS_TEXT
                                layoutParams = setparam()

                                setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5))
                                background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_steps_backgrond_rectangle
                                )
                            }
                            editTextLyout.setPadding(dpToPx(0), dpToPx(10), dpToPx(0), dpToPx(5))
                            editTextLyout.addView(labelText)
                            subcontainer.addView(editTextLyout)
                        }
                        "SingleLineText" -> {
                            val editTextLyout = LinearLayout(requireContext()).apply {
                                layoutParams = setparam()
                            }
                            val labelText = EditText(requireContext()).apply {
                                hint = claimtypedetail[i].Claimfield.label
                                textSize = 16f
                                tag = claimtypedetail[i].Claimfield.id
                                keyHashmap.add(claimtypedetail[i].Claimfield.id)
                                isSingleLine = true
                                inputType = InputType.TYPE_CLASS_TEXT
                                layoutParams = setparam()
                                setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5))
                                background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_steps_backgrond_rectangle
                                )
                            }
                            editTextLyout.setPadding(dpToPx(0), dpToPx(10), dpToPx(0), dpToPx(5))
                            editTextLyout.addView(labelText)
                            subcontainer.addView(editTextLyout)
                        }
                        "SingleLineTextNumeric" -> {
                            val editTextLyout = LinearLayout(requireContext()).apply {
                                layoutParams = setparam()
                            }
                            val labelText = EditText(requireContext()).apply {
                                hint = claimtypedetail[i].Claimfield.label
                                textSize = 16f
                                isSingleLine = true
                                tag = claimtypedetail[i].Claimfield.id
                                keyHashmap.add(claimtypedetail[i].Claimfield.id)
                                inputType = InputType.TYPE_CLASS_NUMBER
                                layoutParams = setparam()
                                setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5))
                                background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_steps_backgrond_rectangle
                                )
                            }
                            editTextLyout.setPadding(dpToPx(0), dpToPx(10), dpToPx(0), dpToPx(5))
                            editTextLyout.addView(labelText)
                            subcontainer.addView(editTextLyout)
                        }
                    }
                }

                //Button
                button.text = "ADD CLAIM"
                button.setPadding(dpToPx(0), dpToPx(20), dpToPx(0), dpToPx(20))

                button.setOnClickListener {
                    try {
                        for (i in 0 until keyHashmap.size){
                            Log.d("LogHash", keyHashmap[i].toString())
                            val edt = subcontainer.findViewWithTag<EditText>(keyHashmap.get(i).toString())
                            if (edt.text.isNullOrEmpty()){
                                Toast.makeText(requireContext(), "Please Enter All Details", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                        }
                        val edttext = subcontainer.findViewWithTag<EditText>("24")
                        val str =  edttext.text.toString().trim()
                        Addclaimcard(str , subcontainer, selectedItem, currentDate, parentContainer)

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }

                subcontainer.gravity = Gravity.CENTER_HORIZONTAL
                subcontainer.addView(button)
                parentContainer.addView(subcontainer)
                nestedscrollview.addView(parentContainer)
                parentLayout.addView(nestedscrollview)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun Addclaimcard(
        str: String,
        subcontainer: LinearLayout,
        selectedItem: String,
        currentDate: String,
        parentContainer: LinearLayout
    ) {
        val mainContainer = LinearLayout(requireContext()) // vertical
        mainContainer.orientation = LinearLayout.VERTICAL
        mainContainer.layoutParams = setparam()
        mainContainer.setPadding(dpToPx(5), dpToPx(20), dpToPx(0), dpToPx(20))

        val containerA = LinearLayout(requireContext()) //Horizontal
        containerA.orientation = LinearLayout.HORIZONTAL

        val textviewA1 = TextView(requireContext()).apply {
            text = "Claim Type : "
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
        }


        val textviewA2 = TextView(requireContext()).apply {
            text = selectedItem
        }

        containerA.addView(textviewA1)
        containerA.addView(textviewA2)

        val containerB = LinearLayout(requireContext()) //Horizontal
        containerB.orientation = LinearLayout.HORIZONTAL

        val textviewB1 = TextView(requireContext()).apply {
            text = "Claim Date : "
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
        }


        val textviewB2 = TextView(requireContext()).apply {
            text = currentDate
        }

        containerB.addView(textviewB1)
        containerB.addView(textviewB2)

        val containerC = LinearLayout(requireContext()) //Horizontal
        containerC.orientation = LinearLayout.HORIZONTAL
        containerC.layoutParams = setparam()

        val textviewC1 = TextView(requireContext()).apply {
            text = "Expense Amount : "
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
        }


        val textviewC2 = TextView(requireContext()).apply {
            text = str
        }

        val textviewC3 = TextView(requireContext()).apply {
            text = "view more ->"
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.END
            setPadding(dpToPx(30), dpToPx(0), dpToPx(20), dpToPx(0))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
        }

        containerC.addView(textviewC1)
        containerC.addView(textviewC2)
        containerC.addView(textviewC3)

        mainContainer.apply {
            addView(containerA)
            addView(containerB)
            addView(containerC)
        }
        Toast.makeText(requireContext(), "Claim Added Successfully", Toast.LENGTH_SHORT).show()
        parentContainer.addView(mainContainer)

        for (i in 0 until keyHashmap.size){
            val edt = subcontainer.findViewWithTag<EditText>(keyHashmap.get(i).toString())
            edt.setText("")
        }
    }


    private fun setparam() : LinearLayout.LayoutParams{
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        return param
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun fetchdata() {
        viewModel.apply {
            getCustomUi()
        }
    }

}