package app.base.utils

import android.text.TextUtils
import android.util.Log
import java.math.BigDecimal
import java.util.regex.Pattern

fun String?.toRMB():String{
    var result = "0.00"
    if(this != null && !TextUtils.isEmpty(this)){
        result = this
        try {
            val bd = BigDecimal(this)
            result = bd.setScale(2,BigDecimal.ROUND_DOWN).toString()
        }catch (e:NumberFormatException){
            e.printStackTrace()
            Log.e("test","toRMB ${this} is not a number")
        }
    }
    return result
}
fun  String?.subZeroAndDot():String {
    return if(this!=null){
        val bd = BigDecimal(this)
        bd.stripTrailingZeros().toPlainString()
    }else{
        "0"
    }
}

fun String?.isNumberic():Boolean{
    return if(this == null || TextUtils.isEmpty(this)){
        false
    }else{
        val pattern = Pattern.compile("^-?[0-9]+.?[0-9]*\$")
        pattern.matcher(this).find()
    }
}
