package gitinternals

import java.io.FileInputStream
import java.util.zip.InflaterInputStream

fun main() {
    try {
        println("Enter .git directory location:")
        val gitBase = readln()

        println("Enter git object hash:")
        val gitHash = readln()
        val fileInputStream = FileInputStream("$gitBase\\objects\\${gitHash.substring(0, 2)}\\${gitHash.substring(2)}")
        val inflater = InflaterInputStream(fileInputStream)
        val currentString = StringBuilder()
        var byteRead: Int
        while (inflater.read().also { byteRead = it } != -1) {
            if (byteRead == 0) {
                val (type, length) = currentString.split(Regex(" "))
                println("type:$type length:$length")
                break
            } else {
                currentString.append(byteRead.toChar())
            }
        }

        inflater.close()
        fileInputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}
