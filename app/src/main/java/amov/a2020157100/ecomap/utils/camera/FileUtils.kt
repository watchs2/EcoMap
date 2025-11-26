package amov.a2020157100.ecomap.utils.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
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


            var bitmap = BitmapFactory.decodeFile(file.absolutePath, options) ?: return filePath

            try {
                val exif = ExifInterface(file.absolutePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }

                if (orientation != ExifInterface.ORIENTATION_NORMAL && orientation != ExifInterface.ORIENTATION_UNDEFINED) {
                    val rotatedBitmap = Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                    )
                    if (bitmap != rotatedBitmap) {
                        bitmap.recycle()
                    }
                    bitmap = rotatedBitmap
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val compressedFile = File(file.parent, "compressed_" + file.name)
            val outputStream = FileOutputStream(compressedFile)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()

            return compressedFile.absolutePath
        }


    }
}