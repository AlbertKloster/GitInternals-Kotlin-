# Stage 7/7: Full tree
## Description
Finally, let's try to get the full filesystem tree of the project for the specified commit.

As you remember from stage 4, a tree object contains the file and the subdirectory list for one folder. To get the full project tree, you should recursively iterate through tree objects.

## Objectives
- Extend your program with the `commit-tree` command.
- Ask the user to specify the commit.
- Print out the full file tree.

## Example
The greater-than symbol followed by a space `>` represents the user input. Note that it's not part of the input.
```
Enter .git directory location:
> task/test/gitone
Enter command:
> commit-tree
Enter commit-hash:
> fd362f3f305819d17b4359444aa83e17e7d6924a
main.kt
readme.txt
some-folder/qq.txt
```
