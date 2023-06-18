package com.example.ass4;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ass4.entities.Chat;

import java.util.List;
@Dao
public interface ChatDao {
    @Query("SELECT * FROM Chat")
    List<Chat> index();
    @Query("SELECT * FROM Chat WHERE id = :id")
Chat get(String id);
       @Insert
 void insert(Chat... chats);

       @Update
 void update(Chat... chats);

       @Delete
 void delete(Chat... chats);
}