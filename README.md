# Stage 5/7: Branches
## Description
Lightweight branches are known as one of the best features of Git. In Git, a branch is just one commit object like the one you parsed in stage 3! Branches do not contain any duplicates of other branches.

The list of your local branches is typically stored in the `.git/refs/heads` directory. The file names in this folder are equal to branch names. The content in these files is equal to the commit ID of the head of the corresponding branch.

The current HEAD is stored in the `.git/HEAD` file.

ORIG_HEAD contains the last HEAD you worked on if you are currently in a “detached head” state.

The list of available branches can be accessed with the `git branch -l` command.

## Objectives
Extend your program with command names. Use the `cat-file` command name for `git-object` file printing.

Add the `list-branches` command. This new command should print out local branch names accessible in the `/refs/heads` directory of the specified `.git` location. The branch list should be sorted in alphabetical order. Branch names should be preceded with `*` followed by one space for the current branch and two spaces for other branches.

## Examples
The greater-than symbol followed by a space `>` represents the user input. Note that it's not part of the input.

<b>Example 1</b>
```
Enter .git directory location:
> task/test/gitone
Enter command:
> list-branches
  feature1
  feature2
* master
```

<b>Example 2</b>
```
Enter .git directory location:
> task/test/gitone
Enter command:
> cat-file
Enter git object hash:
> 490f96725348e92770d3c6bab9ec532564b7ebe0
*BLOB*
fun main() {
    while(true) {
        println("Hello Hyperskill student!")
    }
}
```
