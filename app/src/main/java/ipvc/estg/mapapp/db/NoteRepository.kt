package ipvc.estg.mapapp.db

import androidx.lifecycle.LiveData
import ipvc.estg.mapapp.dao.NoteDao
import ipvc.estg.mapapp.entities.Note


class NoteRepository(private val NoteDao: NoteDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allNote: LiveData<List<Note>> = NoteDao.getAlphabetizedWords()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    suspend fun insert(Note: Note) {
        NoteDao.insert(Note)
    }
}