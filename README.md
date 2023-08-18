# Stage 1/7: What is a Git object
## Description
Let's start with watching a comprehensive <a href="https://www.youtube.com/watch?v=P6jD966jzlk">introductory video from Gitlab</a>. You can also check out a written <a href="https://git-scm.com/book/en/v2/Git-Internals-Git-Objects">introduction to Git</a>.

Here’s a recap of some key points you learned about Git from the introduction:

- Git objects are stored in the `.git/objects` subdirectory of your project
- Git objects are compressed with zlib
- The file path contains a SHA-1 hash of the object contents

Let’s start with reading the simplest type of object, the blob object.

Note: you can use the `git cat-file -p <get object hash>` command to view the Git object contents.

## Objectives
- Write a program that asks the user for the path to the Git blob object, read the object file, decompress (inflate) it with zlib.
- Print out the content of the file.
- Pay attention that the file is using null terminated strings.
- Java module `java.util.zip` contains the zlib inflator and deflator.
- For a convenient method of inflating the compressed data, check out the <a href="https://www.geeksforgeeks.org/java-util-zip-inflaterinputstream-class-java">Geeks for geeks article</a> on the subject.

Note: null-terminated strings are a common data structure. Strings in C and C++ are stored in memory as sequences of characters followed by the character `\x00` also known as NULL. No additional length information is stored. For example, "`Hello World!\x00`".

## Example
The greater-than symbol followed by a space `>` represents the user input. Note that it's not part of the input.
```
Enter git object location:
> task/test/gitone/objects/61/8383db6d7ee3bd2e97b871205f113b6a3ba854
blob 14
Hello world!
```
