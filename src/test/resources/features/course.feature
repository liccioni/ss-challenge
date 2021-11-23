Feature: Course creation

  Scenario: A course is created successfully
    Given there are no courses
    When course with name 'math' is created
    Then course with name 'math' exists

  Scenario: A course name is modified successfully
    Given there are no courses
    And course with name 'math' is created
    When course with name 'math' is modified to 'Basic math'
    Then course with name 'Basic math' exists

  Scenario: A course is deleted successfully
    Given there are no courses
    And course with name 'math' is created
    When course with name 'math' is deleted
    Then course with name 'math' does not exists

  Scenario: An error occurs with duplicated course
    Given there are no courses
    And course with name 'math' is created
    When course with name 'math' is created an error occurs

  Scenario: Find course
    Given there are no courses
    And following courses are created:
      | Math    |
      | History |
      | Science |
    Then course with name 'History' exists

  Scenario: Find all courses
    Given there are no courses
    And following courses are created:
      | Math    |
      | History |
      | Science |
    Then following courses exist:
      | Math    |
      | History |
      | Science |