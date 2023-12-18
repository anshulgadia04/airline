import java.util.*;
import java.util.stream.Collectors;
import java.sql.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

class Flight {
        private String flightNumber;
        private String departureCity;
        private String destinationCity;
        private int availableSeats;
        private List<Seat> seats;
        private double ticketPrice;
        private String departureTime;
        private String flightDate;

        public Flight(String flightNumber, String departureCity, String destinationCity, int totalSeats, double ticketPrice,String departureTime, String flightDate){
        this.flightNumber = flightNumber;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.availableSeats = totalSeats;
        this.seats = new ArrayList<>();

        for (int i = 1; i <= totalSeats; i++) {
            seats.add(new Seat("A" + i));
        }

        this.ticketPrice = ticketPrice;
        this.departureTime = departureTime;
        this.flightDate = flightDate;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getDate() {
        return flightDate;
    }

    public boolean bookSeats(int windowSeatsToBook, int nonWindowSeatsToBook, User user){
        if (windowSeatsToBook + nonWindowSeatsToBook <= availableSeats) {
            List<Seat> bookedSeats = new ArrayList<>();
            double totalTicketPrice = 0.0;

            for (int i = 0; i < windowSeatsToBook; i++) {
                Seat bookedSeat = seats.get(i);
                bookedSeats.add(bookedSeat);
                totalTicketPrice += ticketPrice;
            }

            for (int i = 0; i < nonWindowSeatsToBook; i++) {
                Seat bookedSeat = seats.get(windowSeatsToBook + i);
                bookedSeats.add(bookedSeat);
                totalTicketPrice += ticketPrice;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline", "root", "root")) {
            String sql = "UPDATE flights SET availableSeats = availableSeats - ? WHERE flightNumber = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, windowSeatsToBook + nonWindowSeatsToBook);
                pstmt.setString(2, flightNumber);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

            availableSeats -= (windowSeatsToBook + nonWindowSeatsToBook);

            System.out.println(windowSeatsToBook + " window seat(s) and " + nonWindowSeatsToBook
                    + " non-window seat(s) booked successfully for Flight " + flightNumber);
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println(
                    "**********************************************    Ticket Details    ***************************************************");
            System.out.println();
            System.out.println();
            System.out.println("PASSENGER NAME : " + user.getName());
            System.out.println("PASSENGER AGE : " + user.getAge());
            System.out.println("FLIGHT NUMBER : " + getFlightNumber());
            System.out.println("DESTINATION CITY : " + getDestinationCity());
            System.out.println("DEPARTURE CITY : " + getDepartureTime());
            System.out.println("DEPARTURE DATE : " + getDate());
            System.out.println("DEPARTURE TIME : " + getDepartureTime());
            System.out.println("TOTAL TICKET PRICE : " + totalTicketPrice + " RUPEES");
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("TRANSACTION(s) ID FOR THE BOOKED SEATS :");

            for (Seat bookedSeat : bookedSeats) {
                System.out.println("Seat " + bookedSeat.getSeatNumber() + ": " + bookedSeat.getPNR());
            }
            user.setBookedFlight(this);
            System.out.println();
            System.out.println();
            System.out.println();
            return true;
        }

        else {
            System.out.println("Sorry, there are not enough available seats on Flight " + flightNumber);
            return false;
        }

    }

    public double getWindowSeatExtraCharge() {

        return 100.0;
    }

    public static List<Flight> getAllFlightsFromDatabase() {
        List<Flight> flights = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline", "root", "root");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM flights")) {

            while (resultSet.next()) {
                String flightNumber = resultSet.getString("flightNumber");
                String departureCity = resultSet.getString("departureCity");
                String destinationCity = resultSet.getString("destinationCity");
                int totalSeats = resultSet.getInt("availableSeats");
                double ticketPrice = resultSet.getDouble("ticketPrice");
                String departureTime = resultSet.getString("departureTime");
                java.util.Date date = resultSet.getDate("flightDate");
                String flightDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                Flight flight = new Flight(flightNumber, departureCity, destinationCity, totalSeats, ticketPrice,
                        departureTime, flightDate);
                flights.add(flight);
            }

        } catch (SQLException e) {
            // Handle the exception appropriately, log it, or show a user-friendly message
            System.out.println("Error fetching flights from the database:");
            e.printStackTrace();
        }

        return flights;
    }
}

class Payment{
    public static boolean makeUPIPayment(String upiId, double amount) {
        System.out.println("Making UPI payment to " + upiId + " for amount: " + amount);
        if (upiId.equalsIgnoreCase("anshul04")) {
            return true;
        }
        return false;
    }

    public static boolean makeCreditCardPayment(String cardNumber, String expiryDate, String cvv, double amount,
            String otp) {
        System.out.println("Making debit card payment with card number " + cardNumber + " for amount: " + amount);
        if (otp.equalsIgnoreCase("0000")) {
            return true;
        }

        else {
            System.out.println("Invalid OTP. Payment failed.");
            return false;
        }
    }

    public static boolean makeDebitCardPayment(String cardNumber, String expiryDate, String cvv, double amount,
            String otp) {

        System.out.println("Making debit card payment with card number " + cardNumber + " for amount: " + amount);
        if (otp.equalsIgnoreCase("0000")) {
            return true;
        }

        else {
            System.out.println("Invalid OTP. Payment failed.");
            return false;
        }
    }

}

class User {
    private String name;
    private String email;
    private int age;
    private String phone;
    private Flight bookedFlight;

    public User(String name, String email, int age, String phone) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.phone = phone;
    }

    public void insertIntoUserTicket(String FlightNumber, String DepartureCity, String DestinationCity, int WindowSeat,
            int NonWindowSeat) {
        // Use try-with-resources to automatically close resources
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline", "root", "root");
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO userTicket (email, flightNumber, departureCity, destinationCity,windowSeats,nonWindowSeat) VALUES (?, ?, ?,?,?,?)")) {

            preparedStatement.setString(1, this.email);
            preparedStatement.setString(2, FlightNumber);
            preparedStatement.setString(3, DepartureCity);
            preparedStatement.setString(4, DestinationCity);
            preparedStatement.setInt(5, WindowSeat);
            preparedStatement.setInt(6, NonWindowSeat);

            
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User ticket created  successfully.");
            } else {
                System.out.println("User ticket data insertion failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error creating user ticket:");
            e.printStackTrace(); // Print the stack trace for detailed error information
        }
    }



    public boolean getUserTicket() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline", "root", "root");
                PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT * FROM userTicket WHERE email = ?")) {

            preparedStatement.setString(1, this.email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                String userEmail = "";
                String flightNumber = "";
                String departureCity = "";
                String destinationCity = "";
                int windowSeats = 0;
                int nonWindowSeats = 0;

                while (resultSet.next()) {
                    userEmail = resultSet.getString("email");
                    flightNumber = resultSet.getString("flightNumber");
                    departureCity = resultSet.getString("departureCity");
                    destinationCity = resultSet.getString("destinationCity");
                    windowSeats = resultSet.getInt("windowSeats");
                    nonWindowSeats = resultSet.getInt("nonWindowSeat");
                }

                System.out.println("Email: " + userEmail);
                System.out.println("FlightNumber: " + flightNumber);
                System.out.println("Departure City: " + departureCity);
                System.out.println("Destination City: " + destinationCity);
                System.out.println("window Seats: " + windowSeats);
                System.out.println("non window seats: " + nonWindowSeats);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user ticket from the database:");
            e.printStackTrace();
            return false;
        }
    }

    public void insertUserDataIntoDatabase() {
        // Use try-with-resources to automatically close resources
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline", "root", "root");
                PreparedStatement preparedStatement = connection
                        .prepareStatement("INSERT INTO users (name, email, age , phone) VALUES (?, ?, ?,?)")) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setInt(3, age);
            preparedStatement.setString(4, phone);

            // Execute the SQL update statement
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User data inserted successfully.");
            } else {
                System.out.println("User data insertion failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error inserting user data into the database:");
            e.printStackTrace(); 
        }
    }

    public void setBookedFlight(Flight bookedFlight) {
        this.bookedFlight = bookedFlight;
    }

    public Flight getBookedFlight() {
        return bookedFlight;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }
}

class Seat {
    private String seatNumber;
    private String pnr;

    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.pnr = generatePNR();
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getPNR() {
        return pnr;
    }

    private String generatePNR() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}

public class AirlineReservationSystem {
    private static boolean isDatabaseConnected = false;
    private static boolean isRunning = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (connectToDatabase()) {
            System.out.println("Database connected successfully.");
            isDatabaseConnected = true;
        } else {
            System.out.println("Failed to connect to the database. Please check your database configuration.");

        }

        User user = null;

        while (isRunning && isDatabaseConnected) {
            displayMenu();
            int option = 0; // Initialize the variable

            try {
                System.out.print("Enter an integer option: ");
                option = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }


            switch (option) {
                case 1:
                    user = getUserDetails(scanner);
                    user.insertUserDataIntoDatabase();
                    bookFlight(scanner, user);
                    break;
                case 2:
                    user = authenticateUser(scanner);
                    if (user != null) {
                        System.out.println("Login successful!");
                        System.out.println(
                                "----------------------------------------------        USER INFO       ---------------------------------------------------");

                        if (user.getUserTicket()) {

                        } else {
                            Flight bookedFlight = user.getBookedFlight();
                            if (bookedFlight != null) {

                            } else {
                                System.out.println("No booked flight found for the user.");
                            }
                        }

                    }
                    break;
                case 3:
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println(
                "***********************************************************************************************************************");
        System.out.println();
        System.out.println(
                "*********************************    WELCOME TO THE AIRLINE RESERVATION SYSTEM    **************************************");
        System.out.println();
        System.out.println(
                "***********************************************************************************************************************");

        System.out.println("");
        System.out.println("1. Sign Up");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Select an option: ");
    }

    private static User getUserDetails(Scanner scanner) {
        try {
            System.out.print("Enter your name: ");
            String userName = scanner.next();

            if (!userName.matches("[a-zA-Z]+")) {
                throw new InputMismatchException("Invalid name format. Please enter a valid name.");
            }

            System.out.print("Enter your email: ");
            String userEmail = scanner.next();

            if (!userEmail.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                throw new InputMismatchException("Invalid email format. Please enter a valid email address.");
            }

            System.out.print("Enter your age: ");
            int userAge = scanner.nextInt();

            if (userAge < 0 || userAge > 125) {
                throw new InputMismatchException("Invalid age. Please enter a valid age !");
            }

            String userPhone;
            while (true) {
                System.out.print("Enter your Phone Number: ");
                userPhone = scanner.next();

                if (userPhone.matches("\\d{10}")) {
                    break;
                } else {
                    System.out.println("Error: Invalid phone number format. Please enter a 10-digit phone number.");
                }
            }

            
            return new User(userName, userEmail, userAge, userPhone);
        } catch (InputMismatchException e) {
            System.out.println("Error: " + e.getMessage());

            scanner.nextLine();
            return getUserDetails(scanner);
        }
    }

    private static User authenticateUser(Scanner scanner) {
        try {
            System.out.print("Enter your email: ");
            String userEmail = scanner.next();

            System.out.print("Enter your phone number: ");
            String userPhone = scanner.next();

            // Perform user authentication based on email and phone
            User authenticatedUser = authenticateUserFromDatabase(userEmail, userPhone);

            if (authenticatedUser != null) {
                System.out.println("Authentication successful!");
                return authenticatedUser;
            } else {
                System.out.println("Authentication failed. Please check your email and phone number.");
                return null;
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: " + e.getMessage());
            scanner.nextLine(); // Consume the invalid input
            return null;
        }
    }

    private static User authenticateUserFromDatabase(String userEmail, String userPhone) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline", "root",
                "root")) {
            String query = "SELECT * FROM users WHERE email = ? AND phone = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userEmail);
                preparedStatement.setString(2, userPhone);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // User found in the database, return a User object
                        String name = resultSet.getString("name");
                        int age = resultSet.getInt("age");
                        String phone = resultSet.getString("phone");

                        return new User(name, userEmail, age, phone);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating user from the database:");
            e.printStackTrace();
        }

        return null; // User not found or an error occurred
    }

    

    private static void bookFlight(Scanner scanner, User user) {
        try {
            List<Flight> availableFlights = Flight.getAllFlightsFromDatabase();
    
            System.out.print("Enter departure city: ");
            String departureCity = scanner.next();
            System.out.print("Enter destination city: ");
            String destinationCity = scanner.next();
    
            System.out.println();
            System.out.println("Available flights from " + departureCity + " to " + destinationCity + ":");
            
            List<Flight> matchingFlights = availableFlights.stream()
                    .filter(flight -> flight.getDepartureCity().equalsIgnoreCase(departureCity)
                            && flight.getDestinationCity().equalsIgnoreCase(destinationCity))
                    .collect(Collectors.toList());
    
            if (matchingFlights.isEmpty()) {
                System.out.println("No available flights for the specified route.");
                System.out.print("Do you want to try again(yes : 1 / no : 0) : -  ");
                int res = scanner.nextInt();
                if (res == 1) {
                    bookFlight(scanner, user);
                } else {
                    return;
                }
            }
    
            for (Flight flight : matchingFlights) {
                System.out.println("Flight " + flight.getFlightNumber() + " - " + flight.getDepartureCity() + " to "
                        + flight.getDestinationCity() + " (" + flight.getAvailableSeats() + " seats available) - "
                        + "Departure Time: " + flight.getDepartureTime() + " - Date: " + flight.getDate() + " - Price: "
                        + flight.getTicketPrice() + " Rupees");
            }
    
            System.out.println();
            System.out.print("Enter the flight number you want to book: ");
            String selectedFlightNumber = scanner.next();
            Flight selectedFlight = matchingFlights.stream()
                    .filter(flight -> flight.getFlightNumber().equalsIgnoreCase(selectedFlightNumber)).findFirst()
                    .orElse(null);
    
            if (selectedFlight == null) {
                System.out.println("Invalid flight number. Booking failed.");
                System.out.print("Do you want to try again(yes : 1 / no : 0) : -  ");
                int res = scanner.nextInt();
                if (res == 1) {
                    bookFlight(scanner, user);
                } else {
                    return;
                }
            }
    
            System.out.println("Available seats on Flight " + selectedFlight.getFlightNumber() + ": "
                    + selectedFlight.getAvailableSeats());
    
            System.out.println();
            System.out.print("Enter the number of window seats you want to book: ");
            int windowSeatsToBook = scanner.nextInt();
    
            System.out.println();
            System.out.print("Enter the number of non-window seats you want to book: ");
            int nonWindowSeatsToBook = scanner.nextInt();
    
            if ((windowSeatsToBook < 0 || nonWindowSeatsToBook < 0) || (windowSeatsToBook + nonWindowSeatsToBook == 0)) {
                System.out.println("Invalid number of seats. Booking failed.");
                System.out.print("Do you want to try again(yes : 1 / no : 0) : -  ");
                int res = scanner.nextInt();
                if (res == 1) {
                    bookFlight(scanner, user);
                } else {
                    return;
                }
            }
    
            boolean isStudent = false;
            boolean isArmyOfficer = false;
            System.out.print("Are you a student or army officer? (yes/no): ");
            if (scanner.next().equalsIgnoreCase("yes")) {
                isStudent = true;
                isArmyOfficer = true;
                
            }
    
            double discountPercentage = 0.0;
    
            if (isStudent || isArmyOfficer) {
                discountPercentage = 20.0;
            }
    
            double ticketPrice = selectedFlight.getTicketPrice();
    
            // Apply discount if applicable
            if (discountPercentage > 0) {
                double discountAmount = (ticketPrice * discountPercentage * (nonWindowSeatsToBook + windowSeatsToBook))
                        / 100.0;
                ticketPrice = ticketPrice * (nonWindowSeatsToBook + windowSeatsToBook) - discountAmount;
    
                System.out.println("Congratulations! You've received a " + discountPercentage + "% discount.");
                System.out.println("Bring Valid ID during Verification");
                System.out.println("Discount Amount: " + discountAmount + " Rupees");
                System.out.println("Updated Ticket Price: " + ticketPrice + " Rupees");
            }
    
            selectedFlight.bookSeats(windowSeatsToBook, nonWindowSeatsToBook, user);
            user.insertIntoUserTicket(selectedFlight.getFlightNumber(), selectedFlight.getDepartureCity(),
                    selectedFlight.getDestinationCity(), windowSeatsToBook, nonWindowSeatsToBook);
    
            boolean paymentSuccessful = false;
    
            while (!paymentSuccessful) {
                try {
                    if (selectedFlight.getAvailableSeats() > 0) {
                        System.out.println("Payment Process:");
                        System.out.println("1. UPI Payment");
                        System.out.println("2. Credit Card Payment");
                        System.out.println("3. Debit Card Payment");
                        System.out.print("Select a payment method (1/2/3): ");
                        int paymentMethod = scanner.nextInt();
                        String Paymeth = "";
    
                        if (paymentMethod == 1) {
                            System.out.print("Enter UPI ID: ");
                            Paymeth = "UPI";
                            String upiId = scanner.next();
                            boolean paymentStatus = Payment.makeUPIPayment(upiId, selectedFlight.getTicketPrice());
    
                            if (paymentStatus) {
                                System.out.println("Payment successful!");
                                System.out.println("Ticket booked successfully.");
                                System.out.println();
                                System.out.println(
                                        "----------------------------------------------        RESERVED       ---------------------------------------------------");
    
                                try {
                                    String filePath = "C:\\Users\\Anshul Gadia\\OneDrive\\Desktop\\Portal\\"+user.getName()+".txt";
                                    Path path = Paths.get(filePath);
    
                                    if (!Files.exists(path)) {
                                        Files.createFile(path);
                                    }
                                    String data = "NAME = " + user.getName() +
                                            "\nemail = " + user.getEmail() +
                                            "\nPhone Number = " + user.getPhone() +
                                            "\npayment method = " + Paymeth +
                                            "\nflightNumber = " + selectedFlight.getFlightNumber() +
                                            "\nDestination city = " + selectedFlight.getDestinationCity() +
                                            "\nDeparture city = " + selectedFlight.getDepartureCity() +
                                            "\nDeparture time = " + selectedFlight.getDepartureTime() +
                                            "\nTicket Price = " + selectedFlight.getTicketPrice() * (nonWindowSeatsToBook + windowSeatsToBook) +
                                            "\n";
    
                                    Files.write(path, data.getBytes(), StandardOpenOption.APPEND);
    
                                    System.out.println("File created and data written successfully!");
                                } catch (IOException e) {
                                    System.out.println("An error occurred while writing to the file:");
                                    e.printStackTrace();
                                }
                                paymentSuccessful = true;
                            } else {
                                System.out.println("Payment failed. Ticket booking canceled.");
                            }
                        } else if (paymentMethod == 2 || paymentMethod == 3) {
                            Paymeth = "Debit/Credit";
                            System.out.print("Enter card number: ");
                            String cardNumber = scanner.next();
                            if (!cardNumber.matches("\\d{16}")) {
                                throw new IllegalArgumentException("Error: Card number must be a 16-digit number.");
                            }
    
                            System.out.print("Enter card expiry date (MM/YY): ");
                        String expiryDate = scanner.next();

                        
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
                        YearMonth cardExpiry = YearMonth.parse(expiryDate, formatter);
                        YearMonth currentYearMonth = YearMonth.now();

                        if (cardExpiry.isBefore(currentYearMonth)) {
                            throw new IllegalArgumentException("Error: Card has expired. Please enter a valid expiry date.");
                        }

    
                            System.out.print("Enter CVV: ");
                            String cvv = scanner.next();
                            if (!cvv.matches("\\d{3}")) {
                                throw new IllegalArgumentException("Error: CVV must be a three-digit number.");
                            }
                            System.out.print("Enter OTP: ");
                            String otp = scanner.next();
    
                            boolean paymentStatus;
                            if (paymentMethod == 2) {
                                paymentStatus = Payment.makeCreditCardPayment(cardNumber, expiryDate, cvv,
                                        selectedFlight.getTicketPrice(), otp);
                            } else {
                                paymentStatus = Payment.makeDebitCardPayment(cardNumber, expiryDate, cvv,
                                        selectedFlight.getTicketPrice(), otp);
                            }
    
                            if (paymentStatus) {
                                System.out.println("Payment successful!");
                                System.out.println("Ticket booked successfully.");
                                System.out.println();
                                System.out.println(
                                        "----------------------------------------------        RESERVED       ---------------------------------------------------");
    
                                try {
                                    String filePath = "C:\\Users\\Anshul Gadia\\OneDrive\\Desktop\\Portal\\"+user.getName()+".txt";
                                    Path path = Paths.get(filePath);
    
                                    if (!Files.exists(path)) {
                                        Files.createFile(path);
                                    }
                                    String data = "NAME = " + user.getName() +
                                            "\nemail = " + user.getEmail() +
                                            "\nPhone Number = " + user.getPhone() +
                                            "\npayment method = " + Paymeth +
                                            "\nflightNumber = " + selectedFlight.getFlightNumber() +
                                            "\nDestination city = " + selectedFlight.getDestinationCity() +
                                            "\nDeparture city = " + selectedFlight.getDepartureCity() +
                                            "\nDeparture time = " + selectedFlight.getDepartureTime() +
                                            "\nTicket Price = " + selectedFlight.getTicketPrice() * (nonWindowSeatsToBook + windowSeatsToBook) +
                                            "\n";
    
                                    Files.write(path, data.getBytes(), StandardOpenOption.APPEND);
    
                                    System.out.println("File created and data written successfully!");
                                } catch (IOException e) {
                                    System.out.println("An error occurred while writing to the file:");
                                    e.printStackTrace();
                                }
                                paymentSuccessful = true;
                            } else {
                                System.out.println("Payment failed. Ticket booking canceled.");
                            }
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Error: Invalid input for payment method. Please enter 1, 2, or 3.");
                    scanner.nextLine(); // Consume the invalid input
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                    scanner.nextLine(); // Consume the   invalid input
                } catch (Exception e) {
                    System.out.println("An unexpected error occurred during payment processing: " + e.getMessage());
                    e.printStackTrace(); // Print the stack trace for detailed error information
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input. Please enter valid data.");
            scanner.nextLine(); // Consume the invalid input
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for detailed error information
        }
    }
        

    private static boolean connectToDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline1", "root",
                "root")) {
            return true;
        } catch (SQLException e) {
            System.out.println("Error connecting to the database:");
            e.printStackTrace();
            return false;
        }
    }
}
