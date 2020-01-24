package ru.rpw.radio.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment

import kotlinx.android.synthetic.main.fragment_setting.view.*
import ru.rpw.radio.R

class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        setListenerOnRadioButton(root)
        checkedRightRadio(root)
        return root
    }

    private fun checkedRightRadio(root: View){
        when(getSharedPreference(root.context, "AppForOpenURL")){
            "APP" ->{
                root.radioButtonInAPP.isChecked  = true
            }
            "CHROME" ->{
                root.radioButtonInChrome.isChecked = true
            }
        }
    }

    private fun setListenerOnRadioButton(root: View) {
        root.radioButtonURLApp.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonInAPP -> {
                    setSharedPreference(root.context, "AppForOpenURL", "APP")
                }
                R.id.radioButtonInChrome -> {
                    setSharedPreference(root.context, "AppForOpenURL", "CHROME")
                }
            }
        }
    }

    private fun setSharedPreference(context: Context, preferenceName: String, value: String) {
        val editor = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit()
        editor.putString(preferenceName, value)
        editor.apply()
    }

    private fun getSharedPreference(context: Context, preferenceName: String): String? {
        val prefs = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        return prefs.getString(preferenceName, "NULL")
    }
}