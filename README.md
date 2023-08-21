# Stage 6/7: Git log
## Description
What happens when you ask Git for the log using the git log command? Git iterates through the commits using parent links until it reaches a commit with no parents. This orphan commit is the initial commit for your repo.

## Objectives
- Extend your program with the `log` command.
- It should iterate commits and print out a log for the specified branch.
- Last commit should appear first on the log, and the initial commit should be the last.
- If a commit has two parents print first the merged commit and add "` (merged)`" after the hash number <i>(merged commit is the one that is coming from another branch)</i>.
- Use the output format shown in the example.

## Example
The greater-than symbol followed by a space `>` represents the user input. Note that it's not part of the input.

<b>Example 1</b>
```
Enter .git directory location:
> task/test/gitone
Enter command:
> log
Enter branch name:
feature2
Commit: 97e638cc1c7135580c3ff93162e727148e1bad05
Cypher cypher@matrix commit timestamp: 2020-03-29 17:27:35 +03:00
break our software

Commit: 0eee6a98471a350b2c2316313114185ecaf82f0e
Cypher cypher@matrix commit timestamp: 2020-03-29 17:25:52 +03:00
get docs from feature1

Commit: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0
Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:12:52 +03:00
start kotlin project

Commit: 73324685d9dbd1fdda87f3c5c6f77d79c1b769c2
Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:10:52 +03:00
initial commit
```

<b>Example 2</b>
```
Enter .git directory location:
> task/test/gittwo
Enter command:
> log
Enter branch name:
main
Commit: dcec4e51e2ce4a46a6206d0d4ab33fa99d8b1ab5
Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:49:02 -03:00
awsome hello

Commit: d2c5bedbb2c46945fd84f2ad209a7d4ee047f7f9 (merged)
Ivan Petrovich@moon.org commit timestamp: 2021-12-11 22:43:54 -03:00
hello of the champions

Commit: 5ad3239e54ba7c533d9f215a13ac82d14197cd8f
Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:46:28 -03:00
maybe hello

Commit: 31cddcbd00e715688cd127ad20c2846f9ed98223
Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:31:36 -03:00
simple hello
```
