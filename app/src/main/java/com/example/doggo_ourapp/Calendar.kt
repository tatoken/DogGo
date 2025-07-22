package com.example.doggo_ourapp

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.*
import com.kizitonwose.calendar.view.CalendarView
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class Calendar : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var eventText: TextView
    private lateinit var addEventFab: FloatingActionButton

    private lateinit var monthTitle: TextView

    private var selectedDate: LocalDate? = null
    private val eventsMap: MutableMap<LocalDate, MutableList<Event>> = mutableMapOf()
    private val dayFormatter = DateTimeFormatter.ofPattern("d")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.calendar_layout, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        eventText = view.findViewById(R.id.eventText)
        addEventFab = view.findViewById(R.id.addEventFab)

        monthTitle = view.findViewById(R.id.monthTitle)


        setupCalendar()

        addEventFab.setOnClickListener {
            selectedDate?.let { date -> showAddEventDialog(date) }
        }

        return view
    }

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(12)
        val endMonth = currentMonth.plusMonths(12)
        val daysOfWeek = daysOfWeek()

        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        calendarView.monthScrollListener = { month ->
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
            monthTitle.text = month.yearMonth.format(formatter).capitalize()
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = dayFormatter.format(day.date)

                if (day.position == DayPosition.MonthDate) {
                    container.textView.visibility = View.VISIBLE
                    container.textView.setBackgroundResource(
                        if (day.date == selectedDate) R.drawable.selected_day_bg else 0
                    )
                    container.view.setOnClickListener {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        calendarView.notifyDateChanged(day.date)
                        oldDate?.let { calendarView.notifyDateChanged(it) }
                        updateEventText()
                        addEventFab.visibility = View.VISIBLE
                    }
                } else {
                    container.textView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun updateEventText() {
        val date = selectedDate
        if (date == null) {
            eventText.text = "Premi su una data per vedere o aggiungere eventi."
            addEventFab.visibility = View.GONE
        } else {
            val events = eventsMap[date].orEmpty()
            eventText.text = if (events.isEmpty()) {
                "Nessun evento per il ${date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}."
            } else {
                "Eventi per il ${date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}:\n" +
                        events.joinToString("\n") { "- ${it.time} - ${it.title}: ${it.description}" }
            }
        }
    }

    private fun showAddEventDialog(date: LocalDate) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_event, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.titleInput)
        val descInput = dialogView.findViewById<EditText>(R.id.descInput)
        val timeInput = dialogView.findViewById<EditText>(R.id.timeInput)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Nuovo evento")
            .setView(dialogView)
            .setPositiveButton("Salva") { _, _ ->
                val title = titleInput.text.toString()
                val description = descInput.text.toString()
                if (title.isNotBlank()) {
                    val time = timeInput.text.toString()
                    val event = Event(title, description, time)
                    eventsMap.getOrPut(date) { mutableListOf() }.add(event)
                    updateEventText()
                }
            }
            .setNegativeButton("Annulla", null)
            .show()


        var selectedHour = 0
        var selectedMinute = 0

        timeInput.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    selectedHour = hourOfDay
                    selectedMinute = minute
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    timeInput.setText(formattedTime)
                },
                selectedHour,
                selectedMinute,
                true // 24-hour format
            )
            timePicker.show()
        }

    }

    data class Event(val title: String, val description: String, val time: String)


    class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)
    }
}
