package io.github.pawgli.sudoku.data.local

import androidx.room.*
import retrofit2.http.DELETE

@Dao
interface BoardsDao {

    @Query("SELECT * FROM table_boards WHERE is_saved_by_user = 1")
    fun getAllBoardsSavedByUser(): List<DatabaseBoard>

    @Query("SELECT * FROM table_boards WHERE is_saved_by_user = 0")
    fun getAllEmptyBoards(): List<DatabaseBoard>

    @Query("SELECT * FROM table_boards WHERE is_saved_by_user = 0 AND difficulty = :difficulty")
    fun getAllEmptyBoards(difficulty: String): List<DatabaseBoard>

    @Query("SELECT * FROM table_boards WHERE id = :boardId")
    fun getBoardById(boardId: Int): DatabaseBoard

    @Insert
    fun insertBoard(databaseBoard: DatabaseBoard)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateBoard(databaseBoard: DatabaseBoard)

    @Delete
    fun deleteBoard(databaseBoard: DatabaseBoard)

    @Query("DELETE FROM table_boards WHERE id = :boardId")
    fun deleteBoardById(boardId: Long)
}