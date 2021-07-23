# Bowling

## Requirements: 

	1. JDK 1.8
	2. PostMan, to fire API requests


## Run this Project:
	1. Open in IntelliJ and make sure the version in Project Structure is JDK 1.8 and run the BowlingApplication class.
	2. Run the Jar file in target folder using java -jar PATH_TO_JAR/filename.jar
		`java -jar Bowling-1.0-SNAPSHOT.jar`
	
Note: Using POSTMAN  to fire the API request.

## Details:
	
	This Project has a `com.main` package where the main class, controller and reposritory is located and in the model class has all the necessary model classes like game and frame and scoreboard. Main Business Logic present in the BowlingController class and the Repository stores the games using a HashMap.
	
	Three REST Endpoints are:
		1. newGame, a Post Mapping API to create a new Game. Ex: POST localhost:8080/app/bowling/newGame/{gameName}
		2. roll, a Post Mapping API to roll and knock pins or points. Ex POST localhost:8080/app/bowling/roll/{gameName}/{roll}
		3. getScoreboard, a GET Mapping API to get the active Game score board. Ex: GET localhost:8080/app/bowling/scoreboard/{gameName}

	Model package has:
		1. Game class which stores a game with frames and rolls.
		2. Frame class is used to store one single frame.
		3. Roll is used to roll.
		4. It has a Scoreboard class to keep track of the score such as moves, frame number, frame score, etc.
		
## Test:
	 Test cases like Invalid Roll, Strikes, Spares are included in the BowlingApplicationTests.
	 
## Output:
	I have attached a recorded demo of the APIS using PostMan service by running the jar file in CMD which also shows the logger statements.
