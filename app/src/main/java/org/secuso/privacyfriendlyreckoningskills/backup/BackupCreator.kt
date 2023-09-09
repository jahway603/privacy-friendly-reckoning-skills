package org.secuso.privacyfriendlyreckoningskills.backup


import android.content.Context
import android.preference.PreferenceManager
import android.util.JsonWriter
import android.util.Log
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil.getSupportSQLiteOpenHelper
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil.writeDatabase
import org.secuso.privacyfriendlybackup.api.backup.PreferenceUtil.writePreferences
import org.secuso.privacyfriendlybackup.api.pfa.IBackupCreator
import org.secuso.privacyfriendlyreckoningskills.database.PFASQLiteHelper
import java.io.OutputStream
import java.io.OutputStreamWriter

class BackupCreator : IBackupCreator {
    override fun writeBackup(context: Context, outputStream: OutputStream): Boolean {
        Log.d(TAG, "createBackup() started")
        val outputStreamWriter = OutputStreamWriter(outputStream, Charsets.UTF_8)
        val writer = JsonWriter(outputStreamWriter)
        writer.setIndent("")

        try {
            writer.beginObject()

            Log.d(TAG, "Writing database")
            writer.name("database")

            val database = getSupportSQLiteOpenHelper(context, PFASQLiteHelper.DATABASE_NAME).readableDatabase

            writeDatabase(writer, database)
            database.close()

            Log.d(TAG, "Writing preferences")
            writer.name("preferences")

            val pref = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
            writePreferences(writer, pref)

            Log.d(TAG, "Writing highscores")
            writer.name("highscore")

            val highscore = context.applicationContext.getSharedPreferences("pfa-math-highscore", 0 )
            writePreferences(writer, highscore)

            writer.endObject()
            writer.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error occurred", e)
            e.printStackTrace()
            return false
        }

        Log.d(TAG, "Backup created successfully")
        return true
    }

    companion object {
        const val TAG = "PFABackupCreator"
    }
}