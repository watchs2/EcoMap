package amov.a2020157100.ecomap.utils.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class FileUtils {
    companion object {
        fun getTempFilename(context: Context) : String =
            File.createTempFile(
                "image", ".img",
                context.externalCacheDir
            ).absolutePath

        fun createFileFromUri(
            context: Context,
            uri : Uri,
            filename : String = getTempFilename(context)
        ) : String {
            FileOutputStream(filename).use { outputStream ->
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return filename
        }
        fun compressImage(filePath: String): String{
            val file = File(filePath)


            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, options)


            val maxWidth = 1024
            val maxHeight = 1024
            var inSampleSize = 1

            if (options.outHeight > maxHeight || options.outWidth > maxWidth) {
                val halfHeight: Int = options.outHeight / 2
                val halfWidth: Int = options.outWidth / 2
                while ((halfHeight / inSampleSize) >= maxHeight && (halfWidth / inSampleSize) >= maxWidth) {
                    inSampleSize *= 2
                }
            }


            options.inJustDecodeBounds = false
            options.inSampleSize = inSampleSize
            val bitmap = BitmapFactory.decodeFile(file.absolutePath, options) ?: return filePath // Se falhar, retorna original


            val compressedFile = File(file.parent, "compressed_" + file.name)
            val outputStream = FileOutputStream(compressedFile)


            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()

            return compressedFile.absolutePath
        }


    }
}