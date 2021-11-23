Feature: student creation

  Scenario: A student is created successfully
    Given there are no students
    When student with details is created:
      | firstName | lastName | studentId |
      | Jerry     | Seinfeld | S001      |
    Then student with id 'S001' exists with details:
      | firstName | lastName | studentId |
      | Jerry     | Seinfeld | S001      |

  Scenario: A student name is modified successfully
    Given there are no students
    When student with details is created:
      | firstName | lastName | studentId |
      | George    | Costanza | S002      |
    When student with id 'S002' details are modified to:
      | firstName | lastName | studentId |
      | Larry     | David    | S002      |
    Then student with id 'S002' exists with details:
      | firstName | lastName | studentId |
      | Larry     | David    | S002      |

  Scenario: A student is deleted successfully
    Given there are no students
    When student with details is created:
      | firstName | lastName | studentId |
      | Jerry     | Seinfeld | S001      |
    When student with id 'S001' is deleted
    Then student with id 'S001' does not exists

  Scenario: An error occurs with duplicated student
    Given there are no students
    When student with details is created:
      | firstName | lastName | studentId |
      | Jerry     | Seinfeld | S001      |
    When student with details is created an error occurs:
      | firstName | lastName | studentId |
      | Jerry     | Seinfeld | S001      |

  Scenario: Find all students
    Given there are no students
    And following students are created:
      | firstName | lastName | studentId |
      | Jerry     | Seinfeld | S001      |
      | George    | Costanza | S002      |
      | Elaine    | Benes    | S003      |
      | Cosmo     | Kramer   | S004      |
      | Hello     | Newman   | S005      |
    Then following students exist:
      | firstName | lastName | studentId |
      | Jerry     | Seinfeld | S001      |
      | George    | Costanza | S002      |
      | Elaine    | Benes    | S003      |
      | Cosmo     | Kramer   | S004      |
      | Hello     | Newman   | S005      |
