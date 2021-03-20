package ipvc.estg.mapapp.dao;

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ipvc.estg.mapapp.entities.Note


@Dao
interface NoteDao {

    @Query("SELECT * FROM note_table ORDER BY id ASC")
    fun getAlphabetizedWords(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(Note: Note)

    @Query("DELETE FROM note_table")
    suspend fun deleteAll()

    @Query("DELETE FROM note_table where id = :id")
    suspend fun delete(id:Int)

    @Query("UPDATE note_table SET titulo=:titulo, descricao=:descricao WHERE id=:id")
    suspend fun update(id:Int, titulo:String, descricao:String)
}