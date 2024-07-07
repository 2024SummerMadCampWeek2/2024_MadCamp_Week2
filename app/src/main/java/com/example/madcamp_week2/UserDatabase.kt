package com.example.madcamp_week2

import android.content.Context
import androidx.room.*

@Database(entities = [UserEntity::class], version = 3) // 버전을 1에서 2로 증가
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var instance: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, UserDatabase::class.java, "user-database")
                .fallbackToDestructiveMigration() // 이 줄을 추가합니다.
                .build()
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "profile_image") val profileImage: String?,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "reviewed_books") val reviewedBooks: String, // JSON string
    @ColumnInfo(name = "read_books") val readBooks: String // JSON string
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    fun getUser(name: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Update
    fun updateUser(user: UserEntity)
}