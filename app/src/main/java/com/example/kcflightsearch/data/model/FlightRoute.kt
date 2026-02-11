package com.example.kcflightsearch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "flight_route")
data class FlightRoute(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo("departure_code")
    val departureCode: String,

    @ColumnInfo("destination_code")
    val destinationCode: String
)
