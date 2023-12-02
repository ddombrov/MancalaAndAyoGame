# Project Title
Mancala Game Player

## Description
This assignment should allow you to play a Mancala game via a GUI, with either the Kalah or Ayo rule set and save/load features. Keyword should.
## Getting Started

### Dependencies

Java is required to be installed to run this.

### Executing program

* Compile program in the directory:
```
gradle build
```
* which show display: 
```
> Task :pmdTest
Removed misconfigured rule: LoosePackageCoupling  cause: No packages or classes specified

> Task :pmdMain
Removed misconfigured rule: LoosePackageCoupling  cause: No packages or classes specified
271 PMD rule violations were found. See the report at: file:///course/CIS2430%20Textbook%20Location/Tutorials/GP4/build/reports/pmd/main.html

Deprecated Gradle features were used in this build, making it incompatible with Gradle 9.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

See https://docs.gradle.org/8.1.1/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 10s
5 actionable tasks: 4 executed, 1 up-to-date
```
* then run: 
```
java -cp build/classes/java/main ui.TextUI
```
* which outputs: 
```
k or a?
k (user choice)

Pit1: 4         Pit2: 4         Pit3: 4         Pit4: 4         Pit5: 4         Pit6: 4
Player1 Store: 0                                                Player2 Store: 0
Pit7: 4         Pit8: 4         Pit9: 4 Pit10: 4        Pit11: 4        Pit12: 4
Player 1, please enter the pit number:
```
## Limitations

While both rule sets should not cause errors, the Ayo rules has some incomplete logic. The GameRules class has replaced Board class. Persistance and GUI not yet implemented. Code has duplication due to time constraints.

## Author Information

Name: Daniel Dombrovsky, Contact: ddombrov@uoguelph.ca, Student #: 1211846

## Development History
 
I had done all changes to GP3 instead of GP4 until I realized and started pushing to GP4 repo. My pmd errors are much more numerous than A2 since I had little working in A2 but A3 has a lot of components. I let it make a master but course-corrected to main.

## Acknowledgments

Inspiration, code snippets, etc.
* [awesome-readme](https://github.com/matiassingers/awesome-readme)
* [simple-readme] (https://gist.githubusercontent.com/DomPizzie/7a5ff55ffa9081f2de27c315f5018afc/raw/d59043abbb123089ad6602aba571121b71d91d7f/README-Template.md)



