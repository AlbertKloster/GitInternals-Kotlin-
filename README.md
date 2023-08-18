# Stage 2/7: Git object types
## Description
Git has three types of objects:

- <b>Blob</b> stores file contents
- <b>Tree</b> stores directory structure with filenames and subdirectories
- <b>Commit</b> represents the snapshots of your project

Any Git object file starts with a header. The header is a null-terminated string of text containing the object type and size. <i>(You should already be familiar with null-terminated strings from the previous stage.)</i>

## Objectives
- Write a program that asks the user for the `.git` directory location and the Git object hash.
- Find the file, the path to the file is as follows: first the git directory path, followed by objects folder, followed by a folder with the first two digits of the object hash and inside is located the object file that has the remaining digits of the hash as name. In summary: `[git directory]/objects/[first two digits of hash]/[remaining digits of hash]`
- Output only the object header data, which contains the object type and size, using the format "`type:[type] length:[length]`".

## Examples
Note that we will not use the .git folder in this project as it is impossible to store under Git. The `.git` folder contents needed for stage testing will be stored in the “test” folder.

The greater-than symbol followed by a space `>` represents the user input. Note that it's not part of the input.

<b>Example 1</b>
```
Enter .git directory location:
> /home/my_project/.git
Enter git object hash:
> 0eee6a98471a350b2c2316313114185ecaf82f0e
type:commit length:216
```

<b>Example 2</b>
```
Enter .git directory location:
> task/test/gitone
Enter git object hash:
> 490f96725348e92770d3c6bab9ec532564b7ebe0
type:blob length:85
```

<b>Example 3</b>
```
Enter .git directory location:
> task/test/gitone
Enter git object hash:
> a7b882bbf2db5d90287e9affc7e6f3b3c740b327
type:tree length:35
```
