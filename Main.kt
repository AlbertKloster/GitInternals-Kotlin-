package gitinternals

import java.io.FileInputStream
import java.util.zip.InflaterInputStream


fun main() {
    try {
        // Get the path to the Git blob object file from the user
        println("Enter git object location:")
        val pathToBlob = readln()

        // Open the file for reading
        val fis = FileInputStream(pathToBlob)

        // Create an InflaterInputStream to decompress the data
        val inflater = InflaterInputStream(fis)

        // Read and print the decompressed content as null-terminated strings
        val currentString = StringBuilder()
        var byteRead: Int
        while (inflater.read().also { byteRead = it } != -1) {
            if (byteRead == 0) {
                currentString.append("\n")
            } else {
                currentString.append(byteRead.toChar())
            }
        }

        println(currentString)

        // Close the streams
        inflater.close()
        fis.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}
