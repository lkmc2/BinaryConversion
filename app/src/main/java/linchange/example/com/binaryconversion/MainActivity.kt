package linchange.example.com.binaryconversion

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.textChangedListener

class MainActivity : AppCompatActivity() {
    
    enum class TYPE { //进制的类型枚举
        BIN, OCT, DEC, HEX //枚举分别代表二进制、八进制、十进制、十六进制
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initEvent(); //初始化事件
    }

    //初始化事件
    private fun initEvent() {
        /*
        此处的EditText输入框etBin、etOct、etDec、etHex由kotlin-android-extensions插件
        自动与布局文件activity_main.xml中的id属性绑定，不用findViewById()
         */
        autoSetTextChangeListener(etBin, etOct, etDec, etHex) //批量自动设置EditText的文本改变监听器
    }

    /**
     * 批量自动设置EditText的文本改变监听器
     * @param arrayOfEditText EditText的数组
     */
    fun autoSetTextChangeListener(vararg arrayOfEditText: EditText) {
        var isRunning: Boolean = false //通过标志位防止afterTextChanged多次执行

        arrayOfEditText.forEach { editText -> //使用forEach遍历设置数组中的EditText
            editText.textChangedListener { //给EditText设置文本改变监听器
                afterTextChanged { //设置文本改变后调用的方法
                    //获取当前EditText输入框中的文字，并设置其他三个输入框对应的信息
                    str ->
                    if (!isRunning) { //当未在执行
                        isRunning = true //标志位设为正在执行
                        str.toString().changeNum(getEditType(editText))

                        isRunning = false //标志位设为执行结束
                    }
                }
            }
        }
    }

    /**
     * 根据输入框获取输入的类型
     * @param editText 输入框
     * @return 输入框类型
     */
    private fun getEditType(editText: EditText): TYPE
            = when (editText.id) {
                R.id.etBin -> TYPE.BIN
                R.id.etOct -> TYPE.OCT
                R.id.etDec -> TYPE.DEC
                R.id.etHex -> TYPE.HEX
                else -> TYPE.BIN
            }

    /**
      * 设置其他三个输入框对应的信息
     * @param type 进制的枚举类型
     */
    fun String.changeNum(type: TYPE) {
         val string: String = this //此处的this代表字符串本身
         
        if (string.isEmpty()) { //如果字符串长度为空
            setTipIconAndText(R.drawable.ic_info, "输入数字，自动转换成其他进制") //设置提示图标和提示文字
            clearAllEditTextContent() //清除所有EditText的文本内容
            return
        }

        when (type) { //根据传入的类型进行判断，与java中的switch类似
            TYPE.BIN -> { //二进制事件
                if (checkValidate(string, TYPE.BIN)) { //检查字符串是否符合二进制格式
                    string.setOtherEditTextContent(TYPE.BIN, etOct, etDec, etHex) //批量设置其他的EditText的文本内容
                } else { //不符合二进制格式
                    setWrongTipIconAndText("请输入数字0或1") //设置错误提示图标和文字
                }
            }
            TYPE.OCT -> { //八进制事件
                if (checkValidate(string, TYPE.OCT)) { //检查字符串是否符合八进制格式
                    string.setOtherEditTextContent(TYPE.OCT, etBin, etDec, etHex)
                } else {
                    setWrongTipIconAndText("请输入0到7范围内的数字") //设置错误提示图标和文字
                }
            }
            TYPE.DEC -> { //十进制事件
                if (checkValidate(string, TYPE.DEC)) { //检查字符串是否符合十进制格式
                    string.setOtherEditTextContent(TYPE.DEC, etBin, etOct, etHex) //批量设置其他的EditText的文本内容
                } else {
                    setWrongTipIconAndText("请输入数字0到9") //设置错误提示图标和文字
                }
            }
            TYPE.HEX -> { //十六进制事件
                if (checkValidate(string, TYPE.HEX)) { //检查字符串是否符合十六进制格式
                    string.setOtherEditTextContent(TYPE.HEX, etBin, etOct, etDec) //批量设置其他的EditText的文本内容
                } else {
                    setWrongTipIconAndText("请输入数字及A到F") //设置错误提示图标和文字
                }
            }
            else -> {
                setWrongTipIconAndText("发生未知异常") //设置错误提示图标和文字
            }
        }
    }

    /**
     * 清除所有EditText的文本内容
     */
    fun clearAllEditTextContent() {
        etBin.setText("") //清空二进制输入框
        etOct.setText("") //清空八进制输入框
        etDec.setText("") //清空十进制输入框
        etHex.setText("") //清空十六进制输入框
    }

    /**
     * 设置错误提示图标和文字
     * @param wrongText 错误提示文字
     */
    fun setWrongTipIconAndText(wrongText: String) {
        ivIcon.background = resources.getDrawable(R.drawable.ic_warn) //设置错误图标
        tvTip.text = wrongText //设置错误提示文字
    }

    /**
     * 设置提示图标和提示文字
     * @param drawableId 图片的id
     * @param tipText 提示文字
     */
    fun setTipIconAndText(drawableId: Int, tipText: String) {
        ivIcon.background = resources.getDrawable(drawableId) //设置提示图标
        tvTip.text = tipText //设置提示文字
    }

    /**
     * 批量自动设置其他的EditText的文本内容
     * @param arrayOfEditText EditText的数组
     */
    fun String.setOtherEditTextContent(type: TYPE, vararg arrayOfEditText: EditText) {
        val string: String = this //此处的this代表字符串本身

        val str: String = when (type) { //将N进制转换成十进制
            TYPE.BIN -> Integer.parseInt(string, 2).toString()
            TYPE.OCT -> Integer.parseInt(string, 8).toString()
            TYPE.DEC -> Integer.parseInt(string, 10).toString()
            TYPE.HEX -> Integer.parseInt(string, 16).toString()
        }

        arrayOfEditText.forEach { editText ->
            when (editText.id) { //根据EditText的id进行判断，进行进制的转换
                R.id.etBin -> etBin.setText("" + Integer.toBinaryString(Integer.parseInt(str))) //十进制转二进制
                R.id.etOct -> etOct.setText("" + Integer.toOctalString(Integer.parseInt(str))) //十进制转八进制
                R.id.etDec -> etDec.setText("" + Integer.parseInt(str)) //十进制转十进制
                R.id.etHex -> etHex.setText("" + Integer.toHexString(Integer.parseInt(str)).toUpperCase()) //十进制转十六进制
                else -> setWrongTipIconAndText("发生未知异常") //设置错误提示图标和文字
            }
        }
        //如果进制转换成功，设置提示图标和提示文字
        setTipIconAndText(R.drawable.ic_success, "转换完成")
    }

    /**
     * 检查字符串是否符合对应进制的标准
     * @param str 需要检查的字符串
     * @param type 进制的枚举类型
     * @return 传入的字符串是否符合对应进制的格式
     */
    fun checkValidate(str: String, type: TYPE): Boolean {
        when (type) { //根据传入的类型进行判断，与java中的switch类似
            TYPE.BIN -> { //二进制事件
                val list = listOf<Char>('0','1')
                return considerInRange(str, list) //判断字符串是否完全符合列表规范
            }
            TYPE.OCT -> { //八进制事件
                val list = listOf<Char>('0','1','2','3','4','5','6','7')
                return considerInRange(str, list)
            }
            TYPE.DEC -> { //十进制事件
                val list = listOf<Char>('0','1','2','3','4','5','6','7','8','9')
                return considerInRange(str, list)
            }
            TYPE.HEX -> { //十六进制事件
                val list = listOf<Char>('0','1','2','3','4','5','6','7','8',
                        '9','A','B','C','D','E','F')
                return considerInRange(str, list)
            }
            else -> return false //未知事件，直接返回为假
        }
    }

    /**
     * 判断传入的字符串是否每个字符都在传入的列表中存在
     * @param str 传入的字符串
     * @param list 参照的字符列表
     * @return 是否每个字符都在传入的列表中存在
     */
    fun considerInRange(str: String, list: List<Char>): Boolean {
        //将字符串str变成字符数组，遍历该字符数组
        str.toCharArray().forEachIndexed { _, char -> //char是每次遍历中字符的值
            //将字符变成大写之后判断是否在参照列表中有存在
            if (char.toUpperCase() !in list.toCharArray()) // !in表示不在该列表中
                return false //如果字符数组中有一个不存在参照列表中，该字符串不符合标准
        }
        return true //全部通过，字符串符合标准
    }
}

/**
 * 调用Toast.makeText方法
 * @param text 需要提示的文本
 */
fun Activity.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
