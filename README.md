# java-filmorate
Template repository for Filmorate project.
![ER Diagram](/er_diagram_joint_project.png)//

Backend on java for a service to work with movies and user ratings, the ability to get a list of movies with the highest rating, recommended for viewing by the community.

1) Ability to delete users and movies.
2) Added directors to movies. Output of all directors, sorted by the number of likes.
   Display all movies of the director, sorted by year.
3) Implemented the output of shared movies sorted by their popularity.
4) Now it is possible to show the top N movies according to the number of likes. Filtering can be done via two
Filtering can be done using two parameters: genre and year.
5) Search by filename and director.
6) We have implemented functionality "Reviews". Review options: helpful/helpless. Review type
negative/positive.
7) Implemented "Events". Ability to view recent events on the platform - adding to friends, removal from friends, likes and feedback, which were left by user's friends. 
8) Functionality "Recommendations" has been implemented. The program searches for the user with the maximum overlap
likes. Then it calculates the movies that one liked and the other didn't. And then the program recommends movies that 
the user with similar tastes and the one for whom the recommendation is made has not yet been liked.
