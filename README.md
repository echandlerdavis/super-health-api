# Movie Rentals API

## Description
This is a backend set up for a Movie Rental application. There are three classes created which
each correspond to a Table in the SQL database:
    1. Movie
        - id(Long)
        - sku(String)
        - title(String)
        - genre(String)
        - director(String)
        - dailyRentalCost(Double)
    2. Rental
        - id(Long)
        - rentalDate(String)
        - rentedMovies (List of Rented Movie Objects)
        - rentalTotalCost(Double)
    3. Rented Movie
        - id(Long)
        - movieId(Long)
        - daysRented(int)
        - rental - this is how rented movie is connected to the Rental table, but is invisible to 
        return objects
    
Using postman requests that are available as a collection in a link below, both Movie and Rental tables
have basic CRUD functionality, within specific validation parameters. There is demo data of 20 Movies and
10 Rentals, and when the server is started then the data will be populated, and the user can make requests
to the postman collection. 

### Start the Server

Right-click AppRunner, and select "Run 'AppRunner.main()'"

## PostMan Collection Link
[Link]https://www.postman.com/cdavis2903/workspace/chandler-s-public-workspace/environment/26507437-5c30f867-597c-4f62-91f1-e55bd79998e9

### Connections

By default, this service starts up on port 8085 and accepts cross-origin requests from `*`.

#### JDK

You must have a JDK installed on your machine.

#### Postgres

This server requires that you have Postgres installed and running on the default Postgres port of
5432. It requires that you have a database created on the server with the name of `postgres`

- Your username should be `postgres`
- Your password should be `root`

## Testing

Right-click the testing file you wish to run. There are four options:
    1. MovieApiTest
    2. MovieServiceImplTest
    3. RentalApiTest
    4. RentalServiceImplTest
select "Run 'NameOfFile'" and the tests will begin.

