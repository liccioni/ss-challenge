Feature: Score Features

  Background: Students and courses are created
    Given following students are created:
      | firstName | lastName | studentId |
      | Jerry     | Seinfeld | S001      |
      | George    | Costanza | S002      |
      | Elaine    | Benes    | S003      |
      | Cosmo     | Kramer   | S004      |
      | Hello     | Newman   | S005      |
    And following courses are created:
      | Math    |
      | History |
      | Science |
      | English |
    And students register to courses:
      | studentId | courseNames                  |
      | S001      | Math,English                 |
      | S002      | Math,English,Science         |
      | S003      | History,English              |
      | S004      | Science,English              |
      | S005      | Math,History,Science,English |

  Scenario: Students are assigned scores
    When Students are assigned scores:
      | studentId | courseName | scores                      |
      | S001      | Math       | Test1:9.8,Test2:5.8,Final:7 |
      | S001      | English    | Test1:8,Final:6.5           |
      | S002      | Math       | Test1:4,Test2:5,Final:2     |
      | S002      | Science    | Test1:3,Final:5.5           |
    Then scores match:
      | studentId | courseName | scores                      |
      | S001      | Math       | Test1:9.8,Test2:5.8,Final:7 |
      | S001      | English    | Test1:8,Final:6.5           |
      | S002      | Math       | Test1:4,Test2:5,Final:2     |
      | S002      | Science    | Test1:3,Final:5.5           |