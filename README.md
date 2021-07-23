# Bowling


To run this Project:
	1. Open in IntelliJ and make sure the version in Project Structure is JDK 1.8 and run the BowlingApplication class.
	2. Run the Jar file in target folder using java -jar PATH_TO_JAR/filename.jar
	
Note: Using POSTMAN  to fire the API request.
	
This Project has a com.main package where the main class, controller and reposritory is located and in the model class all the necessary model classes like game and frame and scoreboard.
Main Business Logic present in the BowlingController class and the Repository stores the games using a HashMap.

Model package has:
	Game class which stores a game with frames and rolls.
	Frame class is used to store one single frame.
	Roll is used to roll.
	It also has a Scoreboard class to keep track of the score such as moves, frame number, frame score, etc.
	
I also have tried to compile a few test cases like Invalid roll, strikes, spares in the BowlingApplicationTests.

I have attached a recorded demonstratio of the APIS using PostMan service by running the jar file in CMD which also shows the logger statements.
