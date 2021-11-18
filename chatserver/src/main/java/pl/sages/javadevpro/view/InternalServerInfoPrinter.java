package pl.sages.javadevpro.view;

public class InternalServerInfoPrinter {

    public static void printNewClientConnectedInfo(){
        System.out.println("New client has connected.");
    }

    public static void printClientDisconnectedInfo(String userName) {
        System.out.println("Client " + userName + " has disconnected.");
    }

    public static void printServerRunningInfo(String serverName, int port){
        System.out.println(serverName + " is running on port  " + port);
    }

    public static void printInformationAboutIncorrectCommand() {
        System.out.println("It looks like you wanted to invoke a command");
        System.out.println("to display a list of available commands, use the command: /commands");
    }

    public static void printListOfAvailableCommands() {
        System.out.println("List of available commands:");
        System.out.println("\t/quit - closes application");
    }

    public static void printServerClosingInfo() {
        System.out.println("Server has been closed.");
    }

}
