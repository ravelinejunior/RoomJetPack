package br.com.raveline.roomjetpack.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriber_tb")
data class Subscriber(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    var email: String,

    var peculiarity: String
)
