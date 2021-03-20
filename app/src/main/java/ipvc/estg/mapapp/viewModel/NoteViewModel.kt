package ipvc.estg.mapapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipvc.estg.mapapp.dao.NoteDao
import ipvc.estg.mapapp.db.NoteDB
import ipvc.estg.mapapp.db.NoteRepository
import ipvc.estg.mapapp.entities.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allNote: LiveData<List<Note>>

    init {
        val NoteDao= NoteDB.getDatabase(application, viewModelScope).NoteDao()
        repository = NoteRepository(NoteDao)
        allNote = repository.allNote
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(Note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(Note)
    }

    fun delete(id:Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(id)
    }

    fun update(id:Int,titulo:String, descricao:String) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(id,titulo, descricao)
    }
}