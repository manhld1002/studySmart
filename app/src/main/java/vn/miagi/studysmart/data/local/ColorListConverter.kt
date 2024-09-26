package vn.miagi.studysmart.data.local

import androidx.room.TypeConverter

class ColorListConverter
{
    // Save data to db
    @TypeConverter
    fun fromColorList(colorList: List<Int>): String
    {
        return colorList.joinToString(",") { it.toString() }
    }

    // retrieve from db
    @TypeConverter

    fun toColorList(colorListString: String): List<Int>
    {
        return colorListString.split(",").map { it.toInt()  }
    }
}