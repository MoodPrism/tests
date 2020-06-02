Feature: Press Escape key and receiving confirmation
	Scenario: Press Escape Key
		Given I have the application running
		When I press the key "Escape"
		And The page refreshes
		Then The key should appear on the screen
		


