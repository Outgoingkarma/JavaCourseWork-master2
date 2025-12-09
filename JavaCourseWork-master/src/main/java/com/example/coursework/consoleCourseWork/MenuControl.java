package com.example.coursework.consoleCourseWork;

import com.example.coursework.model.User;
//import com.example.coursework.model.VehicleType;

import java.util.Scanner;

public class MenuControl {
    public static void generateUserMenu(Scanner scanner, Wolt wolt) {
        var cmd = 0;
        while (cmd != 6) {
            System.out.println("""
                    Choose and option:
                    1 - create
                    2 - view all users
                    3 - update
                    4 - update user
                    5 - delete user
                    6 - return to main menu
                    """);
            cmd = scanner.nextInt();
            scanner.nextLine();
            var login = "";


            switch (cmd) {
                case 1:

                   /* while (true) {
                        System.out.println("Enter User data (User class): id;username;password;name;surname;phoneNum;address;licence;bdate;vehicle");
                        String input = scanner.nextLine();
                        String[] info = input.split(";");

                        try {
                            int id = Integer.parseInt(info[0]); // validate id


                            User user = new User(id, info[1], info[2], info[3], info[4], info[5], info[6], info[7]);
                            wolt.getAllSystemusers().add(user);
                            Utils.writeUserInfo(user);

                            break;

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid id input. It should be an integer. Try again!");
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Invalid input format. Please provide all fields separated by ';'.");
                        }
                    }*/

                    System.out.println("Enter User data (User class):username;password;name;surname;phoneNum;address; licence; bdate;vehicle");
                    var input = scanner.nextLine();
                    String[] info = input.split(";");
                    int id = Integer.parseInt(info[0]);
                    // User u = new User(id, info[1], info[2], info[3], info[4], info[5], info[6], info[7]);
                    // wolt.getAllSystemusers().add(u);
                    Utils.writeWoltToFile(wolt);
                    //Driver driver = new Driver(info[0], info[1], info[2], info[3], info[4], info[5], info[6], LocalDate.parse(info[7]), VehicleType.valueOf(info[8]));
                    Utils.writeWoltToFile(wolt);


                    break;
                case 2: {
                    for (var user : wolt.getAllSystemusers()) {
                        System.out.println(user);
                    }
                    break;
                }
                case 3: {
                    System.out.println("Enter login:");
                    login = scanner.nextLine();
                    for (var user : wolt.getAllSystemusers()) {
                        if (user.getLogin().equals(login)) {
                            System.out.println(user.getLogin());
                        }
                    }
                    break;
                }
                case 4: {
                    System.out.println("Enter login:");
                    login = scanner.nextLine();
                    for (var user : wolt.getAllSystemusers()) {
                        if (user.getLogin().equals(login)) {
                            System.out.println("Enter new data for: name;surname");
                            String[] infoForUpdate = scanner.nextLine().split(";");
                            user.setName(infoForUpdate[1]);
                            user.setSurname(infoForUpdate[2]);
                        }
                    }
                    break;
                }
                case 5: {
                    System.out.println("Enter login:");
                    login = scanner.nextLine();
                    for (var user : wolt.getAllSystemusers()) {
                        if (user.getLogin().equals(login)) {
                            wolt.getAllSystemusers().remove(user);
                        }
                    }
                    break;


                }

                default:
                    System.out.println();
            }
        }
    }
}
