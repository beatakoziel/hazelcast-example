package com.hazelcast;

import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.models.Player;
import com.hazelcast.models.SportClub;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
        ClientConfig clientConfig = HazelcastConfig.getClientConfig();
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        IMap<UUID, Player> playersMap = client.getMap("players");
        IMap<UUID, SportClub> clubsMap = client.getMap("clubs");
        while (true) {
            Integer choice = printMenu();
            clearScreen();
            System.out.println(choice);
            if (choice > 0 && choice < 9) {
                switch (choice) {
                    case 1:
                        addElementToDatabase(playersMap, clubsMap);
                        break;
                    case 2:
                        editElement(playersMap, clubsMap);
                        break;
                    case 3:
                        getElementById(playersMap, clubsMap);
                        break;
                    case 4:
                        getAll(playersMap, clubsMap);
                        break;
                    case 5:
                        removeElement(playersMap, clubsMap);
                        break;
                    case 6:
                        calculateAveragePlayerSalary(playersMap);
                        break;
                    case 7:
                        calculateAveragePlayerSalaryClient(playersMap);
                        break;
                    case 8:
                        getElementByName(playersMap, clubsMap);
                        break;
                }
                System.out.println("Press enter to continue...");
                System.in.read();
            } else System.out.println("Wrong number, choose again.");
        }
    }

    private static void getAll(IMap<UUID, Player> players, IMap<UUID, SportClub> clubs) throws IOException {
        System.out.println("Getting all values");
        Integer s = printSubMenu();
        if (s > 0 && s < 3) {
            switch (s) {
                case 1:
                    for (Map.Entry<UUID, Player> e : players.entrySet()) {
                        System.out.println(e.getKey() + " => " + e.getValue());
                    }
                    break;
                case 2:
                    for (Map.Entry<UUID, SportClub> e : clubs.entrySet()) {
                        System.out.println(e.getKey() + " => " + e.getValue());
                    }
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void getElementById(IMap<UUID, Player> players, IMap<UUID, SportClub> clubs) throws IOException {
        System.out.println("Getting by id");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    if (isValidUUID(playerId) && players.containsKey(UUID.fromString(playerId))) {
                        Player player = players.get(UUID.fromString(playerId));
                        System.out.println(playerId + " => " + player.toString());
                    } else System.out.printf("Player with id %s not found.%n", playerId);
                    break;
                case 2:
                    String clubId = scanner.next();
                    if (isValidUUID(clubId) && clubs.containsKey(UUID.fromString(clubId))) {
                        SportClub club = clubs.get(UUID.fromString(clubId));
                        System.out.println(clubId + " => " + club.toString());
                    } else System.out.printf("Club with id %s not found.%n", clubId);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void getElementByName(IMap<UUID, Player> players, IMap<UUID, SportClub> clubs) throws IOException {
        System.out.println("Getting by name");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write name:");
            switch (s) {
                case 1:
                    String playerName = scanner.next();
                    Predicate predicate = Predicates.equal("firstname", playerName);
                    Collection<Player> playersCollection = players.values(predicate);
                    playersCollection.forEach(player -> System.out.println(player.toString()));
                    break;
                case 2:
                    String clubName = scanner.next();
                    Predicate clubPredicate = Predicates.equal("name", clubName);
                    Collection<SportClub> clubsCollection = clubs.values(clubPredicate);
                    clubsCollection.forEach(player -> System.out.println(player.toString()));
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void editElement(IMap<UUID, Player> players, IMap<UUID, SportClub> clubs) {
        System.out.println("Editing");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    if (isValidUUID(playerId) && players.containsKey(UUID.fromString(playerId))) {
                        Player player = getPlayerFromUser(clubs, scanner);
                        players.put(UUID.fromString(playerId), player);
                        System.out.println(playerId + " => " + player.toString());
                    } else System.out.printf("Player with id %s not found.%n", playerId);
                    break;
                case 2:
                    String clubId = scanner.next();
                    if (isValidUUID(clubId) && clubs.containsKey(UUID.fromString(clubId))) {
                        SportClub sportClub = getSportClub(scanner);
                        clubs.put(UUID.fromString(clubId), sportClub);
                        System.out.println(clubId + " => " + sportClub.toString());
                    } else System.out.printf("Club with id %s not found.%n", clubId);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void removeElement(IMap<UUID, Player> players, IMap<UUID, SportClub> clubs) {
        System.out.println("Removing");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    if (isValidUUID(playerId) && players.containsKey(UUID.fromString(playerId))) {
                        Player player = players.remove(UUID.fromString(playerId));
                        System.out.println("Removed:" + playerId + " => " + player.toString());
                    } else System.out.printf("Player with id %s not found.%n", playerId);
                    break;
                case 2:
                    String clubId = scanner.next();
                    if (isValidUUID(clubId) && clubs.containsKey(UUID.fromString(clubId))) {
                        SportClub club = clubs.remove(UUID.fromString(clubId));
                        System.out.println("Removed:" + clubId + " => " + club.toString());
                    } else System.out.printf("Club with id %s not found.%n", clubId);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private final static Pattern UUID_REGEX_PATTERN =
            Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public static boolean isValidUUID(String str) {
        if (str == null) {
            return false;
        }
        return UUID_REGEX_PATTERN.matcher(str).matches();
    }

    private static void addElementToDatabase(IMap<UUID, Player> players, IMap<UUID, SportClub> clubs) {
        System.out.println("Adding to database");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            switch (s) {
                case 1:
                    Player player = getPlayerFromUser(clubs, scanner);
                    players.put(UUID.randomUUID(), player);
                    break;
                case 2:
                    SportClub sportClub = getSportClub(scanner);
                    clubs.put(UUID.randomUUID(), sportClub);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void calculateAveragePlayerSalary(IMap<UUID, Player> players) {
        System.out.println("Calculate average salary");
        Double averageSalary = players.aggregate(Aggregators.integerAvg("salary"));
        System.out.println("Average player salary: " + averageSalary);
    }

    private static void calculateAveragePlayerSalaryClient(IMap<UUID, Player> players) {
        System.out.println("Calculate average salary");
        double averageSalary = players.values().stream()
                .mapToDouble(Player::getSalary)
                .average()
                .orElse(0);
        System.out.println("Average player salary: " + averageSalary);
    }

    private static SportClub getSportClub(Scanner scanner) {
        System.out.println("Write club name:");
        String name = scanner.next();
        System.out.println("Write creation year:");
        Integer creationYear = scanner.nextInt();
        return SportClub.builder()
                .name(name)
                .creationYear(creationYear)
                .build();
    }

    private static Player getPlayerFromUser(IMap<UUID, SportClub> clubs, Scanner scanner) {
        System.out.println("Write player first name:");
        String firstname = scanner.next();
        System.out.println("Write player surname:");
        String surname = scanner.next();
        System.out.println("Write club id:");
        String clubId = scanner.next();
        System.out.println("Write player salary:");
        Integer playerSalary = scanner.nextInt();
        SportClub club = null;
        if (isValidUUID(clubId) && clubs.containsKey(UUID.fromString(clubId))) {
            club = clubs.get(UUID.fromString(clubId));
            System.out.println(clubId + " => " + club.toString());
        } else System.out.printf("Club with id %s not found.%n", clubId);
        return Player.builder()
                .firstname(firstname)
                .surname(surname)
                .club(club)
                .salary(playerSalary)
                .build();
    }

    private static Integer printMenu() {
        System.out.println("\nSPORTS CLUB - HAZELCAST");
        System.out.println("\nChoose operation:");
        System.out.println("1.ADD");
        System.out.println("2.EDIT");
        System.out.println("3.GET BY ID");
        System.out.println("4.GET ALL");
        System.out.println("5.REMOVE");
        System.out.println("6.CALCULATE AVERAGE PLAYER SALARY");
        System.out.println("7.CALCULATE AVERAGE PLAYER SALARY BY CLIENT");
        System.out.println("8.GET BY NAME");
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    private static Integer printSubMenu() {
        System.out.println("\nChoose table:");
        System.out.println("1.PLAYERS");
        System.out.println("2.SPORT CLUBS");
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
