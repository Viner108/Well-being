package com.example.well_being


class MapperSelfAwareness {

    fun toDto():UserHealthDto {
        var userHealthDto:UserHealthDto = UserHealthDto(1,1,"120/60","5","2000.12.12")
        return userHealthDto
    }
    fun toEntity(userHealthDto: UserHealthDto) {


    }
}