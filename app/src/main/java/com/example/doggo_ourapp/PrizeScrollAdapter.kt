import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.PrizeData
import com.example.doggo_ourapp.R
import com.example.doggo_ourapp.SupabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.view.View

class PrizeScrollAdapter(
    private val prizes: List<PrizeData>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<PrizeScrollAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prizeImage1: ImageView = itemView.findViewById(R.id.prizeImage1)
        val prizeTitle1: TextView = itemView.findViewById(R.id.prizeTitle1)
        val prizeImage2: ImageView = itemView.findViewById(R.id.prizeImage2)
        val prizeTitle2: TextView = itemView.findViewById(R.id.prizeTitle2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trophy_page_prize_component, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val index1 = position * 2
        val index2 = index1 + 1

        val prize1 = prizes[index1]
        holder.prizeTitle1.text = prize1.name
        scope.launch {
            val bitmap = SupabaseManager.downloadImage("prize", "${prize1.name}.png")
            holder.prizeImage1.setImageBitmap(bitmap)
        }

        if (index2 < prizes.size) {
            val prize2 = prizes[index2]
            holder.prizeTitle2.text = prize2.name
            scope.launch {
                val bitmap = SupabaseManager.downloadImage("prize", "${prize2.name}.png")
                holder.prizeImage2.setImageBitmap(bitmap)
            }
        } else {
            holder.prizeTitle2.text = ""
            holder.prizeImage2.setImageDrawable(null) // oppure un'immagine placeholder
        }
    }

    override fun getItemCount(): Int = (prizes.size + 1) / 2
}
