import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.w20079934.helpers.exists
import com.w20079934.helpers.read
import com.w20079934.helpers.write
import com.w20079934.models.EntryModel
import com.w20079934.models.EntryStore
import java.time.LocalDate
import java.util.*

val JSON_ENTRIES = "entries.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<EntryModel>>() {}.type
val stringType = object : TypeToken<String>() {}.type
val JSON_NAME = "diary.json"

fun generateRandomId(): Long {
    return Random().nextLong()
}

fun getTodaysDate(): Map<String, Int> {
    val currDate = LocalDate.now()
    return mapOf<String,Int>("year" to currDate.year, "month" to currDate.monthValue, "day" to currDate.dayOfMonth)
}

class EntryJSONStore : EntryStore {

    val context: Context
    var entries = mutableListOf<EntryModel>()
    var diaryName = "World"

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_ENTRIES)) {
            deserializeEntries()
        }
        if (exists(context, JSON_NAME)) {
            deserializeName()
        }
    }

    private fun serializeEntries() {
        val jsonString = gsonBuilder.toJson(entries, listType)
        write(context, JSON_ENTRIES, jsonString)
    }

    private fun deserializeEntries() {
        val jsonString = read(context, JSON_ENTRIES)
        entries = Gson().fromJson(jsonString, listType)
    }

    private fun serializeName() {
        val jsonString = gsonBuilder.toJson(diaryName, stringType)
        write(context, JSON_NAME, jsonString)
    }

    private fun deserializeName() {
        val jsonString = read(context, JSON_NAME)
        diaryName = Gson().fromJson(jsonString, stringType)
    }

    override fun findAll(): List<EntryModel> {
        return entries
    }

    override fun create(entry: EntryModel) {
        entry.id = generateRandomId()
        entry.date = getTodaysDate()
        entries.add(entry)
        serializeEntries()
    }

    fun findOne(id: Long): EntryModel? {
        val foundEntry: EntryModel? = entries.find { p -> p.id == id }
        return foundEntry
    }

    override fun update(entry: EntryModel) {
        val foundEntry = findOne(entry.id)
        if (foundEntry != null) {
            foundEntry.topic = entry.topic
            foundEntry.entry = entry.entry
            foundEntry.image = entry.image
        }
        serializeEntries()
    }

    override fun updateName(name: String) {
        diaryName = name
        serializeName()
    }

    override fun getName(): String {
        return diaryName
    }

    override fun remove(entry: EntryModel) {
        entries.remove(entry)
        serializeEntries()
    }
}