# Stage 3/7: Commits
## Description
A commit is a snapshot of your project. A commit object contains the following information:
- Filesystem tree
- A list of parent commits
- The author's name and email
- Date and time when the commit was originally created
- Committer's name and email (committer is the person who applied the commit)
- Date and time when the commit was applied
- Commit message

The commit object name, also known as "commit ID", is a SHA-1 hash of the commit object’s contents.

A commit may have no parent if it is the initial commit.

A commit may have two parents if it is a merge commit. In this case, the <b>first parent</b> is the preceding commit in the <b>current branch</b>, and the <b>second parent</b> is the preceding commit from the <b>branch being merged</b>.

The author and committer differ if the commit was cherry-picked or changed during a merge, rebase, or another operation.

The commit file structure is straightforward: the git object header is followed by plain text lines in the same order as in the description above.

## Objectives
- Your program now should detect the object type for the specified file, and print out the object’s type and contents.
- The content should be reformatted as in the example. In this stage, you should support blobs and commits.
- If the commit has no parent skip the parents line on output
- If the commit has two parents join their hashes separated by "` | `", keeping the same order that was in the file
- You will also have to translate a Unix epoch timestamp to human readable date and time. You can construct a <a href="https://developer.android.com/reference/java/time/Instant">java.time.Instant</a> object of the timestamp and use <a href="https://developer.android.com/reference/java/time/format/DateTimeFormatter">java.time.format.DateTimeFormatter</a> to print it out.
- It is guaranteed that a name (author or committer) does not contain white spaces in middle of the name
- Commit messages may have multiple lines

Please keep in mind that commit messages usually have an empty line at the end.

## Examples
The greater-than symbol followed by a space `>` represents the user input. Note that it's not part of the input.

<b>Example 1</b>
```
Enter .git directory location:
> task/test/gitone
Enter git object hash:

>490f96725348e92770d3c6bab9ec532564b7ebe0
*BLOB*
fun main() {
    while(true) {
        println("Hello Hyperskill student!")
    }
}
```

<b>Example 2</b>
```
Enter .git directory location:
> task/test/gitone
Enter git object hash:
> 0eee6a98471a350b2c2316313114185ecaf82f0e
*COMMIT*
tree: 79401ddb0e2c0fe0472c813754dd4a8873b66a84
parents: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0
author: Smith mr.smith@matrix original timestamp: 2020-03-29 17:18:20 +03:00
committer: Cypher cypher@matrix commit timestamp: 2020-03-29 17:25:52 +03:00
commit message:
get docs from feature1
```

<b>Example 3</b>
```
Enter .git directory location:
> task/test/gittwo
Enter git object hash:
> 31cddcbd00e715688cd127ad20c2846f9ed98223
*COMMIT*
tree: aaa96ced2d9a1c8e72c56b253a0e2fe78393feb7
author: Kalinka Kali.k4@email.com original timestamp: 2021-12-11 22:31:36 -03:00
committer: Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:31:36 -03:00
commit message:
simple hello
```

<b>Example 4</b>
```
Enter .git directory location:
> task/test/gittwo
Enter git object hash:
> dcec4e51e2ce4a46a6206d0d4ab33fa99d8b1ab5
*COMMIT*
tree: d128f76a96c56ac4373717d3fbba4fa5875ca68f
parents: 5ad3239e54ba7c533d9f215a13ac82d14197cd8f | d2c5bedbb2c46945fd84f2ad209a7d4ee047f7f9
author: Kalinka Kali.k4@email.com original timestamp: 2021-12-11 22:49:02 -03:00
committer: Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:49:02 -03:00
commit message:
awsome hello
```
