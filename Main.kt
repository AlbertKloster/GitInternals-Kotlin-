package gitinternals

import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.InflaterInputStream
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries

fun main() {
    println("Enter .git directory location:")
    val directoryLocation = readln()

    println("Enter command:")
    when (Command.getCommand(readln())) {
        Command.CAT_FILE -> catFile(directoryLocation)
        Command.LIST_BRANCHES -> listBranches(directoryLocation)
        Command.LOG -> log(directoryLocation)
    }

}

fun listBranches(directoryLocation: String) {

    val head = File("$directoryLocation/HEAD").readText()
    val currentBranch = head.split("/").last().trim()

    Path("$directoryLocation/refs/heads").listDirectoryEntries().sorted().forEach {
        print(if (it.fileName.toString() == currentBranch) "* " else "  ")
        println(it.fileName)
    }
}


private fun log(directoryLocation: String) {
    println("Enter branch name:")
    val branchName = readln()
    val gitHash = File("$directoryLocation/refs/heads/$branchName").readText().trim()
    printLog(directoryLocation, gitHash)

}

private fun printLog(directoryLocation: String, gitHash: String) {
    println("Commit: $gitHash")
    val fileName = "$directoryLocation/objects/${gitHash.substring(0, 2)}/${gitHash.substring(2)}"
    val (currentBytes, _) = getCurrentBytesObjectType(FileNameObjectType(fileName, ObjectType.COMMIT))

    println(getCommitterString(currentBytes))
    println(getCommitMessageString(currentBytes))
    println()


    val parentHashList = getParentsString(currentBytes).replace("parents: ", "").split(" | ")
    if (parentHashList.size == 2) {
        val parentHash = parentHashList.last()
        println("Commit: $parentHash (merged)")
        val fileName = "$directoryLocation/objects/${parentHash.substring(0, 2)}/${parentHash.substring(2)}"
        val (currentBytes, _) = getCurrentBytesObjectType(FileNameObjectType(fileName, ObjectType.COMMIT))
        println(getCommitterString(currentBytes))
        println(getCommitMessageString(currentBytes))
        println()
    }

    val parentHash = parentHashList.first()
    if (parentHash.isNotBlank())
        printLog(directoryLocation, parentHash)

}

private fun catFile(directoryLocation: String) {
    println("Enter git object hash:")
    val gitHash = readln()
    val fileName = "$directoryLocation/objects/${gitHash.substring(0, 2)}/${gitHash.substring(2)}"
    printFile(fileName)
}

private fun printFile(fileName: String) {
    var objectType: ObjectType? = ObjectType.BLOB
    try {
        val currentBytesObjectType = getCurrentBytesObjectType(FileNameObjectType(fileName, objectType))
        objectType = currentBytesObjectType.objectType
        when (objectType) {
            ObjectType.BLOB -> printBlob(currentBytesObjectType.currentBytes)
            ObjectType.COMMIT -> printCommit(currentBytesObjectType.currentBytes)
            ObjectType.TREE -> printTree(currentBytesObjectType.currentBytes)
            null -> {}
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun getCurrentBytesObjectType(fileNameObjectType: FileNameObjectType): CurrentBytesObjectType {
    var (fileName, objectType) = fileNameObjectType
    val fileInputStream = FileInputStream(fileName)
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
    inflater.close()
    fileInputStream.close()
    return CurrentBytesObjectType(currentBytes, objectType)
}

private fun printTree(currentBytes: List<Int>) {
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

private fun printBlob(currentBytes: MutableList<Int>) {
    println("*BLOB*")
    println(currentBytes.flatToString())
}

private fun printCommit(currentBytes: MutableList<Int>) {
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
    val parents = getParentsString(currentBytes)
    if (parents.isNotBlank())
        println("parents: $parents")
}

private fun getParentsString(currentBytes: MutableList<Int>): String {
    val regex = """parent [0-9a-z]+""".toRegex()
    val matchResult = regex.findAll(currentBytes.flatToString())
    val result = mutableListOf<String>()
    matchResult.forEach { result.add(it.value.replace("parent ", "")) }
    return result.joinToString(" | ")
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
    println("committer: ${getCommitterString(currentBytes)}")
}

private fun getCommitterString(currentBytes: MutableList<Int>): String {
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

    return "$committer $email commit timestamp: $timeStamp"
}

private fun printCommitMessage(currentBytes: MutableList<Int>) {
    val commitMessageString = getCommitMessageString(currentBytes)
    if (commitMessageString.isNotBlank())
        println("commit message:\n$commitMessageString")
}

private fun getCommitMessageString(currentBytes: MutableList<Int>): String {
    val regex = """committer .*""".toRegex()
    val matchResult = regex.find(currentBytes.flatToString())
    val committer = matchResult?.value
    if (committer != null) {
        val index = currentBytes.flatToString().indexOf(committer) + committer.length
        return currentBytes.flatToString().substring(index).trim()
    }
    return ""
}

private fun String.parseTimeStamp(): String {
    val (seconds, zone) = this.split(" ")
    return Instant.ofEpochSecond(seconds.toLong()).atZone(ZoneOffset.of(zone)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx"))
}
