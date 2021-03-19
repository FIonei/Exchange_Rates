package com.example.exchangerates

import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var firstValute: EditText
    private lateinit var secondValute: EditText
    private lateinit var saveButton: Button
    private lateinit var updateButton: ImageButton
    private lateinit var changeButton: Button
    private lateinit var topSpinner: Spinner
    private lateinit var bottomSpinner: Spinner
    val FILE_NAME = "content.txt"
    var koef: Double = 1.0/73.1019
    var valutesMap: Map<String, Double> = mapOf("Рубль" to 1.0)
    var data = arrayOf("Рубль",
            "Австралийский доллар", "Азербайджанский манат", "Фунт стерлингов Соединенного королевства", "Армянских драмов", "Белорусский рубль",
            "Болгарский лев", "Бразильский реал", "Венгерских форинтов", "Гонконгских долларов", "Датская крона",
            "Доллар США", "Евро", "Индийских рупий", "Казахстанских тенге", "Канадский доллар",
            "Киргизских сомов", "Китайский юань", "Молдавских леев", "Норвежских крон", "Польский злотый",
            "Румынский лей", "СДР (специальные права заимствования)", "Сингапурский доллар", "Таджикских сомони", "Турецких лир",
            "Новый туркменский манат", "Узбекских сумов", "Украинских гривен", "Чешских крон", "Шведских крон",
            "Швейцарский франк", "Южноафриканских рэндов", "Вон Республики Корея", "Японских иен")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()

        firstValute.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                secondValute.setText(convertRate(s))
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        saveButton.setOnClickListener{ save() }
        updateButton.setOnClickListener{ refresh() }
        changeButton.setOnClickListener{ change() }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        topSpinner.adapter = adapter
        bottomSpinner.adapter = adapter
        topSpinner.prompt = "Верхняя валюта"
        bottomSpinner.prompt = "Нижняя валюта"
        topSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                        position: Int, id: Long) {
                if (valutesMap.count() > 1) {
                    koef = takeKoef(topSpinner.getSelectedItem().toString(), bottomSpinner.getSelectedItem().toString())
                    secondValute.setText(convertRate(firstValute.editableText))
                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        bottomSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                        position: Int, id: Long) {
                if (valutesMap.count() > 1) {
                    koef = takeKoef(topSpinner.getSelectedItem().toString(), bottomSpinner.getSelectedItem().toString())
                    secondValute.setText(convertRate(firstValute.editableText))
                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        val file2 = File(getFilesDir().getAbsolutePath(), FILE_NAME)
        if (file2.exists().not()) createContent()
        val file1 = File(getFilesDir().getAbsolutePath(), "valutes.txt")
        if (file1.exists().not()) refresh()
        retrieve()
    }



    private fun createContent() {
        var fos: FileOutputStream? = null
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE)
            fos.write(("Рубль;").toByteArray())
            fos.write(("0;").toByteArray())
            fos.write(("Доллар США;").toByteArray())
            fos.write(("0.0136").toByteArray())
        } catch (ex: IOException) {
            Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                fos?.close()
            } catch (ex: IOException) {
                Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViews(){
            firstValute = findViewById(R.id.firstValute)
            secondValute = findViewById(R.id.secondValute)
            saveButton = findViewById(R.id.saveButton)
            updateButton = findViewById(R.id.updateButton)
            changeButton = findViewById(R.id.changeButton)
            topSpinner = findViewById(R.id.topSpinner)
            bottomSpinner = findViewById(R.id.bottomSpinner)
}

    private fun save() {
        var fos: FileOutputStream? = null
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE)
            fos.write((topSpinner.getSelectedItem().toString() + ";").toByteArray())
            fos.write((firstValute.text.toString() + ";").toByteArray())
            fos.write((bottomSpinner.getSelectedItem().toString() + ";").toByteArray())
            fos.write(koef.toString().toByteArray())
            Toast.makeText(applicationContext, "Файл сохранен", Toast.LENGTH_SHORT).show()
        } catch (ex: IOException) {
            Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                fos?.close()
            } catch (ex: IOException) {
                Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun refresh() {
        val url = "https://www.cbr-xml-daily.ru/daily_json.js"
        var res: String?
        val loadValuteAsync = object:AsyncTask<String, String, String>() {
            override fun onPreExecute() {
            }
            override fun onPostExecute(result: String?) {
                res = result
                val root: DailyInfo = Gson().fromJson<DailyInfo>(res, DailyInfo::class.java!!)
                val date = root.Date
                valutesMap = readValutes(root)
                saveValute(date, valutesMap)
                koef = takeKoef(topSpinner.getSelectedItem().toString(), bottomSpinner.getSelectedItem().toString())
                secondValute.setText(convertRate(firstValute.editableText))
                Toast.makeText(applicationContext, "обновлено", Toast.LENGTH_SHORT).show()
            }

            private fun saveValute(date: String, map: Map<String, Double>) {
                var fos: FileOutputStream? = null
                try {
                    fos = openFileOutput("valutes.txt", MODE_PRIVATE)
                    fos.write((date + "\n").toByteArray())
                    fos.write(map.toString().toByteArray())
                } catch (ex: IOException) {
                    Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
                } finally {
                    try {
                        fos?.close()
                    } catch (ex: IOException) {
                        Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun doInBackground(vararg params: String): String? {
                val result: String?
                val http = HTTPDataHandler()
                result = http.getHTTPDataHandler(params[0])
                return result
            }
        }
    loadValuteAsync.execute(url)
    }

    private fun readValutes(root: DailyInfo): Map<String, Double> {
        val map = mapOf("Рубль" to 1.0,
                root.Valute.aud.Name to root.Valute.aud.Value / root.Valute.aud.Nominal,
                root.Valute.azn.Name to root.Valute.azn.Value / root.Valute.azn.Nominal,
                root.Valute.gbp.Name to root.Valute.gbp.Value / root.Valute.gbp.Nominal,
                root.Valute.amd.Name to root.Valute.amd.Value / root.Valute.amd.Nominal,
                root.Valute.byn.Name to root.Valute.byn.Value / root.Valute.byn.Nominal,
                root.Valute.bgn.Name to root.Valute.bgn.Value / root.Valute.bgn.Nominal,
                root.Valute.brl.Name to root.Valute.brl.Value / root.Valute.brl.Nominal,
                root.Valute.huf.Name to root.Valute.huf.Value / root.Valute.huf.Nominal,
                root.Valute.hkd.Name to root.Valute.hkd.Value / root.Valute.hkd.Nominal,
                root.Valute.dkk.Name to root.Valute.dkk.Value / root.Valute.dkk.Nominal,
                root.Valute.usd.Name to root.Valute.usd.Value / root.Valute.usd.Nominal,
                root.Valute.eur.Name to root.Valute.eur.Value / root.Valute.eur.Nominal,
                root.Valute.inr.Name to root.Valute.inr.Value / root.Valute.inr.Nominal,
                root.Valute.kzt.Name to root.Valute.kzt.Value / root.Valute.kzt.Nominal,
                root.Valute.cad.Name to root.Valute.cad.Value / root.Valute.cad.Nominal,
                root.Valute.kgs.Name to root.Valute.kgs.Value / root.Valute.kgs.Nominal,
                root.Valute.cny.Name to root.Valute.cny.Value / root.Valute.cny.Nominal,
                root.Valute.mdl.Name to root.Valute.mdl.Value / root.Valute.mdl.Nominal,
                root.Valute.nok.Name to root.Valute.nok.Value / root.Valute.nok.Nominal,
                root.Valute.pln.Name to root.Valute.pln.Value / root.Valute.pln.Nominal,
                root.Valute.ron.Name to root.Valute.ron.Value / root.Valute.ron.Nominal,
                root.Valute.xdr.Name to root.Valute.xdr.Value / root.Valute.xdr.Nominal,
                root.Valute.sgd.Name to root.Valute.sgd.Value / root.Valute.sgd.Nominal,
                root.Valute.tjs.Name to root.Valute.tjs.Value / root.Valute.tjs.Nominal,
                root.Valute.tryr.Name to root.Valute.tryr.Value / root.Valute.tryr.Nominal,
                root.Valute.tmt.Name to root.Valute.tmt.Value / root.Valute.tmt.Nominal,
                root.Valute.uzs.Name to root.Valute.uzs.Value / root.Valute.uzs.Nominal,
                root.Valute.auh.Name to root.Valute.auh.Value / root.Valute.auh.Nominal,
                root.Valute.czk.Name to root.Valute.czk.Value / root.Valute.czk.Nominal,
                root.Valute.sek.Name to root.Valute.sek.Value / root.Valute.sek.Nominal,
                root.Valute.chf.Name to root.Valute.chf.Value / root.Valute.chf.Nominal,
                root.Valute.zar.Name to root.Valute.zar.Value / root.Valute.zar.Nominal,
                root.Valute.krw.Name to root.Valute.krw.Value / root.Valute.krw.Nominal,
                root.Valute.jpy.Name to root.Valute.jpy.Value / root.Valute.jpy.Nominal
        )
        return map
    }

    private fun takeKoef(first: String, second: String): Double{
        val koef1 = valutesMap.get(first) ?: -1.0
        val koef2 = valutesMap.get(second) ?: -1.0
        return (koef1 / koef2)
    }

    private fun change() {
        val s1 = findViewById<Spinner>(R.id.topSpinner)
        val s2 = findViewById<Spinner>(R.id.bottomSpinner)
        val id1 = s1.selectedItemPosition
        s1.setSelection(s2.selectedItemPosition)
        s2.setSelection(id1)
        koef = 1 / koef
        firstValute.setText(secondValute.text.toString())
    }

    private fun retrieve() {
        var fin: FileInputStream? = null
        try {
            fin = openFileInput(FILE_NAME)
            val bytes = ByteArray(fin.available())
            fin.read(bytes)
            val text1 = String(bytes)
            for(c in text1) if (c == ';') {
                val default: List<String> = listOf("Рубль", "0", "Доллар США", "0.0136")
                var text: List<String?> = text1.split(";")
                if (text.count() != default.count()) text = default
                topSpinner.setSelection(data.indexOf(text[0]))
                firstValute.setText(text[1].toString())
                koef = text[3]!!.toDouble()
                bottomSpinner.setSelection(data.indexOf(text[2]))
                Toast.makeText(applicationContext, "загружено", Toast.LENGTH_SHORT).show()
                break
            }
        } catch (ex: IOException) {
            Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                if (fin != null) fin.close()
            } catch (ex: IOException) {
                Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun convertRate(s: Editable): String{
        if (s.toString() == "") return "0.0"
        return (s.toString().toDouble() * koef).toString()
    }
}