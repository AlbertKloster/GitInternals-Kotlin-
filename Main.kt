package gitinternals

import java.io.FileInputStream
import java.lang.StringBuilder
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.InflaterInputStream

fun main() {

    var objectType: ObjectType? = ObjectType.BLOB

    try {
        println("Enter .git directory location:")
        val gitBase = readln()

        println("Enter git object hash:")
        val gitHash = readln()
        val fileInputStream = FileInputStream("$gitBase\\objects\\${gitHash.substring(0, 2)}\\${gitHash.substring(2)}")
        val inflater = InflaterInputStream(fileInputStream)
        val currentBytes = mutableListOf<Int>()
        var byteRead: Int
        while (inflater.read().also { byteRead = it } != -1) {
            if (objectType != ObjectType.TREE && byteRead == 0) {
                val string = currentBytes.flatToString()
                val (objectTypeString, _) = string.split(" ")
                try {
                    objectType = ObjectType.getObjectType(objectTypeString)
                } catch (e: RuntimeException) {
                    println(e.message)
                }
                currentBytes.clear()
            } else {
                currentBytes.add(byteRead)
            }
        }

        when (objectType) {
            ObjectType.BLOB -> printBlob(currentBytes)
            ObjectType.COMMIT -> printCommit(currentBytes)
            ObjectType.TREE -> printTree(currentBytes)
            null -> {}
        }

        inflater.close()
        fileInputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun printTree(currentBytes: List<Int>) {
    println("*TREE*")

    val numberAndFilenameList = mutableListOf<String>()
    val hashList = mutableListOf<String>()

    var isHash = false
    val currentHash = StringBuilder()
    val currentNumberAndFilename = StringBuilder()
    for (byte in currentBytes) {

        if (isHash) {
            currentHash.append("%02x".format(byte))
            if (currentHash.length == 40) {
                hashList.add(currentHash.toString())
                currentHash.clear()
                isHash = false
            }
        } else {
            currentNumberAndFilename.append(byte.toChar())
        }

        if (byte == 0) {
            numberAndFilenameList.add(currentNumberAndFilename.removeSuffix("\u0000").toString())
            currentNumberAndFilename.clear()
            isHash = true
        }

    }

    for (i in numberAndFilenameList.indices) {
        val (number, filename) = numberAndFilenameList[i].split(" ")
        println("$number ${hashList[i]} $filename")
    }

}

private fun MutableList<Int>.flatToString() = this.joinToString("") { it.toChar().toString() }

fun printBlob(currentBytes: MutableList<Int>) {
    println("*BLOB*")
    println(currentBytes.flatToString())
}

fun printCommit(currentBytes: MutableList<Int>) {
    println("*COMMIT*")
    printCommitTree(currentBytes)
    printParents(currentBytes)
    printAuthor(currentBytes)
    printCommitter(currentBytes)
    printCommitMessage(currentBytes)

}

private fun printCommitTree(currentBytes: MutableList<Int>) {
    val regex = """tree [0-9a-z]+""".toRegex()
    val matchResult = regex.find(currentBytes.flatToString())
    val tree = matchResult?.value?.replace("tree ", "")
    if (!tree.isNullOrBlank())
        println("tree: $tree")
}

private fun printParents(currentBytes: MutableList<Int>) {
    val regex = """parent [0-9a-z]+""".toRegex()
    val matchResult = regex.findAll(currentBytes.flatToString())
    val result = mutableListOf<String>()
    matchResult.forEach { result.add(it.value.replace("parent ", "")) }
    val parents = result.joinToString(" | ")
    if (parents.isNotBlank())
        println("parents: $parents")
}

private fun printAuthor(currentBytes: MutableList<Int>) {
    val regexAuthor = """author [0-9a-zA-Z]+ """.toRegex()
    val matchResultAuthor = regexAuthor.find(currentBytes.flatToString())
    val author = matchResultAuthor?.value?.replace("author ", "")?.trim()

    val regexEmail = (matchResultAuthor?.value + "<[0-9.@a-zA-Z]+>").toRegex()
    val matchResultEmail = matchResultAuthor?.value?.let { regexEmail.find(currentBytes.flatToString()) }
    val emailTag = matchResultEmail?.value?.replace(regexAuthor, "")
    val email = emailTag?.removePrefix("<")?.removeSuffix(">")

    val regexTimeStamp = "${matchResultEmail?.value} \\d+ [+-]?\\d+".toRegex()
    val matchResultTimeStamp = matchResultEmail?.value?.let { regexTimeStamp.find(currentBytes.flatToString()) }
    val timeStampString = matchResultTimeStamp?.value?.replace(regexEmail, "")?.trim() ?: throw RuntimeException("Invalid time format")
    val timeStamp = timeStampString.parseTimeStamp()


    println("author: $author $email original timestamp: $timeStamp")
}

private fun printCommitter(currentBytes: MutableList<Int>) {
    val regexCommitter = """committer [0-9a-zA-Z]+ """.toRegex()
    val matchResultCommitter = regexCommitter.find(currentBytes.flatToString())
    val committer = matchResultCommitter?.value?.replace("committer ", "")?.trim()

    val regexEmail = (matchResultCommitter?.value + "<[0-9.@a-zA-Z]+>").toRegex()
    val matchResultEmail = matchResultCommitter?.value?.let { regexEmail.find(currentBytes.flatToString()) }
    val emailTag = matchResultEmail?.value?.replace(regexCommitter, "")
    val email = emailTag?.removePrefix("<")?.removeSuffix(">")

    val regexTimeStamp = "${matchResultEmail?.value} \\d+ [+-]?\\d+".toRegex()
    val matchResultTimeStamp = matchResultEmail?.value?.let { regexTimeStamp.find(currentBytes.flatToString()) }
    val timeStampString = matchResultTimeStamp?.value?.replace(regexEmail, "")?.trim() ?: throw RuntimeException("Invalid time format")
    val timeStamp = timeStampString.parseTimeStamp()


    println("committer: $committer $email commit timestamp: $timeStamp")
}

private fun printCommitMessage(currentBytes: MutableList<Int>) {
    val regex = """committer .*""".toRegex()
    val matchResult = regex.find(currentBytes.flatToString())
    val committer = matchResult?.value
    if (committer != null) {
        val index = currentBytes.flatToString().indexOf(committer) + committer.length
        val commitMessage = currentBytes.flatToString().substring(index).trim()
        if (commitMessage.isNotBlank())
            println("commit message:\n$commitMessage")
    }
}

private fun String.parseTimeStamp(): String {
    val (seconds, zone) = this.split(" ")
    return Instant.ofEpochSecond(seconds.toLong()).atZone(ZoneOffset.of(zone)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx"))
}
