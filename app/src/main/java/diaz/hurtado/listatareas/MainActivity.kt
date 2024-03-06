package diaz.hurtado.listatareas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.room.Room

class MainActivity : AppCompatActivity() {

    lateinit var et_tarea: EditText
    lateinit var btn_agregar: Button
    lateinit var listview_tareas: ListView
    lateinit var lista_tareas: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>
    lateinit var btn_editar: Button
    lateinit var btn_eliminar: Button
    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et_tarea = findViewById(R.id.et_tarea)
        btn_agregar = findViewById(R.id.btn_agregar)
        listview_tareas = findViewById(R.id.listview_tareas)
        btn_editar = findViewById(R.id.btn_editar)
        btn_eliminar =findViewById(R.id.btn_eliminar)

        lista_tareas = ArrayList()


         db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "tareas-db"
        ).allowMainThreadQueries().build()

        cargar_tareas()


        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista_tareas)
        listview_tareas.adapter = adapter
        btn_agregar .setOnClickListener{
            var tarea_str = et_tarea.text.toString()

            if (!tarea_str.isNullOrEmpty()){
                var tarea = Tarea(desc = tarea_str)
                db.tareaDao().agregarTarea(tarea)
                lista_tareas.add(tarea_str)
                adapter.notifyDataSetChanged()
                et_tarea.setText("")
            } else{
                Toast.makeText( this,"LLenar Campos", Toast.LENGTH_SHORT).show()
            }



        }



        listview_tareas.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
           var tarea_desc = lista_tareas[position]

            var tarea = db.tareaDao().getTarea(tarea_desc)

            db.tareaDao().eleminarTarea(tarea)

            lista_tareas.removeAt(position)
            adapter.notifyDataSetChanged()
        }

        btn_eliminar.setOnClickListener {
            // Obtener la posición seleccionada del ListView
            val position =   listview_tareas.checkedItemPosition

            if (position != AdapterView.INVALID_POSITION) {
                // Obtener la descripción de la tarea seleccionada
                val tareaDesc = lista_tareas[position]

                // Obtener la tarea desde la base de datos
                val tarea = db.tareaDao().getTarea(tareaDesc)

                // Eliminar la tarea de la base de datos
                db.tareaDao().eleminarTarea(tarea)

                // Eliminar la tarea de la lista
                lista_tareas.removeAt(position)

                // Notificar al adaptador sobre el cambio en los datos
                adapter.notifyDataSetChanged()
            }
        }


    }

    private fun cargar_tareas(){
        var lista_db = db.tareaDao().obtenerTareas()
        for(tarea in lista_db){
            lista_tareas.add(tarea.desc)
        }
    }
}