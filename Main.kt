package gitinternals

import java.io.FileInputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.InflaterInputStream

fun main() {

    var objectType: ObjectType? = null

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
                val (objectTypeString, sizeString) = currentString.split(" ")
                try {
                    objectType = ObjectType.getObjectType(objectTypeString)
                } catch (e: RuntimeException) {
                    println(e.message)
                }
                currentString.clear()
            } else {
                currentString.append(byteRead.toChar())
            }
        }

        when (objectType) {
            ObjectType.BLOB -> printBlob(currentString)
            ObjectType.COMMIT -> printCommit(currentString)
            null -> {}
        }

        inflater.close()
        fileInputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun printBlob(currentString: StringBuilder) {
    println("*BLOB*")
    println(currentString)
}

fun printCommit(currentString: StringBuilder) {
    println("*COMMIT*")
    printTree(currentString)
    printParents(currentString)
    printAuthor(currentString)
    printCommitter(currentString)
    printCommitMessage(currentString)

}

private fun printTree(currentString: StringBuilder) {
    val regex = """tree [0-9a-z]+""".toRegex()
    val matchResult = regex.find(currentString)
    val tree = matchResult?.value?.replace("tree ", "")
    if (!tree.isNullOrBlank())
        println("tree: $tree")
}

private fun printParents(currentString: StringBuilder) {
    val regex = """parent [0-9a-z]+""".toRegex()
    val matchResult = regex.findAll(currentString)
    val result = mutableListOf<String>()
    matchResult.forEach { result.add(it.value.replace("parent ", "")) }
    val parents = result.joinToString(" | ")
    if (!parents.isNullOrBlank())
        println("parents: $parents")
}

private fun printAuthor(currentString: StringBuilder) {
    val regexAuthor = """author [0-9a-zA-Z]+ """.toRegex()
    val matchResultAuthor = regexAuthor.find(currentString)
    val author = matchResultAuthor?.value?.replace("author ", "")?.trim()

    val regexEmail = (matchResultAuthor?.value + "<[0-9.@a-zA-Z]+>").toRegex()
    val matchResultEmail = matchResultAuthor?.value?.let { regexEmail.find(currentString) }
    val emailTag = matchResultEmail?.value?.replace(regexAuthor, "")
    val email = emailTag?.removePrefix("<")?.removeSuffix(">")

    val regexTimeStamp = "${matchResultEmail?.value} \\d+ [+-]?\\d+".toRegex()
    val matchResultTimeStamp = matchResultEmail?.value?.let { regexTimeStamp.find(currentString) }
    val timeStampString = matchResultTimeStamp?.value?.replace(regexEmail, "")?.trim() ?: throw RuntimeException("Invalid time format")
    val timeStamp = timeStampString.parseTimeStamp()


    println("author: $author $email original timestamp: $timeStamp")
}

private fun printCommitter(currentString: StringBuilder) {
    val regexCommitter = """committer [0-9a-zA-Z]+ """.toRegex()
    val matchResultCommitter = regexCommitter.find(currentString)
    val committer = matchResultCommitter?.value?.replace("committer ", "")?.trim()

    val regexEmail = (matchResultCommitter?.value + "<[0-9.@a-zA-Z]+>").toRegex()
    val matchResultEmail = matchResultCommitter?.value?.let { regexEmail.find(currentString) }
    val emailTag = matchResultEmail?.value?.replace(regexCommitter, "")
    val email = emailTag?.removePrefix("<")?.removeSuffix(">")

    val regexTimeStamp = "${matchResultEmail?.value} \\d+ [+-]?\\d+".toRegex()
    val matchResultTimeStamp = matchResultEmail?.value?.let { regexTimeStamp.find(currentString) }
    val timeStampString = matchResultTimeStamp?.value?.replace(regexEmail, "")?.trim() ?: throw RuntimeException("Invalid time format")
    val timeStamp = timeStampString.parseTimeStamp()


    println("committer: $committer $email commit timestamp: $timeStamp")
}

private fun printCommitMessage(currentString: StringBuilder) {
    val regex = """committer .*""".toRegex()
    val matchResult = regex.find(currentString)
    val committer = matchResult?.value
    if (committer != null) {
        val index = currentString.indexOf(committer) + committer.length
        val commitMessage = currentString.substring(index).trim()
        if (commitMessage.isNotBlank())
            println("commit message:\n$commitMessage")
    }
}

private fun String.parseTimeStamp(): String {
    val (seconds, zone) = this.split(" ")
    return Instant.ofEpochSecond(seconds.toLong()).atZone(ZoneOffset.of(zone)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx"))
}
