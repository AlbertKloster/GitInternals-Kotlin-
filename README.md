# Stage 4/7: Trees
## Description
Tree objects store the file name, and the SHA-1 hash of the file content which is the same as the blob file name for this file, or another tree object if it is a subdirectory. Tree objects can hold a group of files and directories.

Tree object file structure is a bit tricky to read. Just like any other Git object, a file tree object starts with a null-terminated header. The header is followed by one or more items consisting of: a permission metadata number, a whitespace, a filename, a null char and a 20-byte long binary SHA-1. Pay attention that there is no whitespace nor null char between the SHA-1 and the next item if there is one.

## Objectives
- Add support for reading tree objects to your current program
- Convert the 20 byte long SHA-1 binary to hexadecimal lowercase string, which should be 40 digits long after convertion
- While making the conversion zeropad the hex representation of a byte if it results in only one digit (ex: a byte with value 10 converts to "0a", 0 converts to "00" and 200 converts to "c8")

## Example
The greater-than symbol followed by a space `>` represents the user input. Note that it's not part of the input.
```
Enter .git directory location:
> task/test/gitone
Enter git object hash:
> 109e8050b41bd10b81be0a51a5e67327f5609551
*TREE*
100644 2b26c15c04375d90203783fb4c2a45ff04b571a6 main.kt
100644 f674b5d3a4c6cef5815b4e72ef2ea1bbe46b786b readme.txt
40000 74198c849dbbcd51d060c59253a4757eedb9bd12 some-folder
```
