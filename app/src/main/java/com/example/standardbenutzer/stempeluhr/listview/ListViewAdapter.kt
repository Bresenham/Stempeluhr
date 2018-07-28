import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.standardbenutzer.stempeluhr.R
import com.example.standardbenutzer.stempeluhr.database.DatabaseEntry
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.formatDateToString
import com.example.standardbenutzer.stempeluhr.helper.Utility.Companion.msToString

class ListViewAdapter(context: Context, list: ArrayList<DatabaseEntry>) : ArrayAdapter<DatabaseEntry>(context, 0, list) {

    private val mContext : Context = context
    private val listEntries : ArrayList<DatabaseEntry> = list

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var listItem = convertView
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.listview_row,parent,false)

        val currentEntry = listEntries[position]

        listItem!!.findViewById<TextView>(R.id.txtDate).text = formatDateToString(currentEntry.getDate())

        listItem.findViewById<TextView>(R.id.txtWorktime).text = msToString(currentEntry.getWorktime())

        listItem.findViewById<TextView>(R.id.txtPlusMinus).text = currentEntry.getPlusMinus()

        if(currentEntry.getPlusMinus()[0] == '-')
            listItem.findViewById<TextView>(R.id.txtPlusMinus).setBackgroundColor(Color.RED)
        else
            listItem.findViewById<TextView>(R.id.txtPlusMinus).setBackgroundColor(Color.GREEN)

        return listItem
    }

    override fun getItem(position: Int): DatabaseEntry {
        return listEntries[position]
    }
}