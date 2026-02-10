package com.example.kcflightsearch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "flight_route",
    primaryKeys = ["departure_code", "destination_code"],
    foreignKeys = [
        ForeignKey(
            entity = Airport::class,
            parentColumns = ["iata_code"],
            childColumns = ["departure_code"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Airport::class,
            parentColumns = ["iata_code"],
            childColumns = ["destination_code"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["departure_code"]),
        Index(value = ["destination_code"])
    ]
)
data class FlightRoute(
    @ColumnInfo(name = "departure_code")
    val departureCode: String,
    @ColumnInfo(name = "destination_code")
    val destinationCode: String
)
