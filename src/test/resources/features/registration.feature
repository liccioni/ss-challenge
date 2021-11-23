Feature: Students register to courses

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

  Scenario: Students register to courses
    When students register to courses:
      | studentId | courseNames                  |
      | S001      | Math,English                 |
      | S002      | Math,English,Science         |
      | S003      | History,English              |
      | S004      | Science,English              |
      | S005      | Math,History,Science,English |
    Then courses have students registered sorted by name:
      | courseName | studentIds               |
      | Math       | S001,S002,S005           |
      | History    | S003,S005                |
      | Science    | S002,S004,S005           |
      | English    | S001,S002,S003,S004,S005 |

  Scenario: Find Students not taking a course
    When students register to courses:
      | studentId | courseNames                  |
      | S001      | Math,English                 |
      | S002      | Math,English,Science         |
      | S003      | History,English              |
      | S004      | Science,English              |
      | S005      | Math,History,Science,English |
    Then students not registered in course:
      | courseName | studentIds     |
      | Math       | S003,S004      |
      | History    | S001,S002,S004 |
      | Science    | S001,S003      |
      | English    |                |

  Scenario: Students register to course twice an error occurs
    When students register to courses an error occurs:
      | studentId | courseNames |
      | S001      | Math        |
      | S001      | Math        |